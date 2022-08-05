package com.foros.test.factory;

import com.foros.model.account.IspAccount;
import com.foros.model.isp.Colocation;
import com.foros.model.isp.ColocationRate;
import com.foros.session.colocation.ColocationService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class ColocationTestFactory extends TestFactory<Colocation> {
    @EJB
    private ColocationService colocationService;

    @EJB
    private IspAccountTestFactory ispAccountTF;

    public void populate(Colocation colocation) {
        colocation.setName(getTestEntityRandomName());

        ColocationRate colocationRate = new ColocationRate();
        colocation.setColocationRate(colocationRate);
        colocationRate.setColocation(colocation);
    }

    @Override
    public Colocation create() {
        IspAccount account = ispAccountTF.createPersistent();
        return create(account);
    }

    public Colocation create(IspAccount account) {
        Colocation colocation = new Colocation();
        colocation.setAccount(account);
        populate(colocation);
        return colocation;
    }

    @Override
    public void persist(Colocation colocation) {
        colocationService.create(colocation);
    }

    @Override
    public void update(Colocation colocation) {
        colocationService.update(colocation);
    }

    @Override
    public Colocation createPersistent() {
        Colocation colocation = create();
        persist(colocation);
        return colocation;
    }

    public Colocation createPersistent(IspAccount account) {
        Colocation colocation = create(account);
        persist(colocation);
        return colocation;
    }

    public void delete(Long colocationId) {
        colocationService.delete(colocationId);
    }
}
