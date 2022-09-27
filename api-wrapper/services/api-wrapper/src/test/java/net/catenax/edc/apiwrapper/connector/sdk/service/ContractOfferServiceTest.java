package net.catenax.edc.apiwrapper.connector.sdk.service;

import net.catenax.edc.apiwrapper.config.ApiWrapperConfig;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.catalog.Catalog;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractOffer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContractOfferServiceTest {

    private final TypeManager typeManager = new TypeManager();
    private final OkHttpClient httpClient = mock(OkHttpClient.class);
    private final Call call = mock(Call.class);
    private final ApiWrapperConfig config = mock(ApiWrapperConfig.class);

    private ContractOfferService service;

    @BeforeEach
    void setUp() {
        when(httpClient.newCall(any())).thenReturn(call);
        when(config.getConsumerEdcDataManagementUrl()).thenReturn("http://an-url");
        when(config.getCatalogCachePeriod()).thenReturn(300L);
        when(config.isCatalogCacheEnabled()).thenReturn(true);
        service = new ContractOfferService(mock(Monitor.class), typeManager, httpClient, config);
    }

    @Test
    void shouldCacheCatalog() throws IOException {
        var contractOffer = createContractOffer("offerId", "assetId");
        var catalog = createCatalog(contractOffer);
        when(call.execute()).thenReturn(successfulResponse(catalog));
        var providerUrl = "http//provider/url";

        var firstOffer = service.findContractOffer4AssetId("assetId", providerUrl);
        var secondOffer = service.findContractOffer4AssetId("assetId", providerUrl);

        assertThat(firstOffer).containsSame(secondOffer.get());
        verify(call, times(1)).execute();
    }

    @Test
    void shouldNotCacheCatalog() throws IOException {
        var contractOffer = createContractOffer("offerId", "assetId");
        var catalog = createCatalog(contractOffer);
        var providerUrl = "http//provider/url";
        when(config.getCatalogCachePeriod()).thenReturn(0L);
        when(config.isCatalogCacheEnabled()).thenReturn(false);

        when(call.execute()).thenReturn(successfulResponse(catalog));
        var firstOffer = service.findContractOffer4AssetId("assetId", providerUrl);

        when(call.execute()).thenReturn(successfulResponse(catalog));
        var secondOffer = service.findContractOffer4AssetId("assetId", providerUrl);

        assertThat(firstOffer).isPresent();
        assertThat(secondOffer).isPresent();
        assertThat(firstOffer.get()).isNotEqualTo(secondOffer.get());
        verify(call, times(2)).execute();
    }

    private Catalog createCatalog(ContractOffer contractOffer) {
        return Catalog.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .contractOffers(List.of(contractOffer))
                .build();
    }

    @NotNull
    private static ContractOffer createContractOffer(String id, String assetId) {
        return ContractOffer.Builder.newInstance()
                .id(id)
                .asset(Asset.Builder.newInstance().id(assetId).build())
                .policy(Policy.Builder.newInstance().build())
                .build();
    }


    @NotNull
    private Response successfulResponse(Catalog catalog) {
        return new Response.Builder()
                .code(200)
                .message("any")
                .body(ResponseBody.create(typeManager.writeValueAsBytes(catalog), okhttp3.MediaType.get("application/json")))
                .protocol(Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://any").build())
                .build();
    }
}
