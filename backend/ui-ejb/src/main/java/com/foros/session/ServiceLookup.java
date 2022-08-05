package com.foros.session;

public interface ServiceLookup {

    <T> T lookup(Class<T> tclass);

}
