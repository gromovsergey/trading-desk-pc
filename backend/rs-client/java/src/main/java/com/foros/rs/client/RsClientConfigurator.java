package com.foros.rs.client;

import com.foros.rs.client.data.DefaultResponseHandler;
import com.foros.rs.client.util.ExceptionFactory;
import com.foros.rs.client.util.ExceptionFactoryImpl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;

public class RsClientConfigurator {

    protected String userToken;
    protected String key;
    private String forosBase;
    private HttpHost proxy;
    private ResponseHandler responseHandler = new DefaultResponseHandler();
    private RequestConfig.Builder requestConfigBuilder = RequestConfig
            .custom()
            .setConnectTimeout(30000);
    private ExceptionFactory exceptionFactory = new ExceptionFactoryImpl();

    public static RsClientConfigurator newConfigurator(String forosBase) {
        RsClientConfigurator configurator = new RsClientConfigurator();
        return configurator.forosBase(forosBase);
    }

    public RsClientConfigurator forosBase(String forosBase) {
        this.forosBase = forosBase;
        return this;
    }

    public RsClientConfigurator userToken(String userToken) {
        this.userToken = userToken;
        return this;
    }

    public RsClientConfigurator key(String key) {
        this.key = key;
        return this;
    }

    public RsClientConfigurator proxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    public RsClientConfigurator responseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
        return this;
    }

    public RsClientConfigurator exceptionFactory(ExceptionFactory exceptionFactory) {
        this.exceptionFactory = exceptionFactory;
        return this;
    }

    public RsClientConfigurator connectTimeout(int connectTimeout) {
        this.requestConfigBuilder.setConnectTimeout(connectTimeout);
        return this;
    }

    public RsClientConfigurator socketTimeout(int socketTimeout) {
        this.requestConfigBuilder.setSocketTimeout(socketTimeout);
        return this;
    }

    public RsClient configure() {
        URI hostUri;
        try {
            hostUri = new URI(forosBase);
        } catch (URISyntaxException e) {
            throw new RsException(e);
        }
        HttpClientContext context = buildHttpContext(hostUri);

        CloseableHttpClient client = configureInternal();
        try {
            return new SingleRsClient(new URI(forosBase + "/rs"), client, context, responseHandler, exceptionFactory);
        } catch (URISyntaxException e) {
            throw new RsException(e);
        }
    }

    public RsClient configureMultiClient(MultiRsClient.ContextGenerator contextGenerator) {
        CloseableHttpClient client = configureInternal();
        try {
            return new MultiRsClient(new URI(forosBase + "/rs"), client, responseHandler, exceptionFactory, contextGenerator);
        } catch (URISyntaxException e) {
            throw new RsException(e);
        }
    }

    private CloseableHttpClient configureInternal() {
        HttpRequestExecutor requestExecutor = buildRequestExecutor();
        RequestConfig requestConfig = requestConfigBuilder().build();
        return clientBuilder(requestExecutor, requestConfig).build();
    }

    protected RequestConfig.Builder requestConfigBuilder() {
        return requestConfigBuilder;
    }

    protected HttpClientBuilder clientBuilder(HttpRequestExecutor requestExecutor, RequestConfig requestConfig) {
        return HttpClientBuilder
                .create()
                .setProxy(proxy)
                .setRequestExecutor(requestExecutor)
                .setDefaultRequestConfig(requestConfig);
    }

    protected HttpRequestExecutor buildRequestExecutor() {
        return new HttpRequestExecutor() {
                @Override
                protected HttpResponse doSendRequest(HttpRequest request, HttpClientConnection conn, HttpContext context) throws IOException, HttpException {
                    request.addHeader("Cache-Control", "no-cache");
                    request.addHeader("Pragma", "no-cache");
                    return super.doSendRequest(request, conn, context);
                }
        };
    }

    protected HttpClientContext buildHttpContext(URI hostUri) {
        return buildHttpContext(hostUri, userToken, key);
    }

    public static HttpClientContext buildHttpContext(URI hostUri, String userToken, String userKey) {
        SecretKey secretKey = new SecretKeySpec(DatatypeConverter.parseBase64Binary(userKey), RsConstants.ALGORITHM);
        ForosCredentials credentials = new ForosCredentials(userToken, secretKey);

        return buildHttpContext(hostUri, credentials);
    }

    public static HttpClientContext buildHttpContext(URI hostUri, ForosCredentials credentials) {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(hostUri.getHost(), AuthScope.ANY_PORT), credentials);

        BasicAuthCache authCache = new BasicAuthCache();
        authCache.put(new HttpHost(hostUri.getHost(), hostUri.getPort(), hostUri.getScheme()), new ForosAuthScheme());

        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credentialsProvider);
        context.setAuthCache(authCache);
        return context;
    }
}
