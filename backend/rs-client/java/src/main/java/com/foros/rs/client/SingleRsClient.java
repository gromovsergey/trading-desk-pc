package com.foros.rs.client;

import com.foros.rs.client.util.ExceptionFactory;

import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;


public class SingleRsClient extends RsClientBase implements RsClient {

    private final HttpContext context;

    public SingleRsClient(
            URI prefix,
            CloseableHttpClient client,
            HttpContext context,
            ResponseHandler defaultResponseHandler,
            ExceptionFactory exceptionFactory
    ) {
        super(prefix, client, defaultResponseHandler, exceptionFactory);
        this.context = context;
    }

    public <T> T post(String uri, HttpEntity body) {
        return post(uri, body, context);
    }

    public <T> T post(String uri, HttpEntity body, ResponseHandler<T> responseHandler) {
        return post(uri, body, responseHandler, context);
    }

    public <T> T get(String uri) {
        return get(uri, context);
    }

    public <T> T get(String uri, ResponseHandler<T> responseHandler) {
        return get(uri, responseHandler, context);
    }
}
