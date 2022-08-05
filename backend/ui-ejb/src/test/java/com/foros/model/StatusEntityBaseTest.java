package com.foros.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.foros.annotations.AllowedStatuses;
import com.foros.model.account.Account;
import com.foros.model.account.AccountsPayableAccountBase;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.ExternalAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.action.Action;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BannedChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.KeywordChannel;
import com.foros.model.channel.PlacementBlacklistChannel;
import com.foros.model.channel.TriggersChannel;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.isp.Colocation;
import com.foros.model.security.User;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.model.site.TagPricing;
import com.foros.model.site.WDTag;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.Template;
import com.foros.util.CollectionUtils;
import com.foros.util.clazz.ClassFilter;
import com.foros.util.clazz.ClassSearcher;

import group.Unit;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class StatusEntityBaseTest {
    @Test
    public void getAllowedStatuses() {
        ActiveDeletedStatusBean instance = new ActiveDeletedStatusBean();
        Status[] expResult = new Status[] {Status.ACTIVE, Status.DELETED};
    
        Status[] result = instance.getAllowedStatuses();
        assertTrue(Arrays.equals(expResult, result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setStatus() {
        ActiveInactiveStatusBean instance = new ActiveInactiveStatusBean();

        instance.setStatus(Status.INACTIVE);
        Status result = instance.getStatus();
        assertEquals(Status.INACTIVE, result);

        instance.setStatus(Status.DELETED);
    }

    @Test
    public void allowedStatuses() throws Exception {
        // create a map
        Map<Class, Status[]> allowedStatusesMap = new HashMap<Class, Status[]>();
        allowedStatusesMap.put(Account.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(AgencyAccount.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(AdvertiserAccount.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(InternalAccount.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(IspAccount.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(CmpAccount.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(PublisherAccount.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(AdvertisingAccountBase.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(AccountsPayableAccountBase.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(ExternalAccount.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(Campaign.class, new Status[] {Status.ACTIVE, Status.DELETED, Status.INACTIVE});
        allowedStatusesMap.put(CampaignCreative.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(CampaignCreativeGroup.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED, Status.PENDING});
        allowedStatusesMap.put(Colocation.class, new Status[] {Status.ACTIVE, Status.DELETED});
        allowedStatusesMap.put(ExpressionChannel.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED, Status.PENDING_INACTIVATION});
        allowedStatusesMap.put(Creative.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED, Status.PENDING});
        allowedStatusesMap.put(CreativeSize.class, new Status[] {Status.ACTIVE, Status.DELETED});
        allowedStatusesMap.put(Template.class, new Status[] {Status.ACTIVE, Status.DELETED});
        allowedStatusesMap.put(CreativeTemplate.class, new Status[] {Status.ACTIVE, Status.DELETED});
        allowedStatusesMap.put(DiscoverTemplate.class, new Status[] {Status.ACTIVE, Status.DELETED});
        allowedStatusesMap.put(Channel.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(TriggersChannel.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(BehavioralChannel.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED, Status.PENDING_INACTIVATION});
        allowedStatusesMap.put(AudienceChannel.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED, Status.PENDING_INACTIVATION});
        allowedStatusesMap.put(DiscoverChannel.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(DiscoverChannelList.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(CategoryChannel.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(KeywordChannel.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(PlacementBlacklistChannel.class, new Status[] {Status.ACTIVE, Status.DELETED});
        allowedStatusesMap.put(Site.class, new Status[] {Status.ACTIVE, Status.DELETED});
        allowedStatusesMap.put(Tag.class, new Status[] {Status.ACTIVE, Status.DELETED});
        allowedStatusesMap.put(WDTag.class, new Status[] {Status.ACTIVE, Status.DELETED});
        allowedStatusesMap.put(TagPricing.class, new Status[] {Status.ACTIVE, Status.DELETED});
        allowedStatusesMap.put(User.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(CCGKeyword.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(Action.class, new Status[] {Status.ACTIVE, Status.DELETED});
        allowedStatusesMap.put(GeoChannel.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(DeviceChannel.class, new Status[] {Status.ACTIVE, Status.INACTIVE, Status.DELETED});
        allowedStatusesMap.put(BannedChannel.class, new Status[] {Status.ACTIVE, Status.DELETED});

        // create a statuses class list
        ClassSearcher classSearcher = new ClassSearcher("com.foros.model", true);
        Set<Class> entityClasses = classSearcher.search(new ClassFilter() {
            @Override
            public boolean accept(Class<?> clazz) {
                    return clazz.isAnnotationPresent(AllowedStatuses.class) && clazz.isAnnotationPresent(Entity.class);
                }
        });
        
        // check statuses
        for (Class<?> entityClass : entityClasses) {
            Status[] allowedStatuses = entityClass.getAnnotation(AllowedStatuses.class).values();
            Status[] expectedStatuses = allowedStatusesMap.get(entityClass);
            assertTrue("No allowed statuses mapping found for " + entityClass, expectedStatuses != null);
            Arrays.sort(allowedStatuses);
            Arrays.sort(expectedStatuses);
            assertTrue(entityClass.getName(), Arrays.equals(expectedStatuses, allowedStatuses));
        }
        
        // all classes with status must be checked
        Collection<Class> notTested = CollectionUtils.subtract(allowedStatusesMap.keySet(), entityClasses);
        assertEquals(0, notTested.size());
    }
}

@AllowedStatuses(values = {Status.ACTIVE, Status.INACTIVE})
class ActiveInactiveStatusBean extends StatusEntityBase {
    @Override
    public Status getParentStatus() {
        return Status.ACTIVE;
    }
}

@AllowedStatuses(values = {Status.ACTIVE, Status.DELETED})
class ActiveDeletedStatusBean extends StatusEntityBase {
    @Override
    public Status getParentStatus() {
        return Status.ACTIVE;
    }
}
