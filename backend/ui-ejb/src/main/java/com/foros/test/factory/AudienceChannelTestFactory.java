package com.foros.test.factory;

import com.foros.model.Country;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.session.channel.service.AudienceChannelService;
import com.foros.session.status.DisplayStatusService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


@Stateless
@LocalBean
public class AudienceChannelTestFactory  extends ChannelTestFactory<AudienceChannel> {

    @EJB
    private AudienceChannelService channelService;

    @EJB
    private DisplayStatusService displayStatusService;

    @EJB
    private AdvertiserAccountTestFactory advertiserAccountTF;

    public void populate(AudienceChannel channel) {
        channel.setName(getTestEntityRandomName());
        channel.setCountry(new Country("US"));
        channel.setVisibility(ChannelVisibility.PRI);
    }

    @Override
    public AudienceChannel create() {
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        return create(account);
    }

    public AudienceChannel create(Account account) {
        AudienceChannel channel = new AudienceChannel();
        channel.setAccount(account);
        populate(channel);
        return channel;
    }

    @Override
    public void persist(AudienceChannel channel) {
        try {
            channelService.createBulk(channel);
            entityManager.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(AudienceChannel channel) {
        try {
            channelService.updateBulk(channel);
            entityManager.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void inactivate(AudienceChannel channel) {
        try {
            channelService.inactivate(channel.getId());
            entityManager.flush();
            refresh(channel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AudienceChannel createPersistent() {
        AudienceChannel channel = create();
        persist(channel);
        entityManager.flush();
        return refresh(channel);
    }

    public AudienceChannel createPersistent(Account account) {
        AudienceChannel channel = create(account);
        persist(channel);
        entityManager.flush();
        return refresh(channel);
    }

    public void delete(AudienceChannel channel) {
        channelService.delete(channel.getId());
    }

    @Override
    public AudienceChannel refresh(AudienceChannel entity) {
        super.refresh(entity);
        return channelService.find(entity.getId());
    }
}
