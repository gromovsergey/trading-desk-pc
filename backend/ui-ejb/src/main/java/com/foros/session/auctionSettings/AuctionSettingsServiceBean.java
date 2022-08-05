package com.foros.session.auctionSettings;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.account.Account;
import com.foros.model.account.AccountAuctionSettings;
import com.foros.model.account.InternalAccount;
import com.foros.model.security.ActionType;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.model.site.TagAuctionSettings;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.security.AuditService;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless(name = "AuctionSettingsService")
@Interceptors({PersistenceExceptionInterceptor.class, RestrictionInterceptor.class, ValidationInterceptor.class})
public class AuctionSettingsServiceBean implements AuctionSettingsService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;

    @Override
    public AccountAuctionSettings findByAccountId(Long accountId) {
        if (accountId == null) {
            throw new EntityNotFoundException("Auction Settings with id = null not found");
        }

        AccountAuctionSettings auctionSettings = em.find(AccountAuctionSettings.class, accountId);

        if (auctionSettings == null) {
            throw new EntityNotFoundException("Auction Settings with id = " + accountId + " not found");
        }

        return auctionSettings;
    }

    @Override
    public TagAuctionSettings findByTagId(Long tagId) {
        if (tagId == null) {
            throw new EntityNotFoundException("Auction Settings with id = null not found");
        }

        return em.find(TagAuctionSettings.class, tagId);
    }

    @Override
    public AccountAuctionSettings findDefaultByTagId(Long tagId) {
        if (tagId == null) {
            throw new EntityNotFoundException("Auction Settings with id = null not found");
        }

        Tag tag = em.find(Tag.class, tagId);

        return em.find(AccountAuctionSettings.class, tag.getSite().getAccount().getInternalAccount().getId());
    }

    @Override
    public List<TagAuctionSettings> findNonDefaultTags(Long accountId) {
        Query query = em.createQuery("select tas from TagAuctionSettings tas" +
                " where tas.tag.site.account.internalAccount.id=:accountId" +
                " and tas.tag.status='A' and tas.tag.site.displayStatusId=:siteDisplayStatusId" +
                " and tas.tag.site.account.displayStatusId=:accountDisplayStatusId" +
                " order by tas.tag.site.account.name, tas.tag.site.name, tas.tag.name");
        query.setParameter("accountId", accountId);
        query.setParameter("siteDisplayStatusId", Site.LIVE.getId());
        query.setParameter("accountDisplayStatusId", Account.LIVE.getId());
        return query.getResultList();
    }

    @Override
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class})
    @Restrict(restriction = "AuctionSettings.update", parameters = "find('Account', #auctionSettings.id)")
    @Validate(validation = "AuctionSettings.update", parameters = "#auctionSettings")
    public void update(AccountAuctionSettings auctionSettings) {
        InternalAccount account = em.find(InternalAccount.class, auctionSettings.getId());
        em.merge(auctionSettings);
        auditService.audit(account, ActionType.UPDATE);
    }

    @Override
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class})
    @Restrict(restriction = "AuctionSettings.update", parameters = "find('Tag', #auctionSettings.id)")
    @Validate(validation = "AuctionSettings.update", parameters = "#auctionSettings")
    public void update(TagAuctionSettings auctionSettings) {
        Tag tag = em.find(Tag.class, auctionSettings.getId());
        if (auctionSettings.isAllAllocationsNull()) {
            TagAuctionSettings existing = findByTagId(auctionSettings.getId());
            if (existing != null) {
                em.remove(existing);
            }
        } else {
            if (tag.getAuctionSettings() == null) {
                tag.setAuctionSettings(auctionSettings);
                em.persist(auctionSettings);
            } else {
                em.merge(auctionSettings);
            }
        }
        auditService.audit(tag, ActionType.UPDATE);
    }
}
