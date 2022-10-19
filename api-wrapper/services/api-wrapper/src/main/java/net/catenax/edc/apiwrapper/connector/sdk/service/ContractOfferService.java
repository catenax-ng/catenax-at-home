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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.groupingBy;

public class ContractOfferService {

    private static final String CATALOG_PATH = "/catalog?limit=%d&offset=%d&providerUrl=%s";
    private final Monitor monitor;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;
    private final ApiWrapperConfig config;

    // ProviderIDSUrl -> AssetId -> List<ContractOffer>
    private final Map<String, Map<String, List<ContractOffer>>> byAssetIdCache = new ConcurrentHashMap<>();

    public ContractOfferService(Monitor monitor, TypeManager typeManager, OkHttpClient httpClient, ApiWrapperConfig config) {
        this.monitor = monitor;
        this.objectMapper = typeManager.getMapper();
        this.httpClient = httpClient;
        this.config = config;

        if (config.isCatalogCacheEnabled()) {
            Executors.newScheduledThreadPool(1)
                    .scheduleAtFixedRate(refreshCache(), 0L, config.getCatalogCachePeriod(), SECONDS);
        }
    }

    public Optional<ContractOffer> findContractOffer4AssetId(
            String assetId,
            String providerConnectorControlPlaneIDSUrl
    ) {
        if (!config.isCatalogCacheEnabled() || !byAssetIdCache.containsKey(providerConnectorControlPlaneIDSUrl)) {
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
        monitor.info("Fetching catalog from " + providerUrl);

        try {
            var catalog = getCatalogFromProvider(providerUrl);
            var byId = catalog.getContractOffers().stream().collect(groupingBy(it -> it.getAsset().getId()));
            byAssetIdCache.put(providerUrl, byId);
        } catch (IOException e) {
            byAssetIdCache.remove(providerUrl);
            monitor.severe("Cannot fetch catalog from " + providerUrl, e);
        }
    }

    private Catalog getCatalogFromProvider(String providerConnectorControlPlaneIDSUrl) throws IOException {
        var limit = config.getCatalogPageSize();
        var offset = 0;
        var reachedLastPage = false;

        String catalogId;
        var contractOffers = new ArrayList<ContractOffer>();

        do {
            var url = config.getConsumerEdcDataManagementUrl() + String.format(
                    CATALOG_PATH,
                    limit,
                    offset,
                    providerConnectorControlPlaneIDSUrl);
            var request = new Request.Builder().url(url);

            config.getHeaders().forEach(request::addHeader);

            try (var response = httpClient.newCall(request.build()).execute()) {
                var body = response.body();

                if (!response.isSuccessful() || body == null) {
                    throw new InternalServerErrorException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
                }

                var catalogPage = objectMapper.readValue(body.string(), Catalog.class);
                if (catalogPage.getContractOffers().size() < limit) {
                    reachedLastPage = true;
                }

                catalogId = catalogPage.getId();
                contractOffers.addAll(catalogPage.getContractOffers());

            } catch (Exception e) {
                monitor.severe(format("Error in calling the control plane at %s", url), e);
                throw e;
            }

            offset += limit;
        } while (!reachedLastPage);


        monitor.info(String.format("Got %d contract-offers from %s", contractOffers.size(), providerConnectorControlPlaneIDSUrl));
        return Catalog.Builder.newInstance()
                .id(catalogId)
                .contractOffers(contractOffers)
                .build();
    }

}
