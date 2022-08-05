package com.foros.session.channel;

import com.foros.model.FrequencyCap;
import com.foros.model.channel.BehavioralParameters;

import java.util.LinkedHashSet;
import java.util.Set;

public class KeywordChannelCsvTO {

    private String name;
    private Character type;
    private String accountName;
    private String countryCode;
    private Set<BehavioralParameters> behavioralParameters;
    private FrequencyCap frequencyCap;

    public KeywordChannelCsvTO() {
        behavioralParameters = new LinkedHashSet<BehavioralParameters>();
    }

    public KeywordChannelCsvTO(String name, Character type, String accountName, String countryCode, Set<BehavioralParameters> behavioralParameters, FrequencyCap frequencyCap) {
        this.name = name;
        this.type = type;
        this.accountName = accountName;
        this.countryCode = countryCode;
        this.behavioralParameters = behavioralParameters;
        this.frequencyCap = frequencyCap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Set<BehavioralParameters> getBehavioralParameters() {
        return behavioralParameters;
    }

    public void setBehavioralParameters(Set<BehavioralParameters> behavioralParameters) {
        this.behavioralParameters = behavioralParameters;
    }

    public FrequencyCap getFrequencyCap() {
        return this.frequencyCap;
    }

    public void setFrequencyCap(FrequencyCap freqCapId) {
        this.frequencyCap = freqCapId;
    }
}
