package com.foros.action.campaign.bulk;

import com.foros.action.bulk.CsvRow;
import com.foros.model.Country;
import com.foros.model.EntityBase;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CcgRate;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.campaign.DeliveryPacing;
import com.foros.model.campaign.RateType;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.TextCreativeOption;
import com.foros.model.security.User;
import com.foros.session.CurrentUserService;
import com.foros.session.ServiceLocator;
import com.foros.util.CollectionUtils;
import com.foros.util.mapper.Converter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class MainCampaignCsvNodeWriter implements CombiningCsvNodeWriter<EntityBase> {

    protected void write(CsvRow row, Campaign campaign) {
        row.set(CampaignFieldCsv.Level, CampaignLevelCsv.Campaign);
        row.set(CampaignFieldCsv.CampaignName, campaign.getName());
        row.set(CampaignFieldCsv.CampaignBudget, campaign.getBudget());
        row.set(CampaignFieldCsv.CampaignStatus, campaign.getStatus());
        row.set(CampaignFieldCsv.CampaignStartDate, campaign.getDateStart());
        row.set(CampaignFieldCsv.CampaignEndDate, campaign.getDateEnd());
        row.set(CampaignFieldCsv.CampaignSalesManager, getUserName(campaign.getSalesManager()));
        row.set(CampaignFieldCsv.CampaignSoldToUser, getUserName(campaign.getSoldToUser()));
        row.set(CampaignFieldCsv.CampaignBillToUser, getUserName(campaign.getBillToUser()));

        DeliveryPacing deliveryPacing = campaign.getDeliveryPacing();
        switch (deliveryPacing) {
            case UNRESTRICTED:
            case DYNAMIC:
                row.set(CampaignFieldCsv.CampaignDailyBudget, deliveryPacing);
                break;
            case FIXED:
                row.set(CampaignFieldCsv.CampaignDailyBudget, campaign.getDailyBudget());
                break;
        }

        if (campaign.getFrequencyCap() != null) {
            row.set(CampaignFieldCsv.CampaignFCPeriod, campaign.getFrequencyCap().getPeriodSpan());
            row.set(CampaignFieldCsv.CampaignFCWindow, campaign.getFrequencyCap().getWindowCount());
            row.set(CampaignFieldCsv.CampaignFCWindowLength, campaign.getFrequencyCap().getWindowLengthSpan());
            row.set(CampaignFieldCsv.CampaignFCLife, campaign.getFrequencyCap().getLifeCount());
        }
    }

    protected void write(CsvRow row, CampaignCreativeGroup ccg) {
        row.set(CampaignFieldCsv.Level, CampaignLevelCsv.AdGroup);
        row.set(CampaignFieldCsv.CampaignName, ccg.getCampaign().getName());
        row.set(CampaignFieldCsv.AdGroupName, ccg.getName());
        row.set(CampaignFieldCsv.AdGroupRate, getRate(ccg.getCcgRate()));
        row.set(CampaignFieldCsv.AdGroupRateType, ccg.getCcgRate().getRateType().getName());
        row.set(CampaignFieldCsv.AdGroupStatus, ccg.getStatus());
        row.set(CampaignFieldCsv.AdGroupBudget, ccg.getBudget());
        DeliveryPacing deliveryPacing = ccg.getDeliveryPacing();
        switch (deliveryPacing) {
            case UNRESTRICTED:
            case DYNAMIC:
                row.set(CampaignFieldCsv.AdGroupDailyBudget, deliveryPacing.getName());
                break;
            case FIXED:
                row.set(CampaignFieldCsv.AdGroupDailyBudget, ccg.getDailyBudget());
                break;
        }
        row.set(CampaignFieldCsv.AdGroupStartDate, ccg.getDateStart());
        row.set(CampaignFieldCsv.AdGroupEndDate, ccg.getDateEnd());
        Country country = ccg.getCountry();
        row.set(CampaignFieldCsv.AdGroupCountryTargeting, country == null ? "" : country.getCountryCode());
        String deviceTargeting = "";
        if (ccg.getDeviceChannels().size() > 0) {
            deviceTargeting = CollectionUtils.join(ccg.getDeviceChannels(), "|", new Converter<DeviceChannel, String>(){
                @Override
                public String item(DeviceChannel value) {
                    return value.getId().toString();
                }
            });
        }
        row.set(CampaignFieldCsv.AdGroupDeviceTargeting, deviceTargeting);
        ChannelTarget channelTarget = ccg.getChannelTarget();
        if (channelTarget != null) {
            switch (channelTarget) {
                case NOT_SET:
                case UNTARGETED:
                    row.set(CampaignFieldCsv.AdGroupChannelTarget, channelTarget);
                    break;
                case TARGETED:
                    row.set(CampaignFieldCsv.AdGroupChannelTarget, ccg.getChannel());
                    break;
            }
        }

        if (ccg.getFrequencyCap() != null) {
            row.set(CampaignFieldCsv.AdGroupFCPeriod, ccg.getFrequencyCap().getPeriodSpan());
            row.set(CampaignFieldCsv.AdGroupFCWindow, ccg.getFrequencyCap().getWindowCount());
            row.set(CampaignFieldCsv.AdGroupFCWindowLength, ccg.getFrequencyCap().getWindowLengthSpan());
            row.set(CampaignFieldCsv.AdGroupFCLife, ccg.getFrequencyCap().getLifeCount());
        }
    }

    protected void write(CsvRow row, CCGKeyword keyword) {
        writeKeyword(row, keyword, keyword.getTriggerType());
    }

    protected void write(CsvRow row, CCGKeyword keyword, CCGKeyword keyword2) {
        writeKeyword(row, keyword, null);
    }

    private void writeKeyword(CsvRow row, CCGKeyword keyword, KeywordTriggerType triggerType) {
        row.set(CampaignFieldCsv.Level, CampaignLevelCsv.Keyword);
        row.set(CampaignFieldCsv.CampaignName, keyword.getCreativeGroup().getCampaign().getName());
        row.set(CampaignFieldCsv.AdGroupName, keyword.getCreativeGroup().getName());
        row.set(CampaignFieldCsv.Keyword, keyword.getOriginalKeyword());
        row.set(CampaignFieldCsv.KeywordType, triggerType == null ? "" : triggerType.getName());
        row.set(CampaignFieldCsv.KeywordRate, keyword.getMaxCpcBid());
        row.set(CampaignFieldCsv.KeywordClickURL, keyword.getClickURL());
        row.set(CampaignFieldCsv.KeywordStatus, keyword.getStatus());
    }

    protected void write(CsvRow row, CampaignCreative cc) {
        row.set(CampaignFieldCsv.Level, CampaignLevelCsv.TextAd);
        row.set(CampaignFieldCsv.CampaignName, cc.getCreativeGroup().getCampaign().getName());
        row.set(CampaignFieldCsv.AdGroupName, cc.getCreativeGroup().getName());
        row.set(CampaignFieldCsv.AdLinkId, cc.getId());
        Creative creative = cc.getCreative();
        row.set(CampaignFieldCsv.AdId, creative.getId());
        row.set(CampaignFieldCsv.AdLinkStatus, creative.getStatus());

        Map<String, CreativeOptionValue> textOptionVaues = new HashMap<>();
        for (CreativeOptionValue value : creative.getOptions()) {
            TextCreativeOption option = TextCreativeOption.byTokenOptional(value.getOption().getToken());
            textOptionVaues.put(option.getToken(), value);
        }

        for (CampaignFieldCsv fieldCsv : CampaignFieldCsv.TEXT_OPTIONS.values()) {
            row.set(fieldCsv, textOptionVaues.get(fieldCsv.getTextOption().getToken()).getValue());
        }

        row.set(CampaignFieldCsv.AdLinkStatus, cc.getStatus());
        row.set(CampaignFieldCsv.AdStatus, creative.getStatus());
        row.set(CampaignFieldCsv.AdApproval, creative.getQaStatus());


        if (cc.getFrequencyCap() != null) {
            row.set(CampaignFieldCsv.AdFCPeriod, cc.getFrequencyCap().getPeriodSpan());
            row.set(CampaignFieldCsv.AdFCWindow, cc.getFrequencyCap().getWindowCount());
            row.set(CampaignFieldCsv.AdFCWindowLength, cc.getFrequencyCap().getWindowLengthSpan());
            row.set(CampaignFieldCsv.AdFCLife, cc.getFrequencyCap().getLifeCount());
        }
    }

    private static String getUserName(User user) {
        return user == null ? "" : user.getEmail();
    }

    private static BigDecimal getRate(CcgRate rate) {
        if (rate != null) {
            switch (rate.getRateType()) {
                case CPA:
                    return rate.getCpa();
                case CPC:
                    return rate.getCpc();
                case CPM:
                    return rate.getCpm();
            }
        }
        return null;
    }

    @Override
    public void write(CsvRow row, EntityBase entity) {
        if (entity instanceof Campaign) {
            write(row, (Campaign)entity);
        } else if (entity instanceof CampaignCreativeGroup) {
            write(row, (CampaignCreativeGroup)entity);
        } else if (entity instanceof CampaignCreative) {
            write(row, (CampaignCreative)entity);
        } else if (entity instanceof CCGKeyword) {
            write(row, (CCGKeyword)entity);
        } else {
            throw new IllegalArgumentException(entity.getClass().toString());
        }
    }

    @Override
    public void write(CsvRow row, EntityBase entity, EntityBase entity2) {
        if (entity instanceof Campaign) {
            write(row, (Campaign)entity);
        } else if (entity instanceof CampaignCreativeGroup) {
            write(row, (CampaignCreativeGroup)entity);
        } else if (entity instanceof CampaignCreative) {
            write(row, (CampaignCreative)entity);
        } else if (entity instanceof CCGKeyword && entity2 instanceof CCGKeyword) {
            write(row, (CCGKeyword)entity, (CCGKeyword)entity2);
        } else {
            throw new IllegalArgumentException(entity.getClass().toString());
        }
    }
}
