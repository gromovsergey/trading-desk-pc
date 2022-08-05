package com.foros.test.factory;

import com.foros.model.campaign.RateType;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelRate;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.persistence.hibernate.HibernateInterceptor;
import com.foros.session.channel.KeywordChannelsHibernateHandler;
import com.foros.util.PersistenceUtils;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.Session;
import org.joda.time.LocalDate;

public abstract class ChannelTestFactory<T extends Channel> extends TestFactory<T>  {

    public Set<BehavioralParameters> getBehavioralParameters(BehavioralChannel channel) {
        BehavioralParameters bParam = new BehavioralParameters();
        Set<BehavioralParameters> channelParameters = new LinkedHashSet<BehavioralParameters>();
        bParam.setMinimumVisits(1L);
        bParam.setTimeFrom(7200L);
        bParam.setTimeTo(10800L);
        bParam.setTriggerType(TriggerType.SEARCH_KEYWORD.getLetter());
        bParam.setChannel(channel);
        channelParameters.add(bParam);

        return channelParameters;
    }

    public ChannelRate createChannelRate(Channel channel, RateType rateType, BigDecimal cpc) {
        ChannelRate channelRate = new ChannelRate();
        channelRate.setChannel(channel);
        channelRate.setCpc(cpc);
        channelRate.setCurrency(channel.getAccount().getCurrency());
        channelRate.setRateType(rateType);
        channelRate.setEffectiveDate(new LocalDate().plusYears(100).toDate());
        return channelRate;
    }

    public void initChannelTriggersHibernateHandler() {
        Session session = PersistenceUtils.getHibernateSession(entityManager);
        HibernateInterceptor hi = PersistenceUtils.getInterceptor(session);
        KeywordChannelsHibernateHandler handler = hi.getKeywordChannelsHibernateInterceptor();
        handler.initialize(session);
    }

    public void executeChannelTriggersHibernateHandler() {
        HibernateInterceptor hi = PersistenceUtils.getInterceptor(entityManager);
        KeywordChannelsHibernateHandler handler = hi.getKeywordChannelsHibernateInterceptor();
        handler.handle();
    }
}
