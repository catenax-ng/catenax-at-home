package net.catenax.edc.apiwrapper;

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
import net.catenax.edc.apiwrapper.cache.InMemoryContractAgreementCache;
import net.catenax.edc.apiwrapper.cache.InMemoryEndpointDataReferenceCache;
import net.catenax.edc.apiwrapper.connector.sdk.service.HttpProxyService;
import net.catenax.edc.apiwrapper.connector.sdk.service.TransferProcessService;
import net.catenax.edc.apiwrapper.config.ApiWrapperConfig;
import net.catenax.edc.apiwrapper.connector.sdk.model.ContractNegotiationDto;
import net.catenax.edc.apiwrapper.connector.sdk.model.ContractOfferDescription;
import net.catenax.edc.apiwrapper.connector.sdk.model.NegotiationInitiateRequestDto;
import net.catenax.edc.apiwrapper.connector.sdk.service.ContractNegotiationService;
import net.catenax.edc.apiwrapper.connector.sdk.service.ContractOfferService;
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
    private final InMemoryEndpointDataReferenceCache endpointDataReferenceCache;
    private final InMemoryContractAgreementCache contractAgreementCache;

    private final ApiWrapperConfig config;

    private Map<String, String> header;

    public ApiWrapperController(Monitor monitor,
                                ContractOfferService contractOfferService,
                                ContractNegotiationService contractNegotiationService,
                                TransferProcessService transferProcessService,
                                HttpProxyService httpProxyService,
                                InMemoryEndpointDataReferenceCache endpointDataReferenceCache,
                                InMemoryContractAgreementCache contractAgreementCache,
                                ApiWrapperConfig config) {
        this.monitor = monitor;
        this.contractOfferService = contractOfferService;
        this.contractNegotiationService = contractNegotiationService;
        this.transferProcessService = transferProcessService;
        this.httpProxyService = httpProxyService;
        this.endpointDataReferenceCache = endpointDataReferenceCache;
        this.contractAgreementCache = contractAgreementCache;
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

        String agreementId = contractAgreementCache.get(assetId);
        if (agreementId == null) {
            // Initialize and negotiate everything
            agreementId = initializeContractNegotiation(providerConnectorUrl, assetId);
        }

        EndpointDataReference dataReference = endpointDataReferenceCache.get(agreementId);
        boolean validDataReference = dataReference != null && InMemoryEndpointDataReferenceCache.endpointDataRefTokenExpired(dataReference);
        if (!validDataReference) {
            monitor.debug("EndpointDataReference does not exists or token is expired.");
            endpointDataReferenceCache.remove(agreementId);

            // Initiate transfer process
            transferProcessService.initiateHttpProxyTransferProcess(
                    agreementId,
                    assetId,
                    config.getConsumerEdcDataManagementUrl(),
                    providerConnectorUrl + IDS_PATH,
                    header
            );

            dataReference = getDataReference(agreementId);
        }

        // Get data through data plane
        try {
            String data = httpProxyService.sendGETRequest(dataReference, subUrl, queryParams);
            Matcher dataMatcher = RESPONSE_PATTERN.matcher(data);
            while (dataMatcher.matches()) {
                data = dataMatcher.group("embeddedData");
                data = data.replace("\\\"", "\"").replace("\\\\", "\\");
                dataMatcher = RESPONSE_PATTERN.matcher(data);
            }
            return data;
        } catch (IOException e) {
            monitor.severe("Call against consumer data plane failed!", e);
            throw e;
        }
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

        String agreementId = contractAgreementCache.get(assetId);
        if (agreementId == null) {
            // Initialize and negotiate everything
            agreementId = initializeContractNegotiation(providerConnectorUrl, assetId);
        }

        EndpointDataReference dataReference = endpointDataReferenceCache.get(agreementId);
        boolean validDataReference = dataReference != null && InMemoryEndpointDataReferenceCache.endpointDataRefTokenExpired(dataReference);
        if (!validDataReference) {
            monitor.debug("EndpointDataReference does not exists or token is expired.");
            endpointDataReferenceCache.remove(agreementId);

            // Initiate transfer process
            transferProcessService.initiateHttpProxyTransferProcess(
                    agreementId,
                    assetId,
                    config.getConsumerEdcDataManagementUrl(),
                    providerConnectorUrl + IDS_PATH,
                    header
            );

            dataReference = getDataReference(agreementId);
        }

        // Get data through data plane
        try {
            String data = httpProxyService.sendPOSTRequest(
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
            return data;
        } catch (IOException e) {
            monitor.severe("Call against consumer data plane failed!", e);
            throw e;
        }
    }

    private String initializeContractNegotiation(String providerConnectorUrl, String assetId) throws InterruptedException, IOException {
        monitor.info("Initialize contract negotiation");
        var contractOffer = contractOfferService.findContractOffer4AssetId(
                assetId,
                config.getConsumerEdcDataManagementUrl(),
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

        String agreementId = negotiation.getContractAgreementId();
        contractAgreementCache.put(assetId, agreementId);

        return agreementId;
    }

    private EndpointDataReference getDataReference(String agreementId) throws InterruptedException {
        EndpointDataReference dataReference = null;
        var waitTimeout = 10;

        while (dataReference == null && waitTimeout > 0) {
            Thread.sleep(1000);
            dataReference = endpointDataReferenceCache.get(agreementId);
            waitTimeout--;
        }

        if (dataReference == null) {
            String errorMsg = "Did not receive callback within 10 seconds from consumer edc.";
            monitor.severe(errorMsg);
            throw new InternalServerErrorException(errorMsg);
        }

        return dataReference;
    }
}
