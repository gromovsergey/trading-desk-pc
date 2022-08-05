package com.foros.rs.client;

import com.foros.rs.client.util.MacUtils;

import java.io.Serializable;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.ContextAwareAuthScheme;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.protocol.HttpContext;

public class ForosAuthScheme implements ContextAwareAuthScheme, Serializable {
    @Override
    public void processChallenge(Header header) throws MalformedChallengeException {
    }

    @Override
    public String getSchemeName() {
        return "FOROS-UI";
    }

    @Override
    public String getParameter(String name) {
        return null;
    }

    @Override
    public String getRealm() {
        return null;
    }

    @Override
    public boolean isConnectionBased() {
        return false;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
        return null;
    }

    @Override
    public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
        long timestamp = System.currentTimeMillis();
        request.addHeader(RsConstants.Headers.TIMESTAMP, Long.toString(timestamp));
        request.addHeader(RsConstants.Headers.AUTHORIZATION, generateAuthString((ForosCredentials) credentials, timestamp));
        return null;
    }

    private String generateAuthString(ForosCredentials credentials, long timestamp) {
        String signature = MacUtils.encode(String.valueOf(timestamp), credentials.getKey());
        return RsConstants.FOROS_TIMESTAMP_AUTH_TYPE + " " + credentials.getUserPrincipal().getName() + ":" + signature;
    }

}
