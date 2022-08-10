package net.catenax.edc.apiwrapper;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.UriInfo;
import net.catenax.edc.apiwrapper.cache.InMemoryContractAgreementCache;
import net.catenax.edc.apiwrapper.cache.InMemoryEndpointDataReferenceCache;
import net.catenax.edc.apiwrapper.config.ApiWrapperConfig;
import net.catenax.edc.apiwrapper.connector.sdk.model.ContractNegotiationDto;
import net.catenax.edc.apiwrapper.connector.sdk.service.ContractNegotiationService;
import net.catenax.edc.apiwrapper.connector.sdk.service.ContractOfferService;
import net.catenax.edc.apiwrapper.connector.sdk.service.HttpProxyService;
import net.catenax.edc.apiwrapper.connector.sdk.service.TransferProcessService;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractOffer;
import org.eclipse.dataspaceconnector.spi.types.domain.edr.EndpointDataReference;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class ApiWrapperControllerTest {

    private final Monitor monitor = mock(Monitor.class);
    private final ContractOfferService contractOfferService = mock(ContractOfferService.class);
    private final ContractNegotiationService contractNegotiationService = mock(ContractNegotiationService.class);
    private final TransferProcessService transferProcessService = mock(TransferProcessService.class);
    private final HttpProxyService httpProxyService = mock(HttpProxyService.class);
    private final InMemoryEndpointDataReferenceCache endpointDataReferenceCache = mock(InMemoryEndpointDataReferenceCache.class);
    private final InMemoryContractAgreementCache contractAgreementCache = mock(InMemoryContractAgreementCache.class);
    private final ApiWrapperConfig apiWrapperConfig = mock(ApiWrapperConfig.class);

    private final ApiWrapperController apiWrapperController = new ApiWrapperController(
            monitor, contractOfferService, contractNegotiationService, transferProcessService,
            httpProxyService, endpointDataReferenceCache, contractAgreementCache, apiWrapperConfig);

    @Test
    void getWrapperFromParameters_agreementIdShouldExistAndDataReferenceShouldBeValid() throws IOException, InterruptedException {

        String providerConnectorUrl = "http://localhost:8081";
        String assetId = "anAsset";
        String subUrl = "aSubUrl";
        UriInfo uriInfo = mock(UriInfo.class);

        Map<String, String> map = new HashMap<>();
        map.put("cid", "reference");

        EndpointDataReference endpointDataReferenceTest = EndpointDataReference.Builder.newInstance()
                .id("idRef")
                .endpoint("anEndpoint")
                .authKey("myKey")
                .authCode("SecCode")
                .properties(map)
                .build();

        when(contractAgreementCache.get(any())).thenReturn("thisisanagreementid");
        when(endpointDataReferenceCache.get(any())).thenReturn(endpointDataReferenceTest);
        when(httpProxyService.sendGETRequest(any(), any(), any())).thenReturn("theDataResult");

        try (MockedStatic<InMemoryEndpointDataReferenceCache> mockedStatic = mockStatic(InMemoryEndpointDataReferenceCache.class)) {

            when(InMemoryEndpointDataReferenceCache.endpointDataRefTokenExpired(any())).thenReturn(Boolean.TRUE);

            String data = apiWrapperController.getWrapper(providerConnectorUrl, assetId, subUrl, uriInfo);
            assertThat(data).isEqualTo("theDataResult");
        }
    }

    @Test
    void postWrapperFromParameters_agreementIdShouldExistAndDataReferenceShouldBeValid() throws IOException, InterruptedException {

        String providerConnectorUrl = "http://localhost:8081";
        String assetId = "anAsset";
        String subUrl = "aSubUrl";
        UriInfo uriInfo = mock(UriInfo.class);

        Map<String, String> map = new HashMap<>();
        map.put("cid", "reference");

        EndpointDataReference endpointDataReferenceTest = EndpointDataReference.Builder.newInstance()
                .id("idRef")
                .endpoint("anEndpoint")
                .authKey("myKey")
                .authCode("SecCode")
                .properties(map)
                .build();

        when(contractAgreementCache.get(any())).thenReturn("thisisanagreementid");
        when(endpointDataReferenceCache.get(any())).thenReturn(endpointDataReferenceTest);
        when(httpProxyService.sendPOSTRequest(any(), any(), any(), any(), any())).thenReturn("theDataResult");

        try (MockedStatic<InMemoryEndpointDataReferenceCache> mockedStatic = mockStatic(InMemoryEndpointDataReferenceCache.class)) {

            when(InMemoryEndpointDataReferenceCache.endpointDataRefTokenExpired(any())).thenReturn(Boolean.TRUE);

            String data = apiWrapperController.postWrapper(providerConnectorUrl, assetId, subUrl, "body", uriInfo);
            assertThat(data).isEqualTo("theDataResult");
        }
    }

    @Test
    void getWrapperFromParameters_ContractOfferIsEmpty() throws IOException {

        String providerConnectorUrl = "http://localhost:8081";
        String assetId = "anAsset";
        String subUrl = "aSubUrl";
        UriInfo uriInfo = mock(UriInfo.class);

        when(contractAgreementCache.get(any())).thenReturn(null);
        when(contractOfferService.findContractOffer4AssetId(any(), any(), any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> apiWrapperController.getWrapper(providerConnectorUrl, assetId, subUrl, uriInfo)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void postWrapperFromParameters_ContractOfferIsEmpty() throws IOException {

        String providerConnectorUrl = "http://localhost:8081";
        String assetId = "anAsset";
        String subUrl = "aSubUrl";
        UriInfo uriInfo = mock(UriInfo.class);

        when(contractAgreementCache.get(any())).thenReturn(null);
        when(contractOfferService.findContractOffer4AssetId(any(), any(), any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> apiWrapperController.postWrapper(providerConnectorUrl, assetId, subUrl, "body", uriInfo)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void getWrapperFromParameters_ContractOfferIsNotEmptyAndDataReferenceShouldNotBeValid() throws IOException, InterruptedException {

        String providerConnectorUrl = "http://localhost:8081";
        String assetId = "anAsset";
        String subUrl = "aSubUrl";
        UriInfo uriInfo = mock(UriInfo.class);

        Map<String, String> map = new HashMap<>();
        map.put("cid", "reference");

        EndpointDataReference endpointDataReferenceTest = EndpointDataReference.Builder.newInstance()
                .id("idRef")
                .endpoint("anEndpoint")
                .authKey("myKey")
                .authCode("SecCode")
                .properties(map)
                .build();

        when(contractAgreementCache.get(any())).thenReturn(null);
        when(contractOfferService.findContractOffer4AssetId(any(), any(), any(), any())).thenReturn(Optional.of(getDummyContractOffer()));
        when(contractNegotiationService.getNegotiation(any(), any(), any())).thenReturn(getDummyContractNegotiaionDto());
        when(httpProxyService.sendGETRequest(any(), any(), any())).thenReturn("theDataResult");
        when(endpointDataReferenceCache.get(any())).thenReturn(endpointDataReferenceTest);

        try (MockedStatic<InMemoryEndpointDataReferenceCache> mockedStatic = mockStatic(InMemoryEndpointDataReferenceCache.class)) {

            when(InMemoryEndpointDataReferenceCache.endpointDataRefTokenExpired(any())).thenReturn(Boolean.TRUE);

            String data = apiWrapperController.getWrapper(providerConnectorUrl, assetId, subUrl, uriInfo);
            assertThat(data).isEqualTo("theDataResult");
        }
    }

    @Test
    void postWrapperFromParameters_ContractOfferIsNotEmptyAndDataReferenceShouldNotBeValid() throws IOException, InterruptedException {

        String providerConnectorUrl = "http://localhost:8081";
        String assetId = "anAsset";
        String subUrl = "aSubUrl";
        UriInfo uriInfo = mock(UriInfo.class);

        Map<String, String> map = new HashMap<>();
        map.put("cid", "reference");

        EndpointDataReference endpointDataReferenceTest = EndpointDataReference.Builder.newInstance()
                .id("idRef")
                .endpoint("anEndpoint")
                .authKey("myKey")
                .authCode("SecCode")
                .properties(map)
                .build();

        when(contractAgreementCache.get(any())).thenReturn(null);
        when(contractOfferService.findContractOffer4AssetId(any(), any(), any(), any())).thenReturn(Optional.of(getDummyContractOffer()));
        when(contractNegotiationService.getNegotiation(any(), any(), any())).thenReturn(getDummyContractNegotiaionDto());
        when(httpProxyService.sendPOSTRequest(any(), any(), any(), any(), any())).thenReturn("theDataResult");
        when(endpointDataReferenceCache.get(any())).thenReturn(endpointDataReferenceTest);

        try (MockedStatic<InMemoryEndpointDataReferenceCache> mockedStatic = mockStatic(InMemoryEndpointDataReferenceCache.class)) {

            when(InMemoryEndpointDataReferenceCache.endpointDataRefTokenExpired(any())).thenReturn(Boolean.TRUE);

            String data = apiWrapperController.postWrapper(providerConnectorUrl, assetId, subUrl, "body", uriInfo);
            assertThat(data).isEqualTo("theDataResult");
        }
    }

    @Test
    void getWrapperFromParameters_shouldNotExistAgreementAndIsNotValidDataReference() throws IOException {

        String providerConnectorUrl = "http://localhost:8081";
        String assetId = "anAsset";
        String subUrl = "aSubUrl";
        UriInfo uriInfo = mock(UriInfo.class);

        Map<String, String> map = new HashMap<>();
        map.put("cid", "reference");

        EndpointDataReference endpointDataReferenceTest = EndpointDataReference.Builder.newInstance()
                .id("idRef")
                .endpoint("anEndpoint")
                .authKey("myKey")
                .authCode("SecCode")
                .properties(map)
                .build();

        when(contractAgreementCache.get(any())).thenReturn(null);
        when(contractOfferService.findContractOffer4AssetId(any(), any(), any(), any())).thenReturn(Optional.of(getDummyContractOffer()));
        when(contractNegotiationService.getNegotiation(any(), any(), any())).thenReturn(getDummyContractNegotiaionDto());

        assertThatThrownBy(() -> apiWrapperController.getWrapper(providerConnectorUrl, assetId, subUrl, uriInfo)).isInstanceOf(InternalServerErrorException.class);

    }

    @Test
    void postWrapperFromParameters_shouldNotExistAgreementAndIsNotValidDataReference() throws IOException {

        String providerConnectorUrl = "http://localhost:8081";
        String assetId = "anAsset";
        String subUrl = "aSubUrl";
        UriInfo uriInfo = mock(UriInfo.class);

        Map<String, String> map = new HashMap<>();
        map.put("cid", "reference");

        EndpointDataReference endpointDataReferenceTest = EndpointDataReference.Builder.newInstance()
                .id("idRef")
                .endpoint("anEndpoint")
                .authKey("myKey")
                .authCode("SecCode")
                .properties(map)
                .build();

        when(contractAgreementCache.get(any())).thenReturn(null);
        when(contractOfferService.findContractOffer4AssetId(any(), any(), any(), any())).thenReturn(Optional.of(getDummyContractOffer()));
        when(contractNegotiationService.getNegotiation(any(), any(), any())).thenReturn(getDummyContractNegotiaionDto());

        assertThatThrownBy(() -> apiWrapperController.postWrapper(providerConnectorUrl, assetId, subUrl, "body", uriInfo)).isInstanceOf(InternalServerErrorException.class);

    }

    private ContractOffer getDummyContractOffer() {

        return ContractOffer.Builder.newInstance()
                .id("DummyPolicyId")
                .policy(Policy.Builder.newInstance().build())
                .asset(Asset.Builder.newInstance().id("assetDummyId").build())
                .provider(URI.create("Uri/Provider"))
                .consumer(URI.create("Uri/Consumer"))
                .offerStart(ZonedDateTime.now())
                .offerEnd(ZonedDateTime.of(2025, 10, 25, 15, 10, 21, 252, ZoneId.of("America/Chicago")))
                .contractStart(ZonedDateTime.now())
                .contractEnd((ZonedDateTime.of(2025, 10, 25, 15, 10, 21, 252, ZoneId.of("America/Chicago"))))
                .build();
    }

    private ContractNegotiationDto getDummyContractNegotiaionDto() {

        return ContractNegotiationDto.Builder.newInstance()
                .contractAgreementId("cAgrId")
                .counterPartyAddress("cpAddress")
                .errorDetail("")
                .id("dtoId")
                .protocol("ids-multipart")
                .state("CONFIRMED")
                .build();
    }
}
