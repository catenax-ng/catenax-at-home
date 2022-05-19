package net.catenax.edc.apiwrapper.security;

import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import org.powermock.reflect.Whitebox;

public class BasicAuthenticationServiceTest {

    private final Monitor monitor = mock(Monitor.class);
    private final Map<String, String> users = Map.of("hello", "myPassword");

    private final BasicAuthenticationService authenticationServiceTest = new BasicAuthenticationService(monitor, users);

    @Test
    void CredentialsHaveNoAuthorizationLabel(){
        Map<String, List<String>> mapCorrectPassword = Map.of("user", new ArrayList<>(Arrays.asList("user dXNlcjpwYXNzd29yZA==", "blablabla")));
        assertThat(authenticationServiceTest.isAuthenticated(mapCorrectPassword)).isEqualTo(false);

    }

    @Test
    void AuthorizationHeaderIsNotValidToken(){
        Map<String, List<String>> mapCorrectPassword = Map.of("Authorization", new ArrayList<>(Arrays.asList("userdXNlcjpwYXNzd29yZA==")));
        assertThat(authenticationServiceTest.isAuthenticated(mapCorrectPassword)).isEqualTo(false);
    }

    @Test
    void shouldReturnIsAuthenticated_shouldReturnNoAuthenticationHeaderSpecified() throws Exception {
        Result<BasicAuthenticationService.BasicAuthCredentials> result = Whitebox.invokeMethod(authenticationServiceTest,"decodeAuthHeader","userdXNlcjpwYXNzd29yZA");
        assertThat(result.getFailureMessages().get(0)).isEqualTo("Authorization header value is not a valid Bearer token");
    }

    @Test
    void shouldReturnAuthorizationHeaderCouldNotBeBase64Decoded() throws Exception {
        Result<BasicAuthenticationService.BasicAuthCredentials> result = Whitebox.invokeMethod(authenticationServiceTest,"decodeAuthHeader","user dXNlñjpwYXNzd29yZA==");
        assertThat(result.getFailureMessages().get(0)).isEqualTo("Authorization header could not be base64 decoded");
    }

    @Test
    void ShouldReturnDecodedAuthorizationHeaderHasFalseFormat() throws Exception {
        Result<BasicAuthenticationService.BasicAuthCredentials> result = Whitebox.invokeMethod(authenticationServiceTest,"decodeAuthHeader","user yesIsayNo");
        assertThat(result.getFailureMessages().get(0)).isEqualTo("Authorization header could not be base64 decoded");
    }

    @Test
    void sendIncorrectPassword() {
        Map<String, List<String>> mapCorrectPassword = Map.of("Authorization", new ArrayList<>(Arrays.asList("user dXNldkqxYXNzd29yZA==", "blablabla")));
        assertThat(authenticationServiceTest.isAuthenticated(mapCorrectPassword)).isEqualTo(false);
    }

    @Test
    void sendCorrectPassword() {
        Map<String, List<String>> mapCorrectPassword = Map.of("Authorization", new ArrayList<>(Arrays.asList("user dXNlcjpwYXNzd29yZA==", "blablabla")));
        assertThat(authenticationServiceTest.isAuthenticated(mapCorrectPassword)).isEqualTo(true);
    }

}
