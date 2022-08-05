package com.foros.action.xml.options;

import com.foros.util.helper.IndexHelper;
import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.session.EntityTO;
import com.foros.session.colocation.ColocationService;
import com.foros.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;

public class ColocationsXmlAction extends AbstractOptionsByAccountAction<EntityTO> {
    @EJB
    private ColocationService colocationService;

    private String ispPair;

    private String ispAccounts;

    public ColocationsXmlAction() {
        super(new NamedTOConverter(true), new OptionStatusFilter(true));
    }

    @AccountId
    @CustomValidator(type = "pair", key = "errors.pair", message = "value.accountPair")
    public String getIspPair() {
        return ispPair;
    }

    public void setIspPair(String ispPair) {
        this.ispPair = ispPair;
    }

    public String getIspAccountIds() {
        return ispAccounts;
    }

    public void setIspAccountIds(String ispAccountIds) {
        this.ispAccounts = ispAccountIds;
    }

    protected Collection<? extends EntityTO> getOptionsByAccount(Long accountId) {
        return IndexHelper.getColocationsList(accountId);
    }

    @Override
    protected Collection<? extends EntityTO> getOptions() throws ProcessException {
        if (!StringUtil.isPropertyEmpty(getIspPair())) {
            return super.getOptions();
        }
        else {
            if (StringUtil.isPropertyEmpty(ispAccounts)) {
                return Collections.emptyList();
            }
            String[] ispAccountsArr = ispAccounts.split(",");
            List<Long> ids = new ArrayList<Long>(ispAccountsArr.length);
            for (String ispAccountId: ispAccountsArr) {
                try {
                    Long id = Long.valueOf(ispAccountId);
                    checkAccountId(id);
                    ids.add(id);
                } catch (NumberFormatException e) {
                    throw new ProcessException("Can't get colocations list. Invalid isp account ids are passed.");
                }
            }
            return getOptionsByAccounts(ids);
        }
    }

    private Collection<? extends EntityTO> getOptionsByAccounts(List<Long> ids) {
        List<EntityTO> res = new ArrayList<EntityTO>();
        res.addAll(colocationService.getByAccountIds(ids));
        return res;
    }
}
