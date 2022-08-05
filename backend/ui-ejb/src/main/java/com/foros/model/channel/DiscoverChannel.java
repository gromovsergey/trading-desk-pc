package com.foros.model.channel;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.audit.serialize.serializer.entity.DiscoverChannelAuditSerializer;
import com.foros.jaxb.adapters.BehavioralParametersListXmlAdapter;
import com.foros.jaxb.adapters.ChannelLinkXmlAdapter;
import com.foros.model.Status;
import com.foros.model.security.NotManagedEntity;
import com.foros.util.SQLUtil;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.SingleLineConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@DiscriminatorValue("D")
@NamedQueries({
    @NamedQuery(name = "DiscoverChannel.availableChannelLists",
            query = "select new com.foros.session.EntityTO(dcl.id, dcl.name, dcl.status) " +
                    "from DiscoverChannelList dcl " +
                    "where dcl.account = :account " +
                    " and dcl.status <> 'D' " +
                    " and upper(dcl.name) like upper(:name) escape '\\' " +
                    "order by dcl.name")
})
@XmlRootElement
@XmlType(propOrder = {
        "discoverAnnotation",
        "discoverQuery",
        "language",
        "behavParamsList",
        "baseKeyword",
        "channelList"
})
@AllowedStatuses(values = {Status.ACTIVE, Status.INACTIVE, Status.DELETED})
@Audit(serializer = DiscoverChannelAuditSerializer.class)
public class DiscoverChannel extends TriggersChannel implements LanguageChannel, BehavioralParametersListChannel, NotManagedEntity {
    @StringSizeConstraint(size = 4000)
    @Column(name = "BASE_KEYWORD")
    private String baseKeyword;

    @ChangesInspection(type = InspectionType.FIELD)
    @JoinColumn(name = "CHANNEL_LIST_ID", referencedColumnName = "CHANNEL_ID")
    @ManyToOne
    private DiscoverChannelList channelList;

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

    public String getBaseKeyword() {
        return baseKeyword;
    }

    public void setBaseKeyword(String baseKeyword) {
        this.baseKeyword = baseKeyword;
        this.registerChange("baseKeyword");
    }

    @XmlElement
    @XmlJavaTypeAdapter(ChannelLinkXmlAdapter.class)
    public DiscoverChannelList getChannelList() {
        return channelList;
    }

    public void setChannelList(DiscoverChannelList channelList) {
        this.channelList = channelList;
        this.registerChange("channelList");
    }

    @Override
    public Status getParentStatus() {
        if (getChannelList() != null) {
            return getChannelList().getInheritedStatus();
        }
        return super.getParentStatus();
    }

    @Override
    protected ChannelNamespace calculateNamespace() {
        return ChannelNamespace.DISCOVER;  
    }

    @Override
    public String getChannelType() {
        return CHANNEL_TYPE_DISCOVER;
    }

    public void setNullUrlKeywords() {
        this.urlKeywords = null;
    }
}
