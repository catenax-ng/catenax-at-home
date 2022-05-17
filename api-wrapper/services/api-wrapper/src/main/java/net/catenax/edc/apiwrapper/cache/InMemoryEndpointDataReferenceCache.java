package net.catenax.edc.apiwrapper.cache;

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

        if (token == null) {
            return true;
        }

        DecodedJWT jwt = JWT.decode(token);
        return !jwt.getExpiresAt().before(new Date(System.currentTimeMillis() + 30 * 1000));
    }
}
