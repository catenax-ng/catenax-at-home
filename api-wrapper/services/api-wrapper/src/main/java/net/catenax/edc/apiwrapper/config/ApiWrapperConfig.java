package net.catenax.edc.apiwrapper.config;

import java.util.Collections;
import java.util.List;

public class ApiWrapperConfig {

    private final String consumerEdcDataManagementUrl;
    private final String consumerEdcApiKeyName;
    private final String consumerEdcApiKeyValue;
    private final List<BasicAuthVaultLabels> basicAuthUsers;

    public ApiWrapperConfig(
            String consumerEdcDataManagementUrl,
            String consumerEdcApiKeyName,
            String consumerEdcApiKeyValue,
            List<BasicAuthVaultLabels> basicAuthUsers)
        {
        this.consumerEdcDataManagementUrl = consumerEdcDataManagementUrl;
        this.consumerEdcApiKeyName = consumerEdcApiKeyName;
        this.consumerEdcApiKeyValue = consumerEdcApiKeyValue;
        this.basicAuthUsers = basicAuthUsers;
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

    public List<BasicAuthVaultLabels> getBasicAuthUsers() {
        return basicAuthUsers;
    }

    public static final class Builder {
        private String consumerEdcDataManagementUrl = null;
        private String consumerEdcApiKeyName = "X-Api-Key";
        private String consumerEdcApiKeyValue = "";
        private List<BasicAuthVaultLabels> basicAuthUsers = Collections.emptyList();

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

        public Builder basicAuthUsers(List<BasicAuthVaultLabels> basicAuthUsers) {
            this.basicAuthUsers = basicAuthUsers;
            return this;
        }

        public ApiWrapperConfig build() {
            return new ApiWrapperConfig(
                    consumerEdcDataManagementUrl,
                    consumerEdcApiKeyName,
                    consumerEdcApiKeyValue,
                    basicAuthUsers
            );
        }
    }
}
