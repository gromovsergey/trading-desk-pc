package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.PathProvider;

import java.io.IOException;

public interface QuotaProvider {
    Quota get(PathProvider pathProvider) throws IOException;
}
