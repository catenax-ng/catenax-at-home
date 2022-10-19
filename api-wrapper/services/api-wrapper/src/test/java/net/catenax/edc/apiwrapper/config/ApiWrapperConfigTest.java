package net.catenax.edc.apiwrapper.config;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

class ApiWrapperConfigTest {

    @Test
    void test() {

        Map<String, String> map = new HashMap<>();
        map.put("userId", "userPwd");

        ApiWrapperConfig config = ApiWrapperConfig.Builder.newInstance()
                .agreementCacheEnabled(true)
                .basicAuthUsers(map)
                .callbackTimeout(10)
                .catalogCachePeriod(300L)
                .catalogPageSize(50)
                .consumerEdcApiKeyValue("apiValue")
                .consumerEdcDataManagementUrl("urlConsumer")
                .build();

        assertAll(
                () -> assertThat(config.isAgreementCacheEnabled()).isTrue(),
                () -> assertThat(config.getBasicAuthUsers()).containsEntry("userId", "userPwd"),
                () -> assertThat(config.getCallbackTimeout()).isEqualTo(10),
                () -> assertThat(config.isCatalogCacheEnabled()).isTrue(),
                () -> assertThat(config.getCatalogCachePeriod()).isEqualTo(300L),
                () -> assertThat(config.getCatalogPageSize()).isEqualTo(50),
                () -> assertThat(config.getConsumerEdcApiKeyName()).isEqualTo("X-Api-Key"),
                () -> assertThat(config.getConsumerEdcApiKeyValue()).isEqualTo("apiValue"),
                () -> assertThat(config.getConsumerEdcDataManagementUrl()).isEqualTo("urlConsumer")
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

    @Test
    void exceptionOnCatalogPageSizeBeenZero() {
        var config = ApiWrapperConfig.Builder.newInstance();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            config.catalogPageSize(0)
        );
    }
}
