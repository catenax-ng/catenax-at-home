package net.catenax.edc.apiwrapper.exceptions;

import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;

public class TimeoutException extends ServerErrorException {
    private static final long serialVersionUID = -2740045367379265061L;

    public TimeoutException(String message) {
        super(message, Response.Status.REQUEST_TIMEOUT);
    }
}
