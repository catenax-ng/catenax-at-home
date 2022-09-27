package net.catenax.edc.apiwrapper.config;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ApiWrapperConfigTest {

    @Test
    void test() {

        Map<String, String> map = new HashMap<>();
        map.put("userId", "userPwd");

        ApiWrapperConfig config = ApiWrapperConfig.Builder.newInstance()
                .consumerEdcDataManagementUrl("urlConsumer")
                .consumerEdcApiKeyValue("apiValue")
                .basicAuthUsers(map)
                .agreementCacheEnabled(true)
                .callbackTimeout(10)
                .catalogCachePeriod(300L)
                .build();

        assertAll(
                () -> assertThat(config.getConsumerEdcDataManagementUrl()).isEqualTo("urlConsumer"),
                () -> assertThat(config.getConsumerEdcApiKeyValue()).isEqualTo("apiValue"),
                () -> assertThat(config.getBasicAuthUsers()).containsEntry("userId", "userPwd"),
                () -> assertThat(config.getConsumerEdcApiKeyName()).isEqualTo("X-Api-Key"),
                () -> assertThat(config.isAgreementCacheEnabled()).isTrue(),
                () -> assertThat(config.getCatalogCachePeriod()).isEqualTo(300L),
                () -> assertThat(config.isCatalogCacheEnabled()).isTrue(),
                () -> assertThat(config.getCallbackTimeout()).isEqualTo(10)
        );
    }

    @Test
    void disabledCatalogCache() {
        ApiWrapperConfig config = ApiWrapperConfig.Builder.newInstance()
                .catalogCachePeriod(0L)
                .build();

        assertAll(
                () -> assertThat(config.getCatalogCachePeriod()).isZero(),
                () -> assertThat(config.isCatalogCacheEnabled()).isFalse()
        );
    }
}
