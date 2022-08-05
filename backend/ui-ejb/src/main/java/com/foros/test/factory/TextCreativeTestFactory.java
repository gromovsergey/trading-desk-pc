package com.foros.test.factory;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.TextCreativeOption;
import com.foros.model.security.AccountType;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.session.template.OptionService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class TextCreativeTestFactory extends CreativeTestFactory {
    @EJB
    private OptionService optionService;

    @Autowired
    private TextCreativeTemplateTestFactory textCreativeTemplateTF;

    @Override
    public Creative create() {
        CreativeTemplate template = textCreativeTemplateTF.create();
        CreativeSize size = creativeSizeTF.findText();
        AdvertiserAccount account = createPersistentAccount(template, size);
        return create(account, template, size);
    }

    public Creative create(AdvertiserAccount account) {
        CreativeTemplate template = textCreativeTemplateTF.create();
        CreativeSize size = creativeSizeTF.findText();
        return create(account, template, size);
    }

    public AdvertiserAccount createPersistentAccount() {
        AccountType accountType = advertiserAccountTypeTF.create();
        accountType.setCPAFlag(CCGType.TEXT, true);
        advertiserAccountTypeTF.persist(accountType);

        return advertiserAccountTF.createPersistent(accountType);
    }

    public Creative findAnyFromAccount(Long advAccountId) {
        return findAny(Creative.class,
            new QueryParam("account", advAccountId),
            new QueryParam("size.defaultName", CreativeSize.TEXT_SIZE, false),
            new QueryParam("template.defaultName", CreativeTemplate.TEXT_TEMPLATE, false));
    }

    @Override
    public void populate(Creative creative) {
        super.populate(creative);
        for (TextCreativeOption textCreativeOption : TextCreativeOption.values()) {
            Option option = optionService.findByTokenFromTextTemplate(textCreativeOption.getToken());
            CreativeOptionValue cov = new CreativeOptionValue();
            cov.setOption(option);
            cov.setValue(option.getDefaultValue());
            creative.getOptions().add(cov);
        }

    }

    @Override
    public AdvertiserAccount createPersistentAccount(CreativeTemplate template, CreativeSize size) {
        AccountType accountType = advertiserAccountTypeTF.create(size, template);
        accountType.setCPAFlag(CCGType.TEXT, true);
        advertiserAccountTypeTF.persist(accountType);
        return advertiserAccountTF.createPersistent(accountType);
    }
}
