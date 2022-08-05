package com.foros.test.factory;

import com.foros.model.campaign.CCGSchedule;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CcgRate;
import com.foros.model.campaign.RateType;

import java.math.BigDecimal;
import org.joda.time.LocalDate;

public abstract class CCGTestFactory extends TestFactory<CampaignCreativeGroup> {
    public CcgRate createCcgRate(CampaignCreativeGroup ccg, RateType rateType, BigDecimal value) {
        CcgRate ccgRate = new CcgRate();
        switch (rateType) {
            case CPA:
                ccgRate.setCpa(value);
                break;
            case CPC:
                ccgRate.setCpc(value);
                break;
            case CPM:
                ccgRate.setCpm(value);
                break;
        }
        ccgRate.setCcg(ccg);
        ccgRate.setRateType(rateType);
        ccgRate.setEffectiveDate(new LocalDate().plusYears(100).toDate());

        ccg.setCcgRate(ccgRate);

        return ccgRate;
    }

    public void addSchedule(CampaignCreativeGroup ccg, Long timeFrom, Long timeTo) {
        CCGSchedule ccgSchedule = new CCGSchedule();
        ccgSchedule.setCampaignCreativeGroup(ccg);
        ccgSchedule.setTimeFrom(timeFrom);
        ccgSchedule.setTimeTo(timeTo);

        ccg.getCcgSchedules().add(ccgSchedule);
    }
}
