package com.foros.test.factory;

import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.util.PersistenceUtils;

import javax.ejb.EJB;
import java.util.LinkedHashSet;

public abstract class CreativeTestFactory extends TestFactory<Creative> {
    @EJB
    protected AdvertiserAccountTypeTestFactory advertiserAccountTypeTF;

    @EJB
    protected DisplayCreativeService displayCreativeService;

    @EJB
    protected AdvertiserAccountTestFactory advertiserAccountTF;

    @EJB
    protected CreativeSizeTestFactory creativeSizeTF;

    protected abstract Creative create(AdvertiserAccount account);

    protected abstract AdvertiserAccount createPersistentAccount(CreativeTemplate template, CreativeSize size);

    public void populate(Creative creative) {
        creative.setName(getTestEntityRandomName());
        creative.setStatus(Status.ACTIVE);
        creative.setQaStatus(ApproveStatus.APPROVED);
        creative.setDisplayStatusId(Creative.LIVE.getId());

        creative.setOptions(new LinkedHashSet<CreativeOptionValue>());
    }

    @Override
    public void persist(Creative creative) {
        displayCreativeService.create(creative);
        entityManager.flush();
    }

    public void update(Creative creative) {
        displayCreativeService.update(creative);
        entityManager.flush();
    }

    public Creative find(Long id) {
        return findAny(Creative.class, new QueryParam("id", id));
    }

    public void approve(Long id) {
        displayCreativeService.approve(id);
    }

    public void activate(Long id) {
        displayCreativeService.activate(id);
    }

    public void delete(Long id) {
        displayCreativeService.delete(id);
    }

    @Override
    public Creative refresh(Creative creative) {
        creative = super.refresh(creative);

        PersistenceUtils.initialize(creative.getOptions());
        PersistenceUtils.initialize(creative.getGroupStates());
        PersistenceUtils.initialize(creative.getCategories());

        return creative;
    }

    public Creative prepareLiveCreative(AdvertiserAccount advertiserAccount, CreativeTemplate template, CreativeSize size) {
        Creative creative = create(advertiserAccount, template, size);
        creative.setStatus(Status.ACTIVE);
        creative.setQaStatus(ApproveStatus.APPROVED);
        return creative;
    }

    public Creative create(AdvertiserAccount account, CreativeTemplate template, CreativeSize size) {
        Creative creative = new Creative();
        creative.setAccount(account);
        creative.setTemplate(template);
        creative.setSize(size);
        populate(creative);
        return creative;
    }

    public Creative create(CreativeTemplate template, CreativeSize size) {
        AdvertiserAccount account = createPersistentAccount(template, size);
        return create(account, template, size);
    }

    @Override
    public Creative createPersistent() {
        Creative creative = create();
        persist(creative);
        return creative;
    }

    public Creative createPersistent(AdvertiserAccount account) {
        Creative creative = create(account);
        persist(creative);
        return creative;
    }

    public Creative createPersistent(CreativeTemplate template, CreativeSize size) {
        Creative creative = create(template, size);
        persist(creative);
        return creative;
    }

    public Creative createPersistent(AdvertiserAccount account, CreativeTemplate template, CreativeSize size) {
        Creative creative = create(account, template, size);
        persist(creative);
        return creative;
    }
}
