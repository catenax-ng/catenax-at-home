package net.catenax.edc.apiwrapper.config;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ApiWrapperConfigTest {

    @Test
    void testBuild() {

        List<BasicAuthVaultLabels> credentialsList = List.of(new BasicAuthVaultLabels("UserId","UserPwd"));

        ApiWrapperConfig apiWrapperTest = ApiWrapperConfig.Builder.newInstance()
                .consumerEdcDataManagementUrl("urlConsumer")
                .consumerEdcApiKeyValue("apiValue")
                .basicAuthUsers(credentialsList).build();

        assertAll(() -> assertThat(apiWrapperTest.getConsumerEdcDataManagementUrl()).isEqualTo("urlConsumer"),
                () -> assertThat(apiWrapperTest.getConsumerEdcApiKeyValue()).isEqualTo("apiValue"),
                () -> assertThat(apiWrapperTest.getBasicAuthUsers().get(0).getUsername()).isEqualTo("UserId"),
                () -> assertThat(apiWrapperTest.getConsumerEdcApiKeyName()).isEqualTo("X-Api-Key"));
    }
}
