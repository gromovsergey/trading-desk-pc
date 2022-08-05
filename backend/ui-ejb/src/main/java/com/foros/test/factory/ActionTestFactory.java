package com.foros.test.factory;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.action.Action;
import com.foros.model.action.ConversionCategory;
import com.foros.session.action.ActionService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class ActionTestFactory extends TestFactory<Action> {
    @EJB
    private ActionService actionService;

    @EJB
    private AdvertiserAccountTestFactory advertiserAccountTestTF;

    public void populate(Action action) {
        action.setName(getTestEntityRandomName());
        action.setConversionCategory(ConversionCategory.SIGNUP);
        action.setClickWindow(2);
        action.setImpWindow(2);
    }

    @Override
    public Action create() {
        AdvertiserAccount account = advertiserAccountTestTF.createPersistent();
        return create(account);
    }

    public Action create(AdvertiserAccount account) {
        Action action = new Action();
        action.setAccount(account);
        populate(action);
        return action;
    }

    @Override
    public void persist(Action action) {
        actionService.create(action);
        entityManager.flush();
    }

    public void update(Action action) {
        actionService.update(action);
    }

    @Override
    public Action createPersistent() {
        Action action = create();
        persist(action);
        return action;
    }

    public Action createPersistent(AdvertiserAccount account) {
        Action action = create(account);
        persist(action);
        return action;
    }

    public void delete(Long actionId) {
        actionService.delete(actionId);
    }
}
