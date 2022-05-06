package org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.InternalServerErrorException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.Utility;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.model.TransferId;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.model.TransferRequestDto;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.TransferType;

import java.io.IOException;
import java.util.Map;

import static java.lang.String.format;

public class TransferProcessService {
   private static final String TRANSFER_PATH = "/transferprocess";
    private final Monitor monitor;
    private final TypeManager typeManager;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public TransferProcessService(Monitor monitor, TypeManager typeManager, OkHttpClient httpClient) {
        this.monitor = monitor;
        this.typeManager = typeManager;
        this.objectMapper = typeManager.getMapper();
        this.httpClient = httpClient;
    }

    public TransferId initiateHttpProxyTransferProcess(String agreementId, String assetId, String consumerEdcDataManagementUrl, String providerConnectorControlPlaneIDSUrl, Map<String, String> headers) throws IOException {
        var url = consumerEdcDataManagementUrl + TRANSFER_PATH;

        DataAddress dataDestination = DataAddress.Builder.newInstance()
                .type("HttpProxy")
                .build();

        TransferType transferType = TransferType.Builder.
                transferType()
                .contentType("application/octet-stream")
                .isFinite(true)
                .build();

        TransferRequestDto transferRequest = TransferRequestDto.Builder.newInstance()
                .assetId(assetId)
                .contractId(agreementId)
                .connectorId("provider")
                .connectorAddress(providerConnectorControlPlaneIDSUrl)
                .protocol("ids-multipart")
                .dataDestination(dataDestination)
                .managedResources(false)
                .transferType(transferType)
                .build();

        var requestBody = RequestBody.create(
                typeManager.writeValueAsString(transferRequest),
                Utility.JSON
        );

        var request = new Request.Builder()
                .url(url)
                .post(requestBody);
        headers.forEach(request::addHeader);

        try (var response = httpClient.newCall(request.build()).execute()) {
            var body = response.body();

            if (!response.isSuccessful() || body == null) {
                throw new InternalServerErrorException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
            }

            // For debugging purposes:
            // var transferProcessId = TransferId.Builder.newInstance().id(body.string()).build();
            var transferProcessId = objectMapper.readValue(body.string(), TransferId.class);

            monitor.info(format("Transfer process (%s) initiated", transferProcessId));

            return transferProcessId;
        } catch (Exception e) {
            monitor.severe(format("Error in calling the control plane at %s", url), e);
            throw e;
        }
    }
}
