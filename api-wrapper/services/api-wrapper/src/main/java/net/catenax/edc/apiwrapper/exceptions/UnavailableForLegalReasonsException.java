package net.catenax.edc.apiwrapper.exceptions;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

public class UnavailableForLegalReasonsException extends ClientErrorException {
    private static final long serialVersionUID = -2740045367379165061L;

    public UnavailableForLegalReasonsException(String message) {
        super(message, Response.Status.UNAVAILABLE_FOR_LEGAL_REASONS);
    }
}
