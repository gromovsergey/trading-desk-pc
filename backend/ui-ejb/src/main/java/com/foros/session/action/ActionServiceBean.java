package com.foros.session.action;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.jaxb.adapters.CampaignGroupLink;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AgencyAccount;
import com.foros.model.action.Action;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.TreeFilterElementTOConverter;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.db.DBConstraint;
import com.foros.session.query.PartialList;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.action.ActionQueryImpl;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.DisplayStatusService;
import com.foros.session.status.StatusService;
import com.foros.util.CollectionUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.comparator.StatusNameTOComparator;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validate;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.hibernate.FlushMode;
import org.joda.time.LocalDate;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "ActionService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class })
public class ActionServiceBean implements ActionService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private UserService userService;

    @EJB
    private AuditService auditService;

    @EJB
    private StatusService statusService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private QueryExecutorService executorService;

    @EJB
    private ValidationService validationService;

    @EJB
    private DisplayStatusService displayStatusService;


    public ActionServiceBean() {
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.create", parameters = "find('AdvertiserAccount', #action.account.id)")
    @Validate(validation = "Action.create", parameters = "#action")
    @Interceptors({ CaptureChangesInterceptor.class })
    public void create(Action action) {
        action.setStatus(Status.ACTIVE);
        action.setDisplayStatus(Action.LIVE_NEED_ATT);
        AdvertiserAccount account = em.getReference(AdvertiserAccount.class, action.getAccount().getId());
        action.setAccount(account);

        auditService.audit(action, ActionType.CREATE);
        em.persist(action);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('Action', #action.id)")
    @Validate(validation = "Action.update", parameters = "#action")
    @Interceptors({ CaptureChangesInterceptor.class })
    public Action update(Action action) {
        auditService.auditDetached(action, ActionType.UPDATE);
        return em.merge(action);
    }

    @Override
    public void refresh(Long id) {
        Action entity = em.find(Action.class, id);
        em.refresh(entity);
    }

    @Override
    public Action findById(Long id) {
        return findByIdInternal(id);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "find('Action', #id)")
    public Action view(Long id) {
        return findById(id);
    }

    private Action findByIdInternal(Long id) {
        Action entity = em.find(Action.class, id);

        if (entity == null) {
            throw new EntityNotFoundException("Action with id=" + id + " not found");
        }

        return entity;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('Action', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public Action delete(Long id) {
        Action entity = em.find(Action.class, id);
        statusService.delete(entity);

        return entity;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.undelete", parameters = "find('Action', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public Action undelete(Long id) {
        Action entity = em.find(Action.class, id);
        statusService.undelete(entity);

        return entity;
    }

    @Override
    public List<TreeFilterElementTO> search(Long accountId) {
        if (accountId == null) {
            return new ArrayList<TreeFilterElementTO>();
        }

        StringBuilder queryString = new StringBuilder();
        queryString.append(" SELECT ");
        queryString.append("  ac.action_id id, ac.name, ac.status, ac.display_status_id, false hasChildren ");
        queryString.append(" FROM Action ac ");
        queryString.append(" WHERE ac.account_id = ? ");
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            queryString.append(" AND ac.status <> 'D'");
        }

        List<TreeFilterElementTO> result = jdbcTemplate.query(
                queryString.toString(),
                new Object[]{accountId},
                new TreeFilterElementTOConverter(Action.displayStatusMap)
        );

        Collections.sort(result, new StatusNameTOComparator<>());
        return result;
    }

    @Override
    public List<ActionTO> findByAccountIdAndDate(Long accountId, LocalDate fromDate, LocalDate toDate, boolean showDeleted) {
        return jdbcTemplate.query("select * from statqueries.actionstats_for_account(?::int,?::date,?::date,?::bool)",
                new Object[]{
                        accountId,
                        fromDate,
                        toDate,
                        showDeleted
                },
                new RowMapper<ActionTO>() {
                    @Override
                    public ActionTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new ActionTO(
                                rs.getLong("action_id"),
                                rs.getString("name"),
                                rs.getInt("conv_category_id"),
                                rs.getLong("display_status_id"),
                                rs.getString("url"),
                                rs.getLong("conversions")
                        );
                    }
                });
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Action> findNonDeletedByAccountId(Long accountId) {
        Query q = em.createNamedQuery("Action.findNonDeletedByAccountId");
        q.setParameter("accountId", accountId);
        List<Action> result = q.getResultList();
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<EntityTO> findEntityTOByMultipleParameters(Long accountId, Long campaignId, Long groupId, boolean showDeleted) {
        Long agencyId = null;
        Long advertiserId = null;
        if (accountId != null) {
            AdvertisingAccountBase account = em.find(AdvertisingAccountBase.class, accountId);
            if (account instanceof AdvertiserAccount && ((AdvertiserAccount) account).isInAgencyAdvertiser()) {
                agencyId = ((AdvertiserAccount) account).getAgency().getId();
            } else if (account instanceof AgencyAccount) {
                agencyId = accountId;
            }
            advertiserId = account instanceof AdvertiserAccount ? accountId : null;
        }

        boolean onlyOwn = !currentUserService.isInternal();
        boolean isAccountManager = currentUserService.isAdvertiserAccountManager();
        boolean isAdvertiserLevelRestricted = currentUserService.isAdvertiserLevelRestricted();

        StringBuilder sql = new StringBuilder("SELECT DISTINCT NEW com.foros.session.EntityTO(a.id, a.name, a.status) ");
        sql.append(groupId != null || campaignId != null ? "FROM CampaignCreativeGroup ccg, IN (ccg.actions) a " : "FROM Action a ");
        sql.append(isAccountManager ? "left join a.account.agency " : "");
        sql.append("WHERE 1 = 1 ");
        if (!showDeleted) {
            sql.append("AND a.account.status <> 'D' ");
        }
        sql.append(agencyId != null ? "AND a.account.agency.id = :agencyId " : "AND a.account.agency is null ");
        sql.append(advertiserId != null ? "AND a.account.id = :advertiserId " : "");
        sql.append(campaignId != null ? "AND ccg.campaign.id = :campaignId " : "");
        sql.append(groupId != null ? "AND ccg.id = :groupId " : "");
        sql.append(showDeleted ? "" : "AND a.status <> 'D' ");
        sql.append(onlyOwn ? "AND coalesce(a.account.agency.id, a.account.id) = :currentAccountId " : "");
        sql.append(isAccountManager ? "AND (a.account.accountManager.id = :currentUser or a.account.agency.accountManager.id = :currentUser) " : "");
        sql.append(isAdvertiserLevelRestricted ? "AND exists (FROM User u JOIN u.advertisers ua WHERE u.id = :currentUser and a.account = ua) " : "");
        sql.append("ORDER BY a.name");

        Query q = em.createQuery(sql.toString());
        if (agencyId != null) {
            q.setParameter("agencyId", agencyId);
        }
        if (advertiserId != null) {
            q.setParameter("advertiserId", advertiserId);
        }
        if (campaignId != null) {
            q.setParameter("campaignId", campaignId);
        }
        if (groupId != null) {
            q.setParameter("groupId", groupId);
        }
        if (onlyOwn) {
            q.setParameter("currentAccountId", currentUserService.getAccountId());
        }
        if (isAccountManager || isAdvertiserLevelRestricted) {
            q.setParameter("currentUser", currentUserService.getUserId());
        }

        List<EntityTO> entityTOs = q.getResultList();
        Collections.sort(entityTOs, new StatusNameTOComparator());
        return entityTOs;
    }

    @Override
    public boolean isLinked(Long actionId) {
        String sql = "select count(ccg_id) from ccgaction where  action_id = :actionId ";

        Query groupCountQuery = em.createNativeQuery(sql);
        groupCountQuery.setParameter("actionId", actionId);

        BigDecimal result = (BigDecimal) groupCountQuery.getSingleResult();

        Long count = result.longValue();

        return count > 0;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view")
    public Result<Action> get(ActionSelector selector) {
        if (!currentUserService.isExternal()
                && CollectionUtils.isNullOrEmpty(selector.getAdvertiserIds())
                && CollectionUtils.isNullOrEmpty(selector.getActionIds())) {
            throw new ConstraintViolationException(BusinessErrors.FIELD_IS_REQUIRED, "errors.api.emptyCriteria.action");
        }

        PartialList<Action> actions = new ActionQueryImpl().
            restrict().
            actions(selector.getActionIds()).
            advertisers(selector.getAdvertiserIds()).
            statuses(selector.getActionStatuses()).
            addDefaultOrder().
            executor(executorService).
            partialList(selector.getPaging());
        return new Result<>(actions);
    }

    @Override
    @Interceptors({ CaptureChangesInterceptor.class })
    @Validate(validation = "Operations.integrity", parameters = { "#operations", "'conversion'" })
    public OperationsResult perform(Operations<Action> operations) {
        // to prevent Hibernate doing auto-flush
        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.MANUAL);

        fetch(operations);

        // validate
        validationService.validate("Action.merge", operations).throwIfHasViolations();

        List<Long> result = new ArrayList<>();

        for (Operation<Action> actionMergeOperation : operations.getOperations()) {
            result.add(processMergeOperation(actionMergeOperation));
        }

        try {
            em.flush();
        } catch (PersistenceException e) {
            if (DBConstraint.ACTION_NAME.match(e)) {
                validationService.validateInNewTransaction("Action.nameConstraintViolations", operations).throwIfHasViolations();
            }

            throw e;
        }

        // let's Hibernate do rest of the job
        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.AUTO);

        return new OperationsResult(result);
    }

    private void fetch(Operations<Action> operations) {
        List<Long> actionIds = new ArrayList<>();
        for (Operation<Action> operation : operations) {
            actionIds.add(operation.getEntity().getId());
        }

        if (!actionIds.isEmpty()) {
            new ActionQueryImpl()
                .actions(actionIds)
                .executor(executorService)
                .list();
        }

        for (Operation<Action> operation : operations) {
            Action action = operation.getEntity();
            if (operation.getOperationType() == OperationType.UPDATE && action.getId() != null) {
                Action existingAction = em.find(Action.class, action.getId());
                if (existingAction != null) {
                    action.setAccount(existingAction.getAccount());
                }
            }
        }
    }

    private Long processMergeOperation(Operation<Action> mergeOperation) {
        Action action = mergeOperation.getEntity();

        switch (mergeOperation.getOperationType()) {
        case CREATE:
            action.setId(null);
            create(action);
            return action.getId();
        case UPDATE:
            updateInternal(action);
            return action.getId();
        }

        throw new RuntimeException(mergeOperation.getOperationType() + " not supported!");
    }

    private void updateInternal(Action action) {
        if (!action.isChanged("version")) {
            Action existing = em.find(Action.class, action.getId());
            action.setVersion(existing.getVersion());
        }

        displayStatusService.update(action);
        auditService.auditDetached(action, ActionType.UPDATE);
        em.merge(action);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "find('Action', #actionId)")
    public Collection<CampaignGroupLink> getAssociations(Long actionId) {
        return jdbcTemplate.query(
                "SELECT ccg.ccg_id, ccg.campaign_id FROM CCGAction CCGACT " +
                        " JOIN CAMPAIGNCREATIVEGROUP  CCG ON CCGACT.CCG_ID = CCG.CCG_ID " +
                        " WHERE CCGACT.ACTION_ID = ?",
                new Object[]{actionId},
                new RowMapper<CampaignGroupLink>() {
                    @Override
                    public CampaignGroupLink mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new CampaignGroupLink(rs.getLong("ccg_id"), rs.getLong("campaign_id"));
                    }
                }
        );
    }
}
