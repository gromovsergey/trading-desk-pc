package com.foros.action.reporting.advertiser.treeFilter;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.AbstractOptionsAction;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.security.AccountRole;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.security.AccountTO;
import com.foros.util.comparator.StatusNameTOComparator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;

public class AgenciesAdvertisersXmlAction extends AbstractOptionsAction<EntityTO> {
    @EJB
    private AccountService accountService;


    private AccountRole accountRole;

    public AgenciesAdvertisersXmlAction() {
        super(new NamedTOConverter(false), new OptionStatusFilter(true));
    }

    @Override
    protected Collection<? extends EntityTO> getOptions() throws ProcessException {
        return prepare(accountService.search(accountRole));
    }

    public AccountRole getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(AccountRole accountRole) {
        this.accountRole = accountRole;
    }

    private List<? extends AccountTO> prepare(List<? extends AccountTO> accountTOs) {
        Collections.sort(accountTOs, new StatusNameTOComparator());
        return accountTOs;
    }

}