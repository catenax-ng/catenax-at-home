package net.catenax.edc.apiwrapper.connector.sdk.service;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.assertThat;

class HttpProxyServiceTest {

    private final Monitor monitorMock = mock(Monitor.class);
    private final OkHttpClient httpClientMock = mock(OkHttpClient.class);

    @Test
    void getUrlTest() {
        HttpProxyService httpProxyService = new HttpProxyService(monitorMock, httpClientMock);
        MultivaluedMap<String, String> parameters = new MultivaluedHashMap<>();

        String connectorUrl = "https://myconnector:8182/public";
        String subpath = "subpath";

        HttpUrl url = httpProxyService.getUrl(connectorUrl, subpath, parameters);
        assertThat(url.url().toString()).isEqualTo(connectorUrl + "/" + subpath);

        url = httpProxyService.getUrl(connectorUrl + "/", subpath, parameters);
        assertThat(url.url().toString()).isEqualTo(connectorUrl + "/" + subpath);
    }

}
