package org.eclipse.dataspaceconnector.apiwrapper.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiWrapperConfigTest {

    @Test
    void testBuild(){

        Map<String,String> map = new HashMap<>();
        map.put("userId","userPwd");

        ApiWrapperConfig apiWrapperTest = ApiWrapperConfig.Builder.newInstance()
                .consumerEdcDataManagementUrl("urlConsumer")
                .consumerEdcControlUrl("urlControl")
                .consumerEdcApiKeyValue("apiValue")
                .basicAuthUsers(map).build();

        assertThat(apiWrapperTest.getConsumerEdcDataManagementUrl()).isEqualTo("urlConsumer");
        assertThat(apiWrapperTest.getConsumerEdcControlUrl()).isEqualTo("urlControl");
        assertThat(apiWrapperTest.getConsumerEdcApiKeyValue()).isEqualTo("apiValue");
        assertThat(apiWrapperTest.getBasicAuthUsers().get("userId")).isEqualTo("userPwd");
        assertThat(apiWrapperTest.getConsumerEdcApiKeyName()).isEqualTo("X-Api-Key");

    }
}
