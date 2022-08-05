package com.foros.session.campaign;

import static com.foros.model.DisplayStatus.Major;
import com.foros.model.DisplayStatus;
import com.foros.model.campaign.CCGKeyword;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CombinedCCGKeywordTO {
    private final List<CCGKeywordTO> keywords = new ArrayList<CCGKeywordTO>();
    private final String keyword;
    private int fractionDigits;

    private BigDecimal impressions;
    private BigDecimal clicks;
    private BigDecimal ctr;
    private BigDecimal ecpm;
    private BigDecimal cost;
    private BigDecimal averageActualCPC;
    private DisplayStatus displayStatus;

    public CombinedCCGKeywordTO(String keyword, int fractionDigits) {
        this.keyword = keyword;
        this.fractionDigits = fractionDigits;
    }

    public String getKeyword() {
        return keyword;
    }

    public List<CCGKeywordTO> getKeywords() {
        return keywords;
    }

    public boolean isSameCPC() {
        Set<BigDecimal> cpcs = new HashSet<BigDecimal>();
        for (CCGKeywordTO keyword : keywords) {
            cpcs.add(keyword.getCpc());
        }
        return cpcs.size() == 1;
    }

    public BigDecimal getCpc() {
        if (isSameCPC()) {
            return keywords.get(0).getCpc();
        }
        return null;
    }

    public BigDecimal getImpressions() {
        if (impressions == null) {
            impressions = BigDecimal.ZERO;
            for (CCGKeywordTO keyword : keywords) {
                impressions = impressions.add(keyword.getImpressions());
            }
        }
        return impressions;
    }

    public BigDecimal getClicks() {
        if (clicks == null) {
            clicks = BigDecimal.ZERO;
            for (CCGKeywordTO keyword : keywords) {
                clicks = clicks.add(keyword.getClicks());
            }
        }
        return clicks;
    }

    public BigDecimal getCtr() {
        if (ctr == null) {
            if (getClicks().compareTo(BigDecimal.ZERO) == 0 || getImpressions().compareTo(BigDecimal.ZERO) == 0) {
                ctr = BigDecimal.ZERO;
            } else {
                ctr = getClicks().multiply(new BigDecimal(100)).divide(getImpressions(), 2, RoundingMode.HALF_UP);
            }
        }
        return ctr;
    }

    public BigDecimal getEcpm() {
        if (ecpm == null) {
            if (getCost().compareTo(BigDecimal.ZERO) == 0 || getImpressions().compareTo(BigDecimal.ZERO) == 0) {
                ecpm = BigDecimal.ZERO;
            } else {
                ecpm = getCost().divide(getImpressions(), fractionDigits, RoundingMode.HALF_UP).multiply(BigDecimal.TEN).multiply(new BigDecimal(100));
            }
        }
        return ecpm;
    }

    public BigDecimal getCost() {
        if (cost == null) {
            cost = BigDecimal.ZERO;
            for (CCGKeywordTO keyword : keywords) {
                cost = cost.add(keyword.getCost());
            }
        }
        return cost;
    }

    public BigDecimal getAverageActualCPC() {
        if (averageActualCPC == null) {
            if (getCost().compareTo(BigDecimal.ZERO) == 0 || getClicks().compareTo(BigDecimal.ZERO) == 0) {
                averageActualCPC = BigDecimal.ZERO;
            } else {
                averageActualCPC = getCost().divide(getClicks(), fractionDigits, RoundingMode.HALF_UP);
            }
        }
        return averageActualCPC;
    }

    public BigDecimal getAudience() {
        BigDecimal audience = keywords.get(0).getAudience();
        for (CCGKeywordTO keyword : keywords) {
            if (audience == null || (keyword.getAudience() != null && keyword.getAudience().compareTo(audience) > 0)) {
                audience = keyword.getAudience();
            }
        }
        return audience;
    }

    public DisplayStatus getDisplayStatus() {
        if (displayStatus == null) {
            Map<Major, DisplayStatus> displayStatuses = new HashMap<Major, DisplayStatus>();
            for (CCGKeywordTO keyword : keywords) {
                displayStatuses.put(keyword.getDisplayStatus().getMajor(), keyword.getDisplayStatus());
            }

            if (displayStatuses.containsKey(Major.LIVE) && !displayStatuses.containsKey(Major.NOT_LIVE)) { // Green and no Reds
                displayStatus = CCGKeyword.LIVE;
            } else if (displayStatuses.containsKey(Major.LIVE) && displayStatuses.containsKey(Major.NOT_LIVE)) { // Green and Red
                displayStatus = CCGKeyword.LIVE_NEED_ATT;
            } else if (displayStatuses.containsKey(Major.NOT_LIVE)) { // Red + Red or Gray + Red
                if (displayStatuses.values().contains(CCGKeyword.NOT_LIVE_PENDING)) {
                    displayStatus = CCGKeyword.NOT_LIVE_PENDING;
                } else if (displayStatuses.values().contains(CCGKeyword.NOT_LIVE_DECLINED)) {
                    displayStatus = CCGKeyword.NOT_LIVE_DECLINED;
                } else {
                    displayStatus = CCGKeyword.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS;
                }
            } else { // All Gray
                if (displayStatuses.containsKey(Major.INACTIVE)) {
                    displayStatus = CCGKeyword.INACTIVE;
                } else {
                    displayStatus = CCGKeyword.DELETED;
                }
            }
        }
        return displayStatus;
    }

    public boolean isNegative() {
        return !keywords.isEmpty() && keywords.get(0).isNegative();
    }
}
