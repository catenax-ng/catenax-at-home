package net.catenax.edc.apiwrapper.connector.sdk.model;

import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NegotiationInitiateRequestDtoTest {

    @Test
    void verifyBuilder() {

        Policy policy = Policy.Builder.newInstance().build();

        ContractOfferDescription contractOfferDescription = new ContractOfferDescription(
                "offerId", "assetId", policy);

        NegotiationInitiateRequestDto dto = NegotiationInitiateRequestDto.Builder.newInstance()
                .connectorId("connectorId")
                .protocol("protocol")
                .connectorAddress("address")
                .offerId(contractOfferDescription).build();

        assertAll(() -> assertThat(dto.getConnectorId()).isEqualTo("connectorId"),
                () -> assertThat(dto.getProtocol()).isEqualTo("protocol"),
                () -> assertThat(dto.getConnectorAddress()).isEqualTo("address"),
                () -> assertThat(dto.getOffer().getOfferId()).isEqualTo("offerId"));

    }
}
