package com.foros.rs.client.data;

import com.foros.rs.client.RsException;
import com.foros.rs.client.rsclient.data.JAXBUtils;

import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;

public class JAXBResponseHandler implements ResponseHandler {
    private static final JAXBContext CONTEXT = JAXBUtils.getContext();

    protected Unmarshaller createUnmarshaller(JAXBContext jaxbContext) throws JAXBException {
        return jaxbContext.createUnmarshaller();
    }

    @Override
    public Object handleResponse(HttpResponse response) throws IOException {
        StreamSource source = new StreamSource(response.getEntity().getContent());
        try {
            return createUnmarshaller(CONTEXT).unmarshal(source);
        } catch (JAXBException e) {
            throw new RsException(e);
        }
    }
}
