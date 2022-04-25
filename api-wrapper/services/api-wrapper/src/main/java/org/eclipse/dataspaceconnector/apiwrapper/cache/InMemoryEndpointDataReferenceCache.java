package org.eclipse.dataspaceconnector.apiwrapper.cache;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.eclipse.dataspaceconnector.spi.types.domain.edr.EndpointDataReference;

import java.util.Date;
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

    public static boolean endpointDataRefTokenExpired(EndpointDataReference dataReference) {
        String token = dataReference.getAuthCode();
        DecodedJWT jwt = JWT.decode(token);
        if (jwt.getExpiresAt().before(new Date())) {
            return false;
        } else {
            return true;
        }
    }
}
