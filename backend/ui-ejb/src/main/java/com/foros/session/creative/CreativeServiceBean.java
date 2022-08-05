package com.foros.session.creative;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.DisplayStatus;
import com.foros.model.Status;
import com.foros.model.creative.Creative;
import com.foros.model.security.ActionType;
import com.foros.model.security.ResultType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.StatusAction;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.ApprovalAction;
import com.foros.util.ConditionStringBuilder;
import com.foros.util.SQLUtil;
import com.foros.util.StringUtil;
import com.foros.util.jpa.DetachedList;
import com.foros.util.jpa.JpaQueryWrapper;
import com.foros.util.jpa.QueryWrapper;
import com.foros.validation.ValidationInterceptor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "CreativeService")
@Interceptors({RestrictionInterceptor.class, PersistenceExceptionInterceptor.class, ValidationInterceptor.class})
public class CreativeServiceBean implements CreativeService {
    @EJB
    private DisplayCreativeService displayCreativeService;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private RestrictionService restrictionService;

    @EJB
    protected CurrentUserService currentUserService;

    @EJB
    protected UserService userService;

    @EJB
    private AuditService auditService;

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public void bulkUpdateStatus(List<Long> setNumberIds, String changeType, String declineReason) {
        if (setNumberIds != null) {
            for (Long creativeId : setNumberIds) {
                Creative creative = find(creativeId);
                if (creative.getParentStatus() == Status.DELETED) {
                    continue;
                }
                if (changeType.equals(StatusAction.ACTIVATE.name()) && restrictionService.isPermitted("AdvertiserEntity.activate", creative)) {
                    displayCreativeService.activate(creativeId);
                } else if (changeType.equals(StatusAction.DELETE.name()) && restrictionService.isPermitted("AdvertiserEntity.delete", creative)) {
                    displayCreativeService.delete(creativeId);
                } else if (changeType.equals(StatusAction.INACTIVATE.name()) && restrictionService.isPermitted("AdvertiserEntity.inactivate", creative)) {
                    displayCreativeService.inactivate(creativeId);
                } else if (changeType.equals(StatusAction.UNDELETE.name()) && restrictionService.isPermitted("AdvertiserEntity.undelete", creative)) {
                    displayCreativeService.undelete(creativeId);
                } else if (changeType.equals(ApprovalAction.APPROVE.name()) && restrictionService.isPermitted("AdvertiserEntity.approve", creative)) {
                    displayCreativeService.approve(creativeId);
                } else if (changeType.equals(ApprovalAction.DECLINE.name()) && restrictionService.isPermitted("AdvertiserEntity.decline", creative)) {
                    displayCreativeService.decline(creativeId, declineReason);
                }
            }
        }
    }

    @Override
    public int findPendingFOROSCreativesCount() {
        return findCreativesCount(null, Arrays.asList(Creative.PENDING_FOROS), null, null, false, false);
    }

    private Creative find(Long id) {
        Creative res = em.find(Creative.class, id);
        if (res == null) {
            throw new EntityNotFoundException("Creative with id=" + id + " not found");
        }
        return res;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "find('Account', #accountId)")
    public List<CreativeTO> findCreatives(
            Long accountId,
            List<DisplayStatus> displayStatuses,
            Long campaignId,
            Long sizeId,
            boolean allowTestAccounts,
            boolean allowDeletedOwner,
            int from,
            int count,
            CreativeSortType orderBy
    ) {

        List<CreativeTO> list = jdbcTemplate.withAuthContext().query(
                "select * from entityqueries.get_creatives(?::int,?::int,?::int,?::bool,?::bool,?::int[],?::varchar,?::varchar,?::int,?::int)",
                new Object[]{
                        accountId,
                        sizeId,
                        campaignId,
                        allowTestAccounts,
                        allowDeletedOwner,
                        jdbcTemplate.createArray("int", DisplayStatus.getIds(displayStatuses)),
                        orderBy.getOrderColumn(),
                        orderBy.getOrderDirection(),
                        from,
                        count
                },
                new RowMapper<CreativeTO>() {
                    @Override
                    public CreativeTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Long creativeId = rs.getLong("creative_id");
                        String name = rs.getString("name");
                        char status = rs.getString("status").charAt(0);
                        char qaStatus = rs.getString("qa_status").charAt(0);
                        DisplayStatus displayStatusFetched = Creative.getDisplayStatus(rs.getLong("display_status_id"));
                        Timestamp version = rs.getTimestamp("version");

                        Long accountId = rs.getLong("account_id");
                        String accountName = rs.getString("account_name");
                        char accountStatus = rs.getString("account_status").charAt(0);

                        Long sizeId = rs.getLong("size_id");
                        String sizeName = rs.getString("size_name");
                        char sizeStatus = rs.getString("size_status").charAt(0);

                        Long templateId = rs.getLong("template_id");
                        String templateName = rs.getString("template_name");
                        char templateStatus = rs.getString("template_status").charAt(0);

                        return CreativeTO.createBuilder(creativeId, name, status, qaStatus, displayStatusFetched)
                                .withVersion(version)
                                .withAccountId(accountId).withAccountName(accountName).withAccountStatus(accountStatus)
                                .withSize(sizeId, sizeName).withSizeDisplayStatus(sizeStatus)
                                .withTemplate(templateId, templateName).withTemplateDisplayStatus(templateStatus)
                                .build();
                    }
                }
        );

        return new DetachedList<>(list, list.size());
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "find('Account', #accountId)")
    public int findCreativesCount(
            Long accountId,
            List<DisplayStatus> displayStatuses,
            Long campaignId,
            Long sizeId,
            boolean allowTestAccounts,
            boolean allowDeletedOwner
    ) {
        return jdbcTemplate.withAuthContext().queryForObject(
                "select * from entityqueries.get_creatives_count(?::int,?::int,?::int,?::bool,?::bool,?::int[])",
                new Object[]{
                        accountId,
                        sizeId,
                        campaignId,
                        allowTestAccounts,
                        allowDeletedOwner,
                        jdbcTemplate.createArray("int", DisplayStatus.getIds(displayStatuses)),
                },
                Integer.class
        );
    }

    @Override
    public List<CreativeTO> findPendingFOROSCreatives(int firstRow, int maxResults) {
        return findCreatives(
                null,
                Arrays.asList(Creative.PENDING_FOROS),
                null, null,
                false,
                false,
                firstRow,
                maxResults,
                CreativeSortType.FIRSTREVIEWED
        );
    }

    @Override
    public boolean isBatchActionPossible(Collection<Long> ids, String action) {
        String restrictionName = "AdvertiserEntity." + action.toLowerCase();
        Collection<Creative> creatives = new JpaQueryWrapper<Creative>(em,
                "select c from Creative c where c.id in :ids")
                .setPrimitiveArrayParameter("ids", ids)
                .getResultList();
        for (Creative creative : creatives) {
            if (creative.getParentStatus() == Status.DELETED || !restrictionService.isPermitted(restrictionName, creative)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Collection<EntityTO> findCreativesForReport(Long accountId, Long campaignId, Long groupId, String name, int maxResults) {
        if (accountId == null && campaignId == null && groupId == null)
            return Collections.emptyList();
        ConditionStringBuilder sql = new ConditionStringBuilder("select new com.foros.session.EntityTO(c.id, c.name, c.status) FROM Creative c");
        sql.append(campaignId != null || groupId != null, ", CampaignCreative cc");
        sql.append(" where 1 = 1");
        sql.append(accountId != null, " and c.account.id = :accountId");
        sql.append(campaignId != null,  " and cc.creative = c AND cc.creativeGroup.campaign.id = :campaignId");
        sql.append(groupId != null, " and cc.creative = c AND cc.creativeGroup.id = :groupId");
        sql.append(StringUtil.isPropertyNotEmpty(name), " and upper(c.name) like upper(:name) escape '\\' ");
        sql.append(!userService.getMyUser().isDeletedObjectsVisible(), " and c.status != 'D' ");
        sql.append(" order by case c.status when 'D' then 2 else case c.status when 'I' then 1 else 0 end end, upper(c.name)");

        QueryWrapper<EntityTO> query = new JpaQueryWrapper<EntityTO>(em, sql.toString())
                .oneIf(accountId != null).setParameter("accountId", accountId)
                .oneIf(campaignId != null).setParameter("campaignId", campaignId)
                .oneIf(groupId != null).setParameter("groupId", groupId)
                .oneIf(StringUtil.isPropertyNotEmpty(name)).setLikeParameter("name", name);
        if (maxResults > 0)
            query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @Override
    public List<EntityTO> getIndexByIds(Collection<Long> creativeIds) {
        //noinspection unchecked
        return em.createQuery("select new com.foros.session.EntityTO(c.id, c.name, c.status) " +
                " from Creative c WHERE " + SQLUtil.formatINClause("c.id", creativeIds))
            .getResultList();
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('Creative',#creativeId)")
    public void resetRejectedCreativeExclusions(Long creativeId) {
        jdbcTemplate.execute("select exclusions.reset_rej_by_creative_id(?::int)", creativeId);
        auditService.logMessage(new Creative(creativeId), ActionType.UPDATE, ResultType.SUCCESS, "Creative was submitted for publisher approval");
    }

    @Override
    public void resetApprovedCreativeExclusions(Long creativeId) {
        jdbcTemplate.execute("select exclusions.reset_app_by_creative_id(?::int)", creativeId);
    }
}
