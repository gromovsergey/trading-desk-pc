package com.foros.action.campaign;

import com.foros.action.BaseActionSupport;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CcgRate;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.ChannelRatesTO;
import com.foros.util.CCGChannelRatesUtil;
import com.foros.util.xml.XmlUtil;
import com.foros.web.taglib.NumberFormatter;

import java.math.BigDecimal;

import javax.ejb.EJB;

public class CCGChannelRatesAction extends BaseActionSupport {
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
    @EJB
    private CampaignCreativeGroupService service;

    private Long channelId;
    private Long ccgId;

    private String text;

    public String getChannelRates() {
        text = XML_HEADER + "<channelRates>" + XmlUtil.Generator.tag("showRates", "false") + "</channelRates>";

        if (channelId == null || ccgId == null) {
            return SUCCESS;
        }

        ChannelRatesTO rates = service.getCcgTargetingRates(ccgId, channelId);

        if (rates == null) {
            return SUCCESS;
        }

        CampaignCreativeGroup ccg = service.find(ccgId);
        String targetingRates = CCGChannelRatesUtil.getPopulatedTargetingRates(rates);
        String totalRates = CCGChannelRatesUtil.getPopulatedTotalRates(rates, ccg.getAccount().getCurrency().getCurrencyCode());

        BigDecimal rate;
        CcgRate ccgRate = ccg.getCcgRate();
        switch (ccgRate.getRateType()) {
            case CPM:
                rate = ccgRate.getCpm();
                break;
            case CPC:
                rate = ccgRate.getCpc();
                break;
            case CPA:
                rate = ccgRate.getCpa();
                break;
            default:
                throw new IllegalArgumentException("Invalid CCG Rate" + ccgRate);
        }

        String inventoryRates = NumberFormatter.formatCurrency(rate, ccg.getAccount().getCurrency().getCurrencyCode())
                + " " + getText("ccg." + ccgRate.getRateType().getName().toLowerCase());

        StringBuilder res = new StringBuilder();

        res.append(XML_HEADER);
        res.append("<channelRates>");
        res.append(XmlUtil.Generator.tag("showRates", "true"));
        res.append(XmlUtil.Generator.tag("targetingRate", targetingRates));
        res.append(XmlUtil.Generator.tag("inventoryRate", inventoryRates));
        res.append(XmlUtil.Generator.tag("totalRate", totalRates));
        res.append("</channelRates>");

        text = res.toString();

        return SUCCESS;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getCcgId() {
        return ccgId;
    }

    public void setCcgId(Long ccgId) {
        this.ccgId = ccgId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
