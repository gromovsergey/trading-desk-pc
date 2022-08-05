package com.foros.model.channel;

import com.foros.annotations.AllowedQAStatuses;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.FrequencyCap;
import com.foros.model.FrequencyCapEntity;
import com.foros.model.channel.trigger.KeywordTrigger;
import com.foros.model.channel.trigger.PageKeywordTrigger;
import com.foros.model.channel.trigger.SearchKeywordTrigger;
import com.foros.model.security.NotManagedEntity;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.RequiredConstraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@DiscriminatorValue("K")
@AllowedQAStatuses(values = {ApproveStatus.APPROVED, ApproveStatus.DECLINED, ApproveStatus.HOLD})
public class KeywordChannel extends Channel implements BehavioralParametersChannel, FrequencyCapEntity,
        NotManagedEntity, LanguageChannel, KeywordTriggersSource {

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "channel", cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    private Set<BehavioralParameters> behavioralParameters = new LinkedHashSet<BehavioralParameters>(2, 1f);

    @JoinColumn(name = "FREQ_CAP_ID", referencedColumnName = "FREQ_CAP_ID")
    @ManyToOne(cascade = CascadeType.PERSIST)
    @ChangesInspection(type = InspectionType.CASCADE)
    private FrequencyCap frequencyCap;

    @Column(name = "LANGUAGE")
    private String language;

    @RequiredConstraint
    @Column(name = "TRIGGER_TYPE", nullable = false, updatable = false)
    @Type(type = "com.foros.persistence.hibernate.type.GenericEnumType", parameters = {
            @Parameter(name = "enumClass", value = "com.foros.model.channel.KeywordTriggerType"),
            @Parameter(name = "identifierMethod", value = "getLetter"),
            @Parameter(name = "valueOfMethod", value = "byLetter")
    })
    private KeywordTriggerType triggerType;

    @Override
    public Set<BehavioralParameters> getBehavioralParameters() {
        return new ChangesSupportSet<BehavioralParameters>(this, "behavioralParameters", behavioralParameters);
    }

    @Override
    public void setBehavioralParameters(Set<BehavioralParameters> behavioralParameters) {
        this.behavioralParameters = behavioralParameters;
        this.registerChange("behavioralParameters");
    }

    @Override
    public FrequencyCap getFrequencyCap() {
        return this.frequencyCap;
    }

    @Override
    public void setFrequencyCap(FrequencyCap freqCapId) {
        this.frequencyCap = freqCapId;
        this.registerChange("frequencyCap");
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(String language) {
        this.language = language;
        this.registerChange("language");
    }

    public KeywordTriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(KeywordTriggerType triggetType) {
        this.triggerType = triggetType;
        this.registerChange("triggetType");
    }

    @Override
    protected ChannelNamespace calculateNamespace() {
        return ChannelNamespace.KEYWORD;
    }

    @Override
    public String getChannelType() {
        return CHANNEL_TYPE_KEYWORD;
    }

    @Override
    public Collection<KeywordTrigger> getAllKeywordTriggers() {
        List<KeywordTrigger> allKeywords = new ArrayList<KeywordTrigger>(2);

        String countryCode = getCountryCode(getCountry());

        if (getTriggerType() == KeywordTriggerType.PAGE_KEYWORD) {
            allKeywords.add(new PageKeywordTrigger(countryCode, getName(), false));
        } else if (getTriggerType() == KeywordTriggerType.SEARCH_KEYWORD) {
            allKeywords.add(new SearchKeywordTrigger(countryCode, getName(), false));
        }

        return allKeywords;
    }

    private String getCountryCode(Country country) {
        return country == null ? null : country.getCountryCode();
    }
}
