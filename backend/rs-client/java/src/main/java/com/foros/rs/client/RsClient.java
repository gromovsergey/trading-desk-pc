package com.foros.rs.client;

import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;


public interface RsClient {

    <T> T post(String uri, HttpEntity body);

    <T> T post(String uri, HttpEntity body, ResponseHandler<T> responseHandler);

    <T> T get(String uri);

    <T> T get(String uri, ResponseHandler<T> responseHandler);
}
