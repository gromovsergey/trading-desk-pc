package com.foros.session.admin.country.ctra;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.Status;
import com.foros.model.ctra.CTRAlgorithmAdvertiserExclusion;
import com.foros.model.ctra.CTRAlgorithmAdvertiserExclusionPK;
import com.foros.model.ctra.CTRAlgorithmCampaignExclusion;
import com.foros.model.ctra.CTRAlgorithmCampaignExclusionPK;
import com.foros.model.ctra.CTRAlgorithmData;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.NamedTO;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.security.AuditService;
import com.foros.util.ConditionStringBuilder;
import com.foros.util.EntityUtils;
import com.foros.util.SQLUtil;
import com.foros.util.StringUtil;
import com.foros.util.jpa.NativeQueryWrapper;
import com.foros.util.jpa.QueryWrapper;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

@Stateless(name = "CTRAlgorithmService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class CTRAlgorithmServiceBean implements CTRAlgorithmService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;

    @Override
    public CTRAlgorithmData find(String countryCode) {
        CTRAlgorithmData algorithmData = em.find(CTRAlgorithmData.class, countryCode);
        if (algorithmData == null) {
            throw new EntityNotFoundException("Algorithm data for country code = " + countryCode + " not found");
        }
        return algorithmData;
    }

    @Override
    public CTRAlgorithmData findByCountryId(Long countryId) {
        return (CTRAlgorithmData) em.createQuery("select d from CTRAlgorithmData d where d.country.countryId = :countryId").
                setParameter("countryId", countryId).
                getSingleResult();

    }

    @Override
    @Restrict(restriction = "Country.update")
    @Validate(validation = "CTRAlgorithm.save", parameters = {"#data", "#advertiserExclusions", "#campaignExclusions"})
    @Interceptors({CaptureChangesInterceptor.class})
    public void save(CTRAlgorithmData data, Collection<Long> advertiserExclusions, Collection<Long> campaignExclusions) {
        CTRAlgorithmData existingData = em.find(CTRAlgorithmData.class, data.getCountryCode());
        prepareAdvertiserExclusions(data, advertiserExclusions);
        prepareCampaignExclusions(data, campaignExclusions);
        if (existingData != null) {
            data = em.merge(data);
        } else {
            em.persist(data);
        }
        auditService.audit(data, ActionType.UPDATE);
    }

    private void prepareAdvertiserExclusions(CTRAlgorithmData data, Collection<Long> advertiserExclusionsIds) {
        Set<CTRAlgorithmAdvertiserExclusion> exclusions = new LinkedHashSet<CTRAlgorithmAdvertiserExclusion>();
        for (Long advertiserId : advertiserExclusionsIds) {
            CTRAlgorithmAdvertiserExclusionPK pk = new CTRAlgorithmAdvertiserExclusionPK();
            pk.setAdvertiserId(advertiserId);
            pk.setCountryCode(data.getCountryCode());
            CTRAlgorithmAdvertiserExclusion exclusion = new CTRAlgorithmAdvertiserExclusion();
            exclusion.setPk(pk);
            exclusions.add(exclusion);
        }
        data.setAdvertiserExclusions(exclusions);
    }

    private void prepareCampaignExclusions(CTRAlgorithmData data, Collection<Long> campaignExclusionsIds) {
        Set<CTRAlgorithmCampaignExclusion> exclusions = new LinkedHashSet<CTRAlgorithmCampaignExclusion>();
        for (Long campaignId : campaignExclusionsIds) {
            CTRAlgorithmCampaignExclusionPK pk = new CTRAlgorithmCampaignExclusionPK();
            pk.setCountryCode(data.getCountryCode());
            pk.setCampaignId(campaignId);
            CTRAlgorithmCampaignExclusion exclusion = new CTRAlgorithmCampaignExclusion();
            exclusion.setPk(pk);
            exclusions.add(exclusion);
        }
        data.setCampaignExclusions(exclusions);
    }

    @Override
    public Collection<NamedTO> displayAdvertisers(Collection<Long> ids) {
        Collection<NamedTO> result = new LinkedList<NamedTO>();
        if (ids.size() > 0) {

            QueryWrapper<Object[]> qw = new NativeQueryWrapper<Object[]>(em, "select ad.account_id ad_id, ad.name ad_name, ad.status ad_status," +
                    " ag.name ag_name, ag.status ag_status" +
                    " from account ad" +
                    " left join account ag on ag.account_id = ad.agency_account_id" +
                    " where ad.account_id in :ids");
            qw.setArrayParameter("ids", ids);
            List<Object[]> resultList = qw.getResultList();
            for (Object[] row : resultList) {
                Long advId = ((Number) row[0]).longValue();

                String advName = (String) row[1];
                Character advStatus = (Character) row[2];
                String agnName = (String) row[3];
                Character agnStatus = (Character) row[4];
                if (advStatus == 'D') {
                    advName = EntityUtils.appendStatusSuffix(advName, Status.DELETED);
                }
                if (StringUtil.isPropertyNotEmpty(agnName)) {
                    if (agnStatus == 'D') {
                        agnName = EntityUtils.appendStatusSuffix(agnName, Status.DELETED);
                    }
                    advName = agnName + "|" + advName;
                }
                result.add(new NamedTO(advId, advName));
            }
        }
        return result;
    }

    @Override
    public Collection<NamedTO> findAdvertisers(String name, String countryCode, int maxResults) {
        Collection<NamedTO> result = new LinkedList<NamedTO>();
        boolean compositeName = name.contains("|");
        String[] composition = name.split("\\|");
        if (composition.length > 0) {
            String agencyName = compositeName ? composition[0] : null;
            String advName = compositeName && composition.length > 1 ? composition[1] : name;
            boolean listAllChildAdvertisers = compositeName && composition.length < 2;
            ConditionStringBuilder sql = new ConditionStringBuilder();
            sql.append("select");
            sql.append(" ad.account_id, ").append(compositeName, "ag.name||'|'||").append("ad.name");
            sql.append(" from account ad");
            sql.append(compositeName, " join account ag on ag.account_id = ad.agency_account_id");
            sql.append(" where ad.role_id in (1,4) and ad.status <> 'D'");
            sql.append(" and ad.country_code = :countryCode");
            sql.append(!listAllChildAdvertisers, " and UPPER(ad.name) like :advName");
            sql.append(!compositeName, " and ad.agency_account_id is null");
            sql.append(compositeName, " and ag.name = :agencyName and ag.status <> 'D'");
            QueryWrapper<Object[]> qw = new NativeQueryWrapper<Object[]>(em, sql.toString()){
                @Override
                public QueryWrapper<Object[]> setLikeParameter(String param, String value) {
                    if (value == null) {
                        value = "";
                    }
                    value = SQLUtil.getEscapedString(value, '\\') + "%";
                    return setParameter(param,value);
                }
            };
            qw.oneIf(!listAllChildAdvertisers).setLikeParameter("advName", advName.toUpperCase());
            qw.oneIf(compositeName).setParameter("agencyName", agencyName);
            qw.setParameter("countryCode", countryCode);
            qw.setMaxResults(maxResults);

            for (Object[] row : qw.getResultList()) {
                result.add(new NamedTO(((Number)row[0]).longValue(), (String) row[1]));
            }
        }
        return result;
    }

    @Override
    public Long findAdvertiserId(String name, String countryCode) {
        Long result = null;
        String[] composition = name.split("\\|");
        boolean compositeName = composition.length > 1;
        String agencyName = compositeName ? composition[0] : null;
        String advName = compositeName ? composition[1] : name;
        ConditionStringBuilder sql = new ConditionStringBuilder();
        sql.append(" select ad.account_id from account ad");
        sql.append(compositeName, " join account ag on ag.account_id = ad.agency_account_id");
        sql.append(" where ad.role_id in (1,4) and ad.status <> 'D'");
        sql.append(" and ad.country_code = :countryCode");
        sql.append(" and ad.name = :advName");
        sql.append(!compositeName, " and ad.agency_account_id is null");
        sql.append(compositeName, " and ag.name = :agencyName and ag.status <> 'D'");
        QueryWrapper<Long> qw = new NativeQueryWrapper<Long>(em, sql.toString());
        qw.setParameter("advName", advName);
        qw.oneIf(compositeName).setParameter("agencyName", agencyName);
        qw.setParameter("countryCode", countryCode);
        List<Long> rs = qw.getResultList();
        if (rs.size() == 1) {
            result = ((Number) rs.get(0)).longValue();
        }
        return result;
    }
}
