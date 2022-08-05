package com.foros.test.factory;

import com.foros.model.Country;
import com.foros.model.account.Account;
import com.foros.model.account.InternalAccount;
import com.foros.model.channel.DiscoverChannel;
import com.foros.session.channel.service.DiscoverChannelService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class DiscoverChannelTestFactory extends ChannelTestFactory<DiscoverChannel> {
    @EJB
    private DiscoverChannelService channelService;

    @EJB
    private CountryTestFactory countryTF;

    @EJB
    private InternalAccountTestFactory internalAccountTF;

    public void populate(DiscoverChannel channel) {
        channel.setName(getTestEntityRandomName());
        channel.setCountry(countryTF.findOrCreatePersistent("US"));
        channel.setLanguage("en");
        channel.getUrls().setPositive("url.com");
        channel.getPageKeywords().setPositive("pageKeyword");
        channel.getSearchKeywords().setPositive("searchKeyword");
        channel.setDiscoverAnnotation("discoverAnnotation");
        channel.setDiscoverQuery("discoverQuery");
    }

    @Override
    public DiscoverChannel create() {
        InternalAccount account = internalAccountTF.createPersistent();
        return create(account);
    }

    public DiscoverChannel create(Account account) {
        DiscoverChannel channel = new DiscoverChannel();
        channel.setAccount(account);
        populate(channel);
        return channel;
    }

    public DiscoverChannel create(Account account, Country country) {
        DiscoverChannel channelList = create(account);
        channelList.setCountry(country);
        return channelList;
    }

    @Override
    public void persist(DiscoverChannel channel) {
        try {
            channelService.create(channel);
            entityManager.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(DiscoverChannel channel) {
        try {
            channelService.update(channel);
            entityManager.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DiscoverChannel createPersistent() {
        DiscoverChannel channel = create();
        persist(channel);
        entityManager.flush();
        return refresh(channel);
    }

    public DiscoverChannel createPersistent(Account account) {
        DiscoverChannel channel = create(account);
        persist(channel);
        entityManager.flush();
        return refresh(channel);
    }
}
