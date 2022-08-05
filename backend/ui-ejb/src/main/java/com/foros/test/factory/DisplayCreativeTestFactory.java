package com.foros.test.factory;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.template.CreativeTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class DisplayCreativeTestFactory extends CreativeTestFactory {
    @Autowired
    protected DisplayCreativeTemplateTestFactory displayCreativeTemplateTF;

    @Override
    public Creative create() {
        CreativeTemplate template = displayCreativeTemplateTF.createPersistent();
        CreativeSize size = template.getTemplateFiles().iterator().next().getCreativeSize();
        AdvertiserAccount account = createPersistentAccount(template, size);
        return create(account, template, size);
    }

    @Override
    public Creative create(AdvertiserAccount account) {
        CreativeTemplate template = displayCreativeTemplateTF.createPersistent(account);
        CreativeSize size = template.getTemplateFiles().iterator().next().getCreativeSize();
        return create(account, template, size);
    }

    @Override
    public AdvertiserAccount createPersistentAccount(CreativeTemplate template, CreativeSize size) {
        AccountType accountType = advertiserAccountTypeTF.create(size, template);
        accountType.setCPAFlag(CCGType.DISPLAY, true);
        advertiserAccountTypeTF.persist(accountType);
        return advertiserAccountTF.createPersistent(accountType);
    }
}
