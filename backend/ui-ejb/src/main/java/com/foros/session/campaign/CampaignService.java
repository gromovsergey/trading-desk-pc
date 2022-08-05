package com.foros.session.campaign;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.TGTType;
import com.foros.model.campaign.WeekSchedule;
import com.foros.model.channel.Channel;
import com.foros.session.EntityTO;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.CampaignSelector;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.ejb.Local;

import org.joda.time.LocalDate;

@Local
public interface CampaignService {

    Long create(Campaign campaign);

    Long createCopy(Long id);

    Campaign update(Campaign campaign);

    void delete(Long id);

    void undelete(Long id);

    void activate(Long id);

    void inactivate(Long id);

    Campaign find(Long id);

    Campaign findForEdit(Long id);

    Campaign view(Long id);

    void refresh(Long id);

    List<EntityTO> getCampaignsByAccount(Long accountId);

    List<EntityTO> getTextCampaignsByAccount(Long accountId);

    List<Object[]> getPendingCCGTreeRawDataForAccount(AdvertiserAccount account);

    CCGStatsTOList getCCGStats(Long campaignId, LocalDate fromDate, LocalDate toDate);

    CampaignStatsTO getStats(Long campaignId);

    ChartStats getChartStats(Long campaignId, String xspec, String y1spec, String y2spec);

    /**
     * Method returns a collection of recently changed campaigns.<br/>
     * Campaign objectes are not fully initialized and are NOT managed in persisntance context.<br/>
     * Collection sorted by descesing campaign <b>modification date.</b><br/>
     * <br/>
     * <b>modification date</b> - is a max(campaingn.versin, max (ccgs.version))
     *
     * @param maxCampaigns - a number of returned campaigns
     * @return sorted collection of campaign objects.
     */
    Collection<Campaign> findRecentlyChanged(int maxCampaigns, int maxCcgsInCampaign);

    void activateGroups(Long campaignId, Collection<Long> ccgIds);
    void inactivateGroups(Long campaignId, Collection<Long> ccgIds);
    void approveGroups(Long campaignId, Collection<Long> ccgIds);
    void declineGroups(Long campaignId, Collection<Long> ccgIds, String reason);

    void createOrUpdateAll(Long accountId, Collection<Campaign> campaigns);
    void validateAll(Long accountId, TGTType tgtType, Collection<Campaign> campaigns);

    boolean isEndDateCleanAllowed(Long campaignId);

    Result<Campaign> get(CampaignSelector campaignSelector);

    OperationsResult perform(Operations<Campaign> operations);

    Set<String> getAffectedCCGForCampaignDelivery(Long campaignId, Collection<? extends WeekSchedule> campaignScheduleSet);

    List<TreeFilterElementTO> searchCampaigns(Long advertiserId, Boolean display, Boolean withDeletedGroups);

    List<TreeFilterElementTO> searchCampaignsBySizeType(Long advertiserId, Long sizeTypeId, Boolean withDeletedGroups);

    BigDecimal calculateDailyBudget(Long campaignId);

    BigDecimal getSpentCampaignBudget(Long campaignId, TimeZone timeZone, Date today);

    boolean hasNoFreqCapWarningForCampaign(Campaign campaign);

    boolean isBatchActionPossible(Collection<Long> ids, String action);

    void activateAll(Collection<Long> ids);

    void inactivateAll(Collection<Long> ids);

    void deleteAll(Collection<Long> ids);

    Collection<Channel> getExcludedChannels(Long campaignId);

    public class CCGStatsTOList extends ArrayList<CCGStatsTO> {
        private boolean isShowPostImpConv;
        private boolean isShowPostClickConv;

        public boolean isShowPostImpConv() {
            return isShowPostImpConv;
        }

        public void setShowPostImpConv(boolean isShowPostImpConv) {
            this.isShowPostImpConv = isShowPostImpConv;
        }

        public boolean isShowPostClickConv() {
            return isShowPostClickConv;
        }

        public void setShowPostClickConv(boolean isShowPostClickConv) {
            this.isShowPostClickConv = isShowPostClickConv;
        }
    }
}