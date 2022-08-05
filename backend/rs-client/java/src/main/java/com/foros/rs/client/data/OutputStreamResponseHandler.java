package com.foros.rs.client.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;

public class OutputStreamResponseHandler implements ResponseHandler<OutputStream> {
    private static final int BUFFER_SIZE = 8096;

    private final OutputStream os;

    public OutputStreamResponseHandler(OutputStream os) {
        this.os = os;
    }

    @Override
    public OutputStream handleResponse(HttpResponse response) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return os;
        }

        int read;
        InputStream is = entity.getContent();
        do {
            read = is.read(buffer);
            if (read > 0) {
                os.write(buffer, 0, read);
            }
        } while (read != -1);

        return os;
    }
}
