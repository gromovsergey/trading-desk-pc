package com.foros.rs.sandbox.factory;

import com.foros.model.campaign.CCGType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.template.CreativeTemplate;
import com.foros.session.channel.TriggerListValidationRules;
import com.foros.test.factory.AgencyAccountTypeTestFactory;

public class AgencyAccountTypeGeneratorFactory extends BaseGeneratorFactory<AccountType, AgencyAccountTypeTestFactory> {
    private CreativeSize size;
    private CreativeTemplate template;

    public AgencyAccountTypeGeneratorFactory(AgencyAccountTypeTestFactory accountTypeTestFactory, CreativeSize size, CreativeTemplate template) {
        super(accountTypeTestFactory);
        this.size = size;
        this.template = template;
    }

    @Override
    protected AccountType createDefault() {
        AccountType accountType = factory.create(size, template);
        accountType.setCPAFlag(CCGType.TEXT, true);
        accountType.setCPCFlag(CCGType.TEXT, true);
        accountType.setCPMFlag(CCGType.TEXT, true);
        accountType.setCPAFlag(CCGType.DISPLAY, true);
        accountType.setCPCFlag(CCGType.DISPLAY, true);
        accountType.setCPMFlag(CCGType.DISPLAY, true);
        accountType.setMaxKeywordLength(TriggerListValidationRules.DEFAULT_RULES.getMaxKeywordLength());
        accountType.setMaxUrlLength(TriggerListValidationRules.DEFAULT_RULES.getMaxUrlLength());
        accountType.setSiteTargetingFlag(true);
        return accountType;
    }
}
