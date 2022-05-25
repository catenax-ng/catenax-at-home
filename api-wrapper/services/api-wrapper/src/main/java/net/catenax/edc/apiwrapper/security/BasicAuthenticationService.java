package net.catenax.edc.apiwrapper.security;

import net.catenax.edc.apiwrapper.config.BasicAuthVaultLabels;
import org.eclipse.dataspaceconnector.api.auth.AuthenticationService;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.eclipse.dataspaceconnector.spi.security.Vault;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BasicAuthenticationService implements AuthenticationService {

    private final Base64.Decoder b64Decoder;
    private final Monitor monitor;
    private final Vault vault;
    private final List<BasicAuthVaultLabels> listAuthVault;

    public BasicAuthenticationService(Monitor monitor, Vault vault, List<BasicAuthVaultLabels> listAuthVault) {
        this.monitor = monitor;
        this.vault = vault;
        this.b64Decoder = Base64.getDecoder();
        this.listAuthVault = listAuthVault;
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

        Predicate<BasicAuthVaultLabels> isCorrectUser = e -> e.getUsername().equals(username);
        Predicate<BasicAuthVaultLabels> isCorrectVaultKey = e -> vault.resolveSecret(e.getVaultKey()).equals(password);

        return this.listAuthVault.stream().filter(isCorrectUser).filter(isCorrectVaultKey).findAny().isPresent();
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
