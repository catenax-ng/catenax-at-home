package net.catenax.edc.apiwrapper.config;

import org.eclipse.dataspaceconnector.spi.EdcSetting;

public final class ApiWrapperConfigKeys {

    @EdcSetting(required = true)
    public static final String CONSUMER_EDC_DATAMANAGEMENT_URL = "wrapper.consumer.edc.datamanagement.url";

    @EdcSetting
    public static final String CONSUMER_EDC_APIKEY_NAME = "wrapper.consumer.edc.apikey.name";

    @EdcSetting
    public static final String CONSUMER_EDC_APIKEY_VALUE = "wrapper.consumer.edc.apikey.value";

    @EdcSetting
    public static final String BASIC_AUTH = "wrapper.auth.basic";

    @EdcSetting
    public static final String CACHE_ENABLED = "wrapper.cache.enabled";

    @EdcSetting
    public static final String CALLBACK_TIMEOUT = "wrapper.callback.timeout";

    @EdcSetting(value = "Catalog cache period in seconds. Default is 300 (5 minutes)")
    public static final String CATALOG_CACHE_PERIOD = "wrapper.cache.catalog.period";

    private ApiWrapperConfigKeys() {}
}
