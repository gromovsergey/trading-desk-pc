package com.foros.model.channel;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.model.Status;
import com.foros.util.changes.ChangesSupportSet;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.annotations.Cascade;

@Entity
@DiscriminatorValue("B")
@XmlRootElement
@XmlType(propOrder = {
        "behavioralParameters",
        "language"
})
@AllowedStatuses(values = {Status.ACTIVE, Status.INACTIVE, Status.DELETED, Status.PENDING_INACTIVATION})
public class BehavioralChannel extends TriggersChannel implements BehavioralParametersChannel, LanguageChannel {
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "channel", cascade = CascadeType.ALL)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    private Set<BehavioralParameters> behavioralParameters = new LinkedHashSet<BehavioralParameters>();

    @ManyToMany(mappedBy = "usedChannels", fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    @Cascade(org.hibernate.annotations.CascadeType.DETACH)
    private Set<ExpressionChannel> parentExpressionChannels = new LinkedHashSet<ExpressionChannel>();

    @Column(name = "LANGUAGE")
    private String language;

    public BehavioralChannel() {
    }

    public BehavioralChannel(Long id) {
        setId(id);
    }
    
    @Override
    @XmlElementWrapper(name = "behavioralParameters")
    @XmlElement(name = "behavioralParameter")
    public Set<BehavioralParameters> getBehavioralParameters() {
        return ChangesSupportSet.wrap(this, "behavioralParameters", behavioralParameters);
    }

    @Override
    public void setBehavioralParameters(Set<BehavioralParameters> behavioralParameters) {
        this.behavioralParameters = behavioralParameters;
        this.registerChange("behavioralParameters");
    }

    public Set<ExpressionChannel> getParentExpressionChannels() {
        return new ChangesSupportSet<ExpressionChannel>(this, "parentExpressionChannels", parentExpressionChannels);
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
    protected ChannelNamespace calculateNamespace() {
        return ChannelNamespace.ADVERTISING;
    }

    @Override
    public String getChannelType() {
        return CHANNEL_TYPE_BEHAVIORAL;
    }
}
