package com.foros.test.factory;

import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.RateType;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.ChannelRate;
import com.foros.session.channel.TriggerService;
import com.foros.session.channel.service.BehavioralChannelService;
import com.foros.session.channel.triggerQA.TriggerQASearchFilter;
import com.foros.session.channel.triggerQA.TriggerQASearchParameters;
import com.foros.session.channel.triggerQA.TriggerQAService;
import com.foros.session.channel.triggerQA.TriggerQASortType;
import com.foros.session.channel.triggerQA.TriggerQATO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class BehavioralChannelTestFactory extends ChannelTestFactory<BehavioralChannel> {
    @EJB
    private BehavioralChannelService channelService;

    @EJB
    private TriggerQAService triggerQAService;

    @EJB
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @EJB
    private TriggerService triggerService;

    public void populate(BehavioralChannel channel) {
        channel.setName(getTestEntityRandomName());
        channel.getPageKeywords().setPositive("pageKeyword");
        channel.getSearchKeywords().setPositive("searchKeyword");
        channel.getUrls().setPositive("url");
        channel.setCountry(new Country("US"));
        channel.setLanguage("en");
        channel.setBehavioralParameters(getBehavioralParameters(channel));
    }

    @Override
    public BehavioralChannel create() {
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        return create(account);
    }

    public BehavioralChannel create(Account account) {
        BehavioralChannel channel = new BehavioralChannel();
        channel.setAccount(account);
        populate(channel);
        return channel;
    }

    @Override
    public void persist(BehavioralChannel channel) {
        try {
            channelService.create(channel);
            entityManager.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(BehavioralChannel channel) {
        try {
            channelService.update(channel);
            entityManager.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void inactivate(BehavioralChannel channel) {
        try {
            channelService.inactivate(channel.getId());
            entityManager.flush();
            refresh(channel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BehavioralChannel createPersistent() {
        BehavioralChannel channel = create();
        persist(channel);
        entityManager.flush();
        return refresh(channel);
    }

    public BehavioralChannel createPersistent(Account account) {
        BehavioralChannel channel = create(account);
        persist(channel);
        entityManager.flush();
        return refresh(channel);
    }

    public void delete(BehavioralChannel channel) {
        channelService.delete(channel.getId());
    }

    public void makeLive(BehavioralChannel behavioralChannel) throws Exception {
        TriggerQASearchParameters parameters = new TriggerQASearchParameters(0, 100, null, TriggerQASearchFilter.ALL,
                null, null, null, null, null, behavioralChannel.getId(), behavioralChannel.getCountry().getCountryCode(),
                null, null, null, null, TriggerQASortType.NEWEST);
        List<TriggerQATO> triggers = triggerQAService.search(parameters);
        for (TriggerQATO trigger : triggers) {
            trigger.setQaStatus(ApproveStatus.APPROVED);
        }
        triggerQAService.update(triggers);
        if (behavioralChannel.getStatus() != Status.ACTIVE) {
            channelService.activate(behavioralChannel.getId());
        }
    }

    public void submitToCmp(BehavioralChannel behavioralChannel) throws Exception {
        makeLive(behavioralChannel);
        ChannelRate rate = new ChannelRate();
        rate.setCpc(BigDecimal.TEN);
        rate.setRateType(RateType.CPC);
        rate.setEffectiveDate(new Date());
        rate.setChannel(behavioralChannel);
        rate.setCurrency(behavioralChannel.getAccount().getCurrency());
        behavioralChannel.setChannelRate(rate);

        channelService.submitToCmp(behavioralChannel);
    }

    @Override
    public BehavioralChannel refresh(BehavioralChannel entity) {
        BehavioralChannel channel = super.refresh(entity);
        channel.resetTriggers(triggerService.getTriggersByChannelId(channel));
        return channel;
    }
}
