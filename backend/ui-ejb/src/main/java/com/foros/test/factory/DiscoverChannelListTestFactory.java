package com.foros.test.factory;

import com.foros.model.Country;
import com.foros.model.account.Account;
import com.foros.model.account.InternalAccount;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.session.channel.service.DiscoverChannelListService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class DiscoverChannelListTestFactory extends ChannelTestFactory<DiscoverChannelList> {
    @EJB
    private DiscoverChannelListService channelListService;

    @EJB
    private CountryTestFactory countryTF;

    @EJB
    private InternalAccountTestFactory internalAccountTF;

    public void populate(DiscoverChannelList channelList) {
        channelList.setName(getTestEntityRandomName());
        channelList.setCountry(countryTF.findOrCreatePersistent("US"));
        channelList.setLanguage("en");
        channelList.setKeywordTriggerMacro("kwtrigger_" + DiscoverChannelList.KEYWORD_TOKEN);
        channelList.setChannelNameMacro("child_" + DiscoverChannelList.KEYWORD_TOKEN);
        channelList.setDiscoverQuery("query_" + DiscoverChannelList.KEYWORD_TOKEN);
        channelList.setDiscoverAnnotation("annotation_" + DiscoverChannelList.KEYWORD_TOKEN);
        channelList.setDescription("Aaa " + DiscoverChannelList.KEYWORD_TOKEN + " Bbb");
    }

    @Override
    public DiscoverChannelList create() {
        InternalAccount account = internalAccountTF.createPersistent();
        return create(account);
    }

    public DiscoverChannelList create(Account account) {
        DiscoverChannelList channel = new DiscoverChannelList();
        channel.setAccount(account);
        populate(channel);
        return channel;
    }

    public DiscoverChannelList create(Account account, Country country) {
        DiscoverChannelList channelList = create(account);
        channelList.setCountry(country);
        return channelList;
    }

    @Override
    public DiscoverChannelList createPersistent() {
        InternalAccount account = internalAccountTF.createPersistent();
        return createPersistent(account);
    }

    public DiscoverChannelList createPersistent(InternalAccount account) {
        return createPersistent(account, "First\r\nSecond\r\nThird");
    }

    public DiscoverChannelList createPersistent(InternalAccount account, String keywords) {
        DiscoverChannelList dcList = create(account);
        dcList.setName(getTestEntityRandomName());
        dcList.setKeywordList(keywords);
        persist(dcList);
        entityManager.flush();
        dcList = refresh(dcList);
        if (dcList.getChildChannels() != null) {
            dcList.getChildChannels().size();
        }
        return dcList;
    }

    @Override
    public void persist(DiscoverChannelList dcList) {
        try {
            channelListService.create(dcList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(DiscoverChannelList dcList) {
        channelListService.update(dcList);
    }
}