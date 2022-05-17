package net.catenax.edc.apiwrapper.connector.sdk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;
import org.eclipse.dataspaceconnector.spi.types.domain.catalog.Catalog;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractOffer;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

public class ContractOfferService {
    private final Monitor monitor;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;

    private static final String CATALOG_PATH = "/catalog?providerUrl=";

    public ContractOfferService(Monitor monitor, TypeManager typeManager, OkHttpClient httpClient) {
        this.monitor = monitor;
        this.objectMapper = typeManager.getMapper();
        this.httpClient = httpClient;
    }

    public Optional<ContractOffer> findContractOffer4AssetId(
            String assetId,
            String consumerEdcDataManagementUrl,
            String providerConnectorControlPlaneIDSUrl,
            Map<String, String> header
    ) throws IOException {
        var catalog = getCatalogFromProvider(
                consumerEdcDataManagementUrl,
                providerConnectorControlPlaneIDSUrl,
                header
        );
        if (catalog.getContractOffers().isEmpty()) {
            throw new BadRequestException("Provider has not contract offers for us. Catalog is empty.");
        }

        return catalog.getContractOffers()
                .stream()
                .filter(it -> it.getAsset().getId().equals(assetId))
                .findFirst();
    }

    private Catalog getCatalogFromProvider(
            String consumerEdcDataManagementUrl,
            String providerConnectorControlPlaneIDSUrl,
            Map<String, String> headers
    ) throws IOException {
        var url = consumerEdcDataManagementUrl + CATALOG_PATH + providerConnectorControlPlaneIDSUrl;
        var request = new Request.Builder()
                .url(url);
        headers.forEach(request::addHeader);

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
