package com.foros.model;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.model.action.Action;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.Channel;
import com.foros.model.channel.Platform;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.currency.Currency;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.model.site.ThirdPartyCreative;
import com.foros.session.channel.triggerQA.TriggerQATO;
import com.foros.session.security.AdvertiserInAgency;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlSeeAlso({
        Campaign.class,
        CampaignCreativeGroup.class,
        CCGKeyword.class,
        Creative.class,
        CampaignCreative.class,
        CreativeCategory.class,
        Channel.class,
        TriggerQATO.class,
        ThirdPartyCreative.class,
        Action.class,
        Currency.class,
        Site.class,
        Tag.class,
        Platform.class,
        AdvertiserInAgency.class
})
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public abstract class EntityBase implements Serializable {

    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Set<String> changes = new HashSet<String>();

    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Context context = new Context();

    @XmlTransient
    @Deprecated
    /**
     * @Deprecated use isChanged, registerChange, unregisterChange methods
     */
    public Collection<String> getChanges() {
        return this.changes;
    }

    public void registerChange(String propertyName) {
        changes.add(propertyName);
    }

    public void registerChange(String ... propertyNames) {
        changes.addAll(Arrays.asList(propertyNames));
    }

    public boolean isChanged() {
        return !changes.isEmpty();
    }

    public boolean isChanged(String... propertyNames) {
        for (String property : propertyNames) {
            if (changes.contains(property)) {
                return true;
            }
        }

        return false;
    }

    public boolean unregisterChange(String... propertyNames) {
        return changes.removeAll(Arrays.asList(propertyNames));
    }

    public void unregisterChanges() {
        changes.clear();
    }

    public boolean retainChanges(String... propertyNames) {
        return changes.retainAll(Arrays.asList(propertyNames));
    }

    public boolean retainChanges(List<String> propertyNames) {
        return changes.retainAll(propertyNames);
    }

    public <T> T getProperty(ExtensionProperty<T> property) {
        return context.getProperty(property);
    }

    public <T> T setProperty(ExtensionProperty<T> property, T value) {
        return context.setProperty(property, value);
    }

    public <T> T removeProperty(ExtensionProperty<T> property) {
        return context.removeProperty(property);
    }

    public void clearProperties() {
        context.clearProperties();
    }
}
