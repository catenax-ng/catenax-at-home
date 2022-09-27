package net.catenax.edc.apiwrapper.config;

import java.util.Collections;
import java.util.Map;

public class ApiWrapperConfig {

    private final String consumerEdcDataManagementUrl;
    private final String consumerEdcApiKeyName;
    private final String consumerEdcApiKeyValue;
    private final Map<String, String> basicAuthUsers;
    private final Boolean agreementCacheEnabled;
    private final int callbackTimeout;
    private final long catalogCachePeriod;

    public ApiWrapperConfig(
            String consumerEdcDataManagementUrl,
            String consumerEdcApiKeyName,
            String consumerEdcApiKeyValue,
            Map<String, String> basicAuthUsers,
            Boolean agreementCacheEnabled,
            int callbackTimeout,
            long catalogCachePeriod) {
        this.consumerEdcDataManagementUrl = consumerEdcDataManagementUrl;
        this.consumerEdcApiKeyName = consumerEdcApiKeyName;
        this.consumerEdcApiKeyValue = consumerEdcApiKeyValue;
        this.basicAuthUsers = basicAuthUsers;
        this.agreementCacheEnabled = agreementCacheEnabled;
        this.callbackTimeout = callbackTimeout;
        this.catalogCachePeriod = catalogCachePeriod;
    }

    public String getConsumerEdcDataManagementUrl() {
        return consumerEdcDataManagementUrl;
    }

    public String getConsumerEdcApiKeyName() {
        return consumerEdcApiKeyName;
    }

    public String getConsumerEdcApiKeyValue() {
        return consumerEdcApiKeyValue;
    }

    public Map<String, String> getBasicAuthUsers() {
        return basicAuthUsers;
    }

    public Boolean isAgreementCacheEnabled() {
        return agreementCacheEnabled;
    }

    public int getCallbackTimeout() {
        return callbackTimeout;
    }

    public Map<String, String> getHeaders() {
        return getConsumerEdcApiKeyValue() != null
                ? Collections.singletonMap(getConsumerEdcApiKeyName(), getConsumerEdcApiKeyValue())
                : Collections.emptyMap();
    }

    public long getCatalogCachePeriod() {
        return catalogCachePeriod;
    }

    public boolean isCatalogCacheEnabled() {
        return getCatalogCachePeriod() != 0L;
    }

    public static final class Builder {
        private String consumerEdcDataManagementUrl = null;
        private String consumerEdcApiKeyName = "X-Api-Key";
        private String consumerEdcApiKeyValue = "";
        private Map<String, String> basicAuthUsers = Collections.emptyMap();
        private Boolean agreementCacheEnabled = false;
        private int callbackTimeout = 20;
        private long catalogCachePeriod;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder consumerEdcDataManagementUrl(String consumerEdcDataManagementUrl) {
            this.consumerEdcDataManagementUrl = consumerEdcDataManagementUrl;
            return this;
        }

        public Builder consumerEdcApiKeyName(String consumerEdcApiKeyName) {
            this.consumerEdcApiKeyName = consumerEdcApiKeyName;
            return this;
        }

        public Builder consumerEdcApiKeyValue(String consumerEdcApiKeyValue) {
            this.consumerEdcApiKeyValue = consumerEdcApiKeyValue;
            return this;
        }

        public Builder basicAuthUsers(Map<String, String> basicAuthUsers) {
            this.basicAuthUsers = basicAuthUsers;
            return this;
        }

        public Builder agreementCacheEnabled(Boolean cacheEnabled) {
            this.agreementCacheEnabled = cacheEnabled;
            return this;
        }

        public Builder callbackTimeout(int callbackTimeout) {
            this.callbackTimeout = callbackTimeout;
            return this;
        }

        public Builder catalogCachePeriod(long catalogCachePeriod) {
            this.catalogCachePeriod = catalogCachePeriod;
            return this;
        }

        public ApiWrapperConfig build() {
            return new ApiWrapperConfig(
                    consumerEdcDataManagementUrl,
                    consumerEdcApiKeyName,
                    consumerEdcApiKeyValue,
                    basicAuthUsers,
                    agreementCacheEnabled,
                    callbackTimeout,
                    catalogCachePeriod
            );
        }
    }
}
