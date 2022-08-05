package com.foros.session.query.campaign;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.TnsBrand;
import com.foros.model.creative.Creative;
import com.foros.model.template.CreativeTemplate;
import com.foros.session.query.AbstractEntityTransformer;

import java.util.Map;

public class YandexCreativeTransformer extends AbstractEntityTransformer<Creative> {

    @Override
    protected Creative transform(Map<String, Object> values) {
        Long id = (Long) values.get("id");

        Creative creative = new Creative();
        creative.setId(id);
        creative.setAccount(new AdvertiserAccount((Long) values.get("accountId")));
        creative.setTemplate(new CreativeTemplate((Long) values.get("templateId")));
        creative.setTnsBrand(new TnsBrand((Long) values.get("tnsBrandId")));
        creative.unregisterChanges();
        return creative;
    }
}
