package org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.edr.EndpointDataReference;

import java.util.Date;

public class ApiWrapperService {

    private final Monitor monitor;

    public ApiWrapperService(Monitor monitor) {
        this.monitor = monitor;
    }

    public boolean endpointDataRefTokenExpired(EndpointDataReference dataReference) {
        String token = dataReference.getAuthCode();
        DecodedJWT jwt = JWT.decode(token);
        if (jwt.getExpiresAt().before(new Date())) {
            monitor.debug("Token for EndpointDataReference is expired.");
            return false;
        } else {
            return true;
        }
    }
}
