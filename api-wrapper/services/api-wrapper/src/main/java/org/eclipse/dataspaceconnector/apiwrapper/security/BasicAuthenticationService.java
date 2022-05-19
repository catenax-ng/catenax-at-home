package org.eclipse.dataspaceconnector.apiwrapper.security;

import org.eclipse.dataspaceconnector.api.auth.AuthenticationService;
import org.eclipse.dataspaceconnector.core.security.fs.FsVault;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.Result;

import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BasicAuthenticationService implements AuthenticationService {

    private final Base64.Decoder b64Decoder;
    private final Monitor monitor;
    private final Map<String, String> users;
    private final FsVault vault = new FsVault(Paths.get("vault.properties"),false);

    public BasicAuthenticationService(Monitor monitor, Map<String, String> users) {
        this.monitor = monitor;
        this.users = users;
        this.b64Decoder = Base64.getDecoder();
    }

    @Override
    public boolean isAuthenticated(Map<String, List<String>> headers) {

        Objects.requireNonNull(headers, "headers");

        return headers.keySet().stream()
                .filter(k -> k.equalsIgnoreCase("Authorization"))
                .map(headers::get)
                .filter(list -> !list.isEmpty())
                .anyMatch(list -> list.stream()
                        .map(this::decodeAuthHeader)
                        .anyMatch(this::checkBasicAuthValid));
    }

    private boolean checkBasicAuthValid(Result<BasicAuthCredentials> basicAuthCredentialsResult) {
        if (basicAuthCredentialsResult.failed()) {
            basicAuthCredentialsResult.getFailureMessages().forEach(monitor::debug);
            return false;
        }

        var credentials = basicAuthCredentialsResult.getContent();
        var username = credentials.username;
        var password = credentials.password;

        var password4Username = vault.resolveSecret("user");

        //var password4Username = users.get(username);

        if (password4Username == null || !password4Username.equals(password)) {
            monitor.debug("Basic auth user could not be found or password wrong");
            return false;
        }
        return true;
    }

    private Result<BasicAuthCredentials> decodeAuthHeader(String authHeader) {
        String[] authCredentials;
        var separatedAuthHeader = authHeader.split(" ");

        if (separatedAuthHeader.length != 2) {
            return Result.failure("Authorization header value is not a valid Bearer token");
        }

        try {
            authCredentials = new String(b64Decoder.decode(separatedAuthHeader[1])).split(":");
        } catch (IllegalArgumentException ex) {
            return Result.failure("Authorization header could not be base64 decoded");
        }

        if (authCredentials.length != 2) {
            return Result.failure("Authorization header could be base64 decoded but is not in format of 'username:password'");
        }

        return Result.success(new BasicAuthCredentials(authCredentials[0], authCredentials[1]));
    }

    static class BasicAuthCredentials {
        String username;
        String password;

        public BasicAuthCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
