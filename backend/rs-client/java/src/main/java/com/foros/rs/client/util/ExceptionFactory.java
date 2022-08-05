package com.foros.rs.client.util;

import com.foros.rs.client.RsException;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface ExceptionFactory {
    RsException handleRemoteException(HttpRequest request, HttpResponse response);

    RsException handleIOException(IOException exception);

    RsException handleURISyntaxException(URISyntaxException exception);
}
