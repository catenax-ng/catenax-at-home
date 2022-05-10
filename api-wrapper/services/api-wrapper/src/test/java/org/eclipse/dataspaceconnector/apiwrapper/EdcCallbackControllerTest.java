package org.eclipse.dataspaceconnector.apiwrapper;

import org.eclipse.dataspaceconnector.apiwrapper.cache.InMemoryEndpointDataReferenceCache;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.edr.EndpointDataReference;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class EdcCallbackControllerTest {

    private final Monitor monitor = mock(Monitor.class);
    private final InMemoryEndpointDataReferenceCache endpointDataReferenceCache = new InMemoryEndpointDataReferenceCache();

    private final EdcCallbackController edcCallbackController = new EdcCallbackController(monitor, endpointDataReferenceCache);

    @Test
    void receiveEdcCallbackByDataReference() {

        Map<String, String> map = new HashMap<>();
        map.put("cid", "reference");

        EndpointDataReference endpointDataReference = EndpointDataReference.Builder.newInstance()
                .id("idRef")
                .endpoint("anEndpoint")
                .authKey("myKey")
                .authCode("SecCode")
                .properties(map)
                .build();

        edcCallbackController.receiveEdcCallback(endpointDataReference);

        var stored = endpointDataReferenceCache.get("reference");

        assertThat(endpointDataReference.getId()).isEqualTo(stored.getId());
    }
}
