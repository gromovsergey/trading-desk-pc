package app.programmatic.ui.flight.tool;

import static com.foros.rs.client.model.advertising.campaign.ChannelTarget.UNTARGETED;
import static com.foros.rs.client.model.advertising.campaign.ChannelTarget.TARGETED;
import static app.programmatic.ui.common.tool.converter.LocalDateTimeConverter.toEndOfDay;
import static app.programmatic.ui.common.tool.converter.LocalDateTimeConverter.toStartOfDay;

import com.foros.rs.client.model.advertising.RateType;
import com.foros.rs.client.model.advertising.campaign.CampaignCreativeGroup;
import com.foros.rs.client.model.advertising.campaign.CCGBidStrategy;
import com.foros.rs.client.model.advertising.campaign.CCGRate;
import com.foros.rs.client.model.advertising.campaign.CCGSchedule;
import com.foros.rs.client.model.advertising.campaign.CCGType;
import com.foros.rs.client.model.advertising.campaign.FrequencyCap;
import com.foros.rs.client.model.advertising.campaign.TargetType;
import com.foros.rs.client.model.entity.EntityLink;
import app.programmatic.ui.common.tool.converter.XmlDateTimeConverter;
import app.programmatic.ui.common.tool.foros.ForosHelper;
import app.programmatic.ui.flight.dao.model.FlightSchedule;
import app.programmatic.ui.flight.dao.model.LineItem;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class CcgBuilder {
    private static final CampaignCreativeGroup CCG_DEFAULTS = initCcgDefaults();

    public static CampaignCreativeGroup build(LineItem lineItem, Long campaignId, Long expressionChannelId,
                                              String countryCode, String zone, CampaignCreativeGroup existing) {
        CampaignCreativeGroup notModifiable = existing != null ? existing : CCG_DEFAULTS;
        CampaignCreativeGroup result = new CampaignCreativeGroup();

        result.setCampaign(ForosHelper.createEntityLink(campaignId));

        setIfNotNull(lineItem.getDeliveryPacing(),  (value) -> result.setDeliveryPacing(BaseBuilder.convertDeliveryPacing(value)));
        setIfNotNull(lineItem.getDailyBudget(),     (value) -> result.setDailyBudget(value));
        setIfNotNull(lineItem.getBudget(),          (value) -> result.setBudget(value));

        updateChannelTarget(expressionChannelId, result);

        result.setDateStart(XmlDateTimeConverter.convertDateTime(toStartOfDay(lineItem.getDateStart()), zone));
        result.setDateEnd(XmlDateTimeConverter.convertDateTime(toEndOfDay(lineItem.getDateEnd()), zone));
        result.setLinkedToCampaignEndDateFlag(lineItem.getDateEnd() == null);

        result.setId(notModifiable.getId());
        result.setName(lineItem.getName());
        //result.setStatus(StatusHelper.getRsStatusByMajorStatus(lineItem.getDisplayStatus()));
        result.setFrequencyCap(BaseBuilder.convertFrequencyCap(lineItem.getFrequencyCap(), notModifiable.getFrequencyCap()));
        result.setCcgRate(convertToCcgRate(lineItem));
        result.setCcgSchedules(lineItem.getSchedules().stream()
                .map(CcgBuilder::convertToCcgSchedule)
                .collect(Collectors.toList()));
        result.setDeliveryScheduleFlag(!lineItem.getSchedules().isEmpty());
        result.setBidStrategy(notModifiable.getBidStrategy());
        result.setCcgType(notModifiable.getCcgType());
        result.setTgtType(notModifiable.getTgtType());
        result.setCountry(countryCode);

        if (lineItem.getMinCtrGoal() != null // ToDo: do we need null checking?
                && BigDecimal.ZERO.compareTo(lineItem.getMinCtrGoal()) > 0) {
            result.setMinCtrGoal(lineItem.getMinCtrGoal());
            result.setBidStrategy(CCGBidStrategy.MINIMUM_CTR_GOAL);
        } else {
            result.setMinCtrGoal(notModifiable.getMinCtrGoal());
            result.setBidStrategy(notModifiable.getBidStrategy());
        }

        result.setSites(idsToLinks(lineItem.getSiteIds()));
        result.setConversions(idsToLinks(lineItem.getConversionIds()));
        result.setGeoChannels(idsToLinks(lineItem.getGeoChannelIds()));
        result.setGeoChannelsExcluded(idsToLinks(lineItem.getExcludedGeoChannelIds()));
        result.setDeviceChannels(idsToLinks(lineItem.getDeviceChannelIds()));
//        result.setColocations(notModifiable.getColocations());

        result.setUpdated(XmlDateTimeConverter.convertTimestamp(lineItem.getCcgVersion(), "GMT"));

        return result;
    }

    public static CampaignCreativeGroup updateChannelTarget(Long expressionChannelId, CampaignCreativeGroup result) {
        result.setChannel(expressionChannelId == null ? null : ForosHelper.createEntityLink(expressionChannelId));
        result.setChannelTarget(expressionChannelId == null ? UNTARGETED : TARGETED);
        return result;
    }

    private static CampaignCreativeGroup initCcgDefaults() {
        CampaignCreativeGroup result = new CampaignCreativeGroup();

        result.setDateEnd(null);
        result.setCcgType(CCGType.DISPLAY);
        result.setMinCtrGoal(BigDecimal.ZERO);
        result.setTgtType(TargetType.CHANNEL);
        result.setBidStrategy(CCGBidStrategy.MAXIMISE_REACH);
        result.setDeliveryScheduleFlag(false);
        result.setFrequencyCap(new FrequencyCap());
        result.setGeoChannels(null);
        result.setGeoChannelsExcluded(null);
        result.setDeviceChannels(null);
        result.setColocations(null);
        result.setConversions(null);

        return result;
    }

    private static CCGSchedule convertToCcgSchedule(FlightSchedule schedule) {
        CCGSchedule result = new CCGSchedule();
        result.setTimeFrom(schedule.getTimeFrom());
        result.setTimeTo(schedule.getTimeTo());

        return result;
    }

    private static CCGRate convertToCcgRate(LineItem lineItem) {
        CCGRate result = new CCGRate();

        switch (lineItem.getRateType()) {
            case CPM:
            case MCPM:
                result.setRateType(RateType.CPM);
                break;
            case CPC:
                result.setRateType(RateType.CPC);
                break;
        }
        result.setValue(lineItem.getRateValue());

        return result;
    }

    private static List<EntityLink> idsToLinks(List<Long> src) {
        return src.stream()
                .map(id -> ForosHelper.createEntityLink(id))
                .collect(Collectors.toList());
    }

    private static <T> void setIfNotNull(T value, SetAware<T> target) {
        if (value != null) {
            target.set(value);
        }
    }

    private static <T extends Collection> void setIfNotEmpty(T value, SetAware<T> target) {
        if (value != null && !value.isEmpty()) {
            target.set(value);
        }
    }

    private interface SetAware<T> {
        void set(T value);
    }
}
