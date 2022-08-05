package com.foros.session.admin.categoryChannel;

import com.foros.test.factory.CategoryChannelTestFactory;
import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.model.channel.CategoryChannel;
import com.foros.session.account.AccountService;
import com.foros.session.admin.categoryChannel.CategoryChannelService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import group.Db;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Db.class)
public class CategoryChannelServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    public AccountService accountService;

    @Autowired
    public CategoryChannelService channelService;

    @Autowired
    public CategoryChannelTestFactory categoryChannelTF;

    @Test
    public void testCreateUpdateChannel() throws InterruptedException {
        CategoryChannel channel = categoryChannelTF.createPersistent();
        assertNotNull(channel.getId());
        assertEquals(channel.getStatus(), Status.ACTIVE);
        assertEquals(channel.getQaStatus(), ApproveStatus.APPROVED);

        CategoryChannel newChannel = new CategoryChannel();
        newChannel.setId(channel.getId());
        String newName = categoryChannelTF.getTestEntityRandomName();
        newChannel.setName(newName);
        newChannel.setAccount(channel.getAccount());
        categoryChannelTF.update(newChannel);

        newChannel = channelService.find(channel.getId());
        assertEquals("Invalid updated channel name", newName, newChannel.getName());
    }

    @Test
    public void testSearch() {
        CategoryChannel channel = categoryChannelTF.createPersistent();
        assertNotNull(channel.getId());
        channelService.delete(channel.getId());

        CategoryChannel channel2 = categoryChannelTF.createPersistent();
        assertNotNull(channel2.getId());

        setDeletedObjectsVisible(true);
        List<CategoryChannelTO> result = channelService.getChannelList(null);
        assertTrue(checkStatus(result, Status.DELETED));
        setDeletedObjectsVisible(false);
        result = channelService.getChannelList(null);
        assertTrue(!checkStatus(result, Status.DELETED));
    }

    @Test
    public void testCreateDeleteChannel() throws InterruptedException {
        CategoryChannel channel = categoryChannelTF.createPersistent();
        assertNotNull(channel.getId());

        channelService.delete(channel.getId());

        CategoryChannel newChannel = channelService.find(channel.getId());
        assertEquals("Status is invalid", Status.DELETED, newChannel.getStatus());
        assertEquals("QA Status is invalid", ApproveStatus.APPROVED, newChannel.getQaStatus());
    }

    private boolean checkStatus(List<CategoryChannelTO> list, Status status) {
        for (CategoryChannelTO item : list) {
            if (item.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }
}
