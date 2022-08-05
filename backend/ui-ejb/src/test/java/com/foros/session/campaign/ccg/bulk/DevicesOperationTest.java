package com.foros.session.campaign.ccg.bulk;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.DeviceChannel;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.test.factory.DisplayCCGTestFactory;

import group.Bulk;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashSet;
import java.util.Set;


@Category({ Unit.class, Bulk.class} )
public class DevicesOperationTest extends AbstractServiceBeanIntegrationTest {

    @Autowired
    private DeviceChannelService deviceChannelService;

    @Autowired
    private DisplayCCGTestFactory displayCCGTF;

    @Test
    public void testAdd() throws Exception {
        CampaignCreativeGroup group1 = createGroupWithRootDevice(deviceChannelService.getMobileDevicesChannel());

        Set<Long> deviceChannelsToAdd = new HashSet<>();
        deviceChannelsToAdd.add(deviceChannelService.getNonMobileDevicesChannel().getId());
        AddDevicesOperation operation = new AddDevicesOperation(deviceChannelsToAdd, deviceChannelService);

        Set<DeviceChannel> result = operation.getChannelsToUpdate(group1);
        // NonMobile must be added to Mobile and then normalized to Browsers
        assertEquals(1, result.size());
        assertEquals(deviceChannelService.getBrowsersChannel(), result.iterator().next());

        CampaignCreativeGroup group2 = createGroupWithRootDevice(deviceChannelService.getBrowsersChannel());
        result = operation.getChannelsToUpdate(group2);
        // Adding child -> result is Parent
        assertEquals(1, result.size());
        assertEquals(deviceChannelService.getBrowsersChannel(), result.iterator().next());

        // All devices
        CampaignCreativeGroup group3 = createGroupWithRootDevice(null);
        result = operation.getChannelsToUpdate(group3);
        // Adding child to all -> all
        assertTrue(result.isEmpty());
    }

    @Test
    public void testRemove() throws Exception {
        // All devices
        CampaignCreativeGroup group1 = createGroupWithRootDevice(null);

        Set<Long> deviceChannelsToRemove = new HashSet<>();
        deviceChannelsToRemove.add(deviceChannelService.getNonMobileDevicesChannel().getId());
        RemoveDevicesOperation operation = new RemoveDevicesOperation(deviceChannelsToRemove, deviceChannelService);

        Set<DeviceChannel> result = operation.getChannelsToUpdate(group1);
        // NonMobile must be removed, so result is Applications and Mobile
        assertEquals(2, result.size());
        assertTrue(result.contains(deviceChannelService.getMobileDevicesChannel()));
        assertTrue(result.contains(deviceChannelService.getApplicationsChannel()));

        CampaignCreativeGroup group2 = createGroupWithRootDevice(deviceChannelService.getBrowsersChannel());
        result = operation.getChannelsToUpdate(group2);
        // NonMobile must be removed, so result is Mobile
        assertEquals(1, result.size());
        assertTrue(result.contains(deviceChannelService.getMobileDevicesChannel()));

        CampaignCreativeGroup group3 = createGroupWithRootDevice(deviceChannelService.getApplicationsChannel());
        result = operation.getChannelsToUpdate(group3);
        // NonMobile can't be removed, because is not selected in group, so result is Applications
        assertEquals(1, result.size());
        assertTrue(result.contains(deviceChannelService.getApplicationsChannel()));

        deviceChannelsToRemove.add(deviceChannelService.getBrowsersChannel().getId());
        operation = new RemoveDevicesOperation(deviceChannelsToRemove, deviceChannelService);
        result = operation.getChannelsToUpdate(group2);
        // NonMobile must be removed, so result is Mobile
        assertEquals(1, result.size());
        assertTrue(result.contains(deviceChannelService.getMobileDevicesChannel()));
    }

    @Test
    public void testSet() throws Exception {
        CampaignCreativeGroup group1 = createGroupWithRootDevice(deviceChannelService.getMobileDevicesChannel());

        Set<Long> deviceChannelsToSet = new HashSet<>();
        deviceChannelsToSet.add(deviceChannelService.getNonMobileDevicesChannel().getId());
        SetDevicesOperation operation = new SetDevicesOperation(deviceChannelsToSet, deviceChannelService);

        Set<DeviceChannel> result = operation.getChannelsToUpdate(group1);
        // NonMobile must be set and be alone, because Mobile will be removed during set operation
        assertEquals(1, result.size());
        assertEquals(deviceChannelService.getNonMobileDevicesChannel(), result.iterator().next());

        CampaignCreativeGroup group2 = createGroupWithRootDevice(deviceChannelService.getNonMobileDevicesChannel());
        result = operation.getChannelsToUpdate(group2);
        assertEquals(1, result.size());
        assertEquals(deviceChannelService.getNonMobileDevicesChannel(), result.iterator().next());

        CampaignCreativeGroup group3 = createGroupWithRootDevice(deviceChannelService.getBrowsersChannel());
        result = operation.getChannelsToUpdate(group3);
        // Even parent must be removed by a child
        assertEquals(1, result.size());
        assertEquals(deviceChannelService.getNonMobileDevicesChannel(), result.iterator().next());

        // All devices
        CampaignCreativeGroup group4 = createGroupWithRootDevice(null);
        result = operation.getChannelsToUpdate(group4);
        // Even All must be removed by a child
        assertEquals(1, result.size());
        assertEquals(deviceChannelService.getNonMobileDevicesChannel(), result.iterator().next());
    }

    CampaignCreativeGroup createGroupWithRootDevice(DeviceChannel rootDeviceChannel) {
        CampaignCreativeGroup group = displayCCGTF.create();
        assertTrue(group.getAccount().getAccountType().getDeviceChannels().contains(deviceChannelService.getApplicationsChannel()));
        assertTrue(group.getAccount().getAccountType().getDeviceChannels().contains(deviceChannelService.getBrowsersChannel()));
        assertTrue(group.getAccount().getAccountType().getDeviceChannels().contains(deviceChannelService.getMobileDevicesChannel()));
        assertTrue(group.getAccount().getAccountType().getDeviceChannels().contains(deviceChannelService.getNonMobileDevicesChannel()));
        assertTrue(group.getAccount().getAccountType().getDeviceChannels().containsAll(deviceChannelService.getNonMobileDevicesChannel().getChildChannels()));
        assertTrue(group.getAccount().getAccountType().getDeviceChannels().containsAll(deviceChannelService.getMobileDevicesChannel().getChildChannels()));

        group.getDeviceChannels().clear();
        // Means 'All'
        if (rootDeviceChannel != null) {
            group.getDeviceChannels().add(rootDeviceChannel);
        }

        displayCCGTF.persist(group);

        if (rootDeviceChannel == null) {
            assertTrue(group.getDeviceChannels().isEmpty());
        } else {
            assertEquals(1, group.getDeviceChannels().size());
            assertEquals(rootDeviceChannel, group.getDeviceChannels().iterator().next());
        }

        return group;
    }
}
