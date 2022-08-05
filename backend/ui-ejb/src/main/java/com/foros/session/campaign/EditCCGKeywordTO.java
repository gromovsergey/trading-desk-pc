package com.foros.session.campaign;

import java.math.BigDecimal;

import com.foros.model.channel.KeywordTriggerType;

public class EditCCGKeywordTO {
    private String originalKeyword;
    private BigDecimal maxCpcBid;
    private String clickURL;
    private KeywordTriggerType triggerType;

    public EditCCGKeywordTO() {
    }

    public String getClickURL() {
        return clickURL;
    }

    public BigDecimal getMaxCpcBid() {
        return maxCpcBid;
    }

    public String getOriginalKeyword() {
        return originalKeyword;
    }

    public KeywordTriggerType getTriggerType() {
        return triggerType;
    }

    public void setClickURL(String clickURL) {
        this.clickURL = clickURL;
    }

    public void setMaxCpcBid(BigDecimal maxCpcBid) {
        this.maxCpcBid = maxCpcBid;
    }

    public void setOriginalKeyword(String originalKeyword) {
        this.originalKeyword = originalKeyword;
    }

    public void setTriggerType(KeywordTriggerType triggerType) {
        this.triggerType = triggerType;
    }

    @Override
    public String toString() {
        return "EditCCGKeywordTO [originalKeyword=" + originalKeyword + ", maxCpcBid="
            + maxCpcBid + ", clickURL=" + clickURL + ", triggerType=" + triggerType + "]";
    }
}
