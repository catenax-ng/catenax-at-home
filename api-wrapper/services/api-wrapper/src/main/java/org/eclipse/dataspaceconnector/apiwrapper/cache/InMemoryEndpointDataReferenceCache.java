package org.eclipse.dataspaceconnector.apiwrapper.cache;

import org.eclipse.dataspaceconnector.spi.types.domain.edr.EndpointDataReference;

import java.util.HashMap;
import java.util.Map;

public class InMemoryEndpointDataReferenceCache {
    private final Map<String, EndpointDataReference> store = new HashMap<>();

    public void put(String agreementId, EndpointDataReference endpointDataReference) {
        store.put(agreementId, endpointDataReference);
    }

    public EndpointDataReference get(String agreementId) {
        return store.get(agreementId);
    }

    public void remove(String agreementId) {
        store.remove(agreementId);
    }
}
