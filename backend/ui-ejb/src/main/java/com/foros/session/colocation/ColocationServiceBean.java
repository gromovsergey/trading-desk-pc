package com.foros.session.colocation;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.Status;
import com.foros.model.account.IspAccount;
import com.foros.model.isp.Colocation;
import com.foros.model.isp.ColocationRate;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.EntityTO;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.query.PartialList;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.colocation.ColocationQuery;
import com.foros.session.query.colocation.ColocationQueryImpl;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.StatusService;
import com.foros.util.DSTimeInterval;
import com.foros.util.SQLUtil;
import com.foros.util.StringUtil;
import com.foros.util.comparator.IdNameComparator;
import com.foros.util.comparator.StatusNameTOComparator;
import com.foros.util.jpa.JpaQueryWrapper;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "ColocationService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class ColocationServiceBean implements ColocationService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;

    @EJB
    private StatusService statusService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private UserService userService;

    @EJB
    private QueryExecutorService executorService;

    @Override
    @Restrict(restriction = "Colocation.view", parameters = "find('IspAccount', #accountId)")
    public Collection<ColocationTO> search(Long accountId) {
        return jdbcTemplate.query(
                "select * from statqueries.colocations(?::int,?::bool)",
                new Object[]{
                        accountId,
                        userService.getMyUser().isDeletedObjectsVisible()
                },
                new RowMapper<ColocationTO>() {
                    @Override
                    public ColocationTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Long id = rs.getLong("colo_id");
                        String name = rs.getString("name");
                        Character status = rs.getString("status").charAt(0);
                        String version = rs.getString("software_version");
                        DSTimeInterval lastUpdate = null;
                        DSTimeInterval lastStatsUpdate = null;
                        if (!StringUtil.isPropertyEmpty(version)) {
                            Date now = new Date();

                            Timestamp updateDate = rs.getTimestamp("last_update");
                            if (updateDate != null) {
                                lastUpdate = new DSTimeInterval(updateDate, now);
                            }

                            Timestamp statsUpdateDate = rs.getTimestamp("last_stats_upload");
                            if (statsUpdateDate != null) {
                                lastStatsUpdate = new DSTimeInterval(statsUpdateDate, now);
                            }
                        }
                        return new ColocationTO(id, name, status, version, lastUpdate, lastStatsUpdate);
                    }
                });
    }

    @Override
    @Restrict(restriction = "Colocation.view")
    public PartialList<Colocation> get(ColocationSelector selector) {
        ColocationQuery query = colocationQuery(selector);
        PartialList<Colocation> colocations = query.executor(executorService)
                .partialList(selector.getPaging());
        return colocations;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Restrict(restriction = "Colocation.list", parameters = "#accountId")
    public List<EntityTO> getIndex(Long accountId) {
        Query q = (userService.getMyUser().isDeletedObjectsVisible()) ?
                em.createNamedQuery("Colocation.entityTO.findByAccountId") :
                em.createNamedQuery("Colocation.entityTO.findNonDeletedByAccountId");
        q.setParameter("accountId", accountId);
        List<EntityTO> result = q.getResultList();
        Collections.sort(result, new IdNameComparator());
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Restrict(restriction = "Colocation.view")
    public List<EntityTO> getByAccountIds(Collection<Long> accountIds) {
        StringBuilder sb = new StringBuilder("SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM Colocation c ");
        sb.append("WHERE ").append(SQLUtil.formatINClause("c.account.id", accountIds));
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            sb.append(" and c.status <> 'D'");
        }
        List<EntityTO> result =  new JpaQueryWrapper<EntityTO>(em, sb.toString()).getResultList();
        Collections.sort(result, new StatusNameTOComparator());
        return result;
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Colocation.update", parameters = "find('Colocation', #colocation.id)")
    @Validate(validation="Colocation.update", parameters = "#colocation")
    public Colocation update(Colocation colocation) {
        Colocation existingColocation = find(colocation.getId());
        auditService.audit(existingColocation, ActionType.UPDATE);

        ColocationRate colocationRate = colocation.getColocationRate();
        if (!ObjectUtils.equals(existingColocation.getColocationRate().getRevenueShare(), colocationRate.getRevenueShare())) {
            colocation.setColocationRate(colocationRate);
            colocationRate.setId(null);
            colocationRate.setColocation(existingColocation);
            colocationRate.setEffectiveDate(new Date());
            em.persist(colocationRate);
        } else {
            // do not merge colocationRate, make it unchanged
            colocation.unregisterChange("colocationRate");
        }
        em.merge(colocation);
        em.flush();
        return colocation;
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Colocation.create", parameters = "find('IspAccount', #colocation.account.id)")
    @Validate(validation="Colocation.create", parameters = "#colocation")
    public Colocation create(Colocation colocation) {
        Long accountId = colocation.getAccount().getId();
        IspAccount account = em.find(IspAccount.class, accountId);
        colocation.setAccount(account);

        account.getColocations().size();
        colocation.setStatus(Status.ACTIVE);

        ColocationRate colocationRate = colocation.getColocationRate();
        colocationRate.setEffectiveDate(new Date(System.currentTimeMillis()));
        colocationRate.setColocation(colocation);
        colocation.setColocationRate(null);

        auditService.audit(colocation, ActionType.CREATE);
        try {
            em.persist(colocation);
            em.persist(colocationRate);
            em.flush();
        } catch (RuntimeException ex) {
            colocationRate.setId(null);
            colocation.setId(null);
            throw ex;
        } finally {
            colocation.setColocationRate(colocationRate);
        }

        account.getColocations().add(colocation);

        em.flush();
        return colocation;
    }

    @Override
    public Colocation find(Long id) {
        Colocation res = em.find(Colocation.class, id);

        if (res == null) {
            throw new EntityNotFoundException("Colocation with id=" + id + " not found");
        }

        return res;
    }

    @Override
    @Restrict(restriction = "Colocation.view", parameters = "find('Colocation', #id)")
    public Colocation view(Long id) {
        return find(id);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Colocation.update", parameters = "find('Colocation', #id)")
    public void delete(Long id) {
        statusService.delete(find(id));
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Colocation.undelete", parameters = "find('Colocation', #id)")
    public void undelete(Long id) {
        statusService.undelete(find(id));
    }

    private ColocationQuery colocationQuery(ColocationSelector selector) {
        return new ColocationQueryImpl()
                .restrict()
                .name(selector.getName())
                .colocations(selector.getColocationIds())
                .accounts(selector.getAccountIds())
                .statuses(selector.getStatuses())
                .addDefaultOrder();
    }
}
