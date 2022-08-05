package com.foros.session.query.campaign;

import com.foros.model.Flags;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.User;
import com.foros.model.template.CreativeTemplate;
import com.foros.session.query.AbstractEntityTransformer;

import java.util.Date;
import java.util.Map;

public class CreativeTransformer extends AbstractEntityTransformer<Creative> {

    @Override
    protected Creative transform(Map<String, Object> values) {
        Long id = (Long) values.get("id");

        Creative creative = new Creative();
        creative.setId(id);
        creative.setName((String) values.get("name"));
        creative.setVersion((java.sql.Timestamp) values.get("version"));
        creative.setStatus(Status.valueOf((char) values.get("status")));

        Long userId = (Long) values.get("qaUserId");
        if(userId != null) {
            User qaUser = new User();
            qaUser.setId(userId);
            creative.setQaUser(qaUser);
        }
        creative.setQaDate((Date) values.get("qaDate"));
        creative.setQaDescription((String) values.get("qaDescription"));

        creative.setAccount(new AdvertiserAccount((Long) values.get("accountId")));
        creative.setSize(new CreativeSize((Long) values.get("sizeId")));
        creative.setTemplate(new CreativeTemplate((Long) values.get("templateId")));

        creative.setEnableAllAvailableSizes(((Flags) values.get("flags")).get(Creative.ENABLE_ALL_AVAILABLE_SIZES_MASK));

        creative.setDisplayStatusId((Long) values.get("displayStatusId"));

        creative.unregisterChanges();
        return creative;
    }
}
