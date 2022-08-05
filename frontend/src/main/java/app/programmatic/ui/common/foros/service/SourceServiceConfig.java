package app.programmatic.ui.common.foros.service;

import com.foros.rs.client.RsClientConfigurator;

import org.apache.http.HttpHost;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.net.URI;
import java.net.URISyntaxException;


@Lazy
@Configuration
@EnableConfigurationProperties({ SourceServiceConfig.ApiSettings.class })
public class SourceServiceConfig {
    @Bean
    public ForosBuilder foros(ApiSettings apiSettings) {
        RsClientConfigurator configurator = RsClientConfigurator.newConfigurator(apiSettings.getUrl())
                .userToken(apiSettings.getUserToken())
                .key(apiSettings.getKey());

        Proxy proxy = apiSettings.getProxy();
        if (proxy != null) {
            configurator.proxy(new HttpHost(proxy.getHost(), proxy.getPort()));
        }

        URI hostUri;
        try {
            hostUri = new URI(apiSettings.getUrl());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return new ForosBuilder(configurator, hostUri, apiSettings.getUserToken(), apiSettings.getKey());
    }

    @Bean
    public SourceServiceImpl sourceService() {
        return new SourceServiceImpl();
    }

    public static class Proxy {
        private String host;
        private int port;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    @ConfigurationProperties("foros.api")
    public static class ApiSettings {
        private String url;
        private String userToken;
        private String key;
        private Proxy proxy;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUserToken() {
            return userToken;
        }

        public void setUserToken(String userToken) {
            this.userToken = userToken;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Proxy getProxy() {
            return proxy;
        }

        public void setProxy(Proxy proxy) {
            this.proxy = proxy;
        }
    }
}
