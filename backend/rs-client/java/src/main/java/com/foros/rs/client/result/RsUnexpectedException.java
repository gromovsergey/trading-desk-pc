package com.foros.rs.client.result;

import com.foros.rs.client.RsException;

import java.io.PrintStream;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;

public class RsUnexpectedException extends RsException {

    private String remoteStackTrace;
    private final RequestLine requestLine;
    private StatusLine statusLine;

    public RsUnexpectedException(RequestLine requestLine, StatusLine statusLine) {
        this(requestLine, statusLine, null);
    }

    public RsUnexpectedException(RequestLine requestLine, StatusLine statusLine, com.foros.rs.client.model.Exception info) {
        super(createErrorMessage(statusLine));
        this.requestLine = requestLine;
        this.statusLine = statusLine;
        if (info != null) {
            this.remoteStackTrace = info.getTrace();
        }
    }

    public String getRemoteStackTrace() {
        return remoteStackTrace;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    private static String createErrorMessage(StatusLine statusLine) {
        String reasonPhrase = statusLine.getReasonPhrase();
        Codes.HttpCode httpCode = Codes.get(statusLine.getStatusCode());
        StringBuilder res = new StringBuilder();
        res.append(statusLine.getStatusCode());
        res.append(' ');
        if (reasonPhrase != null && httpCode != null && !reasonPhrase.equalsIgnoreCase(httpCode.getMessage())) {
            res.append(reasonPhrase).append(" / ").append(httpCode);
        } else if (httpCode != null) {
            res.append(httpCode.getMessage());
        } else {
            res.append(reasonPhrase);
        }
        return res.toString();
    }

    @Override
    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
        if (remoteStackTrace != null) {
            ps.println(remoteStackTrace);
        }
    }
}
