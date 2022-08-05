package com.foros.session.campaign;

import com.foros.model.DisplayStatus;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.campaign.TGTType;
import com.foros.session.EntityTO;
import com.foros.session.channel.ChannelTO;
import com.foros.util.StringUtil;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;

public class CCGStatsTO extends EntityTO {
    private DisplayStatus displayStatus;
    private CCGType ccgType;
    private Date dateStart;
    private Date dateEnd;
    private boolean linkedToCampaignEndDate;
    private ImpClickStatsTO impClick;
    private final long uniqueUsers;
    private final BigDecimal inventoryCost;
    private final BigDecimal targetingCost;
    private final BigDecimal creditUsed;
    private TGTType tgtType;
    private ChannelTO channelTarget;
    private ChannelTarget target;
    private boolean isTargetViewable;
    private BigDecimal totalCost;
    private BigDecimal totalValue;

    public CCGStatsTO(Long id, String name, Long displayStatusId, CCGType ccgType, TGTType tgtType,
                      ImpClickStatsTO.Builder impClickBuilder, long uniqueUsers,
                      BigDecimal inventoryCost, BigDecimal targetingCost, BigDecimal creditUsed,
                      ChannelTO channelTarget, ChannelTarget target, boolean isTargetViewable) {
        super(id, name, 'I');
        this.impClick = impClickBuilder.build();
        this.ccgType = ccgType;
        this.uniqueUsers = uniqueUsers;
        this.inventoryCost = inventoryCost;
        this.targetingCost = targetingCost;
        this.creditUsed = creditUsed;
        this.tgtType = tgtType;
        this.displayStatus = CampaignCreativeGroup.getDisplayStatus(displayStatusId);
        this.channelTarget = channelTarget;
        this.target = target;
        this.isTargetViewable = isTargetViewable;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public boolean isLinkedToCampaignEndDate() {
        return linkedToCampaignEndDate;
    }

    public long getImps() {
        return impClick.getImps();
    }

    public long getClicks() {
        return impClick.getClicks();
    }

    public long getPostImpConv() {
        return impClick.getPostImpConv();
    }

    public long getPostClickConv() {
        return impClick.getPostClickConv();
    }

    public double getCtr() {
        return impClick.getCtr();
    }

    public double postClickConvCr() {
        return impClick.getPostClickConvCr();
    }

    public double postImpConvCr() {
        return impClick.getPostImpConvCr();
    }


    public long getUniqueUsers() {
        return uniqueUsers;
    }

    public BigDecimal getInventoryCost() {
        return inventoryCost;
    }

    public BigDecimal getTargetingCost() {
        return targetingCost;
    }

    public BigDecimal getTotalValue() {
        if (totalValue == null) {
            totalValue = inventoryCost.add(targetingCost);
        }
        return totalValue;
    }

    public BigDecimal getTotalCost() {
        if (totalCost == null) {
            totalCost = getTotalValue().subtract(creditUsed);
        }
        return totalCost;
    }

    public BigDecimal getCreditUsed() {
        return creditUsed;
    }

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public String getCcgTypeExtension() {
        return ccgType.getPageExtension();
    }

    public CCGType getCcgType() {
        return ccgType;
    }

    public void setDisplayStatus(DisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public void setCcgType(CCGType ccgType) {
        this.ccgType = ccgType;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public void setLinkedToCampaignEndDate(boolean linkedToCampaignEndDate) {
        this.linkedToCampaignEndDate = linkedToCampaignEndDate;
    }

    public double getEcpm() {
        return impClick.getImps() == 0 ? 0 : getTotalValue().doubleValue() * 1000 / impClick.getImps();
    }

    public TGTType getTgtType() {
        return tgtType;
    }

    public void setTgtType(TGTType tgtType) {
        this.tgtType = tgtType;
    }

    public ChannelTO getChannelTarget() {
        return channelTarget;
    }

    public ChannelTarget getTarget() {
        return target;
    }

    public static final Comparator<CCGStatsTO> NAME_COMPARATOR = new Comparator<CCGStatsTO>() {
        @Override
        public int compare(CCGStatsTO o1, CCGStatsTO o2) {
            return StringUtil.compareToIgnoreCase(o1.getName(), o2.getName());
        }
    };

    public boolean isTargetViewable() {
        return isTargetViewable;
    }

    public double getPostClickConvCr() {
        return impClick.getPostClickConvCr();
    }

    public double getPostImpConvCr() {
        return impClick.getPostImpConvCr();
    }


    public boolean isShowPostImpConv() {
        return impClick.isShowPostImpConv();
    }

    public boolean isShowPostClickConv() {
        return impClick.isShowPostClickConv();
    }

}
