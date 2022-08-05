package com.foros.session.creative;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.ApprovableEntity;
import com.foros.model.ApproveStatus;
import com.foros.model.EntityBase;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.TnsAdvertiser;
import com.foros.model.account.TnsBrand;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeOptGroupState;
import com.foros.model.creative.CreativeOptGroupStatePK;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeOptionValuePK;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.RTBCategory;
import com.foros.model.creative.SizeType;
import com.foros.model.creative.TextCreativeOption;
import com.foros.model.creative.YandexCreativeTO;
import com.foros.model.security.ActionType;
import com.foros.model.security.User;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.CreativeToken;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionValueUtils;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateFile;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.principal.SecurityContext;
import com.foros.session.BusinessException;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.ServiceLocator;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.UrlValidations;
import com.foros.session.account.AccountService;
import com.foros.session.bulk.BulkOperation;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.cache.CacheService;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.session.campaign.CampaignCreativeService;
import com.foros.session.campaign.bulk.CreativeSelector;
import com.foros.session.campaign.bulk.YandexCreativeSelector;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.OnNoProviderRoot;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.query.PartialList;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.campaign.CreativeQuery;
import com.foros.session.query.campaign.YandexCreativeQuery;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.ApprovalService;
import com.foros.session.status.DisplayStatusService;
import com.foros.session.status.StatusService;
import com.foros.session.template.HtmlOptionFileHelper;
import com.foros.session.template.HtmlOptionHelper;
import com.foros.session.template.OptionGroupStateHelper;
import com.foros.session.template.OptionService;
import com.foros.session.template.OptionValueValidations;
import com.foros.session.template.TemplateService;
import com.foros.session.textad.TextAdImageUtil;
import com.foros.tx.TransactionSupportService;
import com.foros.util.CollectionUtils;
import com.foros.util.EntityUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.SQLUtil;
import com.foros.util.Stats;
import com.foros.util.StringUtil;
import com.foros.util.UploadUtils;
import com.foros.util.bean.Filter;
import com.foros.util.comparator.IdNameComparator;
import com.foros.util.mapper.Converter;
import com.foros.util.preview.CreativeOptionValueSource;
import com.foros.util.preview.PreviewContext;
import com.foros.util.preview.PreviewContextBuilder;
import com.foros.util.preview.PreviewModel;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validate;
import com.foros.validation.strategy.ValidationStrategies;
import com.foros.validation.util.ValidationUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.jdbc.core.RowCallbackHandler;

@Stateless(name = "DisplayCreativeService")
@Interceptors({ RestrictionInterceptor.class, PersistenceExceptionInterceptor.class, ValidationInterceptor.class })
public class DisplayCreativeServiceBean implements DisplayCreativeService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;

    @EJB
    private CreativePreviewService previewService;

    @EJB
    private ApprovalService approvalService;

    @EJB
    private StatusService statusService;

    @EJB
    private DisplayStatusService displayStatusService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private TemplateService templateService;

    @EJB
    private CreativeSizeService creativeSizeService;

    @EJB
    private UserService userService;

    @EJB
    private AccountService accountService;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private TransactionSupportService transactionSupportService;

    @EJB
    private ValidationService validationService;

    @EJB
    private UrlValidations urlValidations;

    @EJB
    private OptionValueValidations optionValueValidations;

    @EJB
    private CreativeValidations creativeValidations;

    @EJB
    private QueryExecutorService executorService;

    @EJB
    private RestrictionService restrictionService;

    @EJB
    private CampaignCreativeService campaignCreativeService;

    @EJB
    private CreativePreviewService creativePreviewService;

    @EJB
    private ConfigService config;

    @EJB
    private CreativeService creativeService;

    @EJB
    private OptionService optionService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    private Set<String> silentOptionTokens = Collections.emptySet();

    private HtmlOptionHelper htmlOptionHelper = new HtmlOptionHelper() {
            @Override
            protected EntityManager getEM() {
                return em;
            }

             @Override
             protected LoggingJdbcTemplate getJdbcTemplate() {
                 return jdbcTemplate;
             }
         };

    private HtmlOptionFileHelper htmlFileHelper = new HtmlOptionFileHelper() {
        @Override
        protected TransactionSupportService getTransactionSupportService() {
            return transactionSupportService;
        }

        @Override
        protected PathProviderService getPathProviderService() {
            return pathProviderService;
        }
    };

    private OptionsAndStatesPersister optionsAndStatesPersister = new OptionsAndStatesPersister() {
        @Override
        protected EntityManager getEm() {
            return em;
        }

        @Override
        protected CampaignCreativeService getCampaignCreativeService() {
            return campaignCreativeService;
        }

        @Override
        protected ConfigService getConfig() {
            return config;
        }

        @Override
        protected boolean isExternal() {
            return currentUserService.isExternal();
        }

        @Override
        protected Set<String> getSilentlyUpdatedOptionTokens() {
            return silentOptionTokens;
        }
    };

    @PostConstruct
    public void init() {
        silentOptionTokens = new HashSet<>(config.detach().get(ConfigParameters.SILENT_OPTION_TOKENS));
    }

    private void prePersist(Creative creative, Creative existingCreative) {
        // Creative size & template
        if (creative.isChanged("size")) {
            creative.setSize(em.find(CreativeSize.class, creative.getSize().getId()));
        } else {
            creative.setSize(existingCreative.getSize());
        }

        if (creative.isChanged("template")) {
            creative.setTemplate(em.find(CreativeTemplate.class, creative.getTemplate().getId()));
        } else {
            creative.setTemplate(existingCreative.getTemplate());
        }

        if (!creative.getTemplate().isExpandable() || !creative.getSize().isExpandable()) {
            creative.setExpandable(false);
        }

        if ((creative.isChanged("expandable") || existingCreative == null) && !creative.isExpandable()) {
            creative.setExpansion(null);
        }

        // Account
        AdvertiserAccount account = existingCreative != null ? existingCreative.getAccount() :
                em.getReference(AdvertiserAccount.class, creative.getAccount().getId());
        creative.setAccount(account);
        creative.unregisterChange("account");

        // categories
        if (creative.isChanged("categories")) {
            if (existingCreative != null && categoriesEqual(creative, existingCreative)) {
                creative.unregisterChange("categories");
            }
        } else {
            // Default value for creation
            if (existingCreative == null) {
                creative.setCategories(accountService.loadCategories(creative.getAccount()));
            }
        }

        if (!creative.isChanged("tnsBrand") && existingCreative != null) {
            creative.setTnsBrand(existingCreative.getTnsBrand());
            creative.unregisterChange("tnsBrand");
        }

        if (currentUserService.isExternal() && existingCreative != null) {
            creative.setTnsBrand(existingCreative.getTnsBrand());
        } else if (!creative.getAccount().getCountry().getCountryCode().equalsIgnoreCase(TnsAdvertiser.COUNTRY_CODE)
                || !config.detach().get(ConfigParameters.YANDEX_TEMPLATE_NAMES).contains(creative.getTemplate().getDefaultName())
                || ObjectUtils.equals(creative.getAccount().getTnsBrand(), creative.getTnsBrand())) {
            creative.setTnsBrand(null);
        }

        if (creative.isTextCreative()) {
            prePersistSizes(creative, existingCreative);
            if (creative.isChanged("options") || existingCreative == null) {
                addHashToCreativeOptions(creative, calculateHash(creative));
            }
        } else {
            creative.unregisterChange("sizeTypes", "tagSizes", "flags");
        }

        if (creative.isChanged("options", "groupStates")) {
            optionsAndStatesPersister.prePersist(creative);
        }
    }

    private void addHashToCreativeOptions(Creative creative, String calculatedHash) {
        Option hashOption = optionService.findByTokenFromTextTemplate(CreativeToken.CREATIVE_HASH.getName());
        CreativeOptionValue optionValue = creative.findOptionValue(hashOption);
        if (optionValue == null) {
            optionValue = new CreativeOptionValue();
            optionValue.setCreative(creative);
            optionValue.setOption(hashOption);
            creative.getOptions().add(optionValue);
        }
        optionValue.setValue(calculatedHash);
    }

    private void prePersistSizes(Creative creative, Creative existingCreative) {
        if (!creative.isChanged("enableAllAvailableSizes")
                && !creative.isChanged("sizeTypes")
                && !creative.isChanged("tagSizes")) {
            return;
        }

        // those 3 fields should be updated all at once
        if (creative.isEnableAllAvailableSizes()) {
            creative.getSizeTypes().clear();
            creative.getTagSizes().clear();
        }

        if (!creative.getSizeTypes().isEmpty()) {
            Set<SizeType> sizeTypes = creative.getSizeTypes();
            Set<SizeType> updatedTypes = new HashSet<>();
            for (SizeType sizeType : sizeTypes) {
                updatedTypes.add(em.find(SizeType.class, sizeType.getId()));
            }
            creative.setSizeTypes(updatedTypes);
        }

        if (!creative.getTagSizes().isEmpty()) {
            Set<CreativeSize> sizes = creative.getTagSizes();
            Set<CreativeSize> updatedSizes = new HashSet<>();
            for (CreativeSize size : sizes) {
                CreativeSize persistent = em.find(CreativeSize.class, size.getId());
                if (!creative.getSizeTypes().contains(persistent.getSizeType())) {
                    updatedSizes.add(persistent);
                }
            }
            creative.setTagSizes(updatedSizes);
        }

        if (existingCreative != null &&
                ObjectUtils.equals(creative.getSizeTypes(), existingCreative.getSizeTypes()) &&
                ObjectUtils.equals(creative.getTagSizes(), existingCreative.getTagSizes())) {
            creative.unregisterChange("sizeTypes", "tagSizes");
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.create", parameters = "find('Account', #creative.account.id)")
    @Interceptors(CaptureChangesInterceptor.class)
    @Validate(validation = "Creative.create", parameters = { "#creative" })
    public Long create(Creative creative) {
        if (SecurityContext.isAdvertiser()) {
            creative.setAccount(em.getReference(AdvertiserAccount.class, SecurityContext.getPrincipal().getAccountId()));
        }

        boolean isStatusChanged = creative.isChanged("status");
        if (!isStatusChanged) {
            creative.setStatus(Status.ACTIVE);
        }
        creative.setQaStatus(advertiserEntityRestrictions.canApprove() ? ApproveStatus.APPROVED : ApproveStatus.HOLD);
        creative.setDisplayStatus(advertiserEntityRestrictions.canApprove() ? Creative.LIVE : Creative.PENDING_FOROS);

        prePersist(creative, null);

        Set<CreativeOptionValue> options = creative.getOptions();
        creative.setOptions(null);

        Set<CreativeOptGroupState> states = creative.getGroupStates();
        creative.setGroupStates(null);

        statusService.makePendingOnChange(creative, isStatusChanged);

        em.persist(creative);

        optionsAndStatesPersister.persist(creative, options, states);

        updateCreativeCategories(creative);

        auditService.audit(creative, ActionType.CREATE);

        displayStatusService.update(creative);

        htmlOptionHelper.processHttpSafeOption(creative);
        htmlOptionHelper.processDynamicFiles(creative);
        htmlFileHelper.updateFilesOnCommit(creative);

        return creative.getId();
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('Creative', #creative.id)")
    @Interceptors(CaptureChangesInterceptor.class)
    @Validate(validation = "Creative.update", parameters = { "#creative" })
    public void update(final Creative creative) {
        updateInternal(creative);

        CacheService cacheService = ServiceLocator.getInstance().lookup(CacheService.class);
        cacheService.evictCollection(Creative.class, "options");
        cacheService.evictCollection(Creative.class, "groupStates");
    }

    private void changeQaStatus(ApprovableEntity entity, ApproveStatus newStatus) {
        entity.setQaStatus(newStatus);
        if (newStatus == ApproveStatus.HOLD) {
            entity.setQaUser(null);
            entity.setQaDate(null);
            entity.setQaDescription(null);
        } else {
            entity.setQaUser(em.find(User.class, currentUserService.getUserId()));
            entity.setQaDate(new Date());
            entity.setQaDescription(null);
        }
    }

    private boolean isChanged(Creative creative) {
        return creative.isChanged(
            "size",
            "name",
            "template",
            "expandable",
            "expansion",
            "options",
            "groupStates",
            "enableAllAvailableSizes",
            "sizeTypes",
            "tagSizes");
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.delete", parameters = "find('Creative', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void delete(Long id) {
        delete(find(id));
    }

    @Restrict(restriction = "AdvertiserEntity.delete", parameters = { "#creative" })
    @Interceptors(CaptureChangesInterceptor.class)
    public void delete(Creative creative) {
        statusService.delete(creative);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.undelete", parameters = "find('Creative', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void undelete(Long id) {
        undelete(find(id));
    }

    @Restrict(restriction = "AdvertiserEntity.undelete", parameters = { "#creative" })
    @Interceptors(CaptureChangesInterceptor.class)
    private void undelete(Creative creative) {
        statusService.undelete(creative);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.inactivate", parameters = "find('Creative', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void inactivate(Long id) {
        inactivate(find(id));
    }

    @Restrict(restriction = "AdvertiserEntity.inactivate", parameters = { "#creative" })
    @Interceptors(CaptureChangesInterceptor.class)
    private void inactivate(Creative creative) {
        statusService.inactivate(creative);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.activate", parameters = "find('Creative', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void activate(Long id) {
        activate(find(id));
    }

    @Restrict(restriction = "AdvertiserEntity.activate", parameters = { "#creative" })
    @Interceptors(CaptureChangesInterceptor.class)
    private void activate(Creative creative) {
        statusService.activate(creative);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.approve", parameters = "find('Creative', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void approve(Long id) {
        approve(find(id));
    }

    @Restrict(restriction = "AdvertiserEntity.approve", parameters = { "#creative" })
    @Interceptors(CaptureChangesInterceptor.class)
    private void approve(Creative creative) {
        for (CreativeCategory category : creative.getCategories()) {
            if (category.getType() == CreativeCategoryType.TAG && category.getQaStatus() == 'H') {
                category.setQaStatus('A');
                category.setDefaultName(category.getDefaultName());
                em.merge(category);
                auditService.audit(category, ActionType.APPROVE);
            }
        }
        approvalService.approve(creative);
        displayStatusService.update(creative);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.decline", parameters = "find('Creative', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void decline(Long id, String dsc) {
        decline(find(id), dsc);
    }

    @Restrict(restriction = "AdvertiserEntity.decline", parameters = { "#creative" })
    @Interceptors(CaptureChangesInterceptor.class)
    private void decline(Creative creative, String dsc) {
        approvalService.decline(creative, dsc);
        displayStatusService.update(creative);
    }

    @Override
    public void refresh(Long id) {
        Creative creative = find(id);
        em.refresh(creative);
    }

    @Override
    public Creative find(Long id) {
        Creative res = em.find(Creative.class, id);
        if (res == null) {
            throw new EntityNotFoundException("Creative with id=" + id + " not found");
        }
        return res;
    }

    @Override
    public Creative findWithOptions(Long id) {
        Creative creative = find(id);
        if (creative == null) {
            throw new EntityNotFoundException("Creative with id=" + id + " not found");
        }
        PersistenceUtils.initialize(creative.getOptions());
        PersistenceUtils.initialize(creative.getGroupStates());
        PersistenceUtils.initialize(creative.getCategories());
        PersistenceUtils.initialize(creative.getTagSizes());
        PersistenceUtils.initialize(creative.getSizeTypes());

        return creative;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "find('Creative', #id)")
    public Creative view(Long id) {
        return findWithOptions(id);
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public Collection<EntityTO> findEntityTOByAdvertiser(Long accountId) {
        return findEntityTOByAdvertiser(accountId, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public Collection<EntityTO> findEntityTOByAdvertiser(Long accountId, boolean isOnlyTextAds) {
        return em.createNamedQuery("Creative.entityTO.findByAdvertiserId")
            .setParameter("accountId", accountId)
            .setParameter("isInternal", SecurityContext.isInternal())
            .setParameter("isOnlyTextAds", isOnlyTextAds)
            .getResultList();
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public Collection<EntityTO> findEntityTOByAdvertiserAndTargetType(Long accountId, boolean isOnlyTextAds, TGTType targetType) {
        return em.createNamedQuery("Creative.entityTO.findByAdvertiserIdAndTargetType", EntityTO.class)
            .setParameter("accountId", accountId)
            .setParameter("isInternal", SecurityContext.isInternal())
            .setParameter("isOnlyTextAds", isOnlyTextAds)
            .setParameter("targetType", targetType.getLetter())
            .getResultList();
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Campaign', #campaignId)")
    public Collection<EntityTO> findByCampaignId(Long campaignId) {
        return findByCampaignId(campaignId, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Restrict(restriction = "Entity.view", parameters = "find('Campaign', #campaignId)")
    public Collection<EntityTO> findByCampaignId(Long campaignId, boolean isOnlyTextAds) {
        return em.createNamedQuery("Creative.findByCampaignId")
            .setParameter("campaignId", campaignId)
            .setParameter("isInternal", SecurityContext.isInternal())
            .setParameter("isOnlyTextAds", isOnlyTextAds)
            .getResultList();
    }

    @Override
    @Restrict(restriction = "Entity.view", parameters = "find('Campaign', #campaignId)")
    public Collection<EntityTO> findByCampaignIdAndTargetType(Long campaignId, boolean isOnlyTextAds, TGTType targetType) {
        return em.createNamedQuery("Creative.findByCampaignIdAndTargetType", EntityTO.class)
            .setParameter("campaignId", campaignId)
            .setParameter("isInternal", SecurityContext.isInternal())
            .setParameter("isOnlyTextAds", isOnlyTextAds)
            .setParameter("targetType", targetType.getLetter())
            .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Restrict(restriction = "Entity.view", parameters = "find('CampaignCreativeGroup', #creativeGroupId)")
    public Collection<EntityTO> findByCreativeGroupId(Long creativeGroupId) {
        return em.createNamedQuery("Creative.findByCreativeGroupId")
            .setParameter("creativeGroupId", creativeGroupId)
            .setParameter("isInternal", SecurityContext.isInternal())
            .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CreativeCategory> findCategoriesByType(CreativeCategoryType type) {
        if (type != null) {
            return em.createNamedQuery("CreativeCategory.findByType")
                .setParameter("type", type)
                .getResultList();
        }

        return em.createNamedQuery("CreativeCategory.findAll").getResultList();
    }

    @Override
    public List<EntityTO> getCreativeSizeOrApplicationFormatLinkedCreatives(TemplateFile templateFile) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(" SELECT DISTINCT NEW com.foros.session.EntityTO ");
        queryBuilder.append("       (c.id, c.name, c.status) ");
        queryBuilder.append(" FROM CreativeTemplate ct, ");
        queryBuilder.append("      TemplateFile ctf, ");
        queryBuilder.append("      Creative c ");
        queryBuilder.append(" WHERE c.status <> 'D' ");
        queryBuilder.append("  and ct.id = ctf.template.id ");
        queryBuilder.append("  and ct.id =  c.template.id ");
        queryBuilder.append("  and c.size.id = :sizeId ");
        queryBuilder.append("  and c.template.id = :templateId ");
        queryBuilder.append("  and ctf.applicationFormat.id = :appFormatId ");

        Query query = em.createQuery(queryBuilder.toString());

        query.setParameter("sizeId", templateFile.getCreativeSize().getId());
        query.setParameter("templateId", templateFile.getTemplate().getId());
        query.setParameter("appFormatId", templateFile.getApplicationFormat().getId());

        @SuppressWarnings("unchecked")
        List<EntityTO> result = query.getResultList();
        sortEntityTO(result);
        return result;
    }

    private void updateCreativeCategories(Creative creative) {
        Set<CreativeCategory> managedCategories = new LinkedHashSet<>();

        boolean isVisualFromTemplate = !creative.getTemplate().getCategories().isEmpty();
        for (CreativeCategory cc : creative.getCategories()) {
            CreativeCategory managed;
            if (!CreativeCategoryType.TAG.equals(cc.getType()) || cc.getId() != null) {
                managed = em.getReference(CreativeCategory.class, cc.getId());
                if (isVisualFromTemplate && CreativeCategoryType.VISUAL.equals(managed.getType())) {
                    continue;
                }
                if (SecurityContext.isInternal() && creative.isApproved()) {
                    managed.setQaStatus('A');
                }
            } else {
                if (SecurityContext.isInternal()) {
                    cc.setQaStatus('A');
                } else {
                    cc.setQaStatus('H');
                }
                em.persist(cc);
                auditService.audit(cc, ActionType.CREATE);
                managed = cc;
            }
            managedCategories.add(managed);
        }
        creative.setCategories(managedCategories);
    }

    @Override
    public CreativeCategory findCategory(Long id) {
        try {
            return (CreativeCategory) em.createNamedQuery("CreativeCategory.findById").setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public CreativeCategory findCategory(CreativeCategoryType type, String name, boolean showHold) {
        try {
            if (showHold) {
                return (CreativeCategory) em.createNamedQuery("CreativeCategory.findByTypeName")
                    .setParameter("type", type)
                    .setParameter("name", name).getSingleResult();
            }

            return (CreativeCategory) em.createNamedQuery("CreativeCategory.findByTypeNameStatus")
                .setParameter("type", type).setParameter("name", name)
                .setParameter("qaStatus", 'A')
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Queries db for the categories (VISUAL, CONTENT or TAG) by name, case insensitive.
     */
    @Override
    public List<CreativeCategory> searchCategory(CreativeCategoryType type, String name, boolean showHold, int maxResults) {
        StringBuilder sql = new StringBuilder("SELECT c FROM CreativeCategory c WHERE c.type = :type ");
        sql.append(" and lower(c.defaultName) like :name ESCAPE '\\'");
        if (!showHold) {
            sql.append(" and c.qaStatus = 'A'");
        }
        sql.append(" ORDER BY c.defaultName");

        name = name == null ? "" : name;

        Query query = em.createQuery(sql.toString());
        query.setParameter("type", type);
        query.setParameter("name", SQLUtil.getEscapedString(name.toLowerCase(), '\\') + "%");
        query.setMaxResults(maxResults);

        @SuppressWarnings("unchecked")
        List<CreativeCategory> result = query.getResultList();

        return result;
    }

    @Override
    public Collection<CampaignCreative> findCampaignCreatives(Long creativeId) {
        return em.createQuery("select cc from CampaignCreative cc where cc.creative.id = :creativeId "
                                     + (!userService.getMyUser().isDeletedObjectsVisible() ?
                " and cc.status <> '" + Status.DELETED.getLetter() + "'" +
                " and cc.creativeGroup.status <> '" + Status.DELETED.getLetter() + "'" +
                " and cc.creativeGroup.campaign.status <> '" + Status.DELETED.getLetter() + "'"
                : ""),
                CampaignCreative.class)
                .setParameter("creativeId", creativeId)
                .getResultList();
    }

    @Override
    public List<EntityTO> findForLink(CampaignCreativeGroup group, Long ccId) {
        Long textTemplateId = templateService.findTextTemplateId();

        Query query = em.createQuery("select new com.foros.session.EntityTO(c.id, c.name, c.status) " +
                " from Creative c where c.status <> :deleted and c.template.id " +
                (CCGType.TEXT == group.getCcgType() ? " = " : " <> ") + ":textTemplateId and c.account.id = :accountId");
        query.setParameter("deleted", Status.DELETED.getLetter());
        query.setParameter("textTemplateId", textTemplateId);
        query.setParameter("accountId", group.getAccount().getId());
        //noinspection unchecked
        List<EntityTO> res = (List<EntityTO>) query.getResultList();

        if (ccId != null) {
            Creative linkedCreative = em.find(CampaignCreative.class, ccId).getCreative();
            if (linkedCreative.getStatus() == Status.DELETED) {
                res.add(new EntityTO(linkedCreative.getId(), linkedCreative.getName(), linkedCreative.getStatus()));
            }
        }

        sortEntityTO(res);
        EntityUtils.applyStatusRules(res, null, true);
        return res;
    }

    /**
     * This should be moved to EntityTO rather than scattered across the code
     */
    private void sortEntityTO(List<EntityTO> result) {
        Collections.sort(result, new IdNameComparator());
    }

    @Override
    public boolean hasCampaignCreative(Long creativeId, Long groupId) {
        Query query = em.createQuery("select count(cc) from CampaignCreative cc where cc.creative.id = :creativeId and cc.creativeGroup.id = :groupId and cc.status <> 'D' ");
        query.setParameter("creativeId", creativeId);
        query.setParameter("groupId", groupId);
        return ((Long) query.getSingleResult()).intValue() > 0;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view")
    public Result<Creative> get(CreativeSelector creativeSelector) {
        if (!currentUserService.isExternal()
                && CollectionUtils.isNullOrEmpty(creativeSelector.getAdvertiserIds())
                && CollectionUtils.isNullOrEmpty(creativeSelector.getCreatives())) {
            throw new BusinessException("Either advertiser IDs or creative IDs!");
        }

        if (!CollectionUtils.isNullOrEmpty(creativeSelector.getSizes()) &&
                !CollectionUtils.isNullOrEmpty(creativeSelector.getExcludedSizes())) {
            throw new BusinessException("Sizes and excluded sizes can't be chosen both");
        }

        if (!CollectionUtils.isNullOrEmpty(creativeSelector.getTemplates()) &&
                !CollectionUtils.isNullOrEmpty(creativeSelector.getExcludedTemplates())) {
            throw new BusinessException("Templates and excluded templates can't be chosen both");
        }

        PartialList<Creative> creatives = createCreativeQuery(creativeSelector)
                .executor(executorService)
                .partialList(creativeSelector.getPaging());

        em.clear();

        if (creatives.isEmpty()) {
            return new Result<>(creatives, creatives.getPaging());
        }

        Set<Long> creativeIds = EntityUtils.getEntityIds(creatives);

        Map<Long, AdvertiserAccount> advertisers = CollectionUtils.lazyMap(new Converter<Long, AdvertiserAccount>() {
            @Override
            public AdvertiserAccount item(Long id) {
                return em.find(AdvertiserAccount.class, id);
            }
        });

        Map<Long, CreativeSize> sizes = CollectionUtils.lazyMap(new Converter<Long, CreativeSize>() {
            @Override
            public CreativeSize item(Long id) {
                return em.find(CreativeSize.class, id);
            }
        });

        Map<Long, CreativeTemplate> templates = CollectionUtils.lazyMap(new Converter<Long, CreativeTemplate>() {
            @Override
            public CreativeTemplate item(Long id) {
                return em.find(CreativeTemplate.class, id);
            }
        });

        Map<ImmutablePair<CreativeTemplate, CreativeSize>, PreviewModel> previewModels = CollectionUtils.lazyMap(
                new Converter<ImmutablePair<CreativeTemplate, CreativeSize>, PreviewModel>() {
            @Override
            public PreviewModel item(ImmutablePair<CreativeTemplate, CreativeSize> pair) {
                return creativePreviewService.buildPreviewModel(pair.getLeft(), pair.getRight());
            }
        });

        Map<Long, List<SizeType>> sizeTypes = readSizeTypes(creativeIds);
        Map<Long, List<CreativeSize>> tagSizes = readTagSizes(creativeIds);
        Map<Long, List<CreativeCategory>> categories = readCreativeCategories(creativeIds);
        Map<Long, List<CreativeOptionValue>> optionValues = readOptionValues(creativeIds);

        for (Creative creative : creatives) {
            CreativeSize size = sizes.get(creative.getSize().getId());
            creative.setSize(size);
            CreativeTemplate template = templates.get(creative.getTemplate().getId());
            creative.setTemplate(template);
            AdvertiserAccount account = advertisers.get(creative.getAccount().getId());
            if (account.getAgency() != null) {
                creative.getAccount().setAgency(new AgencyAccount(account.getAgency().getId()));
            }

            List<SizeType> creativeSizeTypes = sizeTypes.get(creative.getId());
            if (creativeSizeTypes != null) {
                creative.getSizeTypes().addAll(creativeSizeTypes);
            }

            List<CreativeSize> creativeTagSizes = tagSizes.get(creative.getId());
            if (creativeTagSizes != null) {
                creative.getTagSizes().addAll(creativeTagSizes);
            }

            List<CreativeCategory> creativeCategories = categories.get(creative.getId());
            if (creativeCategories != null) {
                creative.getCategories().addAll(creativeCategories);
            }

            List<CreativeOptionValue> creativeOptionValues = optionValues.get(creative.getId());
            if (creativeOptionValues != null) {
                for (CreativeOptionValue creativeOptionValue : creativeOptionValues) {
                    creativeOptionValue.setCreative(creative);
                    creative.getOptions().add(creativeOptionValue);

                    if (creativeOptionValue.isFile()) {
                        creativeOptionValue.setValue(getPreparedFileOptionValue(account, creativeOptionValue));
                    }
                }
            }

            PreviewModel previewModel = previewModels.get(ImmutablePair.of(template, size));
            PreviewContext previewContext = PreviewContextBuilder.empty()
                    .withTemplate(template)
                    .withSize(size)
                    .withOptionValueSource(new CreativeOptionValueSource(account, creative.getOptions()))
                    .build(previewModel.getAllDefinitions());

            creative.setWidth(toLong(previewContext.evaluateToken(CreativeToken.WIDTH)));
            creative.setHeight(toLong(previewContext.evaluateToken(CreativeToken.HEIGHT)));
        }

        return new Result<>(creatives, creatives.getPaging());
    }

    private Long toLong(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private CreativeQuery createCreativeQuery(CreativeSelector textAdSelector) {
        return new CreativeQuery()
            .restrict()
            .advertisers(textAdSelector.getAdvertiserIds())
            .creatives(textAdSelector.getCreatives())
            .statuses(textAdSelector.getStatuses())
            .sizes(textAdSelector.getSizes())
            .excludeSizes(textAdSelector.getExcludedSizes())
            .templates(textAdSelector.getTemplates())
            .excludeTemplates(textAdSelector.getExcludedTemplates())
            .addDefaultOrder();
    }

    private Map<Long, List<SizeType>> readSizeTypes(final Collection<Long> creativeIds) {
        final CollectionUtils.MultiMapBuilder<Long, SizeType> result = new CollectionUtils.MultiMapBuilder<>();

        jdbcTemplate.query(
                "SELECT CREATIVE_ID, SIZE_TYPE_ID FROM CREATIVE_TAGSIZETYPE WHERE CREATIVE_ID = any(?)",
                new Object[] {jdbcTemplate.createArray("int", creativeIds)},
                new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        long creativeId = rs.getLong("CREATIVE_ID");
                        long typeId = rs.getLong("SIZE_TYPE_ID");
                        SizeType sizeType = new SizeType();
                        sizeType.setId(typeId);

                        result.put(creativeId, sizeType);
                    }
                }
        );

        return result.build();
    }

    private Map<Long, List<CreativeSize>> readTagSizes(final Collection<Long> creativeIds) {
        final CollectionUtils.MultiMapBuilder<Long, CreativeSize> result = new CollectionUtils.MultiMapBuilder<>();

        jdbcTemplate.query(
                "SELECT CREATIVE_ID, SIZE_ID FROM CREATIVE_TAGSIZE WHERE CREATIVE_ID = any(?)",
                new Object[] {jdbcTemplate.createArray("int", creativeIds)},
                new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        long creativeId = rs.getLong("CREATIVE_ID");
                        long sizeId = rs.getLong("SIZE_ID");

                        result.put(creativeId, new CreativeSize(sizeId));
                    }
                }
        );

        return result.build();
    }

    private YandexCreativeQuery createYandexCreativeQuery(YandexCreativeSelector selector) {
        return new YandexCreativeQuery()
            .restrict()
            .creatives(selector.getCreatives())
            .addDefaultOrder();
    }

    private Map<Long, List<CreativeOptionValue>> readOptionValues(final Collection<Long> creativeIds) {
        final CollectionUtils.MultiMapBuilder<Long, CreativeOptionValue> result = new CollectionUtils.MultiMapBuilder<>();

        final Map<Long, Option> options = CollectionUtils.lazyMap(new Converter<Long, Option>() {
            @Override
            public Option item(Long id) {
                return em.find(Option.class, id);
            }
        });

        final boolean externalUser = currentUserService.isExternal();

        jdbcTemplate.query(
                "SELECT cov.CREATIVE_ID, cov.OPTION_ID, cov.VALUE FROM CREATIVEOPTIONVALUE cov WHERE cov.CREATIVE_ID = any(?)",
                new Object[]{jdbcTemplate.createArray("int", creativeIds)},
                new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        long optionId = rs.getLong("OPTION_ID");
                        long creativeId = rs.getLong("CREATIVE_ID");

                        Option option = options.get(optionId);

                        if (externalUser && option.isInternalUse()) {
                            return;
                        }

                        if (OptionGroupType.Advertiser != option.getOptionGroup().getType()) {
                            return;
                        }

                        CreativeOptionValue cov = new CreativeOptionValue(new CreativeOptionValuePK(creativeId, optionId));
                        cov.setOption(option);
                        cov.setValue(rs.getString("VALUE"));

                        result.put(creativeId, cov);
                    }
                }
        );

        return result.build();
    }

    private boolean isTextCreative(Creative creative) {
        if (creative == null) {
            return false;
        }

        return templateService.findTextTemplateId().equals(creative.getTemplate().getId())
                && creativeSizeService.findTextSizeId().equals(creative.getSize().getId());
    }

    private void readCreativeTnsArticles(Map<Long, YandexCreativeTO> creatives) {
        if (creatives.isEmpty()) {
            return;
        }

        String sql = "select ccc.creative_id, rtc.rtb_category_key from creativecategory_creative ccc " +
                " join creativecategory cc on ccc.creative_category_id = cc.creative_category_id " +
                " join rtbcategory rtc on rtc.creative_category_id = cc.creative_category_id " +
                " join rtbconnector rtb on rtc.rtb_id=rtb.rtb_id " +
                " where ccc.CREATIVE_ID IN (:creativeIds) and rtb.name = :rtbName";
        //noinspection unchecked
        List<Object[]> resultList = em.createNativeQuery(sql)
                .setParameter("creativeIds", creatives.keySet())
                .setParameter("rtbName", "YANDEX")
                .getResultList();

        for (Object[] objects : resultList) {
            Long creativeId = ((Number) objects[0]).longValue();
            String categoryId = (String) objects[1];
            YandexCreativeTO creativeTo = creatives.get(creativeId);
            creativeTo.addTnsArticle(categoryId);
        }

        for (YandexCreativeTO creativeTO : creatives.values()) {
            CreativeTemplate template = em.find(CreativeTemplate.class, creativeTO.getCreative().getTemplate().getId());
            for (CreativeCategory category : template.getCategories()) {
                for (RTBCategory rtbCategory : category.getRtbCategories()) {
                    if (rtbCategory.getRtbConnector().getName().equalsIgnoreCase("YANDEX")) {
                        creativeTO.addTnsArticle(rtbCategory.getName());
                        break;
                    }
                }
            }
        }
    }

    private Map<Long, List<CreativeCategory>> readCreativeCategories(final Collection<Long> creativeIds) {
        final CollectionUtils.MultiMapBuilder<Long, CreativeCategory> result = new CollectionUtils.MultiMapBuilder<>();

        jdbcTemplate.query(
                "SELECT ccc.CREATIVE_ID, ccc.CREATIVE_CATEGORY_ID FROM CREATIVECATEGORY_CREATIVE ccc WHERE ccc.CREATIVE_ID = ANY(?)",
                new Object[] {jdbcTemplate.createArray("int", creativeIds)},
                new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        Long creativeId = rs.getLong("CREATIVE_ID");
                        Long categoryId = rs.getLong("CREATIVE_CATEGORY_ID");

                        result.put(creativeId, new CreativeCategory(categoryId));
                    }
                }
        );

        return result.build();
    }

    @Override
    @Interceptors({ CaptureChangesInterceptor.class })
    @Validate(validation = "Creative.integrity", parameters = { "#operations", "'creative'" })
    public OperationsResult perform(Operations<Creative> operations) {
        List<Long> res = new ArrayList<>(operations.getOperations().size());

        fetch(operations);

        prepareOperations(operations);

        resolveOptionIds(operations).throwIfHasViolations();

        validationService.validate("Creative.merge", operations).throwIfHasViolations();

        for (Operation<Creative> operation : operations.getOperations()) {
            res.add(processMergeOperation(operation));
        }

        em.flush();

        return new OperationsResult(res);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "#account")
    public ValidationResultTO validateAll(CreativeCsvReaderResult readerResult, Account account) {
        Collection<Creative> creatives = readerResult.getCreatives();
        for (Creative creative : creatives) {
            if (!UploadUtils.getUploadContext(creative).isFatal()) {
                prepareOperation(creative, creative.getId() == null ? OperationType.CREATE : OperationType.UPDATE);
            }
        }
        prepareCategories(creatives);
        prepareTagSizes(creatives);
        validateAllImpl(creatives, account);
        ValidationResultTO result = fillValidationResult(creatives);
        String validationId = saveResults(userService.getMyUser().getAccount().getId(), readerResult);
        result.setId(validationId);
        return result;
    }

    @Override
    @Interceptors({CaptureChangesInterceptor.class})
    public void createOrUpdateAll(String validationResultId) {
        CreativeCsvReaderResult result = getValidatedResults(validationResultId);
        List<Creative> creatives = result.getCreatives();
        if (creatives.isEmpty()) {
            return;
        }

        CreateOrUpdateAllProcessor processor = new CreateOrUpdateAllProcessor(creatives, creatives.get(0).getAccount().getId());
        processor.process();
    }

    private void prepareOperations(Operations<Creative> operations) {
        for (Operation<Creative> operation : operations) {
            prepareOperation(operation.getEntity(), operation.getOperationType());
        }
    }

    private void prepareOperation(Creative creative, OperationType operationType) {
        if (!creative.isChanged("options")) {
            return;
        }

        CreativeTemplate template = getTemplate(creative, operationType);
        if (template != null) {
            fillGroupStates(creative, template.getAdvertiserOptionGroups());
            if (creative.getTemplate() == null) {
                creative.setTemplate(template);
                creative.unregisterChange("template");
            }
        }

        CreativeSize size = getSize(creative, operationType);
        if (size != null) {
            fillGroupStates(creative, size.getAdvertiserOptionGroups());
            if (creative.getSize() == null) {
                creative.setSize(size);
                creative.unregisterChange("size");
            }
        }

        if (isTextCreative(creative)) {
            if (OperationType.UPDATE == operationType) {
                Creative existing = em.find(Creative.class, creative.getId());
                if (existing == null) {
                    return;
                }
                if (!creative.isChanged("sizeTypes")) {
                    PersistenceUtils.initialize(existing.getSizeTypes());
                    creative.setSizeTypes(existing.getSizeTypes());
                }
                if (!creative.isChanged("tagSizes")) {
                    PersistenceUtils.initialize(existing.getTagSizes());
                    creative.setTagSizes(existing.getTagSizes());
                }
                if (!creative.isChanged("enableAllAvailableSizes")) {
                    creative.setEnableAllAvailableSizes(existing.isEnableAllAvailableSizes());
                }
            } else {
                if (!creative.isChanged("enableAllAvailableSizes")) {
                    creative.setEnableAllAvailableSizes(false);
                }
            }
            AdvertiserAccount advertiserAccount = getAccount(creative, operationType);
            if (advertiserAccount != null) {
                Set<CreativeSize> usedSizes = campaignCreativeService.getEffectiveTagSizes(creative, advertiserAccount);
                for (CreativeSize creativeSize : usedSizes) {
                    fillGroupStates(creative, creativeSize.getAdvertiserOptionGroups());
                }
            }
        } else {
            if (OperationType.UPDATE == operationType) {
                Creative existing = em.find(Creative.class, creative.getId());
                if (existing == null) {
                    return;
                }
                creative.setExpandable(existing.isExpandable());
                creative.setExpansion(existing.getExpansion());
            }
        }
    }

    private ValidationContext resolveOptionIds(Operations<Creative> operations) {
        ValidationContext context = ValidationUtil.validationContext().build();

        int index = 0;
        for (Operation<Creative> operation : operations) {
            Creative creative = operation.getEntity();
            if (!creative.isChanged("options")) {
                continue;
            }

            ValidationContext operationContext = context.createSubContext(operation, "operations", index++);
            ValidationContext creativeContext = operationContext.createSubContext(creative, "creative");

            Set<Option> allowedOptions = new HashSet<>();

            CreativeTemplate template = getTemplate(creative, operation.getOperationType());
            if (template != null) {
                allowedOptions.addAll(template.getAdvertiserOptions());
            }

            CreativeSize size = getSize(creative, operation.getOperationType());
            if (size != null) {
                allowedOptions.addAll(size.getAdvertiserOptions());
            }

            if (isTextCreative(creative)) {
                AdvertiserAccount advertiserAccount = getAccount(creative, operation.getOperationType());
                if (advertiserAccount != null) {
                    Set<CreativeSize> usedSizes = campaignCreativeService.getEffectiveTagSizes(creative, advertiserAccount);
                    for (CreativeSize tagSize : usedSizes) {
                        allowedOptions.addAll(tagSize.getAdvertiserOptions());
                    }
                }
            }

            for (CreativeOptionValue value : creative.getOptions()) {
                String token = value.getOption().getToken();
                if (value.getOption().getId() == null && token != null) {
                    Set<Long> optionIds = findOptionIdsByToken(allowedOptions, token);
                    if (optionIds.size() > 1) {
                        creativeContext.addConstraintViolation("creative.option.token.multiple")
                                .withPath("options").withParameters(token);
                    } else if (optionIds.isEmpty()) {
                        creativeContext.addConstraintViolation("creative.option.token.notFound")
                                .withPath("options").withParameters(token);
                    } else {
                        value.getOption().setId(optionIds.iterator().next());
                    }
                }
            }
        }
        return context;
    }

    private Set<Long> findOptionIdsByToken(Set<Option> options, String token) {
        Set<Long> ids = new HashSet<>();
        for (Option option : options) {
            if (token.equals(option.getToken())) {
                ids.add(option.getId());
            }
        }
        return ids;
    }

    private AdvertiserAccount getAccount(Creative creative, OperationType operationType) {
        if (creative == null) {
            return null;
        }

        if (operationType == OperationType.CREATE || creative.isChanged("account")) {
            if (creative.getAccount() == null || creative.getAccount().getId() == null) {
                return null;
            }
            return em.find(AdvertiserAccount.class, creative.getAccount().getId());
        } else if (operationType == OperationType.UPDATE) {
            if (creative.getId() == null) {
                return null;
            }
            Creative existing = em.find(Creative.class, creative.getId());
            if (existing == null) {
                return null;
            }
            return existing.getAccount();
        }
        return null;
    }

    private CreativeTemplate getTemplate(Creative creative, OperationType operationType) {
        if (creative == null) {
            return null;
        }

        if (operationType == OperationType.CREATE || creative.isChanged("template")) {
            if (creative.getTemplate() == null || creative.getTemplate().getId() == null) {
                return null;
            }
            return em.find(CreativeTemplate.class, creative.getTemplate().getId());
        } else if (operationType == OperationType.UPDATE) {
            if (creative.getId() == null) {
                return null;
            }
            Creative existing = em.find(Creative.class, creative.getId());
            if (existing == null) {
                return null;
            }

            if (creative.getTemplate() == null) {
                creative.setTemplate(existing.getTemplate());
            }

            return existing.getTemplate();
        }
        return null;
    }

    private CreativeSize getSize(Creative creative, OperationType operationType) {
        if (creative == null) {
            return null;
        }

        if (operationType == OperationType.CREATE || creative.isChanged("size")) {
            if (creative.getSize() == null || creative.getSize().getId() == null) {
                return null;
            }
            return em.find(CreativeSize.class, creative.getSize().getId());
        } else if (operationType == OperationType.UPDATE) {
            if (creative.getId() == null) {
                return null;
            }
            Creative existing = em.find(Creative.class, creative.getId());
            if (existing == null) {
                return null;
            }

            if (creative.getSize() == null) {
                creative.setSize(existing.getSize());
            }

            return existing.getSize();
        }
        return null;
    }

    private void fillGroupStates(Creative creative, Set<OptionGroup> groups) {
        Set<CreativeOptGroupState> groupStates = creative.getGroupStates();
        for (OptionGroup group : groups) {
            CreativeOptGroupState gs = new CreativeOptGroupState();
            gs.setId(new CreativeOptGroupStatePK(group.getId(), creative.getId() == null ? 0 : creative.getId()));
            gs.setEnabled(true);
            gs.setCollapsed(group.getCollapsability() == OptionGroup.Collapsability.COLLAPSED_BY_DEFAULT);
            groupStates.add(gs);
        }
    }

    private Long processMergeOperation(Operation<Creative> mergeOperation) {
        Creative creative = mergeOperation.getEntity();
        creative.unregisterChange("qaStatus");

        switch (mergeOperation.getOperationType()) {
        case CREATE:
            creative.setId(null);
            return create(creative);
        case UPDATE:
            update(creative);
            return creative.getId();
        }

        throw new RuntimeException(mergeOperation.getOperationType() + " not supported!");
    }

    private void fetch(Operations<Creative> operations) {
        //todo
    }

    @Override
    @Interceptors({ CaptureChangesInterceptor.class })
    public void setClickUrl(List<Long> creativeIds, final String url) {
        // pre-validate
        ValidationContext context = ValidationUtil.createContext();
        urlValidations.validateUrl(context, url, "clickUrl", false);
        context.throwIfHasViolations();

        perform(creativeIds, new ClickUrlOperation() {
            @Override
            protected void perform(Option option, String existingValue, CreativeOptionValue toUpdate) {
                toUpdate.setValue(url);
            }
        });
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public void appendClickUrl(List<Long> creativeIds, final String append) {
        perform(creativeIds, new ClickUrlOperation() {
            @Override
            protected void perform(Option option, String existingValue, CreativeOptionValue toUpdate) {
                if (StringUtil.isPropertyEmpty(existingValue)) {
                    return;
                }
                toUpdate.setValue(existingValue.concat(append));
            }
        });
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public void findReplaceClickUrl(List<Long> creativeIds, final String find, final String replace) {
        perform(creativeIds, new ClickUrlOperation() {
            @Override
            protected void perform(Option option, String existingValue, CreativeOptionValue toUpdate) {
                if (StringUtil.isPropertyEmpty(existingValue)) {
                    return;
                }

                String url = StringUtils.replace(existingValue, find, replace);
                toUpdate.setValue(url);
            }
        });
    }

    private void perform(List<Long> creativeIds, BulkOperation<Creative> operation) {
        for (Long creativeId : creativeIds) {
            final Creative existing = find(creativeId);
            Creative toUpdate = new Creative(existing.getId());
            operation.perform(existing, toUpdate);

            ValidationContext context = ValidationUtil.createContext();
            restrictionService.validateRestriction(context, "AdvertiserEntity.update", existing);
            context.throwIfHasViolations();

            updateInternal(toUpdate);
        }

        CacheService cacheService = ServiceLocator.getInstance().lookup(CacheService.class);
        cacheService.evictCollection(Creative.class, "options");
    }

    private abstract class ClickUrlOperation implements BulkOperation<Creative> {
        private Long textTemplateId;

        private ClickUrlOperation() {
            textTemplateId = templateService.findTextTemplateId();
        }

        @Override
        public void perform(Creative existing, Creative toUpdate) {
            // all editable CLICK_URL options
            Set<Option> clickUrls = findClickUrls(existing);
            if (clickUrls.isEmpty()) {
                return;
            }

            Map<Option, CreativeOptionValue> values = new HashMap<>();
            // default
            for (Option option : clickUrls) {
                values.put(option, null);
            }

            // real value
            for (CreativeOptionValue optionValue : existing.getOptions()) {
                if (clickUrls.contains(optionValue.getOption())) {
                    values.put(optionValue.getOption(), optionValue);
                }
            }

            Map<Option, CreativeOptionValue> updatedValues = new HashMap<>();
            for (Map.Entry<Option, CreativeOptionValue> entry : values.entrySet()) {
                Option option = entry.getKey();
                CreativeOptionValue optionValue = entry.getValue();
                CreativeOptionValue optionValueToUpdate = new CreativeOptionValue(existing.getId(), option.getId());
                optionValueToUpdate.setOption(option);
                String existingValue;
                if (optionValue == null) {
                    existingValue = option.getDefaultValue();
                } else {
                    existingValue = optionValue.getValue();
                    optionValueToUpdate.setVersion(optionValue.getVersion());
                }

                perform(option, existingValue, optionValueToUpdate);

                if (ObjectUtils.equals(option.getDefaultValue(), optionValueToUpdate.getValue())) {
                    // keep default
                    updatedValues.put(option, null);
                } else {
                    // to be updated
                    ValidationContext context = ValidationUtil.createContext();
                    context = context.createSubContext(existing, "clickUrl");

                    Template template = option.getOptionGroup().getTemplate();
                    if (template != null && template.getId().equals(textTemplateId)) {
                        creativeValidations.validateTextOption(context, null, optionValueToUpdate, TextCreativeOption.CLICK_URL);
                    } else {
                        optionValueValidations.validateOptionValue(context, optionValueToUpdate, existing.getAccount());
                    }

                    context.throwIfHasViolations();

                    updatedValues.put(option, optionValueToUpdate);
                }
            }

            if (updatedValues.isEmpty()) {
                return;
            }

            AdvertiserAccount account = em.getReference(AdvertiserAccount.class, existing.getAccount().getId());

            Set<CreativeOptionValue> toUpdateOptions = toUpdate.getOptions();
            for (CreativeOptionValue optionValue : existing.getOptions()) {
                Option option = optionValue.getOption();
                if (!updatedValues.containsKey(option)) {
                    // keep all other option values
                    CreativeOptionValue newValue = new CreativeOptionValue(existing.getId(), option.getId());
                    newValue.setValue(optionValue.isFile() ? getPreparedFileOptionValue(account, optionValue) : optionValue.getValue());
                    newValue.setVersion(optionValue.getVersion());
                    newValue.setOption(option);
                    toUpdateOptions.add(newValue);
                }
            }

            for (CreativeOptionValue optionValue : updatedValues.values()) {
                if (optionValue != null) {
                    toUpdateOptions.add(optionValue);
                }
            }
        }

        private Set<Option> findClickUrls(Creative creative) {
            Set<Option> options = new HashSet<>();

            Set<CreativeSize> availableSizes = new HashSet<>();
            for (TemplateFile templateFile : creative.getTemplate().getTemplateFiles()) {
                if (Status.DELETED != templateFile.getCreativeSize().getStatus()) {
                    availableSizes.add(templateFile.getCreativeSize());
                }
            }
            availableSizes.retainAll(creative.getAccount().getAccountType().getCreativeSizes());

            for (SizeType sizeType : creative.getSizeTypes()) {
                for (CreativeSize size : sizeType.getSizes()) {
                    if (availableSizes.contains(size)) {
                        addClickUrlOptions(options, size.getAdvertiserOptions(), creative.getGroupStates());
                    }
                }
            }

            for (CreativeSize size : creative.getTagSizes()) {
                if (availableSizes.contains(size)) {
                    addClickUrlOptions(options, size.getAdvertiserOptions(), creative.getGroupStates());
                }
            }

            addClickUrlOptions(options, creative.getSize().getAdvertiserOptions(), creative.getGroupStates());
            addClickUrlOptions(options, creative.getTemplate().getAdvertiserOptions(), creative.getGroupStates());

            return options;
        }

        private void addClickUrlOptions(Set<Option> options, Collection<Option> allOptions, Set<CreativeOptGroupState> groupStates) {
            for (final Option option : allOptions) {
                if (!option.getToken().equals(CreativeToken.CRCLICK.getName())) {
                    continue;
                }

                CreativeOptGroupState groupState = CollectionUtils.find(groupStates, new Filter<CreativeOptGroupState>() {
                    @Override
                    public boolean accept(CreativeOptGroupState element) {
                        return element.getGroupId().equals(option.getOptionGroup().getId());
                    }
                });

                if (!OptionGroupStateHelper.isGroupEnabled(option.getOptionGroup(), groupState)) {
                    continue;
                }

                options.add(option);
            }
        }

        protected abstract void perform(Option option, String existingValue, CreativeOptionValue toUpdate);
    }

    @Override
    public void validateAll(Long advertiserId, Collection<Creative> creatives) {
        if (creatives.isEmpty()) {
            return;
        }

        Map<String, Long> existingCreatives = getExistingCreativesByHash(advertiserId);

        for (Creative creative : creatives) {
            UploadContext uploadContext = UploadUtils.getUploadContext(creative);

            if (creative.getId() != null) {
                uploadContext.mergeStatus(UploadStatus.UPDATE);
            } else {
                String hash = calculateHash(creative);
                Long existingCreativeId = existingCreatives.get(hash);
                if (existingCreativeId == null) {
                    uploadContext.mergeStatus(UploadStatus.NEW);
                } else {
                    creative.setId(existingCreativeId);
                    uploadContext.mergeStatus(UploadStatus.UPDATE);
                }

            }

            // validate fields
            if (!uploadContext.isFatal()) {
                ValidationContext context = validationService.validate(
                    ValidationStrategies.exclude(uploadContext.getWrongPaths()), "Creative.createOrUpdate", creative, advertiserId);
                UploadUtils.setErrors(creative, context.getConstraintViolations());
            }
        }
    }

    private Map<String, Long> getExistingCreativesByHash(Long advertiserId) {
        Map<String, Long> creativesByHash = new HashMap<>();

        Query query = em.createNativeQuery("select optv.value, optv.creative_id from creativeoptionvalue optv join creative cr on optv.creative_id = cr.creative_id " +
                " where  cr.account_id = :accountId and cr.status = 'A'  and optv.option_id = :optionId order by optv.version desc")
            .setParameter("accountId", advertiserId)
            .setParameter("optionId", optionService.findByTokenFromTextTemplate(CreativeToken.CREATIVE_HASH.getName()).getId());

        List<Object[]> sqlResult = query.getResultList();
        for (Object[] result : sqlResult) {
            creativesByHash.put((String) result[0], ((Number) result[1]).longValue());
        }
        return creativesByHash;
    }

    @Override
    public String calculateHash(Creative creative) {
        TextCreativeOption[] textCreativeOptions = TextCreativeOption.values();
        Option[] textOptions = new Option[textCreativeOptions.length];
        for (int i = 0; i < textCreativeOptions.length; i++) {
            TextCreativeOption textCreativeOption = textCreativeOptions[i];
            textOptions[i] = optionService.findByTokenFromTextTemplate(textCreativeOption.getToken());
        }

        StringBuilder source = new StringBuilder();
        for (CreativeOptionValue optionValue : creative.getOptions()) {
            for (Option option : textOptions) {
                if (option.getId().equals(optionValue.getOptionId())) {
                    if (!OptionValueUtils.isDefaultValue(option, optionValue.getValue())) {
                        source.append(option.getToken());
                        source.append(':');
                        source.append(optionValue.getValue());
                    }
                }
            }
            }

        return DigestUtils.md5Hex(source.toString());
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('Account', #accountId)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void createOrUpdateAll(Long accountId, List<Creative> creatives) {
        new CreateOrUpdateAllProcessor(creatives, accountId).process();
    }

    private void fetchCreatives(Long accountId, List<Creative> creatives) {
        Collection<Long> creativeIds = new ArrayList<>(creatives.size());

        for (Creative creative : creatives) {
            UploadUtils.throwIfErrors(creative);
            if (creative.getId() != null) {
                creativeIds.add(creative.getId());
            }
        }

        if (!creativeIds.isEmpty()) {
            em.createQuery("select c  from Creative c " +
                    " where c.id in (:creativeIds) and c.status <> 'D'")
                .setParameter("creativeIds", creativeIds).getResultList();
        }
    }

    private void updateInternal(Creative creative) {
        Creative existingCreative = find(creative.getId());
        if (!creative.isChanged("version")) {
            creative.setVersion(existingCreative.getVersion());
        }

        prePersist(creative, existingCreative);

        boolean qaStatusChanged = creative.isChanged("qaStatus") && creative.getQaStatus() != existingCreative.getQaStatus();

        if (creative.isChanged("options", "groupStates")) {
            // If http safe option is really changed it will be recalculated later by invoking
            // htmlOptionHelper.processHttpSafeOption(existingCreative);
            // The reason of transfer: creative.version MUST NOT be updated if nothing is changed
            transferHttpSafeValueFromExisting(existingCreative, creative);
            optionsAndStatesPersister.merge(existingCreative, creative);
        }

        boolean isCategoriesChanged = creative.isChanged("categories");
        if (isCategoriesChanged) {
            updateCreativeCategories(creative);
        }

        auditService.audit(existingCreative, ActionType.UPDATE);
        existingCreative = em.merge(creative);

        boolean isChanged = isChanged(existingCreative);
        if (isChanged) {
            statusService.makePendingOnChange(existingCreative, existingCreative.isChanged("status"));
            PersistenceUtils.performHibernateLock(em, existingCreative);
        }
        if (isChanged || isCategoriesChanged) {
            approvalService.makePendingOnChange(existingCreative);
        }

        if (qaStatusChanged) {
            changeQaStatus(creative, creative.getQaStatus());
        }

        previewService.deletePreview(existingCreative);

        if (isChanged || isCategoriesChanged) {
            creativeService.resetApprovedCreativeExclusions(existingCreative.getId());
        }

        displayStatusService.update(existingCreative);

        htmlOptionHelper.processHttpSafeOption(existingCreative);
        htmlOptionHelper.processDynamicFiles(existingCreative);
        htmlFileHelper.updateFilesOnCommit(existingCreative);
    }

    private void transferHttpSafeValueFromExisting(Creative existing, Creative updated) {
        Option httpSafeOption = htmlOptionHelper.findHttpSafeOption(existing);
        if (httpSafeOption == null) {
            return;
        }

        CreativeOptionValue httpSafeValue = updated.findOptionValue(httpSafeOption);
        if (httpSafeValue == null) {
            CreativeOptionValue existingHttpSafeValue = existing.findOptionValue(httpSafeOption);
            if (existingHttpSafeValue != null) {
                CreativeOptionValue detachedOptionValue = new CreativeOptionValue(existingHttpSafeValue.getId());
                detachedOptionValue.setOption(existingHttpSafeValue.getOption());
                detachedOptionValue.setValue(existingHttpSafeValue.getValue());
                detachedOptionValue.setVersion(existingHttpSafeValue.getVersion());

                updated.getOptions().add(detachedOptionValue);
            }
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view")
    public Result<YandexCreativeTO> getYandexCreativeTO(YandexCreativeSelector creativeSelector) {
        if (!currentUserService.isExternal()
                && CollectionUtils.isNullOrEmpty(creativeSelector.getCreatives())) {
            throw new BusinessException("Creative IDs must be not null!");
        }

        PartialList<Creative> creatives = createYandexCreativeQuery(creativeSelector)
            .executor(executorService)
            .partialList(creativeSelector.getPaging());

        Map<Long, AdvertiserAccount> advertisers = CollectionUtils.lazyMap(new Converter<Long, AdvertiserAccount>() {
            @Override
            public AdvertiserAccount item(Long id) {
                return em.find(AdvertiserAccount.class, id);
            }
        });

        HashMap<Long, YandexCreativeTO> creativesById = new HashMap<>();

        for (Creative creative : creatives) {
            creative.setAccount(advertisers.get(creative.getAccount().getId()));
            YandexCreativeTO creativeTo = new YandexCreativeTO(creative);
            creativesById.put(creative.getId(), creativeTo);
        }

        readCreativeTnsArticles(creativesById);

        return new Result<>(new ArrayList<>(creativesById.values()), creativeSelector.getPaging());
    }

    private ValidationResultTO fillValidationResult(Collection<Creative> creatives) {
        ValidationResultTO validationResult = new ValidationResultTO();
        for (Creative creative : creatives) {
            addValidationResult(creative, validationResult, validationResult.getCreatives());
        }
        return validationResult;
    }

    private void addValidationResult(EntityBase entity, ValidationResultTO validationResult, Stats stats) {
        UploadContext context = entity.getProperty(UploadUtils.UPLOAD_CONTEXT);
        switch (context.getStatus()) {
            case NEW:
                stats.setCreated(stats.getCreated() + 1);
                break;
            case UPDATE:
                stats.setUpdated(stats.getUpdated() + 1);
                break;
            case REJECTED:
                validationResult.setLineWithErrors(validationResult.getLineWithErrors() + 1);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private String saveResults(Long accountId, CreativeCsvReaderResult readerResult) {
        for(Creative creative : readerResult.getCreatives()) {
            UploadContext uploadContext = UploadUtils.getUploadContext(creative);
            uploadContext.getErrors(); // forced flush
        }
        UUID uuid = UUID.randomUUID();
        FileSystem fs = getBulkPP(accountId).createFileSystem();
        ObjectOutputStream oos = null;
        try {
            OutputStream os = fs.openFile(getFileName(uuid, "creatives"));
            oos = new ObjectOutputStream(os);
            oos.writeObject(readerResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(oos);
        }
        return uuid.toString();
    }

    private String getFileName(String validationResultId, String suffix) {
        return getFileName(UUID.fromString(validationResultId), suffix);
    }

    private String getFileName(UUID uuid, String suffix) {
        return uuid.toString() + "." + suffix;
    }

    private PathProvider getBulkPP(Long accountId) {
        return pathProviderService.getBulkUpload().getNested(accountId.toString(), OnNoProviderRoot.AutoCreate);
    }

    private void validateAllImpl(Collection<Creative> creatives, Account account) {
        if (creatives.isEmpty()) {
            return;
        }

        Set<Long> creativeIds = new HashSet<>(creatives.size());
        for (Creative creative : creatives) {
            UploadContext uploadContext = UploadUtils.getUploadContext(creative);
            if (!creative.getAccount().getId().equals(account.getId())) {
                uploadContext.addError("errors.operation.not.permitted");
                continue;
            }

            String validationName;
            OperationType operationType;
            if (creative.getId() != null) {
                uploadContext.mergeStatus(UploadStatus.UPDATE);
                if (!creativeIds.add(creative.getId())) {
                    uploadContext
                            .addError("creative.upload.error.duplicateCreativeId")
                            .withPath("id");
                }
                validationName = "Creative.update";
                operationType = OperationType.UPDATE;
                Creative existingCreative = em.find(Creative.class, creative.getId());
                if (existingCreative != null) {
                    creative.setVersion(existingCreative.getVersion());
                } else {
                    uploadContext
                            .addFatal("errors.entity.notFound")
                            .withPath("id");
                }
            } else {
                uploadContext.mergeStatus(UploadStatus.NEW);
                validationName = "Creative.create";
                operationType = OperationType.CREATE;
            }

            if (uploadContext.isFatal()) {
                continue;
            }

            ValidationContext context = ValidationUtil.createContext();
            advertiserEntityRestrictions.canMerge(context, creative, operationType);
            if (context.hasViolations()) {
                UploadUtils.setErrors(creative, context.getConstraintViolations());
                continue;
            }

            context = validationService.validate(
                    ValidationStrategies.exclude(uploadContext.getWrongPaths()),
                    validationName,
                    creative);
            UploadUtils.setErrors(creative, context.getConstraintViolations());
        }
    }

    private void prepareCategories(Collection<Creative> creatives) {
        Map<String, Long> contentCategoriesMap = getCategoryIdsMap(CreativeCategoryType.CONTENT);
        Map<String, Long> visualCategoriesMap = getCategoryIdsMap(CreativeCategoryType.VISUAL);

        for (Creative creative : creatives) {
            for (CreativeCategory category : creative.getCategories()) {
                Long categoryId = category.getType() == CreativeCategoryType.CONTENT ?
                        contentCategoriesMap.get(category.getDefaultName()) : visualCategoriesMap.get(category.getDefaultName());
                if (categoryId == null) {
                    UploadUtils.getUploadContext(creative)
                            .addFatal("creative.upload.error.invalidCategory")
                            .withParameters(category.getDefaultName());
                    continue;
                }

                category.setId(categoryId);
            }

            // Restoring tags
            if (creative.getId() != null) {
                Creative existing = em.find(Creative.class, creative.getId());
                if (existing != null) {
                    Set<CreativeCategory> allCategories = creative.getCategories();
                    for (CreativeCategory creativeCategory : existing.getCategories()) {
                        if (creativeCategory.getType() == CreativeCategoryType.TAG) {
                            CreativeCategory cc = new CreativeCategory(creativeCategory.getId());
                            cc.setDefaultName(creativeCategory.getDefaultName());
                            cc.setType(creativeCategory.getType());
                            allCategories.add(cc);
                        }
                    }
                }
            }
        }
    }

    private void prepareTagSizes(Collection<Creative> creatives) {
        for (Creative creative : creatives) {
            // Restoring tagSizes options
            if (creative.getId() != null) {
                Creative existing = em.find(Creative.class, creative.getId());
                if (existing == null) {
                    continue;
                }

                CreativeSize size = creative.getSize();
                CreativeTemplate template = creative.getTemplate();

                List<CreativeOptionValue> tagSizesValues = new ArrayList<>(existing.getOptions().size());
                for (CreativeOptionValue value : existing.getOptions()) {
                    if (size != null && size.hasOption(value.getOption()) ||
                            template != null && template.hasOption(value.getOption())) {
                        // Option is not from tag sizes, but from main creative size / template
                        continue;
                    }
                    CreativeOptionValue tagSizesValue = new CreativeOptionValue(value.getId());
                    tagSizesValue.setValue(
                        value.isFile() ? getPreparedFileOptionValue(existing.getAccount(), value) : value.getValue());
                    tagSizesValue.setOption(new Option(value.getOptionId()));

                    tagSizesValues.add(tagSizesValue);
                }

                creative.getOptions().addAll(tagSizesValues);
            }
        }
    }

    private Map<String, Long> getCategoryIdsMap(CreativeCategoryType type) {
        List<CreativeCategory> resultList = findCategoriesByType(type);
        Map<String, Long> result = new HashMap<>(resultList.size());
        for (CreativeCategory category : resultList) {
            result.put(category.getDefaultName(), category.getId());
        }
        return result;
    }

    @Override
    public CreativeCsvReaderResult getValidatedResults(String validationResultId) {
        FileSystem fs = getBulkPP(userService.getMyUser().getAccount().getId()).createFileSystem();
        ObjectInputStream ois = null;
        try {
            InputStream is = fs.readFile(getFileName(validationResultId, "creatives"));
            ois = new ObjectInputStream(is);
            return (CreativeCsvReaderResult) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(ois);
        }
    }

    private boolean categoriesEqual(Creative creative, Creative existingCreative) {
        return ObjectUtils.equals(fetchCreativeCategories(creative), fetchCreativeCategories(existingCreative));
    }

    private Set<CreativeCategory> fetchCreativeCategories(Creative creative) {
        Set<CreativeCategory> templateCategories = creative.getTemplate().getCategories();
        if (templateCategories.isEmpty()) {
            return creative.getCategories();
        }

        Set<CreativeCategory> result = new HashSet<>(creative.getCategories().size() + templateCategories.size());
        result.addAll(creative.getCategories());
        result.addAll(templateCategories);
        return result;
    }

    private String getPreparedFileOptionValue(final AdvertiserAccount account, final CreativeOptionValue cov) {
        Template template = cov.getOption().getOptionGroup().getTemplate();
        if (template != null && template.isText() && TextCreativeOption.IMAGE_FILE.getToken().equals(cov.getOption().getToken())) {
            return TextAdImageUtil.getSourceFilePath(config, account, cov.getValue());
        }

        return OptionValueUtils.getFileStripped(cov);
    }

    private class CreateOrUpdateAllProcessor {
        private final List<Creative> creatives;
        private final Long accountId;

        public CreateOrUpdateAllProcessor(List<Creative> creatives, Long accountId) {
            this.creatives = creatives;
            this.accountId = accountId;
        }

        public void process() {
            if (creatives.isEmpty()) {
                return;
            }

            fetchCreatives(accountId, creatives);

            for (Creative creative : creatives) {
                if (creative.getId() == null) {
                    creative.setAccount(new AdvertiserAccount(accountId));
                    create(creative);
                } else {
                    updateInternal(creative);
                }
            }

            CacheService cacheService = ServiceLocator.getInstance().lookup(CacheService.class);
            cacheService.evictCollection(Creative.class, "options");
            cacheService.evictCollection(Creative.class, "groupStates");

            PersistenceUtils.flushAndClear(em, new Filter<Integer>() {
                @Override
                public boolean accept(Integer entitiesSize) {
                    return entitiesSize > 1000;
                }
            });
        }
    }
}
