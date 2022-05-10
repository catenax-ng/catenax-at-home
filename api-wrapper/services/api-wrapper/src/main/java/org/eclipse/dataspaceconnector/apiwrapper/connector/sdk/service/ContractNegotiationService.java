package org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.InternalServerErrorException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.Utility;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.model.ContractNegotiationDto;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.model.NegotiationId;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.model.NegotiationInitiateRequestDto;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;

import java.io.IOException;
import java.util.Map;

import static java.lang.String.format;

public class ContractNegotiationService {
    private final Monitor monitor;
    private final TypeManager typeManager;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final String NEGOTIATION_PATH = "/contractnegotiations";

    public ContractNegotiationService(Monitor monitor, TypeManager typeManager, OkHttpClient httpClient) {
        this.monitor = monitor;
        this.typeManager = typeManager;
        this.objectMapper = typeManager.getMapper();
        this.httpClient = httpClient;
    }

    public NegotiationId initiateNegotiation(
            NegotiationInitiateRequestDto contractOfferRequest,
            String consumerEdcDataManagementUrl,
            Map<String, String> headers
    ) throws IOException {
        var url = consumerEdcDataManagementUrl + NEGOTIATION_PATH;
        var requestBody = RequestBody.create(
                typeManager.writeValueAsString(contractOfferRequest),
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
            // var negotiationId = NegotiationId.Builder.newInstance().id(body.string()).build();
            var negotiationId = objectMapper.readValue(body.string(), NegotiationId.class);

            monitor.info("Started negotiation with ID: " + negotiationId.getId());

            return negotiationId;
        } catch (Exception e) {
            monitor.severe(format("Error in calling the control plane at %s", url), e);
            throw e;
        }
    }

    public ContractNegotiationDto getNegotiation(NegotiationId negotiationId, String connectorEdcDataManagementUrl, Map<String, String> headers) throws IOException {
        var url = format("%s/%s", connectorEdcDataManagementUrl + NEGOTIATION_PATH, negotiationId.getId());
        var request = new Request.Builder()
                .url(url);
        headers.forEach(request::addHeader);

        try (var response = httpClient.newCall(request.build()).execute()) {
            var body = response.body();

            if (!response.isSuccessful() || body == null) {
                throw new InternalServerErrorException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
            }

            var negotiation = objectMapper.readValue(body.string(), ContractNegotiationDto.class);
            monitor.info(format("Negotiation %s is in state '%s' (agreementId: %s)", negotiationId.getId(), negotiation.getState(), negotiation.getContractAgreementId()));

            return negotiation;
        } catch (Exception e) {
            monitor.severe(format("Error in calling the Control plane at %s", url), e);
            throw e;
        }
    }
}
