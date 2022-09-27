package net.catenax.edc.apiwrapper.connector.sdk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.InternalServerErrorException;
import net.catenax.edc.apiwrapper.config.ApiWrapperConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;
import org.eclipse.dataspaceconnector.spi.types.domain.catalog.Catalog;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractOffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.groupingBy;

public class ContractOfferService {
    private final Monitor monitor;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;
    private final ApiWrapperConfig config;
    // ProviderIDSUrl -> AssetId -> List<ContractOffer>
    private final Map<String, Map<String, List<ContractOffer>>> byAssetIdCache = new ConcurrentHashMap<>();

    private static final String CATALOG_PATH = "/catalog?limit=1000000000&providerUrl=";

    public ContractOfferService(Monitor monitor, TypeManager typeManager, OkHttpClient httpClient, ApiWrapperConfig config) {
        this.monitor = monitor;
        this.objectMapper = typeManager.getMapper();
        this.httpClient = httpClient;
        this.config = config;
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(refreshCache(), 0L, config.getCatalogCachePeriod(), SECONDS);
    }

    public Optional<ContractOffer> findContractOffer4AssetId(
            String assetId,
            String providerConnectorControlPlaneIDSUrl
    ) {
        if (!byAssetIdCache.containsKey(providerConnectorControlPlaneIDSUrl)) {
           fetchCatalog(providerConnectorControlPlaneIDSUrl);
        }

        return Optional.of(providerConnectorControlPlaneIDSUrl)
                .map(byAssetIdCache::get)
                .map(it -> it.get(assetId))
                .flatMap(it -> it.stream().findFirst());
    }

    @NotNull
    private Runnable refreshCache() {
        return () -> byAssetIdCache.keySet().forEach(this::fetchCatalog);
    }

    private void fetchCatalog(String providerUrl) {
        try {
            var catalog = getCatalogFromProvider(providerUrl);
            var byId = catalog.getContractOffers().stream().collect(groupingBy(it -> it.getAsset().getId()));
            byAssetIdCache.put(providerUrl, byId);
        } catch (IOException e) {
            monitor.severe("Cannot fetch catalog from " + providerUrl, e);
        }
    }

    private Catalog getCatalogFromProvider(String providerConnectorControlPlaneIDSUrl) throws IOException {
        var url = config.getConsumerEdcDataManagementUrl() + CATALOG_PATH + providerConnectorControlPlaneIDSUrl;
        var request = new Request.Builder()
                .url(url);
        config.getHeaders().forEach(request::addHeader);

        try (var response = httpClient.newCall(request.build()).execute()) {
            var body = response.body();

            if (!response.isSuccessful() || body == null) {
                throw new InternalServerErrorException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
            }

            return objectMapper.readValue(body.string(), Catalog.class);
        } catch (Exception e) {
            monitor.severe(format("Error in calling the control plane at %s", url), e);
            throw e;
        }
    }

}
