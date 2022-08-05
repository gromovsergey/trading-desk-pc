package com.foros.rs.client;

import com.foros.rs.client.util.ExceptionFactory;

import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;


public class MultiRsClient extends RsClientBase implements RsClient {

    private final ContextGenerator contextGenerator;

    public MultiRsClient(
            URI prefix,
            CloseableHttpClient client,
            ResponseHandler defaultResponseHandler,
            ExceptionFactory exceptionFactory,
            ContextGenerator contextGenerator
    ) {
        super(prefix, client, defaultResponseHandler, exceptionFactory);
        this.contextGenerator = contextGenerator;
    }

    public <T> T post(String uri, HttpEntity body) {
        return post(uri, body, contextGenerator.get());
    }

    public <T> T post(String uri, HttpEntity body, ResponseHandler<T> responseHandler) {
        return post(uri, body, responseHandler, contextGenerator.get());
    }

    public <T> T get(String uri) {
        return get(uri, contextGenerator.get());
    }

    public <T> T get(String uri, ResponseHandler<T> responseHandler) {
        return get(uri, responseHandler, contextGenerator.get());
    }

    public interface ContextGenerator {
        HttpContext get();
    }
}
