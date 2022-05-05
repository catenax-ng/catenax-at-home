package org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.negotiation.ContractNegotiation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContractNegotiationDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void verifySerialization() throws JsonProcessingException {
        var dto = ContractNegotiationDto.Builder.newInstance()
                .contractAgreementId("test-contract-agreement-id")
                .counterPartyAddress("test-counter-party-address")
                .errorDetail("test-error-detail")
                .id("test-id")
                .protocol("test-protocol")
                .state("test-state")
                .type(ContractNegotiation.Type.PROVIDER)
                .build();

        var str = objectMapper.writeValueAsString(dto);

        assertThat(str).isNotNull();

        var deserialized = objectMapper.readValue(str, ContractNegotiationDto.class);
        assertThat(deserialized).usingRecursiveComparison().isEqualTo(dto);
    }
}
