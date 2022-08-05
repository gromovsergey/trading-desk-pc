package app.programmatic.ui.flight.tool;

import com.foros.rs.client.model.advertising.campaign.Campaign;
import com.foros.rs.client.model.advertising.campaign.CampaignBidStrategy;
import com.foros.rs.client.model.advertising.campaign.CampaignType;
import com.foros.rs.client.model.advertising.campaign.DeliveryPacing;
import com.foros.rs.client.model.advertising.campaign.FrequencyCap;
import com.foros.rs.client.model.advertising.campaign.MarketplaceType;
import app.programmatic.ui.common.tool.converter.XmlDateTimeConverter;
import app.programmatic.ui.common.tool.foros.ForosHelper;
import app.programmatic.ui.flight.dao.model.BidStrategy;
import app.programmatic.ui.flight.dao.model.Flight;

import java.math.BigDecimal;

import static app.programmatic.ui.common.tool.converter.LocalDateTimeConverter.toEndOfDay;
import static app.programmatic.ui.common.tool.converter.LocalDateTimeConverter.toStartOfDay;


public class CampaignBuilder {
    private static final Campaign CAMPAIGN_DEFAULTS = initCampaignDefaults();

    public static Campaign build(Flight flight, Long userId, String zone) {
        return build(flight, userId, zone, null);
    }

    public static Campaign build(Flight flight, Long userId, String zone, Campaign existing) {
        Campaign notModifiable = existing != null ? existing : CAMPAIGN_DEFAULTS;
        Campaign campaign = new Campaign();

        campaign.setId(notModifiable.getId());
        campaign.setCampaignType(notModifiable.getCampaignType());
        campaign.setCommission(notModifiable.getCommission());
        campaign.setMarketplaceType(notModifiable.getMarketplaceType());
        campaign.setMaxPubShare(notModifiable.getMaxPubShare());

        campaign.setAccount(ForosHelper.createAdvertiserLink(flight.getOpportunity().getAccountId()));
        campaign.setName(flight.getName());
        //campaign.setStatus(StatusHelper.getRsStatusByMajorStatus(flight.getDisplayStatus()));
        campaign.setSoldToUser(ForosHelper.createEntityLink(userId));
        campaign.setBillToUser(ForosHelper.createEntityLink(userId));
        campaign.setDateEnd(XmlDateTimeConverter.convertDateTime(toEndOfDay(flight.getDateEnd()), zone));
        campaign.setDateStart(XmlDateTimeConverter.convertDateTime(toStartOfDay(flight.getDateStart()), zone));
        campaign.setDeliveryPacing(BaseBuilder.convertDeliveryPacing(flight.getDeliveryPacing()));
        campaign.setDailyBudget(flight.getDailyBudget());
        campaign.setBidStrategy(convertToCampaignBidStrategy(flight.getBidStrategy()));
        campaign.setFrequencyCap(BaseBuilder.convertFrequencyCap(flight.getFrequencyCap(), notModifiable.getFrequencyCap()));

        return campaign;
    }

    private static CampaignBidStrategy convertToCampaignBidStrategy(BidStrategy bidStrategy) {
        switch (bidStrategy) {
            case MAXIMISE_REACH: return CampaignBidStrategy.MAXIMISE_REACH;
            case CTR_BY_AMOUNT: return CampaignBidStrategy.CTR_BY_AMOUNT;
            case CTR_BY_PREDICTION: return CampaignBidStrategy.CTR_BY_PREDICTION;
            case MARGIN: return CampaignBidStrategy.MARGIN;
        }

        throw new IllegalArgumentException("Unexpected flight status: " + bidStrategy);
    }

    private static Campaign initCampaignDefaults() {
        Campaign campaign = new Campaign();

        campaign.setCampaignType(CampaignType.DISPLAY);
        campaign.setCommission(BigDecimal.ZERO);
        campaign.setDeliveryPacing(DeliveryPacing.FIXED);
        campaign.setBidStrategy(CampaignBidStrategy.CTR_BY_AMOUNT);
        campaign.setMarketplaceType(MarketplaceType.NOT_SET);
        campaign.setMaxPubShare(BigDecimal.ONE);
        campaign.setFrequencyCap(new FrequencyCap());
        campaign.setSalesManager(null);
        campaign.setBudget(null);

        return campaign;
    }
}
