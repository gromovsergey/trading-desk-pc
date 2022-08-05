package com.foros.rs.client.test;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

public class MockResponse extends BasicHttpResponse {
    public MockResponse() {
        super(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
    }

    public MockResponse(HttpEntity httpEntity) {
        this();
        setEntity(httpEntity);
    }
}
