package com.foros.session.account.yandex.brand;

import com.foros.model.account.TnsBrand;
import com.foros.session.account.yandex.TnsParser;

public class TnsBrandParser extends TnsParser<TnsBrand> {

    public TnsBrandParser(TnsHandler<TnsBrand> handler) {
        super(handler);
    }

    protected Class getClazz() {
        return TnsBrand.class;
    }
}
