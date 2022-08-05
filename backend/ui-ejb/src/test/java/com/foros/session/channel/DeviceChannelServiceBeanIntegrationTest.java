package com.foros.session.channel;

import com.foros.model.Status;
import com.foros.model.channel.DeviceChannel;
import com.foros.session.EntityTO;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.test.factory.DeviceChannelTestFactory;
import com.foros.util.EntityUtils;
import com.foros.util.tree.TreeNode;

import group.Db;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class DeviceChannelServiceBeanIntegrationTest extends AbstractChannelServiceBeanIntegrationTest<DeviceChannel> {
    @Autowired
    private DeviceChannelService deviceChannelService;

    @Autowired
    private DeviceChannelTestFactory deviceChannelTF;

    @Test
    public void testDeviceChannelFindByName() throws Exception {
        deviceChannelService.getMobileDevicesChannel();
    }

    @Test
    public void testDeviceChannelUpdate() throws Exception {
        DeviceChannel channel = deviceChannelTF.createPersistent();
        deviceChannelTF.refresh(channel);
        assertNotNull(channel.getId());

        Timestamp version = channel.getVersion();
        DeviceChannel updatedChannel = deviceChannelTF.create();
        updatedChannel.setId(channel.getId());
        updatedChannel.setVersion(channel.getVersion());
        deviceChannelService.update(updatedChannel);
        entityManager.flush();
        channel = deviceChannelTF.refresh(channel);
        assertFalse(version.equals(channel.getVersion()));

        // update triggers only
        version = channel.getVersion();

        updatedChannel = deviceChannelTF.create();
        updatedChannel.setId(channel.getId());
        updatedChannel.setName(deviceChannelTF.getTestEntityRandomName());
        updatedChannel.setVersion(version);

        deviceChannelService.update(updatedChannel);
        entityManager.flush();

        channel = deviceChannelTF.refresh(channel);
        assertFalse(version.equals(channel.getVersion()));
    }

    @Test
    public void testSearch() {
        DeviceChannel channel = deviceChannelTF.createPersistent();
        assertNotNull(channel);
        commitChanges();

        deviceChannelService.delete(channel.getId());
        commitChanges();

        setDeletedObjectsVisible(true);
        List<DeviceChannelTO> result = deviceChannelService.getChannelList(null);
        assertTrue(checkStatus(result, Status.DELETED));
        setDeletedObjectsVisible(false);
        result = deviceChannelService.getChannelList(null);
        assertTrue(!checkStatus(result, Status.DELETED));
    }

    @Test
    public void testDelete() throws Exception {
        DeviceChannel channel = deviceChannelTF.createPersistent();
        assertNotNull(channel);
        clearContext();

        deviceChannelService.delete(channel.getId());
        entityManager.flush();
    }

    @Test
    public void testSearchAssociatedCampaings() throws Exception {
        Long channelId = deviceChannelService.getMobileDevicesChannel().getId();
        deviceChannelService.searchAssociatedCampaigns(channelId, 0, 100);
    }

    private boolean checkStatus(List<DeviceChannelTO> list, Status status) {
        for (DeviceChannelTO item : list) {
            if (item.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testGetChannelAncestorsChain() {
        Long channelId = deviceChannelService.getMobileDevicesChannel().getId();
        DeviceChannel mobileChannel = deviceChannelService.findById(channelId);
        DeviceChannel child = mobileChannel.getChildChannels().iterator().next();

        List<EntityTO> res1 = deviceChannelService.getChannelAncestorsChain(child.getId(), true);
        assertNotNull(res1);
        assertEquals(3, res1.size());

        List<EntityTO> res2 = deviceChannelService.getChannelAncestorsChain(child.getId(), false);
        assertNotNull(res2);
        assertEquals(2, res2.size());
    }

    @Test
    public void testTreeRoot() {
        TreeNode<EntityTO> browsersRoot = deviceChannelService.getBrowsersTreeRoot();
        assertNotNull(browsersRoot);
        assertTrue(browsersRoot.getChildren().size() > 0);

        TreeNode<EntityTO> applicationsRoot = deviceChannelService.getApplicationsTreeRoot();
        assertNotNull(applicationsRoot);
        assertTrue(applicationsRoot.getChildren().size() == 0);
    }

    @Test
    public void testNormalizeFullTree() {
        DeviceChannel browsers = deviceChannelService.getBrowsersChannel();
        Set<Long> allDevicesIdsCollection = EntityUtils.getEntityIds(deviceChannelService.getChannelList(browsers.getId()));
        allDevicesIdsCollection.add(browsers.getId());

        // Most Top = 'Browsers'
        Set<DeviceChannel> normalizedCollection = deviceChannelService.getNormalizedDeviceChannelsCollection(allDevicesIdsCollection, allDevicesIdsCollection);
        assertEquals(1, normalizedCollection.size());
        assertEquals(browsers, normalizedCollection.iterator().next());
    }

    @Test
    public void testNormalizeFullSubtreeOfRoot() {
        DeviceChannel browsers = deviceChannelService.getBrowsersChannel();
        Set<Long> devicesIdsCollection = EntityUtils.getEntityIds(deviceChannelService.getChannelList(browsers.getId()));
        Set<Long> allDevicesIdsCollection = new HashSet<>(devicesIdsCollection);
        allDevicesIdsCollection.add(browsers.getId());

        // 'Browsers' must be added to normalized (because all children selected) and be the only one, because it is most top channel
        Set<DeviceChannel> normalizedCollection = deviceChannelService.getNormalizedDeviceChannelsCollection(devicesIdsCollection, allDevicesIdsCollection);
        assertEquals(1, normalizedCollection.size());
        assertEquals(browsers, normalizedCollection.iterator().next());
    }

    @Test
    public void testNormalizeFullTreeWithDisabledRoot() {
        DeviceChannel browsers = deviceChannelService.getBrowsersChannel();
        Set<Long> devicesIdsCollection = EntityUtils.getEntityIds(deviceChannelService.getChannelList(browsers.getId()));
        Set<Long> allDevicesIdsCollection = new HashSet<>(devicesIdsCollection);
        allDevicesIdsCollection.add(browsers.getId());

        // All selected, but 'Browsers' not allowed, so all its children must be chosen
        Set<DeviceChannel> normalizedCollection = deviceChannelService.getNormalizedDeviceChannelsCollection(allDevicesIdsCollection, devicesIdsCollection);
        Set<DeviceChannel> browsersChildren = browsers.getChildChannels();
        assertEquals(browsersChildren, normalizedCollection);
    }

    @Test
    public void testNormalizeTreeWithUnselectedNodeInTheMiddle() {
        DeviceChannel browsers = deviceChannelService.getBrowsersChannel();
        Set<Long> devicesIdsCollection = EntityUtils.getEntityIds(deviceChannelService.getChannelList(browsers.getId()));
        Set<Long> allDevicesIdsCollection = new HashSet<>(devicesIdsCollection);
        allDevicesIdsCollection.add(browsers.getId());

        devicesIdsCollection.remove(deviceChannelService.getMobileDevicesChannel().getId());
        devicesIdsCollection.remove(deviceChannelService.getNonMobileDevicesChannel().getId());

        DeviceChannel middleDeviceChannel = deviceChannelService.getMobileDevicesChannel();
        while (middleDeviceChannel.getChildChannels().size() < 2) {
            if (middleDeviceChannel.getChildChannels().isEmpty()) {
                break;
            }
            devicesIdsCollection.remove(middleDeviceChannel.getId());
            middleDeviceChannel = middleDeviceChannel.getChildChannels().iterator().next();
        }
        assertTrue("Please create at least one device channel with at least 2 children", middleDeviceChannel.getChildChannels().size() > 1);

        // Un-selecting channel and first child
        devicesIdsCollection.remove(middleDeviceChannel.getId());
        Iterator<DeviceChannel> middleDeviceChannelIterator = middleDeviceChannel.getChildChannels().iterator();
        DeviceChannel removedChildChannel = middleDeviceChannelIterator.next();
        devicesIdsCollection.remove(removedChildChannel.getId());

        assertFalse("Please work here with device channel, which have children", removedChildChannel.getChildChannels().isEmpty());
        // 'Browsers' must be added to normalized (because all grand children selected) and be the only one, because it is most top channel
        Set<DeviceChannel> normalizedCollection = deviceChannelService.getNormalizedDeviceChannelsCollection(devicesIdsCollection, allDevicesIdsCollection);
        assertEquals(1, normalizedCollection.size());
        assertEquals(browsers, normalizedCollection.iterator().next());

        // Now, un-selecting all children and grandchildren of removedChildNode, so it must not be selected automatically
        removeAllChildren(removedChildChannel, devicesIdsCollection);
        Set<DeviceChannel> expectedChannels = new HashSet<>();
        while (middleDeviceChannelIterator.hasNext()) {
            Long channelId = middleDeviceChannelIterator.next().getId();
            DeviceChannel channel = deviceChannelService.findById(channelId);
            expectedChannels.add(channel);
        }
        // Other 'Browsers' children will be chosen
        expectedChannels.add(deviceChannelService.getNonMobileDevicesChannel());

        normalizedCollection = deviceChannelService.getNormalizedDeviceChannelsCollection(devicesIdsCollection, allDevicesIdsCollection);
        assertEquals(expectedChannels, normalizedCollection);
    }

    @Test
    public void testNormalizeHoleNode() {
        DeviceChannel browsers = deviceChannelService.getBrowsersChannel();
        DeviceChannel mobileChannel = deviceChannelService.getMobileDevicesChannel();
        Set<Long> devicesIdsCollection = EntityUtils.getEntityIds(deviceChannelService.getChannelList(browsers.getId()));
        devicesIdsCollection.add(browsers.getId());
        devicesIdsCollection.remove(mobileChannel.getId());

        // Mobile is excluded from selected and from allowed (simulates 'hole' on ccg device targeting page)
        Set<DeviceChannel> normalizedCollection = deviceChannelService.getNormalizedDeviceChannelsCollection(devicesIdsCollection, devicesIdsCollection);
        assertEquals(1, normalizedCollection.size());
        assertEquals(browsers, normalizedCollection.iterator().next());

        // Now, Browsers is a 'hole' too, so can't be selected
        devicesIdsCollection.remove(browsers.getId());
        // Non-Mobile and Mobile children are expected
        normalizedCollection = deviceChannelService.getNormalizedDeviceChannelsCollection(devicesIdsCollection, devicesIdsCollection);
        Set<DeviceChannel> expectedResult = new HashSet<>(mobileChannel.getChildChannels());
        expectedResult.add(deviceChannelService.getNonMobileDevicesChannel());
        assertEquals(expectedResult, normalizedCollection);
    }

    @Test
    public void testNormalizeWithVariousStatuses() {
        DeviceChannel browsers = deviceChannelService.getBrowsersChannel();
        Set<Long> devicesIdsCollection = EntityUtils.getEntityIds(deviceChannelService.getChannelList(browsers.getId()));
        devicesIdsCollection.add(browsers.getId());
        DeviceChannel mobileChannel = deviceChannelService.getMobileDevicesChannel();

        // Terminal Node will be removed
        DeviceChannel terminalChannel = mobileChannel;
        while (!terminalChannel.getChildChannels().isEmpty()) {
            terminalChannel = terminalChannel.getChildChannels().iterator().next();
        }

        devicesIdsCollection.remove(terminalChannel.getId());

        // Browsers must not be in the result, because terminal node is Active and not selected (and not allowed), so its parent wil be unselected and so on.
        Set<DeviceChannel> normalizedCollection = deviceChannelService.getNormalizedDeviceChannelsCollection(devicesIdsCollection, devicesIdsCollection);
        assertFalse(normalizedCollection.contains(terminalChannel));
        assertFalse(normalizedCollection.contains(browsers));

        // Now, terminal node is inactive
        deviceChannelService.inactivate(terminalChannel.getId());
        commitChangesAndClearContext();
        // Node is excluded from selected and from allowed, and Inactive. So it must not participate in selection of parent.
        normalizedCollection = deviceChannelService.getNormalizedDeviceChannelsCollection(devicesIdsCollection, devicesIdsCollection);
        assertEquals(1, normalizedCollection.size());
        assertEquals(browsers, normalizedCollection.iterator().next());

        // Now, terminal node is deleted
        deviceChannelService.delete(terminalChannel.getId());
        commitChangesAndClearContext();
        // Node is excluded from selected and from allowed, and Deleted. So it must not participate in selection of parent
        normalizedCollection = deviceChannelService.getNormalizedDeviceChannelsCollection(devicesIdsCollection, devicesIdsCollection);
        assertEquals(1, normalizedCollection.size());
        assertEquals(browsers, normalizedCollection.iterator().next());
    }

    private void removeAllChildren(DeviceChannel node, Set<Long> devicesIdsCollection) {
        for (DeviceChannel child : node.getChildChannels()) {
            devicesIdsCollection.remove(child.getId());
            removeAllChildren(child, devicesIdsCollection);
        }
    }
}
