package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.PathProvider;

public class NullQuotaProvider implements QuotaProvider {
    public static final QuotaProvider INSTANCE = new NullQuotaProvider();

    public Quota get(PathProvider pathProvider) {
        return new Quota();
    }
}
