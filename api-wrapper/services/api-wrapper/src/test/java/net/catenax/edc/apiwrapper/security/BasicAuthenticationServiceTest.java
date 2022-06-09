package net.catenax.edc.apiwrapper.security;

import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class BasicAuthenticationServiceTest {

    private final Monitor monitor = mock(Monitor.class);
    private final Map<String, String> users = Map.of("hello", "myPassword");

    private final BasicAuthenticationService authenticationServiceTest = new BasicAuthenticationService(monitor, users);

    @Test
    void isAuthenticated_shouldReturnNoAuthenticationHeaderSpecified() {

        Map<String, List<String>> map = Map.of("Authorization", new ArrayList<>());
        assertThat(authenticationServiceTest.isAuthenticated(map)).isFalse();
    }

    @Test
    void isAuthenticated_shouldReturnHeaderFormatNotSupported() {

        Map<String, List<String>> map1authorizationComponent = Map.of("Authorization", new ArrayList<>(List.of("Only1Compoment")));
        Map<String, List<String>> map3authorizationComponents = Map.of("Authorization", new ArrayList<>(Arrays.asList("First", "2nd", "3")));

        assertThat(authenticationServiceTest.isAuthenticated(map1authorizationComponent)).isFalse();
        assertThat(authenticationServiceTest.isAuthenticated(map3authorizationComponents)).isFalse();
    }

    @Test
    void isAuthenticated_shouldReturnIncorrectlyDecodedAuthorizationHeaderNotSupported() {
        Map<String, List<String>> mapUnsupported = Map.of(
                "Authorization", new ArrayList<>(Arrays.asList("user password", "blablabla"))
        );

        assertThat(authenticationServiceTest.isAuthenticated(mapUnsupported)).isFalse();
    }

    @Test
    void isAuthenticated_shouldReturnPasswordWrong() {
        Map<String, List<String>> mapWrongPassword = Map.of("Authorization", new ArrayList<>(Arrays.asList("user aGVsbG86bXlQYXNzd29yQW==", "blablabla")));

        assertThat(authenticationServiceTest.isAuthenticated(mapWrongPassword)).isFalse();
    }

    @Test
    void isAuthenticated_shouldReturnCorrectAuthentication() {
        Map<String, List<String>> mapWrongPassword = Map.of("Authorization", new ArrayList<>(Arrays.asList("user aGVsbG86bXlQYXNzd29yZA==", "blablabla")));

        assertThat(authenticationServiceTest.isAuthenticated(mapWrongPassword)).isTrue();
    }
}
