package com.foros.birt.services.birt.axis;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.AxisHttpSession;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.transport.http.HTTPTransport;
import org.apache.axis.transport.http.ServletEndpointContextImpl;
import org.apache.axis.utils.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageFactory {

    private AxisServer server;

    public MessageContext createContext(AxisServer server, HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.server = server;

        MessageContext context = createMessageContext(request, response);

        Message message = createMessage(request);

        processMessageEncoding(context, message);

        context.setRequestMessage(message);

        return context;
    }

    /**
     * Place the Request message in the MessagContext object - notice
     * that we just leave it as a 'ServletRequest' object and let the
     * Message processing routine convert it - we don't do it since we
     * don't know how it's going to be used - perhaps it might not
     * even need to be parsed.
     *
     * @return a message context
     * @param request
     * @param response
     */
    private MessageContext createMessageContext(HttpServletRequest request, HttpServletResponse response) throws AxisFault {

        MessageContext context = new MessageContext(server);

        ServletContext servletContext = request.getServletContext();

        String requestPath = getRequestPath(request);
        String webInfPath = servletContext.getRealPath("/WEB-INF");

        /* Set the Transport */
        /*********************/
        context.setTransportName(HTTPTransport.DEFAULT_TRANSPORT_NAME);

        /* Save some HTTP specific info in the bag in case someone needs it */
        /********************************************************************/
        //context.setProperty(Constants.MC_JWS_CLASSDIR, jwsClassDir);
        context.setProperty(Constants.MC_HOME_DIR, servletContext.getRealPath("/"));
        context.setProperty(Constants.MC_RELATIVE_PATH, requestPath);
        //context.setProperty(HTTPConstants.MC_HTTP_SERVLET, this);

        context.setProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST, request);
        context.setProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE, response);

        context.setProperty(HTTPConstants.MC_HTTP_SERVLETLOCATION, webInfPath);
        context.setProperty(HTTPConstants.MC_HTTP_SERVLETPATHINFO, request.getPathInfo());
        context.setProperty(HTTPConstants.HEADER_AUTHORIZATION, request.getHeader(HTTPConstants.HEADER_AUTHORIZATION));
        context.setProperty(Constants.MC_REMOTE_ADDR, request.getRemoteAddr());

        // Set up a javax.xml.rpc.server.ServletEndpointContext
        ServletEndpointContextImpl sec = new ServletEndpointContextImpl();

        context.setProperty(Constants.MC_SERVLET_ENDPOINT_CONTEXT, sec);

        /* Save the real path */
        /**********************/
        String realpath = servletContext.getRealPath(requestPath);

        if (realpath != null) {
            context.setProperty(Constants.MC_REALPATH, realpath);
        }

        context.setProperty(Constants.MC_CONFIGPATH, webInfPath);

        context.setProperty(MessageContext.TRANS_URL, HttpUtils.getRequestURL(request).toString());

        context.setUseSOAPAction(true);
        context.setSOAPActionURI(getSoapAction(request));

        context.setSession(new AxisHttpSession(request));

        return context;
    }

    /**
     * getRequestPath a returns request path for web service padded with
     * request.getPathInfo for web services served from /services directory.
     * This is a required to support serving .jws web services from /services
     * URL. See AXIS-843 for more information.
     *
     * @param request HttpServletRequest
     * @return String
     */
    private static String getRequestPath(HttpServletRequest request) {
        return request.getServletPath() + ((request.getPathInfo() != null) ?
                request.getPathInfo() : "");
    }

    private static Message createMessage(HttpServletRequest request) throws IOException {
        Message message = new Message(
                request.getInputStream(),
                false,
                request.getHeader(HTTPConstants.HEADER_CONTENT_TYPE),
                request.getHeader(HTTPConstants.HEADER_CONTENT_LOCATION)
        );

        // Transfer HTTP headers to MIME headers for request message.
        MimeHeaders requestMimeHeaders = message.getMimeHeaders();
        for (Enumeration e = request.getHeaderNames(); e.hasMoreElements(); ) {
            String headerName = (String) e.nextElement();
            for (Enumeration f = request.getHeaders(headerName); f.hasMoreElements(); ) {
                String headerValue = (String) f.nextElement();
                requestMimeHeaders.addHeader(headerName, headerValue);
            }
        }

        return message;
    }

    private static void processMessageEncoding(MessageContext context, Message message) {
        // put character encoding of request to message context
        // in order to reuse it during the whole process.
        try {
            String requestEncoding = (String) message.getProperty(SOAPMessage.CHARACTER_SET_ENCODING);

            if (requestEncoding != null) {
                context.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, requestEncoding);
            }
        } catch (SOAPException e1) {
        }
    }

    /**
     * Extract the SOAPAction header.
     * if SOAPAction is null then we'll we be forced to scan the body for it.
     * if SOAPAction is "" then use the URL
     * @param req incoming request
     * @return the action
     * @throws org.apache.axis.AxisFault
     */
    private static String getSoapAction(HttpServletRequest req) throws AxisFault {
        String soapAction = req.getHeader(HTTPConstants.HEADER_SOAP_ACTION);
        if (soapAction == null) {
            String contentType = req.getHeader(HTTPConstants.HEADER_CONTENT_TYPE);
            if(contentType != null) {
                int index = contentType.indexOf("action");
                if(index != -1){
                    soapAction = contentType.substring(index + 7);
                }
            }
        }

        if (soapAction == null) {
            throw new AxisFault(
                    "Client.NoSOAPAction",
                    Messages.getMessage("noHeader00", "SOAPAction"),
                    null, null
            );
        }

        // the SOAP 1.1 spec & WS-I 1.0 says:
        // soapaction    = "SOAPAction" ":" [ <"> URI-reference <"> ]
        // some implementations leave off the quotes
        // we strip them if they are present
        if (soapAction.startsWith("\"") && soapAction.endsWith("\"")
                && soapAction.length() >= 2) {
            int end = soapAction.length() - 1;
            soapAction = soapAction.substring(1, end);
        }

        if (soapAction.length() == 0) {
            soapAction = req.getContextPath(); // Is this right?

        }
        return soapAction;
    }


}
