package com.foros.rs.client.data;

import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.test.MockResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.http.entity.ByteArrayEntity;
import org.junit.Assert;
import org.junit.Test;

public class JAXBEntityTest {

    @Test
    public void test() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new JAXBEntity(new OperationsResult()).writeTo(baos);

        JAXBResponseHandler responseHandler = new JAXBResponseHandler();
        Object o = responseHandler.handleResponse(new MockResponse(new ByteArrayEntity(baos.toByteArray())));
        Assert.assertNotNull(o);
        Assert.assertEquals(OperationsResult.class, o.getClass());
    }
}
