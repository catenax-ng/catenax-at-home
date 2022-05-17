package net.catenax.edc.apiwrapper;

import net.catenax.edc.apiwrapper.cache.InMemoryContractAgreementCache;
import net.catenax.edc.apiwrapper.cache.InMemoryEndpointDataReferenceCache;
import net.catenax.edc.apiwrapper.connector.sdk.service.TransferProcessService;
import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.api.auth.AuthenticationRequestFilter;
import net.catenax.edc.apiwrapper.config.ApiWrapperConfig;
import net.catenax.edc.apiwrapper.config.ApiWrapperConfigKeys;
import net.catenax.edc.apiwrapper.connector.sdk.service.ContractNegotiationService;
import net.catenax.edc.apiwrapper.connector.sdk.service.ContractOfferService;
import net.catenax.edc.apiwrapper.connector.sdk.service.HttpProxyService;
import net.catenax.edc.apiwrapper.security.BasicAuthenticationService;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.configuration.Config;

public class ApiWrapperExtension implements ServiceExtension {

    private static final String DEFAULT_CONTEXT_ALIAS = "default";
    private static final String CALLBACK_CONTEXT_ALIAS = "callback";

    @Inject
    private WebService webService;

    @Inject
    private OkHttpClient httpClient;

    @Override
    public String name() {
        return "AAS-API-Wrapper";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var config = createApiWrapperConfig(context.getConfig());
        var monitor = context.getMonitor();
        var typeManager = context.getTypeManager();

        // Register basic authentication filter
        if (!config.getBasicAuthUsers().isEmpty()) {
            var authService = new BasicAuthenticationService(context.getMonitor(), config.getBasicAuthUsers());
            webService.registerResource(new AuthenticationRequestFilter(authService));
        }

        // In-memory stores
        var endpointDataReferenceCache = new InMemoryEndpointDataReferenceCache();
        var contractAgreementCache = new InMemoryContractAgreementCache();

        // Setup controller
        var contractOfferService = new ContractOfferService(monitor, typeManager, httpClient);
        var contractOfferRequestService = new ContractNegotiationService(monitor, typeManager, httpClient);
        var transferProcessService = new TransferProcessService(monitor, typeManager, httpClient);
        var httpProxyService = new HttpProxyService(monitor, httpClient);

        webService.registerResource(DEFAULT_CONTEXT_ALIAS, new ApiWrapperController(
                monitor,
                contractOfferService,
                contractOfferRequestService,
                transferProcessService,
                httpProxyService,
                endpointDataReferenceCache,
                contractAgreementCache,
                config
        ));
        webService.registerResource(CALLBACK_CONTEXT_ALIAS, new EdcCallbackController(monitor, endpointDataReferenceCache));
    }

    private ApiWrapperConfig createApiWrapperConfig(Config config) {
        ApiWrapperConfig.Builder builder = ApiWrapperConfig.Builder.newInstance();

        builder.consumerEdcDataManagementUrl(config.getString(ApiWrapperConfigKeys.CONSUMER_EDC_DATAMANAGEMENT_URL));

        var consumerEdcApiKeyName = config.getString(ApiWrapperConfigKeys.CONSUMER_EDC_APIKEY_NAME, null);
        if (consumerEdcApiKeyName != null) {
            builder.consumerEdcApiKeyName(consumerEdcApiKeyName);
        }

        var consumerEdcApiKeyValue = config.getString(ApiWrapperConfigKeys.CONSUMER_EDC_APIKEY_VALUE, null);
        if (consumerEdcApiKeyValue != null) {
            builder.consumerEdcApiKeyValue(consumerEdcApiKeyValue);
        }

        var basicAuthUsers = config.getConfig(ApiWrapperConfigKeys.BASIC_AUTH).getRelativeEntries();
        if (!basicAuthUsers.isEmpty()) {
            builder.basicAuthUsers(basicAuthUsers);
        }

        return builder.build();
    }
}
