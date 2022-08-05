package com.foros.test.factory;

import com.foros.model.channel.DeviceChannel;
import com.foros.session.channel.service.DeviceChannelService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class DeviceChannelTestFactory extends ChannelTestFactory<DeviceChannel> {
    @EJB
    private DeviceChannelService channelService;

    public void populate(DeviceChannel channel) {
        channel.setName(getTestEntityRandomName());
        channel.setExpression("test expression");
    }

    @Override
    public DeviceChannel create() {
        DeviceChannel channel = new DeviceChannel();
        populate(channel);
        return channel;
    }

    public DeviceChannel create(DeviceChannel parent) {
        DeviceChannel channel = new DeviceChannel();
        channel.setParentChannel(parent);
        populate(channel);
        return channel;
    }

    @Override
    public void persist(DeviceChannel channel) {
        try {
            channelService.create(channel);
            entityManager.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(DeviceChannel channel) {
        try {
            channelService.update(channel);
            entityManager.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DeviceChannel createPersistent() {
        DeviceChannel channel = create(getMobileDeviceChannel());
        persist(channel);
        entityManager.flush();
        return refresh(channel);
    }

    public DeviceChannel createPersistent(DeviceChannel parent) {
        DeviceChannel channel = create(parent);
        persist(channel);
        entityManager.flush();
        return refresh(channel);
    }

    public void delete(DeviceChannel channel) {
        channelService.delete(channel.getId());
    }

    public DeviceChannel getMobileDeviceChannel() {
        return channelService.getMobileDevicesChannel();
    }

    public DeviceChannel getNonMobileDeviceChannel() {
        return channelService.getNonMobileDevicesChannel();
    }

    public DeviceChannel getBrowsersChannel() {
        return channelService.getBrowsersChannel();
    }

    public DeviceChannel getApplicationsChannel() {
        return channelService.getApplicationsChannel();
    }
}
