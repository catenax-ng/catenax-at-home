package org.eclipse.dataspaceconnector.apiwrapper;

import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.api.auth.AuthenticationRequestFilter;
import org.eclipse.dataspaceconnector.apiwrapper.config.ApiWrapperConfig;
import org.eclipse.dataspaceconnector.apiwrapper.config.ApiWrapperConfigKeys;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service.ContractNegotiationService;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service.ContractOfferService;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service.HttpProxyService;
import org.eclipse.dataspaceconnector.apiwrapper.connector.sdk.service.TransferProcessService;
import org.eclipse.dataspaceconnector.apiwrapper.security.BasicAuthenticationService;
import org.eclipse.dataspaceconnector.apiwrapper.store.InMemoryContractAgreementStore;
import org.eclipse.dataspaceconnector.apiwrapper.store.InMemoryEndpointDataReferenceStore;
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
        var endpointDataReferenceStore = new InMemoryEndpointDataReferenceStore();
        var contractAgreementStore = new InMemoryContractAgreementStore();

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
                endpointDataReferenceStore,
                contractAgreementStore,
                config
        ));
        webService.registerResource(CALLBACK_CONTEXT_ALIAS, new EdcCallbackController(monitor, endpointDataReferenceStore));
    }

    private ApiWrapperConfig createApiWrapperConfig(Config config) {
        ApiWrapperConfig.Builder builder = ApiWrapperConfig.Builder.newInstance();

        builder.consumerEdcUrl(config.getString(ApiWrapperConfigKeys.CONSUMER_EDC_URL));

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
