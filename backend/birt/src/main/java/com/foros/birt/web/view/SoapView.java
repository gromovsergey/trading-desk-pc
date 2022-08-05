package com.foros.birt.web.view;

import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.springframework.web.servlet.view.AbstractView;

public class SoapView extends AbstractView {

    private MessageContext messageContext;

    public SoapView(MessageContext messageContext) {
        this.messageContext = messageContext;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Message message = messageContext.getResponseMessage();

        if (message != null) {
            setHeaders(message.getMimeHeaders(), response);
            setEncoding(message);
            setContentType(response, message);

            message.writeTo(response.getOutputStream());

            if (!response.isCommitted()) {
                response.flushBuffer();
            }
        } else {
            // No content, so just indicate accepted
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        }
    }

    private void setContentType(HttpServletResponse response, Message message) throws AxisFault {
        //determine content type from message response
        response.setContentType(message.getContentType(messageContext.getSOAPConstants()));
    }

    private void setEncoding(Message message) throws SOAPException {
        // synchronize the character encoding of request and response
        String responseEncoding = (String) messageContext.getProperty(SOAPMessage.CHARACTER_SET_ENCODING);

        if (responseEncoding != null) {
            message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, responseEncoding);
        }
    }

    private void setHeaders(MimeHeaders responseMimeHeaders, HttpServletResponse response) {
        // Transfer MIME headers to HTTP headers for response message.
        for (Iterator i = responseMimeHeaders.getAllHeaders(); i.hasNext(); ) {
            MimeHeader responseMimeHeader = (MimeHeader) i.next();
            response.addHeader(responseMimeHeader.getName(), responseMimeHeader.getValue());
        }
    }

}
