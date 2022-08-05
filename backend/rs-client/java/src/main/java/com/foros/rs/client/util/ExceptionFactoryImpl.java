package com.foros.rs.client.util;

import com.foros.rs.client.RsException;
import com.foros.rs.client.RsNotAuthorizedException;
import com.foros.rs.client.data.DefaultResponseHandler;
import com.foros.rs.client.model.ConstraintViolations;
import com.foros.rs.client.result.RsConstraintViolationException;
import com.foros.rs.client.result.RsUnexpectedException;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;

public class ExceptionFactoryImpl implements ExceptionFactory {
    private ResponseHandler defaultResponseHandler;

    public ExceptionFactoryImpl() {
        this(new DefaultResponseHandler());
    }

    public ExceptionFactoryImpl(ResponseHandler defaultResponseHandler) {
        this.defaultResponseHandler = defaultResponseHandler;
    }

    @Override
    public RsException handleRemoteException(HttpRequest request, HttpResponse response) {
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        switch (statusCode) {
            case 403:
                return new RsNotAuthorizedException();
            case 412: // constraint violations
                ConstraintViolations constraintViolations;
                try {
                    constraintViolations = (ConstraintViolations) defaultResponseHandler.handleResponse(response);
                } catch (IOException e) {
                    return handleIOException(e);
                }
                return new RsConstraintViolationException(constraintViolations.getConstraintViolations());
            case 500: // unexpected error
                com.foros.rs.client.model.Exception exception;
                try {
                    exception = (com.foros.rs.client.model.Exception) defaultResponseHandler.handleResponse(response);
                } catch (Exception e) {
                    exception = null;
                }
                return new RsUnexpectedException(request.getRequestLine(), statusLine, exception);
            default: // other exceptions, even more unexpected than 500
                return new RsUnexpectedException(request.getRequestLine(), statusLine);
        }
    }

    @Override
    public RsException handleIOException(IOException exception) {
        return new RsException(exception);
    }

    @Override
    public RsException handleURISyntaxException(URISyntaxException exception) {
        return new RsException(exception);
    }
}
