package com.foros.reporting.meta.olap;

import com.phorm.oix.olap.OlapIdentifier;

public class SimpleMemberResolver implements MemberResolver {

    private OlapIdentifier identifier;

    public SimpleMemberResolver(OlapIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public OlapIdentifier resolve(Object context) {
        return identifier;
    }
}
