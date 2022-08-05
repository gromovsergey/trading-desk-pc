package com.foros.session.account.yandex.advertiser;

import com.foros.model.account.TnsAdvertiser;
import com.foros.session.account.yandex.TnsParser;

public class TnsAdvertiserParser extends TnsParser<TnsAdvertiser> {

    public TnsAdvertiserParser(TnsHandler<TnsAdvertiser> handler) {
        super(handler);
    }

    protected Class getClazz() {
        return TnsAdvertiser.class;
    }
}
