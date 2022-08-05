package com.foros.action.colocation;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.IspSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.IspAccount;
import com.foros.session.UtilityService;
import com.foros.session.colocation.ColocationService;
import com.foros.session.colocation.ColocationTO;
import com.foros.util.context.RequestContexts;

import java.util.Collection;

import javax.ejb.EJB;

public class ListColocationAction extends BaseActionSupport implements RequestContextsAware, IspSelfIdAware {

    @EJB
    private ColocationService colocationService;

    @EJB
    private UtilityService utilityService;

    // parameter
    private Long ispId;

    // model
    private Collection<ColocationTO> colocations;
    private IspAccount account;

    @ReadOnly
    public String list() {
        colocations = colocationService.search(ispId);
        account = utilityService.findById(IspAccount.class, ispId);
        return SUCCESS;
    }

    @Override
    public void switchContext(RequestContexts context) {
        context.getIspContext().switchTo(ispId);
    }

    public Collection<ColocationTO> getColocations() {
        return colocations;
    }

    public IspAccount getAccount() {
        return account;
    }

    @Override
    public void setIspId(Long ispId) {
        this.ispId = ispId;
    }

}
