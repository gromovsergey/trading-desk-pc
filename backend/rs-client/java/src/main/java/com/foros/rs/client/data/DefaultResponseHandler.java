package com.foros.rs.client.data;

import com.foros.rs.client.MimeType;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;

public class DefaultResponseHandler implements ResponseHandler {
    private ResponseHandler xmlResponseHandler = new JAXBResponseHandler();

    public DefaultResponseHandler() {
    }

    public DefaultResponseHandler(ResponseHandler xmlResponseHandler) {
        this.xmlResponseHandler = xmlResponseHandler;
    }

    @Override
    public Object handleResponse(HttpResponse response) throws IOException {

        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == 204) {
            return null;
        }

        ContentType contentType = ContentType.get(response.getEntity());
        if (contentType == null) {
            return null;
        }

        MimeType mimeType;
        try {
            mimeType = MimeType.parse(contentType.getMimeType());
        } catch (Exception e) {
            return null;
        }

        if (mimeType == MimeType.APPLICATION_XML) {
            return xmlResponseHandler.handleResponse(response);
        } else if (response.getEntity() != null && response.getEntity().getContentLength() > 0) {
            byte[] buf = new byte[(int)response.getEntity().getContentLength()];
            response.getEntity().getContent().read(buf);
            return buf;
        }

        return null;
    }
}
