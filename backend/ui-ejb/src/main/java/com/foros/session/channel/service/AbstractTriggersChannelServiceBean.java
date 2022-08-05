package com.foros.session.channel.service;

import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.model.channel.TriggersChannel;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.model.channel.trigger.TriggersHolder;
import com.foros.model.channel.trigger.UrlTrigger;
import com.foros.session.channel.TriggerService;
import com.foros.util.command.executor.HibernateWorkExecutorService;
import com.foros.util.xml.QADescriptionChannelMinUrlTriggerThreshold;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.EJB;

abstract class AbstractTriggersChannelServiceBean<C extends TriggersChannel>
        extends AbstractChannelServiceBean<C> implements BehavioralParametersFinder<C> {

    @EJB
    HibernateWorkExecutorService executorService;

    @EJB
    protected TriggerService triggerService;

    public C findWithTriggers(Long channelId) {
        C channel = find(channelId);
        if (!channel.isTriggersInitialized()) {
            channel.resetTriggers(triggerService.getTriggersByChannelId(channel));
        }
        return channel;
    }

    protected void checkMinUrlTriggerThreshold(C channel) {
        if (channel.getStatus() == Status.INACTIVE) {
            return;
        }
        if (!isUrlTriggersEnabled(channel)) {
            return;
        }
        long uniqueGroupsCount = 0;
        Collection<UrlTrigger> urlTriggers;
        if (!channel.getUrls().isEmpty()) {
            urlTriggers = channel.getUrls().getAll();
            UrlTrigger.calcMasked(urlTriggers);
            Set<String> groups = new HashSet<String>();
            for (UrlTrigger trigger : urlTriggers) {
                if (!trigger.isMasked() && !trigger.isNegative()) {
                    groups.add(trigger.getGroup());
                }
            }
            uniqueGroupsCount = groups.size();
        } else {
            urlTriggers = Collections.emptyList();
        }
        long threshold = channel.getCountry().getMinUrlTriggerThreshold();
        if (threshold > 0 && urlTriggers.size() > 0 && uniqueGroupsCount < threshold) {
            channel.setQaUser(null);
            channel.setQaDate(new Date());
            channel.setQaStatus(ApproveStatus.DECLINED);
            channel.setQaDescriptionObject(new QADescriptionChannelMinUrlTriggerThreshold(threshold, uniqueGroupsCount));
        }
    }

    protected void makeApprovedOnChange(C existing, C channel) {
        if (existing.getQaStatus() != ApproveStatus.DECLINED) {
            return;
        }
        boolean triggersCountryChanged = existing.getTriggers().size() > 0 && channel.isChanged("country") && !existing.getCountry().getCountryCode().equals(channel.getCountry().getCountryCode());
        boolean pageKeywordsChanged = channel.isChanged("pageKeywords") && !TriggersHolder.equals(existing.getPageKeywords(), channel.getPageKeywords());
        boolean searchKeywordsChanged = channel.isChanged("searchKeywords") && !TriggersHolder.equals(existing.getSearchKeywords(), channel.getSearchKeywords());
        boolean urlsChanged = channel.isChanged("urls") && !TriggersHolder.equals(existing.getUrls(), channel.getUrls());
        boolean urlKeywordsChanged = channel.isChanged("urlKeywords") && !TriggersHolder.equals(existing.getUrlKeywords(), channel.getUrlKeywords());

        // approval status for auto-declined channel to be recalculated when URL triggers become disabled
        if (!urlsChanged
                && isUrlTriggersEnabled(existing)
                && !isUrlTriggersEnabled(channel)
                && existing.getQaDescriptionObject() instanceof QADescriptionChannelMinUrlTriggerThreshold
                ) {
            urlsChanged = true;
        }

        if (triggersCountryChanged || pageKeywordsChanged || searchKeywordsChanged || urlKeywordsChanged || urlsChanged || !isUrlTriggersEnabled(channel)) {
            existing.setQaUser(null);
            existing.setQaDate(null);
            existing.setQaStatus(ApproveStatus.APPROVED);
            existing.setQaDescriptionObject(null);
        }
    }

    private boolean isUrlTriggersEnabled(C channel) {
        return TriggerType.findBehavioralParameters(getBehavioralParameters(channel), TriggerType.URL) != null;
    }
}
