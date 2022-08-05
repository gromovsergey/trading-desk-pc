package com.foros.session.account.yandex;

import com.foros.config.Config;
import com.foros.config.ConfigParameters;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;

public class YandexTnsHttpClientBuilder {
    public static CloseableHttpClient build(Config config) {
        HttpRequestExecutor requestExecutor = buildRequestExecutor(config.get(ConfigParameters.YANDEX_O_AUTH_TOKEN));
        RequestConfig requestConfig = buildRequestConfig();

        return HttpClientBuilder.create()
                .setRequestExecutor(requestExecutor)
                .setDefaultRequestConfig(requestConfig)
                .setSSLSocketFactory(new SSLConnectionSocketFactory(createSSLContext(), new AllowAllHostnameVerifier()))
                .build();
    }

    private static HttpRequestExecutor buildRequestExecutor(final String oAuthToken) {
        return new HttpRequestExecutor() {
            @Override
            protected HttpResponse doSendRequest(HttpRequest request, HttpClientConnection conn, HttpContext context) throws IOException, HttpException {
                request.addHeader("Cache-Control", "no-cache");
                request.addHeader("Pragma", "no-cache");
                request.addHeader("Authorization", "OAuth " + oAuthToken);
                return super.doSendRequest(request, conn, context);
            }
        };
    }

    private static RequestConfig buildRequestConfig() {
        return RequestConfig
                .custom()
                .setConnectTimeout(120000)
                .setSocketTimeout(120000)
                .setConnectionRequestTimeout(120000)
                .setAuthenticationEnabled(false)
                .build();
    }

    private static SSLContext createSSLContext() {
        try {
            return new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}