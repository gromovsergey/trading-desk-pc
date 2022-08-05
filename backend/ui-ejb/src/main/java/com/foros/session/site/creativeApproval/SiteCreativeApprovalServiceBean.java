package com.foros.session.site.creativeApproval;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.changeNode.ForceFieldChangeEntityChange;
import com.foros.config.ConfigService;
import com.foros.model.account.Account;
import com.foros.model.security.ActionType;
import com.foros.model.site.CreativeRejectReason;
import com.foros.model.site.Site;
import com.foros.model.site.SiteCreativeApproval;
import com.foros.model.site.SiteCreativeApprovalStatus;
import com.foros.model.site.SiteCreativePK;
import com.foros.model.site.ThirdPartyCreative;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.bulk.IdNameTO;
import com.foros.session.query.PartialList;
import com.foros.session.security.AuditService;
import com.foros.session.site.CreativeCategoryRecType;
import com.foros.session.site.KeyValueRecType;
import com.foros.session.site.SiteService;
import com.foros.util.CollectionUtils;
import com.foros.util.UrlUtil;
import com.foros.util.mapper.Converter;
import com.foros.util.posgress.PGArray;
import com.foros.util.posgress.PGRow;
import com.foros.util.preview.PreviewHelper;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "SiteCreativeApprovalService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class SiteCreativeApprovalServiceBean implements SiteCreativeApprovalService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;

    @EJB
    private SiteService siteService;

    @EJB
    private WalledGardenService wgService;

    @EJB
    private ConfigService configService;

    @EJB
    private RestrictionService restrictionService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @Override
    @Restrict(restriction = "PublisherEntity.reviewCreatives", parameters = "#siteId")
    @Validate(validation = "SiteCreativeApproval.update", parameters = {"#siteId", "#operation"})
    @Interceptors(CaptureChangesInterceptor.class)
    public SiteCreativeApproval update(Long siteId, SiteCreativeApprovalOperation operation) {
        Site site = em.find(Site.class, siteId);
        auditService.audit(site, ActionType.UPDATE);

        return updateInternal(site, operation, true);
    }

    @Override
    @Restrict(restriction = "PublisherEntity.reviewCreatives", parameters = "#approvalOperations.siteId")
    @Validate(validation = "SiteCreativeApproval.perform", parameters = "#approvalOperations")
    @Interceptors(CaptureChangesInterceptor.class)
    public void perform(SiteCreativeApprovalOperations approvalOperations) {
        Long siteId = approvalOperations.getSiteId();
        Site site = em.find(Site.class, siteId);
        auditService.audit(site, ActionType.UPDATE);

        for (SiteCreativeApprovalOperation operation : approvalOperations.getOperations()) {
            updateInternal(site, operation, false);
        }
    }

    @Override
    @Restrict(restriction = "PublisherEntity.reviewCreatives", parameters = "#operations.siteId")
    @Validate(validation = "ThirdPartyCreative.perform", parameters = "#operations")
    @Interceptors(CaptureChangesInterceptor.class)
    public void perform(ThirdPartyCreativesUpdateOperations operations) {
        Long siteId = operations.getSiteId();
        for (ThirdPartyCreative updated : operations.getThirdPartyCreatives()) {
            updated.setSiteId(siteId);
            ThirdPartyCreative existing = em.find(ThirdPartyCreative.class, updated.getId());
            if (existing == null) {
                if (updated.getPendingThirdPartyApproval() == null) {
                    updated.setPendingThirdPartyApproval(Boolean.FALSE);
                }
                em.persist(updated);
            } else {
                em.merge(updated);
            }
        }
    }

    private SiteCreativeApproval updateInternal(Site site, SiteCreativeApprovalOperation operation, boolean checkVersion) {
        SiteCreativePK id = new SiteCreativePK(operation.getCreative().getId(), site.getId());

        resetThirdPartyCreativeApproval(id);

        SiteCreativeApproval existing = em.find(SiteCreativeApproval.class, id);
        if (SiteCreativeApprovalOperationType.RESET == operation.getType()) {
            if (existing != null) {
                em.remove(existing);
                ForceFieldChangeEntityChange.addCollectionChange(
                        site,
                        "creativeApprovals",
                        existing,
                        ChangeType.REMOVE
                );
            }
            return null;
        } else {
            SiteCreativeApproval approval = new SiteCreativeApproval();
            approval.setId(id);
            approval.setApproval(operation.getType().getTarget());
            approval.setFeedback(operation.getFeedback());
            approval.setRejectReason(operation.getRejectReason());

            if (existing == null) {
                em.persist(approval);
                ForceFieldChangeEntityChange.addCollectionChange(
                        site,
                        "creativeApprovals",
                        approval,
                        ChangeType.ADD
                );
            } else {
                if (checkVersion) {
                    approval.setApprovalDate(operation.getVersion());
                } else {
                    approval.setApprovalDate(existing.getApprovalDate());
                }
                approval = em.merge(approval);
                ForceFieldChangeEntityChange.addCollectionChange(
                        site,
                        "creativeApprovals",
                        approval,
                        ChangeType.UNCHANGED
                );
            }
            return approval;
        }
    }

    private void resetThirdPartyCreativeApproval(SiteCreativePK id) {
        ThirdPartyCreative existing = em.find(ThirdPartyCreative.class, id);
        if (existing == null || !existing.getPendingThirdPartyApproval()) {
            return;
        }

        ThirdPartyCreative creative = new ThirdPartyCreative();
        creative.setId(id);
        creative.setPendingThirdPartyApproval(Boolean.FALSE);
        em.merge(creative);
    }

    @Override
    public PartialList<SiteCreativeApprovalTO> searchCreativeApprovals(CreativeExclusionBySiteSelector selector) {
        return searchImpl(selector, new SiteCreativeApprovalRowCallbackHandler());
    }

    @Override
    public PartialList<ThirdPartyCreative> searchThirdParty(CreativeExclusionBySiteSelector selector) {
        return searchImpl(selector, new ThirdPartyCreativeRowCallbackHandler(selector.getSiteId()));
    }

    private <T> PartialList<T> searchImpl(final CreativeExclusionBySiteSelector selector, SiteCreativeBaseRowCallbackHandler<T> handler) {
        if (selector.getSiteId() == null) {
            throw new ConstraintViolationException(BusinessErrors.FIELD_IS_REQUIRED, "errors.api.emptyCriteria.siteCreativeApproval");
        }
        Site site = em.find(Site.class, selector.getSiteId());
        if (site == null) {
            throw new EntityNotFoundException("Site with id=" + selector.getSiteId() + " not found!");
        }

        restrictionService.isPermitted("PublisherEntity.viewCreativesApproval", site);

        selector.setDestinationUrl(UrlUtil.stripSchema(selector.getDestinationUrl()));

        final Array approvalsArray = selector.getApprovals() == null ? null :
                jdbcTemplate.createArray("char", CollectionUtils.convert(selector.getApprovals(),
                        new Converter<SiteCreativeApprovalStatus, Character>() {
                            @Override
                            public Character item(SiteCreativeApprovalStatus value) {
                                return value.getLetter();
                            }
                        }
                ));

        final Array creativeIdsArray = jdbcTemplate.createArray("integer", selector.getCreativeIds());

        jdbcTemplate.query(
                "select * from exclusions.get_by_site_id(?::integer, ?::character[], ?::varchar, ?::integer[], ?::timestamp, ?::integer, ?::boolean, ?::boolean, ?::integer, ?::integer)",
                new Object[] {
                        selector.getSiteId(),
                        approvalsArray,
                        selector.getDestinationUrl(),
                        creativeIdsArray,
                        selector.getMinCreativeVersion(),
                        selector.getSizeId(),
                        selector.getHasThirdPartyId(),
                        selector.getPendingThirdPartyApproval(),
                        selector.getPaging().getCount(),
                        selector.getPaging().getFirst()
                },
                handler
        );
        List<T> data = handler.getData();

        int total;
        if (data.size() < selector.getPaging().getCount()) {
            total = selector.getPaging().getFirst() + data.size();
        } else {
            total = jdbcTemplate.queryForObject(
                    "select * from exclusions.get_by_site_id_cnt(?::integer, ?::character[], ?::varchar, ?::integer[], ?::timestamp, ?::integer, ?::boolean, ?::boolean)",
                    new Object[] {
                            selector.getSiteId(),
                            approvalsArray,
                            selector.getDestinationUrl(),
                            creativeIdsArray,
                            selector.getMinCreativeVersion(),
                            selector.getSizeId(),
                            selector.getHasThirdPartyId(),
                            selector.getPendingThirdPartyApproval()
                    },
                    Integer.class
            );
        }

        return new PartialList<>(total, selector.getPaging(), data);
    }

    private interface SiteCreativeBaseRowCallbackHandler<T> extends RowCallbackHandler {
        List<T> getData();
    }

    private class SiteCreativeApprovalRowCallbackHandler implements SiteCreativeBaseRowCallbackHandler<SiteCreativeApprovalTO> {
        private List<SiteCreativeApprovalTO> data = new ArrayList<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            CreativeForApprovalTO creative = new CreativeForApprovalTO();
            creative.setId(rs.getLong("creative_id"));
            creative.setDestinationUrl(rs.getString("creative_destination_url"));
            creative.setSize(new IdNameTO(rs.getLong("size_id"), rs.getString("size_name")));
            creative.setTemplateId(rs.getLong("template_id"));
            creative.setVersion(rs.getTimestamp("creative_version"));
            String calculatePreviewUrl = PreviewHelper.calculatePreviewUrl(
                    configService.detach(), creative.getTemplateId(), creative.getSizeId(), creative.getId());
            creative.setPreviewUrl(calculatePreviewUrl);

            Array visualCategoriesArr = rs.getArray("creative_visual_categories");
            creative.setVisualCategories(toCategoriesList(visualCategoriesArr));

            Array contentCategoriesArr = rs.getArray("creative_content_categories");
            creative.setContentCategories(toCategoriesList(contentCategoriesArr));

            SiteCreativeApprovalTO to = new SiteCreativeApprovalTO();
            to.setCreative(creative);
            to.setApprovalStatus(SiteCreativeApprovalStatus.valueOf(rs.getString("creative_approval_status").charAt(0)));
            to.setVersion(rs.getTimestamp("creative_approval_version"));
            to.setFeedback(rs.getString("feedback"));
            int reasonId = rs.getInt("reject_reason_id");
            if(!rs.wasNull()) {
                to.setRejectReason(CreativeRejectReason.valueOf(reasonId));
            }
            data.add(to);
        }

        /** @noinspection unchecked*/
        private List<CreativeCategoryRecType> toCategoriesList(final Array array) throws SQLException {
            return PGArray.read(array, new PGRow.Converter<CreativeCategoryRecType>() {
                @Override
                public CreativeCategoryRecType item(PGRow row) {
                    CreativeCategoryRecType creativeCategoryRec = new CreativeCategoryRecType();
                    creativeCategoryRec.setName(row.getString(1));
                    try {
                        creativeCategoryRec.setRtbCategories(PGArray.read(row.getSubArray(2, array), new PGRow.Converter<KeyValueRecType>() {
                            @Override
                            public KeyValueRecType item(PGRow row) {
                                KeyValueRecType rec = new KeyValueRecType();
                                rec.setKey(row.getString(0));
                                rec.setName(row.getString(1));
                                return rec;
                            }
                        }));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return creativeCategoryRec;
                }
            });
        }

        @Override
        public List<SiteCreativeApprovalTO> getData() {
            return data;
        }
    }

    private class ThirdPartyCreativeRowCallbackHandler implements SiteCreativeBaseRowCallbackHandler<ThirdPartyCreative> {
        private Long siteId;
        private List<ThirdPartyCreative> data = new ArrayList<>();

        public ThirdPartyCreativeRowCallbackHandler(Long siteId) {
            this.siteId = siteId;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            ThirdPartyCreative to = new ThirdPartyCreative();
            to.setId(new SiteCreativePK(rs.getLong("creative_id"), siteId));
            to.setThirdPartyCreativeId(rs.getString("third_party_creative_id"));
            String thirdPartyApproval = rs.getString("third_party_approval");
            to.setPendingThirdPartyApproval(thirdPartyApproval == null || thirdPartyApproval.charAt(0) == 'N' ? Boolean.FALSE : Boolean.TRUE);

            data.add(to);
        }

        @Override
        public List<ThirdPartyCreative> getData() {
            return data;
        }
    }

    @Override
    public CreativeSiteApprovals sitesByCreative(Long creativeId) {
        final CreativeSiteApprovals result = new CreativeSiteApprovals();
        result.setList(jdbcTemplate.query(
                "select * from exclusions.get_by_creative_id(?::integer)",
                new Object[] { creativeId },
                new RowMapper<CreativeSiteApprovalTO>() {
                    @Override
                    public CreativeSiteApprovalTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        CreativeSiteApprovalTO to = new CreativeSiteApprovalTO();
                        to.setPublisher(new IdNameTO(
                                rs.getLong("publisher_id"),
                                rs.getString("publisher_name")
                        ));
                        to.setPublisherDisplayStatus(Account.getDisplayStatus(rs.getLong("publisher_display_status_id")));
                        to.setSite(new IdNameTO(
                                rs.getLong("site_id"),
                                rs.getString("site_name")
                        ));
                        to.setSiteDisplayStatus(Site.getDisplayStatus(rs.getLong("site_display_status_id")));
                        to.setApprovalStatus(SiteCreativeApprovalStatus.valueOf(rs.getString("creative_approval_status").charAt(0)));
                        to.setFeedback(rs.getString("feedback"));
                        int reasonId = rs.getInt("reject_reason_id");
                        if(!rs.wasNull()) {
                            to.setRejectReason(CreativeRejectReason.valueOf(reasonId));
                        }

                        if (to.getApprovalStatus() == SiteCreativeApprovalStatus.REJECTED) {
                            result.hasRejected(true);
                        }

                        return to;
                    }
                }
        ));

        return result;
    }

}
