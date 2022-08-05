package com.foros.session.campaign;

import com.foros.model.Status;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.CCGKeywordSelector;
import com.foros.session.channel.TriggerService;
import com.foros.session.db.DBConstraint;
import com.foros.session.query.PartialList;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.campaign.CCGKeywordQuery;
import com.foros.session.query.campaign.CCGKeywordQueryImpl;
import com.foros.session.query.campaign.CampaignCreativeGroupQueryImpl;
import com.foros.session.status.DisplayStatusService;
import com.foros.util.CollectionMerger;
import com.foros.util.CollectionUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.UploadUtils;
import com.foros.util.VersionCollisionException;
import com.foros.util.mapper.Pair;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validate;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.strategy.ValidationStrategies;
import com.foros.validation.util.DuplicateChecker;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "CCGKeywordService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class CCGKeywordServiceBean implements CCGKeywordService {
    public static final Status DEFAULT_STATUS = Status.ACTIVE;
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private CampaignCreativeGroupService ccgService;

    @EJB
    private TriggerService triggerService;

    @EJB
    private DisplayStatusService displayStatusService;

    @EJB
    private QueryExecutorService executorService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private ValidationService validationService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @Override
    @Restrict(restriction = "AdvertiserEntity.view")
    public Result<CCGKeyword> get(CCGKeywordSelector keywordSelector) {
        if (!currentUserService.isExternal()
                && CollectionUtils.isNullOrEmpty(keywordSelector.getAdvertiserIds())
                && CollectionUtils.isNullOrEmpty(keywordSelector.getCampaigns())
                && CollectionUtils.isNullOrEmpty(keywordSelector.getCreativeGroups())
                && CollectionUtils.isNullOrEmpty(keywordSelector.getKeywords())) {
            throw new ConstraintViolationException(BusinessErrors.FIELD_IS_REQUIRED, "errors.api.emptyCriteria.keyword");
        }

        PartialList<CCGKeyword> keywords = createCCGKeywordQuery()
                .advertisers(keywordSelector.getAdvertiserIds())
                .campaigns(keywordSelector.getCampaigns())
                .creativeGroups(keywordSelector.getCreativeGroups())
                .keywords(keywordSelector.getKeywords())
                .statuses(keywordSelector.getStatuses())
                .addDefaultOrder()
                .executor(executorService)
                .partialList(keywordSelector.getPaging());

        return new Result<CCGKeyword>(keywords);
    }

    @Override
    @Validate(validation = "Operations.integrity", parameters = {"#operations", "'ccgKeyword'"})
    public OperationsResult perform(Operations<CCGKeyword> operations) {
        fetch(operations);

        // validation
        validationService.validate("CCGKeyword.merge", operations).throwIfHasViolations();

        List<Long> result = merge(operations.getOperations());

        try {
            em.flush();
        } catch (PersistenceException e) {
            if (DBConstraint.CCGKEYWORD_TRIGGER.match(e)) {
                validationService.validateInNewTransaction("CCGKeyword.keywordConstraintViolations", operations).throwIfHasViolations();
            }
            throw e;
        }
        return new OperationsResult(result);
    }

    private void fetch(Operations<CCGKeyword> operations) {
        Set<Long> keywordIds = new HashSet<Long>();
        Set<Long> groupIds = new HashSet<Long>();

        for (Operation<CCGKeyword> operation : operations.getOperations()) {
            CCGKeyword keyword = operation.getEntity();

            if (keyword == null) {
                continue;
            }

            if (keyword.getId() != null) {
                keywordIds.add(keyword.getId());
            }

            CampaignCreativeGroup ccg = keyword.getCreativeGroup();
            if (ccg == null) {
                continue;
            }

            if (ccg.getId() != null) {
                groupIds.add(ccg.getId());
            }
        }

        if (!groupIds.isEmpty()) {
            // fetch everything we need (keywords, ccgs, campaigns etc.)
            new CampaignCreativeGroupQueryImpl()
                    .creativeGroups(groupIds)
                    .executor(executorService)
                    .list();
        }

        if (!keywordIds.isEmpty()) {
            new CCGKeywordQueryImpl()
                    .keywords(keywordIds)
                    .executor(executorService)
                    .list();
        }

        for (Operation<CCGKeyword> operation : operations) {
            CCGKeyword keyword = operation.getEntity();
            if (operation.getOperationType() == OperationType.UPDATE && keyword.getId() != null) {
                CCGKeyword existingKeyword = em.find(CCGKeyword.class, keyword.getId());
                if (existingKeyword != null) {
                    keyword.setCreativeGroup(existingKeyword.getCreativeGroup());
                }
            }
        }
    }

    @Override
    public CCGKeyword find(Long id) {
        if (id == null) {
            throw new EntityNotFoundException("Entity with id = null is not found");
        }
        CCGKeyword keyword = em.find(CCGKeyword.class, id);
        if (keyword == null) {
            throw new EntityNotFoundException("Entity with id = " + id + " is not found");
        }
        return keyword;
    }

    private List<Long> merge(List<Operation<CCGKeyword>> mergeOperations) {
        List<Long> result = new ArrayList<Long>(mergeOperations.size());

        List<CCGKeyword> toBeCreated = new ArrayList<CCGKeyword>();

        for (Operation<CCGKeyword> operation : mergeOperations) {
            if (operation.getOperationType() == OperationType.CREATE) {
                toBeCreated.add(operation.getEntity());
            }
        }

        undeleteByCreate(toBeCreated);

        for (Operation<CCGKeyword> operation : mergeOperations) {
            CCGKeyword keyword = operation.getEntity();
            CampaignCreativeGroup existingCcg;
            if (operation.getOperationType() == OperationType.CREATE) {
                existingCcg = em.find(CampaignCreativeGroup.class, keyword.getCreativeGroup().getId());
            } else {
                existingCcg = find(keyword.getId()).getCreativeGroup();
            }
            execute(keyword, existingCcg);
            result.add(keyword.getId());
        }

        return result;
    }

    private void undeleteByCreate(List<CCGKeyword> toBeCreated) {
        if (toBeCreated.isEmpty()) {
            return;
        }

        List<CCGKeyword> existingDeleted = createCCGKeywordQuery()
                .existingByKeyword(toBeCreated)
                .statuses(Collections.singleton(Status.DELETED))
                .executor(executorService)
                .list();

        HashMap<Key, CCGKeyword> existingByKey = new HashMap<Key, CCGKeyword>();

        for (CCGKeyword keyword : existingDeleted) {
            existingByKey.put(new Key(keyword), keyword);
        }

        for (CCGKeyword keyword : toBeCreated) {
            CCGKeyword existing = existingByKey.get(new Key(keyword));
            if (existing != null) {
                keyword.setId(existing.getId());
            }
        }
    }

    private boolean execute(CCGKeyword keyword, CampaignCreativeGroup existingCcg) {
        boolean isUpdated;
        CCGKeyword existing = keyword.getId() == null ? null : find(keyword.getId());

        // set default status
        if (!keyword.isChanged("status")) {
            // existing is deleted or new
            if ((existing != null && existing.getStatus() == Status.DELETED) || existing == null) {
                keyword.setStatus(DEFAULT_STATUS);
            }
        }

        if (!keyword.isChanged("version") && existing != null) {
            keyword.setVersion(existing.getVersion());
        }

        keyword.setCreativeGroup(existingCcg);

        if (keyword.getId() == null) {
            keyword.setChannelId(null);
            em.persist(keyword);
            triggerService.addToBulkLinkCCGKeywords(keyword);
            isUpdated = true;
        } else {
            keyword.unregisterChange("channelId");
            isUpdated = em.merge(keyword).isChanged();
        }

        displayStatusService.update(existingCcg);

        return isUpdated;
    }

    private CCGKeywordQuery createCCGKeywordQuery() {
        return new CCGKeywordQueryImpl().restrict();
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('CampaignCreativeGroup', #ccgId)")
    @Validate(validation = "CCGKeyword.createOrUpdateAll", parameters = {"#ccgKeywords", "#ccgId"})
    public void update(Collection<CCGKeyword> ccgKeywords, Long ccgId, Timestamp ccgVersion) {
        CampaignCreativeGroup existingCcg = em.find(CampaignCreativeGroup.class, ccgId);

        if (existingCcg.getVersion().compareTo(ccgVersion) != 0) {
            throw new VersionCollisionException();
        }

        prePersist(existingCcg.getCcgKeywords(), ccgKeywords);

        boolean isKeywordUpdated = false;
        for (CCGKeyword ccgKeyword : ccgKeywords) {
            isKeywordUpdated |= execute(ccgKeyword, existingCcg);
        }
        if (isKeywordUpdated) {
            PersistenceUtils.performHibernateLock(em, existingCcg);
        }
    }

    private void prePersist(final Collection<CCGKeyword> existingCcgKeywords, final Collection<CCGKeyword> ccgKeywords) {
        final LinkedList<CCGKeyword> toBeDeleted = new LinkedList<CCGKeyword>();

        (new CollectionMerger<CCGKeyword>(existingCcgKeywords, ccgKeywords) {
            @Override
            protected Object getId(CCGKeyword keyword, int index) {
                return new Pair<String, KeywordTriggerType>(keyword.getOriginalKeyword(), keyword.getTriggerType());
            }

            @Override
            protected boolean add(CCGKeyword updated) {
                return false;
            }

            @Override
            protected void update(CCGKeyword persistent, CCGKeyword updated) {
                updated.setId(persistent.getId());
            }

            @Override
            protected boolean delete(CCGKeyword persistent) {
                CCGKeyword keyword = new CCGKeyword();
                keyword.setId(persistent.getId());
                keyword.setStatus(Status.DELETED);
                toBeDeleted.add(keyword);
                return false;
            }
        }).merge();

        ccgKeywords.addAll(toBeDeleted);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('CampaignCreativeGroup', #ccgId)")
    public void activate(Collection<Long> ids, Long ccgId) {
        updateStatus(ids, ccgId, "A");
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('CampaignCreativeGroup', #ccgId)")
    public void inactivate(Collection<Long> ids, Long ccgId) {
        updateStatus(ids, ccgId, "I");
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('CampaignCreativeGroup', #ccgId)")
    public void delete(Collection<Long> ids, Long ccgId) {
        updateStatus(ids, ccgId, "D");
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.undeleteChildren", parameters = "find('CampaignCreativeGroup', #ccgId)")
    @Validate(validation = "CCGKeyword.undelete", parameters = {"#ids", "#ccgId"})
    public void undelete(Collection<Long> ids, Long ccgId) {
        updateStatus(ids, ccgId, "U");
    }

    private void updateStatus(Collection<Long> ids, Long ccgId, String status) {
        CampaignCreativeGroup ccg = ccgService.find(ccgId);
        triggerService.updateCCGKeywordsStatus(ccgId, ids, status);
        em.refresh(ccg);
        displayStatusService.update(ccg);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<CCGKeyword> findAll(Long ccgId) {
        return em.createNamedQuery("CCGKeyword.findByGroup").setParameter("groupId", ccgId).getResultList();
    }

    @Override
    public void validateAll(CampaignCreativeGroup ccg, Collection<CCGKeyword> ccgKeywords, TGTType tgtType) {
        if (ccgKeywords.isEmpty()) {
            return;
        }

        Map<Pair<String,KeywordTriggerType>, CCGKeyword> existingKeywords = new HashMap<Pair<String, KeywordTriggerType>, CCGKeyword>(ccgKeywords.size());
        if (ccg.getId() != null) {
            @SuppressWarnings({"unchecked"})
            List<CCGKeyword> keywords = em.createNamedQuery("CCGKeyword.findByGroup").setParameter("groupId", ccg.getId()).getResultList();
            for (CCGKeyword keyword : keywords) {
                existingKeywords.put(new Pair<String, KeywordTriggerType>(keyword.getOriginalKeyword(),keyword.getTriggerType()), keyword);
            }
        }

        DuplicateChecker
                .create(CCGKeyword.IDENTIFIER_FETCHER)
                .check(ccgKeywords)
                .updateUploadStatus("originalKeyword");

        for (CCGKeyword keyword : ccgKeywords) {
            UploadContext uploadStatus = UploadUtils.getUploadContext(keyword);

            CCGKeyword existing = existingKeywords.get(new Pair<String, KeywordTriggerType>(keyword.getOriginalKeyword(),keyword.getTriggerType()));

            if (existing != null) {
                keyword.setId(existing.getId());
                if (existing.getStatus() == Status.DELETED) {
                    // rest of the code should handle it as creation
                    existing = null;
                }
            }

            uploadStatus.mergeStatus(existing == null ? UploadStatus.NEW : UploadStatus.UPDATE);

            // validate fields
            if (!uploadStatus.isFatal()) {
                ValidationContext context = validationService.validate(ValidationStrategies.exclude(uploadStatus.getWrongPaths()),
                        "CCGKeyword.createOrUpdate", keyword, ccg, tgtType);
                UploadUtils.setErrors(keyword, context.getConstraintViolations());
            }
            if (UploadUtils.isLinkWithErrors(keyword.getCreativeGroup()) || UploadUtils.isLinkWithErrors(keyword.getCreativeGroup().getCampaign())) {
                uploadStatus.mergeStatus(UploadStatus.REJECTED);
            }
        }

        ValidationContext result = validationService.validate("CCGKeyword.count", ccg, ccgKeywords, existingKeywords.values());
        for (CCGKeyword ccgKeyword : ccgKeywords) {
            if (UploadUtils.getUploadContext(ccgKeyword).getStatus() == UploadStatus.NEW) {
                UploadUtils.setErrors(ccgKeyword, result.getConstraintViolations());
            }
        }
    }

    @Override
    public void createOrUpdateAll(Long id, Set<CCGKeyword> ccgKeywords) {
        if (ccgKeywords.isEmpty()) {
            return;
        }
        for (CCGKeyword ccgKeyword : ccgKeywords) {
            UploadUtils.throwIfErrors(ccgKeyword);
        }

        CampaignCreativeGroup existingCcg = em.find(CampaignCreativeGroup.class, id);
        for (CCGKeyword keyword : ccgKeywords) {
            execute(keyword, existingCcg);
        }
    }

    private static class Key {
        private Long ccgId;
        private String originalKeyword;

        private Key(Long ccgId, String originalKeyword) {
            this.ccgId = ccgId;
            this.originalKeyword = originalKeyword;
        }

        private Key(CCGKeyword keyword) {
            this(keyword.getCreativeGroup().getId(), keyword.getOriginalKeyword());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (!ccgId.equals(key.ccgId)) return false;
            if (!originalKeyword.equals(key.originalKeyword)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = ccgId.hashCode();
            result = 31 * result + originalKeyword.hashCode();
            return result;
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "find('CampaignCreativeGroup', #ccgId)")
    public List<EditCCGKeywordTO> findCCGKeywords(Long ccgId) {
        return jdbcTemplate.query(
                "select * from entityqueries.ccg_keywords_edit_data(?::int)",
                new Object[]{ccgId},
                new RowMapper<EditCCGKeywordTO>() {
                    @Override
                    public EditCCGKeywordTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        String originalKeyword = rs.getString("original_keyword");
                        BigDecimal maxCpcBid = rs.getBigDecimal("max_cpc_bid");
                        String triggerType = rs.getString("trigger_type");
                        String clickUrl = rs.getString("click_url");

                        EditCCGKeywordTO editCCGKeywordTO = new EditCCGKeywordTO();
                        editCCGKeywordTO.setOriginalKeyword(originalKeyword);
                        editCCGKeywordTO.setMaxCpcBid(maxCpcBid);
                        editCCGKeywordTO.setTriggerType((triggerType != null) ? KeywordTriggerType.byLetter(triggerType.charAt(0)) : null);
                        editCCGKeywordTO.setClickURL(clickUrl);
                        return editCCGKeywordTO;
                    }
                }
        );
    }
}
