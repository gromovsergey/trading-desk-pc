package com.foros.reporting.meta.olap;

import com.phorm.oix.olap.OlapIdentifier;

public interface MemberResolver<T> {

    OlapIdentifier resolve(T context);

}
