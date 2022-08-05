package com.foros.action.colocation;

import com.foros.action.BaseActionSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.IspAccount;
import com.foros.model.isp.Colocation;
import com.foros.session.colocation.ColocationService;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class ColocationActionSupport extends BaseActionSupport implements RequestContextsAware, ModelDriven<Colocation> {

    @EJB
    protected ColocationService colocationService;

    protected Colocation colocation = emptyColocation();

    private Colocation emptyColocation() {
        Colocation colocation = new Colocation();
        colocation.setAccount(new IspAccount());
        return colocation;
    }

    @Override
    public Colocation getModel() {
        return colocation;
    }

    @Override
    public void switchContext(RequestContexts context) {
        context.getIspContext().switchTo(colocation.getAccount().getId());
    }
}
