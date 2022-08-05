package com.foros.session.channel;

import com.foros.model.channel.KeywordTriggerType;
import com.foros.session.DisplayStatusEntityTO;

public class KeywordChannelTO extends DisplayStatusEntityTO {

    private KeywordTriggerType triggerType;
    private String countryCode;
    private DisplayStatusEntityTO account = new DisplayStatusEntityTO();

    public KeywordChannelTO() {
        super();
    }

    public KeywordTriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(KeywordTriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public DisplayStatusEntityTO getAccount() {
        return account;
    }

    public void setAccount(DisplayStatusEntityTO account) {
        this.account = account;
    }

}
