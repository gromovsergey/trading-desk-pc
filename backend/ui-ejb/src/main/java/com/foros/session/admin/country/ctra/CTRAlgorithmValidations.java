package com.foros.session.admin.country.ctra;

import com.foros.model.campaign.Campaign;
import com.foros.model.ctra.CTRAlgorithmAdvertiserExclusion;
import com.foros.model.ctra.CTRAlgorithmData;
import com.foros.util.ConditionStringBuilder;
import com.foros.util.jpa.JpaQueryWrapper;
import com.foros.util.jpa.NativeQueryWrapper;
import com.foros.util.jpa.QueryWrapper;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;

import com.foros.validation.strategy.ValidationMode;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@LocalBean
@Stateless
@Validations
public class CTRAlgorithmValidations {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Validation
    public void validateSave(ValidationContext validationContext, @ValidateBean(ValidationMode.CREATE) CTRAlgorithmData data, Collection<Long> advertiserExclusions, Collection<Long> campaignExclusions) {
        ValidationContext context = validationContext.createSubContext(data);

        validateAdvertiserExclusions(context, advertiserExclusions, data.getCountryCode());
        validateCampaignExclusions(context, campaignExclusions, data.getCountryCode());
        validateAdjustments(context, data);
    }

    private void validateAdjustments(ValidationContext context, CTRAlgorithmData data) {
        Integer clicksInterval1Days = data.getClicksInterval1Days();
        Integer clicksInterval2Days = data.getClicksInterval2Days();
        if (clicksInterval1Days != null && clicksInterval2Days != null && clicksInterval1Days >= clicksInterval2Days) {
            context
                .addConstraintViolation("ctrAlgorithmData.interval.error")
                .withPath("historyClicks");
        }

        Integer impsInterval1Days = data.getImpsInterval1Days();
        Integer impsInterval2Days = data.getImpsInterval2Days();
        if (impsInterval1Days != null && impsInterval2Days != null && impsInterval1Days >= impsInterval2Days) {
            context
            .addConstraintViolation("ctrAlgorithmData.interval.error")
            .withPath("historyImpressions");
        }
    }

    private void validateAdvertiserExclusions(ValidationContext context, Collection<Long> advertiserExclusions, String countryCode) {
        if (!advertiserExclusions.isEmpty()) {

            CTRAlgorithmData persistentData = em.find(CTRAlgorithmData.class, countryCode);
            Set<CTRAlgorithmAdvertiserExclusion> persistentExclusions = persistentData.getAdvertiserExclusions();
            Set<Long> existingIds = new HashSet(persistentExclusions.size());
            for (CTRAlgorithmAdvertiserExclusion ex : persistentExclusions) {
                existingIds.add(ex.getPk().getAdvertiserId());
            }
            boolean hadExclusions = existingIds.size() > 0;

            ConditionStringBuilder sql = new ConditionStringBuilder();
            sql.append("select count(*) from account ad")
                    .append(" left join account ag on ag.account_id = ad.agency_account_id")
                    .append(" where ad.role_id in (1,4) and ad.account_id in :ids")
                    .append(" and ad.country_code = :countryCode")
                    .append(" and ( ad.status <> 'D' and (ag.status <> 'D' or ag.status is null)")
                    .append(hadExclusions, " or ad.account_id in :existingIds")
                    .append(" )");
            QueryWrapper qw = new NativeQueryWrapper(em, sql.toString());

            qw.setArrayParameter("ids", advertiserExclusions);
            qw.setParameter("countryCode", countryCode);
            qw.oneIf(hadExclusions).setArrayParameter("existingIds", existingIds);
            long realAdvertisersCount = ((Number) qw.getSingleResult()).longValue();
            if (advertiserExclusions.size() != realAdvertisersCount) {
                context.addConstraintViolation("errors.invalidInput").withPath("advertiserExclusions");
            }
        }
    }

    private void validateCampaignExclusions(ValidationContext context, Collection<Long> campaignExclusions, String countryCode) {
        if (!campaignExclusions.isEmpty()) {
            JpaQueryWrapper<Campaign> qw = new JpaQueryWrapper<Campaign>(em, "select c from Campaign c where c.id in :ids and c.account.country.countryCode = :countryCode");
            qw.setArrayParameter("ids", campaignExclusions);
            qw.setParameter("countryCode", countryCode);
            List<Campaign> realCampaigns = qw.getResultList();
            if (realCampaigns.size() != campaignExclusions.size()) {
                for (Long excludedId : campaignExclusions) {
                    Campaign stub = new Campaign(excludedId);
                    if (!realCampaigns.contains(stub)) {
                        context.addConstraintViolation("ctrAlgorithmData.byCampaign.wrongIdError").withPath("campaignExclusions").withParameters(excludedId, "{global.country." + countryCode + ".name}");
                    }
                }
            }
        }
    }
}
