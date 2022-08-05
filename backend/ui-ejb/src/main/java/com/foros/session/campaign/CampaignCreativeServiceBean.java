package com.foros.session.campaign;

import static com.foros.model.template.OptionValueUtils.IMAGE_RESIZED_FOLDER;
import com.foros.changes.CaptureChangesInterceptor;
import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.changeNode.ForceFieldChangeEntityChange;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.FrequencyCap;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.security.AccountType;
import com.foros.model.security.ActionType;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.OptionValueUtils;
import com.foros.model.template.TemplateFile;
import com.foros.persistence.hibernate.ManualFlushInterceptor;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessException;
import com.foros.session.CurrentUserService;
import com.foros.session.LocalizableNameEntityComparator;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.StatusAction;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.TreeFilterElementTOConverter;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.cache.CacheService;
import com.foros.session.campaign.bulk.CreativeLinkSelector;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.session.fileman.FileUtils;
import com.foros.session.fileman.OnNoProviderRoot;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.frequencyCap.FrequencyCapMerger;
import com.foros.session.query.PartialList;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.campaign.CampaignCreativeGroupQueryImpl;
import com.foros.session.query.campaign.CampaignCreativeQuery;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.DisplayStatusService;
import com.foros.session.status.StatusService;
import com.foros.session.template.TemplateService;
import com.foros.session.textad.TextAdImageUtil;
import com.foros.session.workflow.WorkflowService;
import com.foros.util.CollectionUtils;
import com.foros.util.EntityUtils;
import com.foros.util.ImageUtil;
import com.foros.util.PersistenceUtils;
import com.foros.util.SQLUtil;
import com.foros.util.UploadUtils;
import com.foros.util.command.executor.HibernateWorkExecutorService;
import com.foros.util.comparator.StatusNameTOComparator;
import com.foros.util.jpa.JpaQueryWrapper;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validate;
import com.foros.validation.strategy.ValidationStrategies;
import com.foros.validation.util.DuplicateChecker;
import com.foros.validation.util.EntityIdFetcher;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.collections.Predicate;

@Stateless(name = "CampaignCreativeService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class })
public class CampaignCreativeServiceBean implements CampaignCreativeService {
    private final static URL BAD_IMAGE_FILE = CampaignCreativeServiceBean.class.getClassLoader().getResource("com/foros/session/textad/bad_image.png");
    private static final Pattern ACCOUNT_DIR = Pattern.compile("\\d+");
    private final static Logger logger = Logger.getLogger(CampaignCreativeServiceBean.class.getName());

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private AuditService auditService;

    @EJB
    private DisplayCreativeService displayCreativeService;

    @EJB
    private CampaignCreativeGroupService creativeGroupService;

    @EJB
    private StatusService statusService;

    @EJB
    protected DisplayStatusService displayStatusService;

    @EJB
    protected UserService userService;

    @EJB
    private HibernateWorkExecutorService hibernateExecutorService;

    @EJB
    private RestrictionService restrictionService;

    @EJB
    private WorkflowService workflowService;

    @EJB
    private ValidationService validationService;

    @EJB
    private QueryExecutorService queryExecutorService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private CacheService cacheService;

    @EJB
    private TemplateService templateService;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private ConfigService config;

    private FrequencyCapMerger<CampaignCreative> frequencyCapMerger = new FrequencyCapMerger<CampaignCreative>() {
        @Override
        protected EntityManager getEm() {
            return em;
        }
    };

    public CampaignCreativeServiceBean() {
    }

    private void prePersist(CampaignCreative campaignCreative) {
        Creative creative;

        if (campaignCreative.getId() == null) {
            CampaignCreativeGroup creativeGroup = em.getReference(CampaignCreativeGroup.class, campaignCreative.getCreativeGroup().getId());
            creative = em.getReference(Creative.class, campaignCreative.getCreative().getId());

            AdvertiserAccount creativeGroupAccount = creativeGroup.getAccount();
            AdvertiserAccount creativeAccount = creative.getAccount();

            if (!creativeGroupAccount.equals(creativeAccount)) {
                throw new BusinessException("Not permitted creative.");
            }

            campaignCreative.setCreativeGroup(creativeGroup);
            campaignCreative.setCreative(creative);
            if (currentUserService.isExternal() && creative.isTextCreative()) {
                campaignCreative.unregisterChange("weight");
            }
        }

        // frequency caps
        FrequencyCap frequencyCap = campaignCreative.getFrequencyCap();
        if (frequencyCap != null && frequencyCap.isEmpty()) {
            campaignCreative.setFrequencyCap(null);
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.create", parameters = "find('CampaignCreativeGroup', #campaignCreative.creativeGroup.id)")
    @Interceptors(CaptureChangesInterceptor.class)
    @Validate(validation = "CampaignCreative.create", parameters = "#campaignCreative")
    public Long create(CampaignCreative campaignCreative) {
        createInternal(campaignCreative);
        return campaignCreative.getId();
    }

    private void createInternal(CampaignCreative campaignCreative) {
        campaignCreative.setStatus(Status.ACTIVE);
        campaignCreative.setDisplayStatus(CampaignCreative.LIVE);

        prePersist(campaignCreative);
        em.persist(campaignCreative);
        CampaignCreativeGroup persistentCreativeGroup = campaignCreative.getCreativeGroup();
        auditService.audit(persistentCreativeGroup, ActionType.UPDATE);

        addCampaignCreativeToGroup(persistentCreativeGroup, campaignCreative);

        displayStatusService.update(campaignCreative);
        displayStatusService.update(campaignCreative.getCreativeGroup());
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.createBulk", parameters = "#ccgIds")
    @Interceptors(CaptureChangesInterceptor.class)
    public Long createCreativeWithLinks(Creative creative, CampaignCreative campaignCreative, Collection<Long> ccgIds) throws IOException {
        campaignCreative.setCreative(creative);
        ValidationContext createContext = validationService.validate("CampaignCreative.createCreativeWithLinks", creative, campaignCreative);
        createContext.throwIfHasViolations();

        displayCreativeService.create(creative);

        Collection<CampaignCreativeGroup> groups = new ArrayList<>(ccgIds.size());
        if (!CollectionUtils.isNullOrEmpty(ccgIds)) {
            List<CampaignCreativeGroup> list = new CampaignCreativeGroupQueryImpl()
                .creativeGroups(ccgIds)
                .executor(queryExecutorService)
                .list();
            groups.addAll(list);
        }
        for (CampaignCreativeGroup group : groups) {
            CampaignCreative newCampaignCreative = new CampaignCreative();
            newCampaignCreative.setCreative(creative);
            newCampaignCreative.setCreativeGroup(group);
            newCampaignCreative.setWeight(campaignCreative.getWeight());
            newCampaignCreative.setFrequencyCap(campaignCreative.getFrequencyCap() != null ? EntityUtils.clone(campaignCreative.getFrequencyCap()) : null);
            newCampaignCreative.setVersion(creative.getVersion());

            create(newCampaignCreative);
        }

        return creative.getId();
    }

    private CampaignCreative createDefaultCampaignCreative(Creative creative, CampaignCreativeGroup creativeGroup) {
        CampaignCreative campaignCreative = new CampaignCreative();
        campaignCreative.setCreative(creative);
        campaignCreative.setCreativeGroup(creativeGroup);
        campaignCreative.setWeight(CampaignCreative.DEFAULT_WEIGHT);
        campaignCreative.setFrequencyCap(null);
        campaignCreative.setVersion(creative.getVersion());
        return campaignCreative;
    }

    @Override
    @Interceptors({ ManualFlushInterceptor.class, CaptureChangesInterceptor.class })
    public void createAll(Long advertiserId, Collection<Long> creativeIds, Collection<Long> groupIds, boolean isDisplay) {
        Collection<Creative> creatives = new ArrayList<>(creativeIds.size());
        if (!CollectionUtils.isNullOrEmpty(creativeIds)) {
            Query query = em.createQuery("SELECT c FROM Creative c WHERE c.id in :creativeIds");
            query.setParameter("creativeIds", creativeIds);
            //noinspection unchecked
            creatives.addAll(query.getResultList());
        }

        Collection<CampaignCreativeGroup> groups = new ArrayList<>(groupIds.size());
        if (!CollectionUtils.isNullOrEmpty(groupIds)) {
            List<CampaignCreativeGroup> list = new CampaignCreativeGroupQueryImpl()
                .creativeGroups(groupIds)
                .executor(queryExecutorService)
                .list();
            groups.addAll(list);
        }

        ValidationContext createContext = validationService.validate("CampaignCreative.createAll", advertiserId, creativeIds, groupIds, isDisplay);
        createContext.throwIfHasViolations();

        for (Creative creative : creatives) {
            for (CampaignCreativeGroup group : groups) {
                if (!displayCreativeService.hasCampaignCreative(creative.getId(), group.getId())) {
                    CampaignCreative campaignCreative = createDefaultCampaignCreative(creative, group);
                    create(campaignCreative);
                }
            }
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('CampaignCreative', #campaignCreative.id)")
    @Interceptors(CaptureChangesInterceptor.class)
    @Validate(validation = "CampaignCreative.update", parameters = "#campaignCreative")
    public void update(CampaignCreative campaignCreative) {
        campaignCreative.unregisterChange("id");
        campaignCreative.unregisterChange("creative");
        campaignCreative.unregisterChange("creativeGroup");
        CampaignCreative existingCC = findForUpdate(campaignCreative.getId());

        updateInternal(campaignCreative, existingCC);
    }

    private void updateInternal(CampaignCreative campaignCreative, CampaignCreative existingCC) {
        prePersist(campaignCreative);
        //audit
        CampaignCreativeGroup creativeGroup = existingCC.getCreativeGroup();
        auditService.audit(creativeGroup, ActionType.UPDATE);

        ForceFieldChangeEntityChange.addCollectionChange(
            creativeGroup,
            "campaignCreatives",
            existingCC,
            ChangeType.UNCHANGED
            );

        frequencyCapMerger.merge(campaignCreative, existingCC);

        EntityUtils.copy(existingCC, campaignCreative);
        displayStatusService.update(existingCC);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.delete", parameters = "find('CampaignCreative', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void delete(Long id) {
        CampaignCreative cc = findForUpdate(id);
        delete(cc);
    }

    private void delete(CampaignCreative cc) {
        if (workflowService.getStatusWorkflow(cc).isActionAvailable(StatusAction.DELETE)) {
            statusService.delete(cc);
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.undelete", parameters = "find('CampaignCreative', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void undelete(Long id) {
        CampaignCreative cc = findForUpdate(id);
        undelete(cc);
    }

    private void undelete(CampaignCreative cc) {
        if (workflowService.getStatusWorkflow(cc).isActionAvailable(StatusAction.UNDELETE)) {
            statusService.undelete(cc);
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.inactivate", parameters = "find('CampaignCreative', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void inactivate(Long id) {
        CampaignCreative cc = findForUpdate(id);
        inactivate(cc);
    }

    private void inactivate(CampaignCreative cc) {
        if (workflowService.getStatusWorkflow(cc).isActionAvailable(StatusAction.INACTIVATE)) {
            statusService.inactivate(cc);
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.activate", parameters = "find('CampaignCreative', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void activate(Long id) {
        CampaignCreative cc = findForUpdate(id);
        activate(cc);
    }

    private void activate(CampaignCreative cc) {
        if (workflowService.getStatusWorkflow(cc).isActionAvailable(StatusAction.ACTIVATE)) {
            statusService.activate(cc);
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('CampaignCreativeGroup', #ccgId)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void activateAll(Long ccgId, Collection<Long> ids) {
        for (Long id : ids) {
            CampaignCreative campaignCreative = findForUpdate(id);
            if (campaignCreative.getCreativeGroup().getId().equals(ccgId)) {
                activate(campaignCreative);
            }
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('CampaignCreativeGroup', #ccgId)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void inactivateAll(Long ccgId, Collection<Long> ids) {
        for (Long id : ids) {
            CampaignCreative campaignCreative = findForUpdate(id);
            if (campaignCreative.getCreativeGroup().getId().equals(ccgId)) {
                inactivate(campaignCreative);
            }
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('CampaignCreativeGroup', #ccgId)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void deleteAll(Long ccgId, Collection<Long> ids) {
        for (Long id : ids) {
            CampaignCreative campaignCreative = findForUpdate(id);
            if (campaignCreative.getCreativeGroup().getId().equals(ccgId)) {
                delete(campaignCreative);
            }
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.undeleteChildren", parameters = "find('CampaignCreativeGroup', #ccgId)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void undeleteAll(Long ccgId, Collection<Long> ids) {
        for (Long id : ids) {
            CampaignCreative campaignCreative = findForUpdate(id);
            if (campaignCreative.getCreativeGroup().getId().equals(ccgId)) {
                undelete(campaignCreative);
            }
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "find('CampaignCreative', #id)")
    public CampaignCreative view(Long id) {
        return find(id);
    }

    @Override
    public CampaignCreative find(Long id) {
        CampaignCreative res = em.find(CampaignCreative.class, id);
        if (res == null) {
            throw new EntityNotFoundException("CampaignCreative with id=" + id + " not found");
        }

        return res;
    }

    @Override
    public void refresh(Long id) {
        CampaignCreative cc = find(id);
        em.refresh(cc);
    }

    private CampaignCreative findForUpdate(Long id) {
        CampaignCreative cc = find(id);
        // initialize CampaignCreatives list for change inspector
        CampaignCreativeGroup group = cc.getCreativeGroup();
        PersistenceUtils.initialize(group.getCampaignCreatives());
        return cc;
    }

    @Override
    @Restrict(restriction = "Entity.access", parameters = "find('CampaignCreativeGroup', #ccgId)")
    public List<TreeFilterElementTO> searchCreatives(Long ccgId) {
        if (ccgId == null) {
            return new ArrayList<TreeFilterElementTO>();
        }

        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT cc.cc_id id, c.name, cc.status, cc.display_status_id, false hasChildren ");
        queryString.append("FROM CampaignCreative cc, Creative c ");
        queryString.append("WHERE cc.creative_id = c.creative_id AND cc.ccg_id = ? ");
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            queryString.append(" AND cc.status <> 'D'");
        }

        List<TreeFilterElementTO> result = jdbcTemplate.query(
                queryString.toString(),
                new Object[]{ccgId},
                new TreeFilterElementTOConverter(CampaignCreative.displayStatusMap)
        );

        Collections.sort(result, new StatusNameTOComparator<TreeFilterElementTO>());
        return result;
    }

    @Override
    public List<TreeFilterElementTO> searchCreativesBySizeType(Long ccgId, Long sizeTypeId) {
        if (ccgId == null || sizeTypeId == null) {
            return new ArrayList<TreeFilterElementTO>();
        }

        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT cc.cc_id id, c.name, cc.status, cc.display_status_id, false hasChildren ");
        queryString.append("FROM CampaignCreative cc, Creative c ");
        queryString.append("WHERE cc.creative_id = c.creative_id AND cc.ccg_id = ? ");
        queryString.append("AND EXISTS (SELECT 1 FROM CreativeSize cz WHERE cz.size_type_id=? AND c.size_id = cz.size_id) ");
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            queryString.append(" AND cc.status <> 'D'");
        }

        List<TreeFilterElementTO> result = jdbcTemplate.query(
                queryString.toString(),
                new Object[]{
                        ccgId,
                        sizeTypeId
                },
                new TreeFilterElementTOConverter(CampaignCreative.displayStatusMap)
        );

        Collections.sort(result, new StatusNameTOComparator<>());
        return result;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('CampaignCreativeGroup', #ccgId)")
    public void moveCreativesToExistingSet(final Long ccgId, final List<Long> ids, final Timestamp creativeMaxVersion, final Long setNumber) {
        jdbcTemplate.withAuthContext().execute(
                "select campaigncreative.move_to_set(?::numeric,?::int[],?::timestamp)",
                setNumber,
                jdbcTemplate.createArray("int", prepareCreatives(ccgId, ids)),
                creativeMaxVersion
        );

        jdbcTemplate.scheduleEviction();
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('CampaignCreativeGroup', #ccgId)")
    public void moveCreativesToNewSet(final Long ccgId, final List<Long> ids, final Timestamp creativeMaxVersion, final Long setNumber) {
        jdbcTemplate.withAuthContext().execute(
                "select campaigncreative.insert_set(?::numeric,?::int[],?::timestamp)",
                setNumber,
                jdbcTemplate.createArray("int", prepareCreatives(ccgId, ids)),
                creativeMaxVersion
        );
        jdbcTemplate.scheduleEviction();
    }


    private Collection<Long> prepareCreatives(final Long ccgId, List<Long> ids) {
        Collection result = org.apache.commons.collections.CollectionUtils.select(ids, new Predicate() {
            @Override
            public boolean evaluate(Object id) {
                CampaignCreative campaignCreative = find((Long) id);
                return ccgId.equals(campaignCreative.getCreativeGroup().getId());
            }
        });
        //noinspection unchecked
        return result;
    }

    @Override
    public Timestamp getCreativesMaxVersionByCcgId(Long ccgId) {
        return jdbcTemplate.queryForObject("select max(version) from CampaignCreative where ccg_id = ?::int", Timestamp.class, ccgId);
    }

    @Override
    public int getCreativeSetCountByCcgId(Long ccgId) {
        return jdbcTemplate.queryForObject(
            "select coalesce(max(cc.set_number), 0) from CampaignCreative cc where cc.ccg_id = ?::int", Integer.class, ccgId);
    }

    @Override
    public boolean isBatchActionPossible(Collection<Long> ids, String action) {
        String restrictionName = "AdvertiserEntity." + action.toLowerCase();
        Collection<CampaignCreative> creatives = new JpaQueryWrapper<CampaignCreative>(em,
            "select cc from CampaignCreative cc where cc.id in :ids")
            .setPrimitiveArrayParameter("ids", ids)
            .getResultList();
        for (CampaignCreative creative : creatives) {
            if (!restrictionService.isPermitted(restrictionName, creative)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view")
    public Result<CampaignCreative> get(CreativeLinkSelector creativeLinkSelector) {
        if (!currentUserService.isExternal()
                && CollectionUtils.isNullOrEmpty(creativeLinkSelector.getAdvertiserIds())
                && CollectionUtils.isNullOrEmpty(creativeLinkSelector.getCampaigns())
                && CollectionUtils.isNullOrEmpty(creativeLinkSelector.getCreativeGroups())
                && CollectionUtils.isNullOrEmpty(creativeLinkSelector.getCreatives())
                && CollectionUtils.isNullOrEmpty(creativeLinkSelector.getCreativeLinks())) {
            throw new BusinessException("Either advertiser IDs or campaign IDs or group IDs or creative IDs or creative link IDs should be supplied!");
        }
        CampaignCreativeQuery query = new CampaignCreativeQuery()
            .restrict()
            .advertisers(creativeLinkSelector.getAdvertiserIds())
            .campaigns(creativeLinkSelector.getCampaigns())
            .creativeGroups(creativeLinkSelector.getCreativeGroups())
            .creatives(creativeLinkSelector.getCreatives())
            .campaignCreatives(creativeLinkSelector.getCreativeLinks())
            .statuses(creativeLinkSelector.getStatuses())
            .addDefaultOrder();

        PartialList<CampaignCreative> creatives = query
            .executor(queryExecutorService)
            .partialList(creativeLinkSelector.getPaging());

        em.clear();

        boolean external = currentUserService.isExternal();
        for (CampaignCreative campaignCreative : creatives) {
            if (external && campaignCreative.getCreative().isTextCreative()) {
                campaignCreative.setWeight(null);
            }
            FrequencyCap frequencyCap = campaignCreative.getFrequencyCap();
            if (frequencyCap != null) {
                frequencyCap.setVersion(null);
            }
        }

        return new Result<>(creatives, creatives.getPaging());
    }

    @Override
    @Interceptors({ CaptureChangesInterceptor.class })
    @Validate(validation = "Operations.integrity", parameters = { "#operations", "'creativeLink'" })
    public OperationsResult perform(Operations<CampaignCreative> operations) {

        List<Long> res = new ArrayList<Long>(operations.getOperations().size());

        fetch(operations);

        validationService.validate("CampaignCreative.merge", operations).throwIfHasViolations();

        for (Operation<CampaignCreative> operation : operations.getOperations()) {

            CampaignCreative campaignCreative = operation.getEntity();
            campaignCreative.unregisterChange("qaStatus");

            switch (operation.getOperationType()) {
            case CREATE:
                campaignCreative.setId(null);
                res.add(create(campaignCreative));
                break;
            case UPDATE:
                update(campaignCreative);
                res.add(campaignCreative.getId());
                break;
            default:
                throw new RuntimeException(operation.getOperationType() + " not supported!");
            }
        }

        em.flush();

        return new OperationsResult(res);
    }

    private void fetch(Operations<CampaignCreative> operations) {
        List<Long> ccIds = new ArrayList<>();
        for (Operation<CampaignCreative> operation : operations) {
            Long id = operation.getEntity().getId();
            if (id != null) {
                ccIds.add(id);
            }
        }

        if (ccIds.isEmpty()) {
            return;
        }

        em.createQuery("select cc from CampaignCreative cc " +
                " join fetch cc.creative c " +
                " join fetch c.options " +
                " where cc.id in :ids ")
            .setParameter("ids", ccIds)
            .getResultList();

        for (Operation<CampaignCreative> operation : operations) {
            CampaignCreative cc = operation.getEntity();
            if (operation.getOperationType() == OperationType.UPDATE && cc.getId() != null) {
                CampaignCreative existingCampaignCreative = em.find(CampaignCreative.class, cc.getId());
                if (existingCampaignCreative != null) {
                    cc.setCreativeGroup(existingCampaignCreative.getCreativeGroup());
                }
            }
        }
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public void createOrUpdateAll(Long ccgId, Collection<CampaignCreative> campaignCreatives) {
        if (campaignCreatives.isEmpty()) {
            return;
        }

        em.createQuery("select cc " +
                " from CampaignCreative cc " +
                " join fetch cc.creative c " +
                " where cc.creativeGroup.id = :creativeGroupId and c.status <> 'D'")
            .setParameter("creativeGroupId", ccgId).getResultList();

        for (CampaignCreative cc : new ArrayList<>(campaignCreatives)) {
            UploadUtils.throwIfErrors(cc);

            Long ccId = cc.getId();
            if (ccId == null) {
                createInternal(cc);
            } else {
                if (currentUserService.isExternal() && cc.getCreative().isTextCreative()) {
                    cc.unregisterChange("weight");
                }
                CampaignCreative existingCC = find(cc.getId());
                Creative oldCreative = existingCC.getCreative();
                if (!oldCreative.isTextCreative()) {
                    throw new BusinessException("Only Text Ads are supported here");
                }

                AdvertiserAccount account = existingCC.getAccount();
                if (!account.getAccountType().isAllowTextAdvertisingFlag()) {
                    throw new BusinessException("Text advertising is not allowed.");
                }

                updateInternal(cc, existingCC);
            }
        }
    }

    private void addCampaignCreativeToGroup(CampaignCreativeGroup ccg, CampaignCreative campaignCreative) {
        // optimize Text Ad creation, try to not load ccg.getCampaignCreatives(), it may be huge.
        if (PersistenceUtils.isInitialized(ccg.getCampaignCreatives())) {
            ccg.getCampaignCreatives().add(campaignCreative);
        } else {
            ForceFieldChangeEntityChange.addCollectionChange(
                ccg,
                "campaignCreatives",
                campaignCreative,
                ChangeType.ADD
                );
            cacheService.evictCollection(CampaignCreativeGroup.class, "campaignCreatives", ccg.getId());
        }
    }

    @Override
    public void validateAll(CampaignCreativeGroup ccg, Collection<CampaignCreative> campaignCreatives, TGTType tgtType) {
        if (campaignCreatives.isEmpty()) {
            return;
        }

        Map<Long, CampaignCreative> existingCampaignCreatives = new HashMap<>(campaignCreatives.size());
        if (ccg.getId() != null) {
            @SuppressWarnings({ "unchecked" })
            List<CampaignCreative> ccs = em.createNamedQuery("Creative.findCreativeGroupsByGroupId").setParameter("creativeGroupId", ccg.getId()).getResultList();
            for (CampaignCreative cc : ccs) {
                existingCampaignCreatives.put(cc.getId(), cc);
            }
        }

        DuplicateChecker<CampaignCreative> checker = DuplicateChecker.create(new EntityIdFetcher<CampaignCreative>());
        for (CampaignCreative campaignCreative : campaignCreatives) {
            UploadContext uploadContext = UploadUtils.getUploadContext(campaignCreative);

            if (!checker.check(campaignCreative)) {
                uploadContext.addError("errors.duplicate.id").withPath("id");
            }

            if (campaignCreative.getId() == null) {
                uploadContext.mergeStatus(UploadStatus.NEW);
            } else {
                CampaignCreative existing = existingCampaignCreatives.get(campaignCreative.getId());
                if (existing != null) {
                    campaignCreative.getCreative().setVersion(existing.getCreative().getVersion());
                    uploadContext.mergeStatus(UploadStatus.UPDATE);
                }
            }

            // validate fields
            if (!uploadContext.isFatal()) {
                ValidationContext context = validationService.validate(
                    ValidationStrategies.exclude(uploadContext.getWrongPaths()), "CampaignCreative.createOrUpdate", campaignCreative, ccg, tgtType);
                UploadUtils.setErrors(campaignCreative, context.getConstraintViolations());
            }
            if (UploadUtils.isLinkWithErrors(campaignCreative.getCreativeGroup()) || UploadUtils.isLinkWithErrors(campaignCreative.getCreativeGroup().getCampaign())) {
                uploadContext.mergeStatus(UploadStatus.REJECTED);
            }
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('Campaign', #campaignId)")
    public List<Long> findCreativeIdsForBulkUpdate(Long campaignId, Collection<Long> ccgIds) {
        @SuppressWarnings({ "unchecked" })
        List<Long> creativeIds = em.createQuery("select distinct cc.creative.id from CampaignCreative cc " +
                " where cc.creativeGroup.campaign.id = :campaignId " +
                " and cc.status != 'D' " +
                " and cc.creative.status != 'D' " +
                " and cc.creativeGroup.status != 'D' " +
                " and " + SQLUtil.formatINClause("cc.creativeGroup.id", ccgIds))
            .setParameter("campaignId", campaignId)
            .getResultList();

        return creativeIds;
    }

    @Override
    public Collection<SizeType> findSizeTypesForEditByAccountId(Long accountId) {
        Account account = em.find(Account.class, accountId);
        return findSizeTypes(account.getAccountType());
    }

    @Override
    public Collection<SizeType> findSizeTypesForEdit(Long ccgId) {
        CampaignCreativeGroup ccg = em.find(CampaignCreativeGroup.class, ccgId);
        AccountType accountType = ccg.getAccount().getAccountType();

        return findSizeTypes(accountType);
    }

    private Collection<SizeType> findSizeTypes(AccountType accountType) {
        Set<SizeType> res = new TreeSet<>(new LocalizableNameEntityComparator());
        Map<Long, SizeType> sizeTypesById = new HashMap<>();

        Collection<CreativeSize> availableSizes = getAllTagSizes(accountType);

        em.clear();
        for (CreativeSize size : availableSizes) {
            SizeType sizeType = sizeTypesById.get(size.getSizeType().getId());
            if (sizeType == null) {
                sizeType = size.getSizeType();
                sizeType.setSizes(new TreeSet<CreativeSize>(new LocalizableNameEntityComparator()));
                sizeTypesById.put(sizeType.getId(), sizeType);
                res.add(sizeType);
            }
            sizeType.getSizes().add(size);
        }

        return res;
    }

    private Collection<CreativeSize> getAllTagSizes(AccountType accountType) {
        AccountType existingAccountType = em.find(AccountType.class, accountType.getId());
        CreativeTemplate textTemplate = templateService.findTextTemplate();
        Set<CreativeSize> sizes = new HashSet<>();
        for (TemplateFile templateFile : textTemplate.getTemplateFiles()) {
            if (Status.DELETED != templateFile.getCreativeSize().getStatus()) {
                sizes.add(templateFile.getCreativeSize());
            }
        }

        sizes.retainAll(existingAccountType.getCreativeSizes());

        return sizes;
    }

    @Override
    public Set<CreativeSize> getEffectiveTagSizes(Creative creative, AdvertiserAccount account) {
        Collection<CreativeSize> accountTypeSizes = getAllTagSizes(account.getAccountType());
        if (creative.isEnableAllAvailableSizes()) {
            return new HashSet<>(accountTypeSizes);
        }

        HashSet<CreativeSize> allowedSizes = new HashSet<>();

        for (SizeType sizeType : creative.getSizeTypes()) {
            sizeType = em.find(SizeType.class, sizeType.getId());
            allowedSizes.addAll(sizeType.getSizes());
        }

        for (CreativeSize size : creative.getTagSizes()) {
            size = em.find(CreativeSize.class, size.getId());
            allowedSizes.add(size);
        }

        allowedSizes.retainAll(accountTypeSizes);
        return allowedSizes;
    }

    @Override
    public Set<CreativeSize> getEffectiveTagSizes(Creative creative) {
        return getEffectiveTagSizes(creative, creative.getAccount());
    }

    @Override
    public void updateImagePreview(AdvertiserAccount account, String imagePath) {
        String textAdImagesResizedRoot = OptionValueUtils.getTextAdImagesResizedRoot(config, account);
        imagePath = TextAdImageUtil.getSourceFilePath(config, account, imagePath);

        PathProvider creativesPP = pathProviderService.getCreatives();
        File imageFile = creativesPP.getNested(OptionValueUtils.getTextAdImagesRoot(config, account), OnNoProviderRoot.AutoCreate).getPath(imagePath);

        String resizedName = TextAdImageUtil.getResizedFileName(imageFile.getName());
        String path = FileUtils.extractPathToFile(imagePath);

        File resized = creativesPP.getNested(textAdImagesResizedRoot + "/" + path, OnNoProviderRoot.AutoCreate)
            .getPath(resizedName);

        if (!resized.exists() || resized.lastModified() < imageFile.lastModified()) {
            createPreview(imageFile, resized);
        }
    }

    private void createPreview(File imageFile, File resizedFile) {
        try {
            BufferedImage bi = ImageIO.read(imageFile);
            if (bi == null) {
                logger.log(Level.WARNING, "Unable to create preview for image file: " + imageFile.getPath() + ". Unsupported Image Type.");
                return;
            }
            BufferedImage resizedBi = ImageUtil.getScaledSaveProportions(bi, 110, 80);
            ImageIO.write(resizedBi, "png", resizedFile);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to create preview for image file: " + imageFile.getPath() + ". " + e.getMessage());
        } finally {
            if (!resizedFile.exists()) {
                generateBadImagePreview(resizedFile);
            }
        }
    }

    @Override
    public void updateAllImagePreviews() {
        final File creativesFolder = pathProviderService.getCreatives().getPath("").getAbsoluteFile();

        String textAdImagesFolderName = config.get(ConfigParameters.TEXT_AD_IMAGES_FOLDER);
        for (File file1 : listFiles(creativesFolder)) {
            if (file1.isDirectory() && ACCOUNT_DIR.matcher(file1.getName()).matches()) {
                for (File file2 : listFiles(file1)) {
                    if (file2.isDirectory()) {
                        if (ACCOUNT_DIR.matcher(file2.getName()).matches()) {
                            for (File file3 : listFiles(file2)) {
                                if (file3.getName().equals(textAdImagesFolderName)) {
                                    processDir(file3.toPath());
                                    break;
                                }
                            }
                        } else if (file2.getName().equals(textAdImagesFolderName)) {
                            processDir(file2.toPath());
                        }
                    }
                }
            }
        }
    }

    private File[] listFiles(File creativesFolder) {
        File[] res = creativesFolder.listFiles();
        return res == null ? new File[0] : res;
    }

    private void processDir(final Path textAdImagesDir) {
        final Map<Path, BasicFileAttributes> sourceImages = new HashMap<>();
        final Map<Path, BasicFileAttributes> resizedImages = new HashMap<>();

        try {
            Files.walkFileTree(textAdImagesDir, new FileVisitor<Path>() {
                boolean inResizedDir;

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (isResizedDir(dir)) {
                        inResizedDir = true;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (isResizedDir(dir)) {
                        inResizedDir = false;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
                    if (inResizedDir) {
                        resizedImages.put(file, attr);
                    } else {
                        sourceImages.put(file, attr);
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    logger.log(Level.WARNING, "Can't read file attributes: " + file, exc);
                    return FileVisitResult.CONTINUE;
                }

                private boolean isResizedDir(Path dir) {
                    return textAdImagesDir.resolve(IMAGE_RESIZED_FOLDER).equals(dir);
                }
            });
        } catch (IOException exc) {
            logger.log(Level.WARNING, "Failed to process Text Ad images in directory: " + textAdImagesDir, exc);
        }

        for (Map.Entry<Path, BasicFileAttributes> sourceImagePair : sourceImages.entrySet()) {
            Path sourceImage = sourceImagePair.getKey();

            String resizedImageName = TextAdImageUtil.getResizedFileName(sourceImage.getFileName().toString());
            Path relativeSourceImage = textAdImagesDir.relativize(sourceImage);
            Path resizedImagePath = textAdImagesDir
                .resolve(IMAGE_RESIZED_FOLDER)
                .resolve(relativeSourceImage.resolveSibling(resizedImageName));

            BasicFileAttributes resizedImageAttr = resizedImages.remove(resizedImagePath);
            if (resizedImageAttr != null && resizedImageAttr.lastModifiedTime().toMillis() < sourceImagePair.getValue().lastModifiedTime().toMillis()) {
                createPreview(sourceImage.toFile(), resizedImagePath.toFile());
            }
        }

        for (Path resizedImage : resizedImages.keySet()) {
            try {
                Files.deleteIfExists(resizedImage);
            } catch (IOException exc) {
                logger.log(Level.WARNING, "Can't delete file: " + resizedImage);
            }
        }
    }

    private void generateBadImagePreview(File resizedFile) {
        try (InputStream is = BAD_IMAGE_FILE.openStream()) {
            Files.copy(is, resizedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.log(
                    Level.SEVERE,
                    "Unable to copy bad image file to '{0}': {1}",
                    new Object[]{resizedFile.getAbsolutePath(), e.getMessage()}
            );
        }
    }

}
