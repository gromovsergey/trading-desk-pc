package com.foros.model.channel;

import com.foros.annotations.AllowedQAStatuses;
import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.RequiredConstraint;

import com.foros.validation.constraint.SizeConstraint;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.annotations.Cascade;

@Entity
@DiscriminatorValue("E")
@NamedQueries({
        @NamedQuery(name = "ExpressionChannel.findByAccountIdAndName",
                query = "SELECT c FROM ExpressionChannel c WHERE c.account.id = :accountId AND c.name = :channelName")
})
@XmlRootElement
@AllowedStatuses(values = {Status.ACTIVE, Status.INACTIVE, Status.DELETED, Status.PENDING_INACTIVATION})
@AllowedQAStatuses(values = {ApproveStatus.APPROVED})
@XmlType(propOrder = {
        "expression"
})
public class ExpressionChannel extends Channel {
    @RequiredConstraint
    @SizeConstraint(max = 1024, message="errors.expression.tooLarge")
    @Column(name = "EXPRESSION")
    private String expression;

    @JoinTable(name = "EXPRESSIONUSEDCHANNEL",
            joinColumns = {@JoinColumn(name = "EXPRESSION_CHANNEL_ID", referencedColumnName = "CHANNEL_ID")},
            inverseJoinColumns = {@JoinColumn(name = "USED_CHANNEL_ID", referencedColumnName = "CHANNEL_ID")}
        )
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ChangesInspection(type = InspectionType.NONE)
    private Set<Channel> usedChannels = new LinkedHashSet<Channel>();

    @ManyToMany(mappedBy = "usedChannels", fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    @Cascade(org.hibernate.annotations.CascadeType.DETACH)
    private Set<ExpressionChannel> parentExpressionChannels = new LinkedHashSet<ExpressionChannel>();

    public ExpressionChannel() {
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
        this.registerChange("expression");
    }

    @XmlTransient
    public Set<Channel> getUsedChannels() {
        return new ChangesSupportSet<Channel>(this, "usedChannels", usedChannels);
    }

    public void setUsedChannels(Set<Channel> usedChannels) {
        this.usedChannels = usedChannels;
        this.registerChange("usedChannels");
    }

    public Set<ExpressionChannel> getParentExpressionChannels() {
        return new ChangesSupportSet<ExpressionChannel>(this, "parentExpressionChannels", parentExpressionChannels);
    }

    @Override
    protected ChannelNamespace calculateNamespace() {
        return ChannelNamespace.ADVERTISING;
    }

    @Override
    @XmlTransient
    public Set<CategoryChannel> getCategories() {
        throw new UnsupportedOperationException("Expression channels do not support categories");
    }

    @Override
    public void setCategories(Set<CategoryChannel> categories) {
        throw new UnsupportedOperationException("Expression channels do not support categories");
    }

    @Override
    public String getChannelType() {
        return CHANNEL_TYPE_EXPRESSION;
    }
}
