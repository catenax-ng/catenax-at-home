package org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.TransferType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferRequestDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void verifySerialization() throws JsonProcessingException {
        var dto = TransferRequestDto.Builder.newInstance()
                .connectorAddress("connector")
                .contractId("contract")
                .dataDestination(DataAddress.Builder.newInstance()
                        .type("a-type")
                        .keyName("my_way")
                        .properties(new HashMap<>())
                        .build())
                .managedResources(true)
                .properties(new HashMap<>())
                .transferType(TransferType.Builder.transferType().build())
                .protocol("ids-multipart")
                .connectorId("con-id")
                .assetId("asset-id")
                .build();

        var str = objectMapper.writeValueAsString(dto);

        assertThat(str).isNotNull();

        var deserialized = objectMapper.readValue(str, TransferRequestDto.class);
        assertThat(deserialized).usingRecursiveComparison().isEqualTo(dto);
    }
}
