package com.foros.test.factory;

import com.foros.model.campaign.CCGType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Template;
import com.foros.security.AccountRole;

import java.util.Arrays;
import java.util.HashSet;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class AgencyAccountTypeTestFactory extends AccountTypeTestFactory {

    @EJB
    private CreativeSizeTestFactory sizeTF;

    @EJB
    private CreativeTemplateTestFactory templateTF;

    @Override
    public AccountType create() {
        AccountType accountType = new AccountType();
        accountType.setTemplates(new HashSet<Template>());
        accountType.setCreativeSizes(new HashSet<CreativeSize>());
        accountType.setAccountRole(AccountRole.AGENCY);
        accountType.setMaxKeywordLength(100L);
        accountType.setMaxUrlLength(1000L);
        accountType.setMaxKeywordsPerGroup(2000L);
        accountType.setMaxKeywordsPerChannel(2000L);
        accountType.setMaxUrlsPerChannel(2000L);
        accountType.setIoManagement(false);
        accountType.setCampaignCheck(false);
        accountType.setChannelCheck(false);

        populate(accountType);

        return accountType;
    }

    public AccountType create(CreativeSize[] creativeSizes, CreativeTemplate[] templates) {
        AccountType accountType = create();

        accountType.getCreativeSizes().clear();
        if (creativeSizes != null) {
            accountType.getCreativeSizes().addAll(Arrays.asList(creativeSizes));
        }

        accountType.getTemplates().clear();
        if (templates != null) {
            accountType.getTemplates().addAll(Arrays.asList(templates));
        }
        return accountType;
    }

    public AccountType create(CreativeSize creativeSize, CreativeTemplate template) {
        return create(creativeSize == null ? null : new CreativeSize[] { creativeSize },
                template == null ? null : new CreativeTemplate[] { template });
    }

    @Override
    public AccountType createPersistent() {
        AccountType accountType = create();
        persist(accountType);
        return accountType;
    }

    public AccountType createPersistent(CreativeSize[] creativeSizes, CreativeTemplate[] templates) {
        AccountType accountType = create(creativeSizes, templates);
        persist(accountType);
        return accountType;
    }

    public AccountType createPersistent(CreativeSize creativeSize, CreativeTemplate template) {
        return createPersistent(creativeSize == null ? null : new CreativeSize[] { creativeSize },
                template == null ? null : new CreativeTemplate[] { template });
    }

    @Override
    public void populate(AccountType accountType) {
        super.populate(accountType);
        CreativeSize size = sizeTF.createPersistent();
        accountType.getCreativeSizes().add(size);
        CreativeTemplate tpl = templateTF.createPersistent();
        accountType.getTemplates().add(tpl);
        accountType.setCPAFlag(CCGType.DISPLAY, true);
    }
}
