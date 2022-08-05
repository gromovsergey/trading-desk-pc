package com.foros.model.channel;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.jaxb.adapters.BehavioralParametersListXmlAdapter;
import com.foros.jaxb.adapters.CategoryLinkXmlAdapter;
import com.foros.jaxb.adapters.KeywordListXmlAdapter;
import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.model.security.NotManagedEntity;
import com.foros.util.SQLUtil;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.ExpressionSymbolsOnlyConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.SingleLineConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;

@Entity
@DiscriminatorValue("L")
@AllowedStatuses(values = {Status.ACTIVE, Status.INACTIVE, Status.DELETED})
@XmlType(propOrder = {
        "discoverAnnotation",
        "discoverQuery",
        "language",
        "behavParamsList"
})
public class DiscoverChannelList extends Channel implements LanguageChannel, BehavioralParametersListChannel, NotManagedEntity  {
    public static final String KEYWORD_TOKEN = "##KEYWORD##";

    @RequiredConstraint
    @StringSizeConstraint(size = 200)
    @ExpressionSymbolsOnlyConstraint
    @Column(name = "CHANNEL_NAME_MACRO")
    private String channelNameMacro;

    @RequiredConstraint
    @StringSizeConstraint(size = 200)
    @Column(name = "KEYWORD_TRIGGER_MACRO")
    private String keywordTriggerMacro;

    @Transient
    private String keywordList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "channelList", cascade = {})
    @OrderBy("name")
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Set<DiscoverChannel> childChannels = new LinkedHashSet<DiscoverChannel>();

    @Column(name = "DISCOVER_QUERY")
    @StringSizeConstraint(size = 4000)
    @RequiredConstraint
    @SingleLineConstraint
    private String discoverQuery;

    @Column(name = "DISCOVER_ANNOTATION")
    @StringSizeConstraint(size = 4000)
    @RequiredConstraint
    @SingleLineConstraint
    private String discoverAnnotation;

    @Column(name = "LANGUAGE")
    private String language;

    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    @JoinColumn(name = "BEHAV_PARAMS_LIST_ID", referencedColumnName = "BEHAV_PARAMS_LIST_ID")
    @ManyToOne
    private BehavioralParametersList behavParamsList;

    @XmlElement(required = true)
    public String getDiscoverQuery() {
        return discoverQuery;
    }

    public void setDiscoverQuery(String discoverQuery) {
        this.discoverQuery = discoverQuery;
        this.registerChange("discoverQuery");
    }

    @XmlElement(required = true)
    public String getDiscoverAnnotation() {
        return discoverAnnotation;
    }

    public void setDiscoverAnnotation(String discoverAnnotation) {
        this.discoverAnnotation = discoverAnnotation;
        this.registerChange("discoverAnnotation");
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

    @Override
    @XmlJavaTypeAdapter(BehavioralParametersListXmlAdapter.class)
    public BehavioralParametersList getBehavParamsList() {
        return behavParamsList;
    }

    @Override
    public void setBehavParamsList(BehavioralParametersList behavParamsList) {
        this.behavParamsList = behavParamsList;
        this.registerChange("behavParamsList");
    }

    @XmlElement(name = "keywordList")
    @XmlJavaTypeAdapter(KeywordListXmlAdapter.class)
    public String getKeywordList() {
        if (!this.isChanged("keywordList")) {
            setKeywordList(asKeywords(getChildChannels()));
        }
        return keywordList;
    }

    public void setKeywordList(String keywordList) {
        this.keywordList = keywordList;
        this.registerChange("keywordList");
    }

    public DiscoverChannelList() {
        setQaStatus(ApproveStatus.APPROVED);
    }

    public String getChannelNameMacro() {
        return channelNameMacro;
    }

    public void setChannelNameMacro(String channelNameMacro) {
        this.channelNameMacro = channelNameMacro;
        this.registerChange("channelNameMacro");
    }

    public Set<DiscoverChannel> getChildChannels() {
        return new ChangesSupportSet<DiscoverChannel>(this, "childChannels", childChannels);
    }

    public void setChildChannels(Set<DiscoverChannel> childChannels) {
        this.childChannels = childChannels;
        this.registerChange("childChannels");
    }

    public String getKeywordTriggerMacro() {
        return keywordTriggerMacro;
    }

    public void setKeywordTriggerMacro(String keywordTriggerMacro) {
        this.keywordTriggerMacro = keywordTriggerMacro;
        this.registerChange("keywordTriggerMacro");
    }

    @Override
    protected ChannelNamespace calculateNamespace() {
        return ChannelNamespace.DISCOVER_LIST;
    }

    @Override
    public String getChannelType() {
        return CHANNEL_TYPE_DISCOVER_CHANNEL_LIST;
    }

    @Override
    @XmlElementWrapper(name = "categories")
    @XmlElement(name = "category")
    @XmlJavaTypeAdapter(CategoryLinkXmlAdapter.class)
    public Set<CategoryChannel> getCategories() {
        return super.getCategories();
    }

    public static String asKeywords(Set<DiscoverChannel> childChannels) {
        StringBuilder keywordBuilder = new StringBuilder();
        for (DiscoverChannel channel: childChannels) {
                keywordBuilder.append(channel.getBaseKeyword()).append("\n");
        }
        return (StringUtils.chomp(keywordBuilder.toString()));
    }

}
