package com.foros.rs.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class ForosTimestampAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private long timestamp;

    public ForosTimestampAuthenticationToken(String userUid, byte[] signature, long timestamp) {
        super(userUid, signature);
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
