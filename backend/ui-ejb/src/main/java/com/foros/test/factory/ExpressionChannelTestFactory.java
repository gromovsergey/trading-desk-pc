package com.foros.test.factory;

import com.foros.model.Country;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.channel.ExpressionChannel;
import com.foros.session.channel.service.ExpressionChannelService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class ExpressionChannelTestFactory extends ChannelTestFactory<ExpressionChannel> {
    @EJB
    private ExpressionChannelService channelService;

    @EJB
    private AdvertiserAccountTestFactory advertiserAccountTF;

    public void populate(ExpressionChannel channel) {
        channel.setName(getTestEntityRandomName());
        channel.setCountry(new Country("US"));
    }

    @Override
    public ExpressionChannel create() {
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        return create(account);
    }

    public ExpressionChannel create(Account account) {
        ExpressionChannel channel = new ExpressionChannel();
        channel.setAccount(account);
        channel.setExpression("^");
        populate(channel);
        return channel;
    }

    @Override
    public void persist(ExpressionChannel channel) {
        channelService.create(channel);
        entityManager.flush();
    }

    public void update(ExpressionChannel channel) {
        channelService.update(channel);
        entityManager.flush();
    }

    @Override
    public ExpressionChannel createPersistent() {
        ExpressionChannel channel = create();
        persist(channel);
        return channel;
    }

    public ExpressionChannel createPersistent(Account account) {
        ExpressionChannel channel = create(account);
        persist(channel);
        return channel;
    }

    public void delete(ExpressionChannel channel) {
        channelService.delete(channel.getId());
    }

    public void makePublic(ExpressionChannel expressionChannel) throws Exception {
        channelService.makePublic(expressionChannel.getId(), expressionChannel.getVersion());
    }
}
