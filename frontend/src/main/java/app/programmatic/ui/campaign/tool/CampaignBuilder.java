package app.programmatic.ui.campaign.tool;

import com.foros.rs.client.model.advertising.AdvertiserLink;
import com.foros.rs.client.model.advertising.campaign.Campaign;
import com.foros.rs.client.model.advertising.campaign.CampaignType;
import com.foros.rs.client.model.advertising.campaign.DeliveryPacing;
import com.foros.rs.client.model.advertising.campaign.FrequencyCap;
import com.foros.rs.client.model.advertising.campaign.MarketplaceType;
import com.foros.rs.client.model.entity.EntityLink;
import com.foros.rs.client.model.entity.Status;

import app.programmatic.ui.common.datasource.tools.RsTools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


public class CampaignBuilder {

    public static Campaign build(ResultSet rs, Long id) throws SQLException {
        Campaign result = new Campaign();
        result.setId(id);
        result.setName(rs.getString("name"));
        result.setAccount(createAdvertiserLink(rs.getLong("account_id")));
        result.setFrequencyCap(createFrequencyCap(rs));
        result.setStatus(toStatus(rs.getString("status").charAt(0)));
        result.setSoldToUser(createEntityLink(rs.getLong("sold_to_user_id")));
        result.setBillToUser(createEntityLink(rs.getLong("bill_to_user_id")));
        result.setCommission(rs.getBigDecimal("commission"));
        result.setMaxPubShare(rs.getBigDecimal("max_pub_share"));
        result.setDateStart(convertTimestamp(rs.getTimestamp("date_start")));
        result.setDateEnd(convertTimestamp(rs.getTimestamp("date_end")));
        result.setMarketplaceType(toMarketplaceType(rs.getString("marketplace")));
        result.setCampaignType(toCampaignType(rs.getString("campaign_type").charAt(0)));
        result.setDailyBudget(rs.getBigDecimal("daily_budget"));
        result.setDeliveryPacing(toDeliveryPacing(rs.getString("delivery_pacing").charAt(0)));

        return result;
    }

    private static AdvertiserLink createAdvertiserLink(Long id) {
        AdvertiserLink entityLink = new AdvertiserLink();
        entityLink.setId(id);
        return entityLink;
    }

    private static EntityLink createEntityLink(Long id) {
        EntityLink entityLink = new EntityLink();
        entityLink.setId(id);
        return entityLink;
    }

    private static FrequencyCap createFrequencyCap(ResultSet rs) throws SQLException {
        FrequencyCap frequencyCap = new FrequencyCap();
        frequencyCap.setId(RsTools.getNullableLong(rs, "freq_cap_id"));
        if (frequencyCap.getId() == null) {
            return frequencyCap;
        }

        frequencyCap.setLifeCount(RsTools.getNullableLong(rs, "life_count"));
        frequencyCap.setPeriod(RsTools.getNullableLong(rs, "period"));
        frequencyCap.setWindowCount(RsTools.getNullableLong(rs, "window_count"));
        frequencyCap.setWindowLength(RsTools.getNullableLong(rs, "window_length"));
        return frequencyCap;
    }

    private static Status toStatus(Character chr) {
        switch (chr) {
            case 'A': return Status.ACTIVE;
            case 'I': return Status.INACTIVE;
            case 'D': return Status.DELETED;
        }
        throw new RuntimeException("Unexpected campaign status " + chr);
    }

    private static XMLGregorianCalendar convertTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp.getTime());
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private static MarketplaceType toMarketplaceType(String source) {
        if (source == null) {
            return MarketplaceType.NOT_SET;
        }
        return MarketplaceType.valueOf(source);
    }

    private static CampaignType toCampaignType(Character chr) {
        if (chr.equals('D')) {
            return CampaignType.DISPLAY;
        }
        return CampaignType.TEXT;
    }

    private static DeliveryPacing toDeliveryPacing(Character chr) {
        if (chr.equals('D')) {
            return DeliveryPacing.DYNAMIC;
        }
        if (chr.equals('F')) {
            return DeliveryPacing.FIXED;
        }
        return DeliveryPacing.UNRESTRICTED;
    }
}
