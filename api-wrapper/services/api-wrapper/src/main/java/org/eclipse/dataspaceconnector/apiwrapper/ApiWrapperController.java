package org.eclipse.dataspaceconnector.apiwrapper;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.dataspaceconnector.apiwrapper.config.ApiWrapperConfig;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.model.ContractNegotiationDto;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.model.ContractOfferDescription;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.model.NegotiationInitiateRequestDto;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service.ContractNegotiationService;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service.ContractOfferService;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service.HttpProxyService;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service.TransferProcessService;
import org.eclipse.dataspaceconnector.apiwrapper.store.InMemoryContractAgreementStore;
import org.eclipse.dataspaceconnector.apiwrapper.store.InMemoryEndpointDataReferenceStore;
import org.eclipse.dataspaceconnector.policy.model.Action;
import org.eclipse.dataspaceconnector.policy.model.Permission;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.policy.model.PolicyType;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.edr.EndpointDataReference;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Consumes()
@Produces({ MediaType.APPLICATION_JSON })
@Path("/service")
public class ApiWrapperController {

    // Connection configurations
    private static final String IDS_PATH = "/api/v1/ids/data";
    private static final Pattern RESPONSE_PATTERN = Pattern.compile("\\{\"data\":\"(?<embeddedData>.*)\"\\}");

    private final Monitor monitor;
    private final ContractOfferService contractOfferService;
    private final ContractNegotiationService contractNegotiationService;
    private final TransferProcessService transferProcessService;
    private final HttpProxyService httpProxyService;

    // In-memory stores
    private final InMemoryEndpointDataReferenceStore endpointDataReferenceStore;
    private final InMemoryContractAgreementStore contractAgreementStore;

    private final ApiWrapperConfig config;

    private Map<String, String> header;

    public ApiWrapperController(Monitor monitor,
                                ContractOfferService contractOfferService,
                                ContractNegotiationService contractNegotiationService,
                                TransferProcessService transferProcessService,
                                HttpProxyService httpProxyService,
                                InMemoryEndpointDataReferenceStore endpointDataReferenceStore,
                                InMemoryContractAgreementStore contractAgreementStore,
                                ApiWrapperConfig config) {
        this.monitor = monitor;
        this.contractOfferService = contractOfferService;
        this.contractNegotiationService = contractNegotiationService;
        this.transferProcessService = transferProcessService;
        this.httpProxyService = httpProxyService;
        this.endpointDataReferenceStore = endpointDataReferenceStore;
        this.contractAgreementStore = contractAgreementStore;
        this.config = config;

        if (config.getConsumerEdcApiKeyValue() != null) {
            this.header = Collections.singletonMap(config.getConsumerEdcApiKeyName(), config.getConsumerEdcApiKeyValue());
        }
    }

    @GET
    @Path("/{assetId}/{subUrl:.+}")
    public String getWrapper(
            @QueryParam("provider-connector-url") String providerConnectorUrl,
            @PathParam("assetId") String assetId,
            @PathParam("subUrl") String subUrl,
            @Context UriInfo uriInfo
    ) throws InterruptedException, IOException {
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        // Initialize and negotiate everything
        var agreementId = initializeContractNegotiation(providerConnectorUrl, assetId);

        // Initiate transfer process
        transferProcessService.initiateHttpProxyTransferProcess(
                agreementId,
                assetId,
                config.getConsumerEdcControlUrl(),
                providerConnectorUrl + IDS_PATH,
                header
        );

        EndpointDataReference dataReference = getDataReference(agreementId);

        // Get data through data plane
        String data = "";
        try {
            data = httpProxyService.sendGETRequest(dataReference, subUrl, queryParams);
            Matcher dataMatcher = RESPONSE_PATTERN.matcher(data);
            while (dataMatcher.matches()) {
                data = dataMatcher.group("embeddedData");
                data = data.replace("\\\"", "\"").replace("\\\\", "\\");
                dataMatcher = RESPONSE_PATTERN.matcher(data);
            }
        } catch (IOException e) {
            monitor.severe("Call against consumer data plane failed!", e);
        }
        return data;
    }

    @POST
    @Path("/{assetId}/{subUrl:.+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String postWrapper(
            @QueryParam("provider-connector-url") String providerConnectorUrl,
            @PathParam("assetId") String assetId,
            @PathParam("subUrl") String subUrl,
            String body,
            @Context UriInfo uriInfo
    ) throws InterruptedException, IOException {
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        // Initialize and negotiate everything
        var agreementId = initializeContractNegotiation(providerConnectorUrl, assetId);

        // Initiate transfer process
        transferProcessService.initiateHttpProxyTransferProcess(
                agreementId,
                assetId,
                config.getConsumerEdcControlUrl(),
                providerConnectorUrl + IDS_PATH,
                header
        );

        EndpointDataReference dataReference = getDataReference(agreementId);

        // Get data through data plane
        String data = "";
        try {
            data = httpProxyService.sendPOSTRequest(
                    dataReference,
                    subUrl,
                    queryParams,
                    body,
                    Objects.requireNonNull(okhttp3.MediaType.parse("application/json"))
            );
            Matcher dataMatcher = RESPONSE_PATTERN.matcher(data);
            while (dataMatcher.matches()) {
                data = dataMatcher.group("embeddedData");
                data = data.replace("\\\"", "\"").replace("\\\\", "\\");
                dataMatcher = RESPONSE_PATTERN.matcher(data);
            }
        } catch (IOException e) {
            monitor.severe("Call against consumer data plane failed!", e);
        }
        return data;
    }

    private String initializeContractNegotiation(String providerConnectorUrl, String assetId) throws InterruptedException, IOException {
        String agreementId = contractAgreementStore.get(assetId);

        if (agreementId != null) {
            monitor.debug("Found already existing contract agreement in cache");
            return agreementId;
        }

        monitor.info("Initialize contract negotiation");
        var contractOffer = contractOfferService.findContractOffer4AssetId(
                assetId,
                config.getConsumerEdcControlUrl(),
                providerConnectorUrl + IDS_PATH,
                header
        );

        if (contractOffer.isEmpty()) {
            throw new BadRequestException("No contract offer found which correspond with the given asset id.");
        }

        // Initiate negotiation
        var policy = Policy.Builder.newInstance()
                .permission(Permission.Builder.newInstance()
                        .target(contractOffer.get().getAsset().getId())
                        .action(Action.Builder.newInstance().type("USE").build())
                        .build())
                .type(PolicyType.SET)
                .build();
        var contractOfferDescription = new ContractOfferDescription(
                contractOffer.get().getId(),
                contractOffer.get().getAsset().getId(),
                null,
                policy
        );
        var contractNegotiationRequest = NegotiationInitiateRequestDto.Builder.newInstance()
                .offerId(contractOfferDescription)
                .connectorId("provider")
                .connectorAddress(providerConnectorUrl + IDS_PATH)
                .protocol("ids-multipart")
                .build();
        var negotiationId = contractNegotiationService.initiateNegotiation(
                contractNegotiationRequest,
                config.getConsumerEdcDataManagementUrl(),
                header
        );

        // Check negotiation state
        ContractNegotiationDto negotiation = null;

        while (negotiation == null || !negotiation.getState().equals("CONFIRMED")) {
            Thread.sleep(1000);
            negotiation = contractNegotiationService.getNegotiation(
                    negotiationId,
                    config.getConsumerEdcDataManagementUrl(),
                    header
            );
        }

        agreementId = negotiation.getContractAgreementId();
        contractAgreementStore.put(assetId, agreementId);

        return agreementId;
    }

    private EndpointDataReference getDataReference(String agreementId) throws InterruptedException {
        EndpointDataReference dataReference = null;
        var waitTimeout = 10;

        while (dataReference == null && waitTimeout > 0) {
            Thread.sleep(1000);
            dataReference = endpointDataReferenceStore.get(agreementId);
            waitTimeout--;
        }

        if (dataReference == null) {
            throw new InternalServerErrorException("Did not receive callback within 10 seconds from consumer edc.");
        }

        return dataReference;
    }
}
