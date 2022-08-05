package com.foros.session.channel;

import com.foros.model.Status;
import com.foros.model.account.InternalAccount;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.session.channel.service.AudienceChannelService;
import com.foros.test.factory.AudienceChannelTestFactory;

import group.Db;

import java.sql.Timestamp;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;


@Category(Db.class)
public class AudienceChannelServiceBeanIntegrationTest extends AbstractChannelServiceBeanIntegrationTest<AudienceChannel> {

    @Autowired
    private AudienceChannelService audienceChannelService;

    @Autowired
    private AudienceChannelTestFactory audienceChannelTF;

    @Test
    public void testAudienceChannelUpdate() throws Exception {
        InternalAccount account = internalAccountTF.createPersistent();

        AudienceChannel channel = audienceChannelTF.createPersistent(account);
        audienceChannelTF.refresh(channel);
        assertNotNull(channel.getId());

        Timestamp version = channel.getVersion();
        AudienceChannel updatedChannel = audienceChannelTF.create(account);
        updatedChannel.setId(channel.getId());
        Long id = audienceChannelService.updateBulk(updatedChannel);
        entityManager.flush();
        assertNotNull(id);
        channel = audienceChannelTF.refresh(channel);
        assertFalse(version.equals(channel.getVersion()));

        version = channel.getVersion();

        updatedChannel = audienceChannelTF.create(account);
        updatedChannel.setId(channel.getId());
        updatedChannel.setName(audienceChannelTF.getTestEntityRandomName());

        audienceChannelService.updateBulk(updatedChannel);
        entityManager.flush();

        channel = audienceChannelTF.refresh(channel);
        assertFalse(version.equals(channel.getVersion()));
    }

    @Test
    public void testMakePublic() throws Exception {
        InternalAccount account = internalAccountTF.createPersistent();
        AudienceChannel channel = audienceChannelTF.createPersistent(account);
        audienceChannelService.makePublic(channel.getId(), channel.getVersion());

        AudienceChannel updated = audienceChannelService.view(channel.getId());
        assertEquals(ChannelVisibility.PUB, updated.getVisibility());
    }

    @Test
    public void testUpdateStatus() {
        InternalAccount account = internalAccountTF.createPersistent();
        AudienceChannel channel = audienceChannelTF.createPersistent(account);
        getEntityManager().flush();
        getEntityManager().clear();

        channel = audienceChannelTF.refresh(channel);

        // Inactivate
        AudienceChannel toUpdate = new AudienceChannel();
        toUpdate.setId(channel.getId());
        toUpdate.setStatus(Status.INACTIVE);

        audienceChannelService.updateBulk(toUpdate);
        getEntityManager().flush();
        getEntityManager().clear();

        channel = audienceChannelTF.refresh(channel);
        assertEquals(Status.INACTIVE, channel.getStatus());

        // Activate
        toUpdate = new AudienceChannel();
        toUpdate.setId(channel.getId());
        toUpdate.setStatus(Status.ACTIVE);

        audienceChannelService.updateBulk(toUpdate);
        getEntityManager().flush();
        getEntityManager().clear();

        channel = audienceChannelTF.refresh(channel);
        assertEquals(Status.ACTIVE, channel.getStatus());
    }
}
