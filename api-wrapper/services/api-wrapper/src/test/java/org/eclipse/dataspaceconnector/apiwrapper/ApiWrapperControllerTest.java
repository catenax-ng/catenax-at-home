package org.eclipse.dataspaceconnector.apiwrapper;

import jakarta.ws.rs.core.UriInfo;
import org.eclipse.dataspaceconnector.apiwrapper.ApiWrapperController;
import org.eclipse.dataspaceconnector.apiwrapper.cache.InMemoryContractAgreementCache;
import org.eclipse.dataspaceconnector.apiwrapper.cache.InMemoryEndpointDataReferenceCache;
import org.eclipse.dataspaceconnector.apiwrapper.config.ApiWrapperConfig;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service.ContractNegotiationService;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service.ContractOfferService;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service.HttpProxyService;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service.TransferProcessService;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.edr.EndpointDataReference;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
    void getWrapperFromParameters_shouldExistsAgreementIdAndDataReference() throws IOException, InterruptedException {

        String providerConnectorUrl = "http://localhost:8081";
        String assetId = "anAsset";
        String subUrl = "aSubUrl";
        UriInfo uriInfo = mock(UriInfo.class);

        Map<String,String> map = new HashMap<>();
        map.put("cid","reference");

        EndpointDataReference endpointDataReferenceTest = EndpointDataReference.Builder.newInstance()
                .id("idRef")
                .endpoint("anEndpoint")
                .authKey("myKey")
                .authCode("SecCode")
                .properties(map)
                .build();

        when(contractAgreementCache.get(any())).thenReturn("thisisanagreementid");
        when(endpointDataReferenceCache.get(any())).thenReturn(endpointDataReferenceTest);
        when(httpProxyService.sendGETRequest(any(),any(),any())).thenReturn("theDataResult");

        apiWrapperController.getWrapper(providerConnectorUrl,assetId,subUrl,uriInfo);

    }

}
