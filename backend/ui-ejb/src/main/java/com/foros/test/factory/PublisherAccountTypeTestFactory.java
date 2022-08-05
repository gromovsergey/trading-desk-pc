package com.foros.test.factory;

import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.security.AdvExclusionsType;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.Template;
import com.foros.security.AccountRole;

import java.util.HashSet;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class PublisherAccountTypeTestFactory extends AccountTypeTestFactory {

    @EJB
    private CreativeSizeTestFactory sizeTF;

    @Override
    public AccountType create() {
        AccountType accountType = new AccountType();
        accountType.setTemplates(new HashSet<Template>());
        accountType.setCreativeSizes(new HashSet<CreativeSize>());
        accountType.setAccountRole(AccountRole.PUBLISHER);
        accountType.setShowIframeTag(false);
        accountType.setShowBrowserPassbackTag(false);
        accountType.setAdvExclusions(AdvExclusionsType.DISABLED);

        populate(accountType);

        return accountType;
    }

    @Override
    public void populate(AccountType accountType) {
        super.populate(accountType);
        CreativeSize size = sizeTF.createPersistent();
        accountType.getCreativeSizes().add(size);

    }

    public AccountType create(CreativeSize creativeSize, Template template) {
        AccountType accountType = create();

        accountType.getCreativeSizes().clear();
        if (creativeSize != null) {
            accountType.getCreativeSizes().add(creativeSize);
        }

        accountType.getTemplates().clear();
        if (template != null) {
            accountType.getTemplates().add(template);
        }
        return accountType;
    }

    @Override
    public AccountType createPersistent() {
        AccountType accountType = create();
        persist(accountType);
        return accountType;
    }

    public AccountType createPersistent(CreativeSize creativeSize, Template template) {
        AccountType accountType = create(creativeSize, template);
        persist(accountType);
        return accountType;
    }

    public AccountType findAny() {
        AccountType accountType = findAny(AccountType.class, new QueryParam("accountRole", AccountRole.PUBLISHER));
        return accountType;
    }
}
