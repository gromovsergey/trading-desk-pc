package com.foros.action.admin.accountType;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.security.AccountType;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.admin.accountType.AccountTypeService;
import com.foros.util.StringUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;

public class ListAccountTypeAction  extends BaseActionSupport {

    @EJB
    protected AccountTypeService service;

    private List<AccountType> entities;

    @ReadOnly
    @Restrict(restriction = "AccountType.view")
    public String list() {
        List<AccountType> all = service.findAll();
        Collections.sort(all, new Comparator<AccountType>() {
            @Override
            public int compare(AccountType o1, AccountType o2) {
                return StringUtil.compareToIgnoreCase(o1.getName(), o2.getName());
            }
        });

        setEntities(all);
        return SUCCESS;
    }

    public List<AccountType> getEntities() {
        return entities;
    }

    public void setEntities(List<AccountType> entities) {
        this.entities = entities;
    }
}
