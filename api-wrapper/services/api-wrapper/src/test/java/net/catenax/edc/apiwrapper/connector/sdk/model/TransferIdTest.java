package net.catenax.edc.apiwrapper.connector.sdk.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransferIdTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void verifySerialization() throws JsonProcessingException {

        TransferId transferId = TransferId.Builder.newInstance().id("transferId").build();

        var str = objectMapper.writeValueAsString(transferId);

        assertThat(str).isNotNull();

        var deserialized = objectMapper.readValue(str, TransferId.class);
        assertThat(deserialized).usingRecursiveComparison().isEqualTo(transferId);
    }

}
