package com.foros.framework.support;

import com.foros.util.context.RequestContexts;

public interface RequestContextsAware {

    void switchContext(RequestContexts contexts);

}
