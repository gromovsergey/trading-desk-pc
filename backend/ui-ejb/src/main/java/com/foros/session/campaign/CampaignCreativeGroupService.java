package com.foros.session.campaign;

import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.GeoChannel;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.EntityTO;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.bulk.BulkOperation;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.CampaignCreativeGroupSelector;
import com.foros.session.campaign.ccg.expressionPerformance.ExpressionPerformanceReportParameters;
import com.foros.session.channel.targeting.CCGTargetingStatsTO;
import com.foros.session.creative.CreativeSetTO;
import com.foros.session.query.PartialList;
import com.foros.session.status.Approvable;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import org.joda.time.LocalDate;

@Local
public interface CampaignCreativeGroupService extends Approvable {

    Long create(CampaignCreativeGroup creativeGroup);

    void linkConversions(Collection<Long> ids, Collection<Long> conversionIds);

    void createAll(List<CampaignCreativeGroup> creativeGroups);

    Long createCopy(Long id);

    Long createCopyWithNewCampaign(Long id, Long campaignId);

    CampaignCreativeGroup update(CampaignCreativeGroup creativeGroup);

    void delete(Long id);

    void undelete(Long id);

    void inactivate(Long id);

    void activate(Long id);

    void refresh(Long id);

    CampaignCreativeGroup find(Long id);

    CampaignCreativeGroup findWithCC(Long id);

    CampaignCreativeGroup view(Long id);

    PartialCreativeSetTOList getLinkedCreatives(Long ccgId, LocalDate dateFrom, LocalDate dateTo, int from, int count);

    List<CombinedCCGKeywordTO> getLinkedKeywords(Long ccgId, LocalDate dateFrom, LocalDate dateTo);

    List<EntityTO> getIndex(Long campaignId);

    List<EntityTO> getIndexByIds(Collection<Long> ccgIds);

    List<EntityTO> getIndexByTargetType(Long campaignId, TGTType targetType);

    Collection<EntityTO> fetchTargetableSites(boolean testAccount, String countryCode);

    Collection<ISPColocationTO> findLinkedColocations(Long id);

    Collection<ISPColocationTO> findColocationsByIds(Set<Long> ids);

    Collection<ISPColocationTO> findColocationsByName(String name, String countryCode, boolean testAccount, int maxResults);

    ChartStats getChartStats(Long ccgId, String xspec, String y1spec, String y2spec);

    long getTotalImpressions(Long ccgId);

    boolean isBatchActionPossible(Collection<Long> ids, String action);

    BigDecimal calculateDynamicDailyBudget(Long ccgId, Long campaignId, Long accountId, BigDecimal budget, Date startDate, Date endDate);

    boolean checkFixedDailyBudget(Long ccgId, Long campaignId, Long accountId, BigDecimal totalBudget, BigDecimal dailyBudget,
                                  Date startDate, Date endDate);

    void createOrUpdateAll(Long campaignId, Collection<CampaignCreativeGroup> creativeGroups);

    CCGLightWeightStatsTO getLightWeightStats(Long ccgId, boolean isGross);

    ChannelRatesTO getChannelTargetingRates(Long channelId);

    ChannelRatesTO getCcgTargetingRates(Long ccgId, Long channelId);

    void validateAll(Campaign campaign, TGTType tgtType, Collection<CampaignCreativeGroup> creativeGroups);

    Result<CampaignCreativeGroup> get(CampaignCreativeGroupSelector ccgSelector);

    OperationsResult perform(Operations<CampaignCreativeGroup> ccgOperations);

    void perform(Long advertiserId, List<Long> ccgIds, BulkOperation<CampaignCreativeGroup> operation);

    CampaignCreativeGroup findForUpdateTarget(Long id);

    CampaignCreativeGroup findForUpdateUserSampleGroups(Long id);

    void updateTarget(CampaignCreativeGroup group);

    boolean isFallingOutOfCampaignSchedule(Long ccgId);

    List<TreeFilterElementTO> searchGroups(Long campaignId);

    List<TreeFilterElementTO> searchGroupsBySizeType(Long campaignId, Long sizeTypeId);

    void updateUserSampleGroups(CampaignCreativeGroup group);

    void updateGeoTarget(CampaignCreativeGroup group, List<GeoChannel> geoChannels);

    void updateDeviceTargeting(CampaignCreativeGroup group);

    SimpleReportData getExpressionPerformanceReportData(ExpressionPerformanceReportParameters parameters);

    CCGTargetingStatsTO fetchTargetingStats(Long ccgId, boolean withSiteStats);

    boolean hasNoFreqCapWarningForCcg(CampaignCreativeGroup group);

    /**
     * @param filterByCountry is added to optionally exclude sites of publishers whose country differs from site advertiser one see OUI-24235
     */
    Collection<EntityTO> fetchLinkedSites(Long ccgId, boolean filterByCountry);

    List<String> findCCGNamesByCampaign(Long campaignId);

    public class PartialCreativeSetTOList extends PartialList<CreativeSetTO> {
        private static final PartialCreativeSetTOList EMPTY_LIST = new PartialCreativeSetTOList(0, 0, Collections.<CreativeSetTO>emptyList(), false, false);

        private boolean showPostImpConv;
        private boolean showPostClickConv;


        public boolean isShowPostImpConv() {
            return showPostImpConv;
        }

        public boolean isShowPostClickConv() {
            return showPostClickConv;
        }

        public PartialCreativeSetTOList(int total, int from, List<CreativeSetTO> values, boolean showPostImpConv, boolean showPostClickConv) {
            super(total, from, values);
            this.showPostImpConv = showPostImpConv;
            this.showPostClickConv = showPostClickConv;
        }

        public static PartialCreativeSetTOList emptyList() {
            return EMPTY_LIST;
        }

    }

    List<LinkedConversionTO> getLinkedConversions(Long ccgId, LocalDate dateFrom, LocalDate dateTo);
}
