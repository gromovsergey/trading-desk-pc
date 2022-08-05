package com.foros.rs.client;

import com.foros.rs.client.data.StopChunkingWrapper;
import com.foros.rs.client.util.ExceptionFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;


public class RsClientBase implements Closeable {

    private final URI prefix;
    private final CloseableHttpClient client;
    private final ResponseHandler defaultResponseHandler;
    private final ExceptionFactory exceptionFactory;

    public RsClientBase(
            URI prefix,
            CloseableHttpClient client,
            ResponseHandler defaultResponseHandler,
            ExceptionFactory exceptionFactory
    ) {
        this.prefix = prefix;
        this.client = client;
        this.defaultResponseHandler = defaultResponseHandler;
        this.exceptionFactory = exceptionFactory;
    }

    public <T> T post(String uri, HttpEntity body, HttpContext context) {
        //noinspection unchecked
        return (T) post(uri, body, defaultResponseHandler, context);
    }

    public <T> T post(String uri, HttpEntity body, ResponseHandler<T> responseHandler, HttpContext context) {
        HttpPost request = new HttpPost(fullUri(uri));
        request.setEntity(StopChunkingWrapper.wrap(body));
        try {
            return client.execute(request, new ResponseHandlerImpl<T>(request, responseHandler), context);
        } catch (IOException e) {
            throw exceptionFactory.handleIOException(e);
        }
    }

    public <T> T get(String uri, HttpContext context) {
        //noinspection unchecked
        return (T) get(uri, defaultResponseHandler, context);
    }

    public <T> T get(String uri, ResponseHandler<T> responseHandler, HttpContext context) {
        try {
            HttpGet request = new HttpGet(fullUri(uri));
            return client.execute(request, new ResponseHandlerImpl<T>(request, responseHandler), context);
        } catch (IOException e) {
            throw exceptionFactory.handleIOException(e);
        }
    }

    private URI fullUri(String path) {
        try {
            return new URI(prefix + path);
        } catch (URISyntaxException e) {
            throw new RsException(e);
        }
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    private class ResponseHandlerImpl<T> implements ResponseHandler<T> {
        private final HttpRequest request;
        private final ResponseHandler responseHandler;

        public ResponseHandlerImpl(HttpRequest request, ResponseHandler responseHandler) {
            this.request = request;
            this.responseHandler = responseHandler;
        }

        @Override
        public T handleResponse(HttpResponse response) throws IOException {
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode >= 400) {
                throw exceptionFactory.handleRemoteException(request, response);
            }
            //noinspection unchecked
            return (T) responseHandler.handleResponse(response);
        }
    }

}
