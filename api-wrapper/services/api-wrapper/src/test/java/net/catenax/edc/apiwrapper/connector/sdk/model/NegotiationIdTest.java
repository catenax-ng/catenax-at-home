package net.catenax.edc.apiwrapper.connector.sdk.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NegotiationIdTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void verifySerialization() throws JsonProcessingException {
        NegotiationId negotiationId = NegotiationId.Builder.newInstance().id("negotiationId").build();

        var str = objectMapper.writeValueAsString(negotiationId);

        assertThat(str).isNotNull();

        var deserialized = objectMapper.readValue(str, NegotiationId.class);
        assertThat(deserialized).usingRecursiveComparison().isEqualTo(negotiationId);
    }

}
