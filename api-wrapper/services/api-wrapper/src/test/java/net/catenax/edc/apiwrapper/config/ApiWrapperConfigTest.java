package net.catenax.edc.apiwrapper.config;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ApiWrapperConfigTest {

    @Test
    void testBuild() {

        Map<String, String> map = new HashMap<>();
        map.put("userId", "userPwd");

        ApiWrapperConfig apiWrapperTest = ApiWrapperConfig.Builder.newInstance()
                .consumerEdcDataManagementUrl("urlConsumer")
                .consumerEdcApiKeyValue("apiValue")
                .basicAuthUsers(map).build();

        assertAll(() -> assertThat(apiWrapperTest.getConsumerEdcDataManagementUrl()).isEqualTo("urlConsumer"),
                () -> assertThat(apiWrapperTest.getConsumerEdcApiKeyValue()).isEqualTo("apiValue"),
                () -> assertThat(apiWrapperTest.getBasicAuthUsers().get("userId")).isEqualTo("userPwd"),
                () -> assertThat(apiWrapperTest.getConsumerEdcApiKeyName()).isEqualTo("X-Api-Key"));
    }
}
