package com.foros.session.channel;

import com.foros.annotations.AllowedQAStatuses;
import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.DisplayStatus;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.campaign.RateType;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.session.channel.exceptions.ChannelNotFoundExpressionException;
import com.foros.session.channel.exceptions.ExpressionConversionException;
import com.foros.session.channel.service.ExpressionChannelService;
import com.foros.session.channel.service.ExpressionService;
import com.foros.session.status.DisplayStatusService;
import com.foros.test.factory.AudienceChannelTestFactory;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.CategoryChannelTestFactory;
import com.foros.test.factory.ExpressionChannelTestFactory;

import group.Db;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class ExpressionChannelServiceBeanIntegrationTest extends AbstractChannelServiceBeanIntegrationTest<ExpressionChannel> {
    @Autowired
    private ExpressionChannelService expressionChannelService;

    @Autowired
    private ExpressionService expressionService;

    @Autowired
    private ExpressionChannelTestFactory expressionChannelTF;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private AudienceChannelTestFactory audienceChannelTF;

    @Autowired
    private CategoryChannelTestFactory categoryChannelTF;

    @Autowired
    private DisplayStatusService displayStatusServiceBean;

    @Test
    public void testDisplayStatus() throws Exception {
        ExpressionChannel used = expressionChannelTF.createPersistent();
        used.setStatus(Status.ACTIVE);
        used.setQaStatus(ApproveStatus.APPROVED);
        Country country = used.getAccount().getCountry();
        country.setLowChannelThreshold(0L);
        country.setHighChannelThreshold(0L);
        entityManager.flush();

        displayStatusServiceBean.update(used);
        entityManager.flush();
        entityManager.refresh(used);

        assertEquals(ExpressionChannel.LIVE, used.getDisplayStatus());

        ExpressionChannel channel = expressionChannelTF.create();
        channel.setExpression(used.getId().toString());
        expressionChannelService.create(channel);
        entityManager.flush();
        assertEquals(1, channel.getUsedChannels().size());
        entityManager.flush();

        // normal channel
        testDisplayStatus(channel, ExpressionChannel.LIVE,
                array(Status.ACTIVE),
                array(ApproveStatus.APPROVED)
        );
        ApproveStatus[] allowedQaStatuses = ExpressionChannel.class.getAnnotation(AllowedQAStatuses.class).values();
        testDisplayStatus(channel, ExpressionChannel.INACTIVE,
                array(Status.INACTIVE),
                array(allowedQaStatuses)
        );
        testDisplayStatus(channel, ExpressionChannel.DELETED,
                array(Status.DELETED),
                array(allowedQaStatuses)
        );

        channel.setChannelRate(expressionChannelTF.createChannelRate(channel, RateType.CPC, new BigDecimal(1)));
        expressionChannelService.submitToCmp(channel);

        testDisplayStatus(channel, ExpressionChannel.LIVE_PENDING_INACTIVATION,
                array(Status.PENDING_INACTIVATION),
                array(ApproveStatus.APPROVED)
        );
        // bad expression
        used.setStatus(Status.DELETED);
        entityManager.flush();
        displayStatusServiceBean.update(used);
        entityManager.flush();
        entityManager.refresh(used);
        assertEquals(ExpressionChannel.DELETED, used.getDisplayStatus());
        testDisplayStatus(channel, ExpressionChannel.NOT_LIVE_CHANNELS_NEED_ATT,
                array(Status.ACTIVE),
                array(ApproveStatus.APPROVED)
        );
        // restore
        used.setStatus(Status.ACTIVE);
        entityManager.flush();
        displayStatusServiceBean.update(used);
        entityManager.flush();
        entityManager.refresh(used);

        // TODO uncomment after REPL-145
        /*// unique users
        country.setLowChannelThreshold(10000L);
        country.setHighChannelThreshold(20000L);
        entityManager.flush();

        testDisplayStatus(channel, ExpressionChannel.NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS,
                array(Status.ACTIVE),
                array(ApproveStatus.APPROVED)
        );
        */
    }

    @Test
    public void testUpdateWithDisplayStatus() throws Exception {
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        account.setFlags(Account.TEST_FLAG);

        ExpressionChannel channel = expressionChannelTF.createPersistent(account);

        BehavioralChannel behavioralChannel = behavioralChannelTF.createPersistent(account);

        clearContext();
        commitChanges();

        behavioralChannel.getPageKeywords().setPositive("pageKeyword" + System.currentTimeMillis());
        behavioralChannel.getSearchKeywords().setPositive("searchKeyword" + System.currentTimeMillis());
        behavioralChannel.getUrls().setPositive("url" + System.currentTimeMillis());
        behavioralChannel.setStatus(Status.INACTIVE);
        behavioralChannelTF.update(behavioralChannel);

        clearContext();
        commitChanges();

        behavioralChannel = behavioralChannelTF.refresh(behavioralChannel);
        assertEquals(behavioralChannel.getDisplayStatusId(), Channel.INACTIVE.getId());

        String expressionString = "[" + account.getName() + "|" + behavioralChannel.getName() + "]";

        String cdml = expressionService.convertFromHumanReadable(expressionString, channel.getCountry().getCountryCode());

        channel.setExpression(cdml);
        entityManager.clear();

        expressionChannelService.update(channel);
        entityManager.flush();

        channel = expressionChannelTF.refresh(channel);
        assertEquals(channel.getDisplayStatusId(), Channel.NOT_LIVE_CHANNELS_NEED_ATT.getId());
        assertEquals(1, channel.getUsedChannels().size());
        assertTrue(channel.getUsedChannels().contains(behavioralChannel));
    }

    @Test
    public void testUpdateWithStatusChangeDate() {
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        ExpressionChannel channel = expressionChannelTF.createPersistent(account);
        entityManager.clear();
        ExpressionChannel updated = new ExpressionChannel();
        updated.setStatus(Status.DELETED);
        updated.setId(channel.getId());
        assertNull(updated.getStatusChangeDate());
        expressionChannelService.update(updated);
        assertTrue(updated.isChanged("statusChangeDate"));
        assertNotNull(updated.getStatusChangeDate());
    }

    @Test
    public void testConvertToHumanReadable() throws ExpressionConversionException {
        Account account = internalAccountTF.createPersistent();

        // create channel
        ExpressionChannel channel1 = expressionChannelTF.createPersistent(account);
        // delete it
        expressionChannelService.delete(channel1.getId());

        ExpressionChannel channel2 = expressionChannelTF.createPersistent(account);

        // create active channel with the deleted channel's name
        ExpressionChannel channel3 = expressionChannelTF.create(account);
        channel3.setName(channel1.getName());
        expressionChannelTF.persist(channel3);

        commitChanges();
        clearContext();

        String name1 = channel3.getAccount().getName() + "|" + channel3.getName();
        String name2 = channel2.getAccount().getName() + "|" + channel2.getName();

        String cdml = "[" + name1 + "] OR [" + name2 + "]";
        String convertedCdml = expressionService.convertFromHumanReadable(cdml, channel1.getCountry().getCountryCode());
        String expectedCdml = channel3.getId() + "|" + channel2.getId();
        assertEquals(expectedCdml, convertedCdml);
    }

    @Test(expected = ChannelNotFoundExpressionException.class)
    public void testConvertToHumanReadableWithCategoryChannelInExpression() throws ExpressionConversionException {
        Account account = internalAccountTF.createPersistent();
        CategoryChannel categoryChannel = categoryChannelTF.createPersistent((InternalAccount) account);
        String name1 = account.getName() + "|" + categoryChannel.getName();
        String cdml = "[" + name1 + "]";

        expressionService.convertFromHumanReadable(cdml, account.getCountry().getCountryCode());
    }

    @Test
    public void testEditSuperseded() throws Exception {
        CmpAccount account = cmpAccountTF.createPersistent();
        ExpressionChannel channel = expressionChannelTF.createPersistent(account);

        ExpressionChannel supersededBy1 = expressionChannelTF.createPersistent(account);
        ExpressionChannel supersededBy2 = expressionChannelTF.createPersistent(account);

        // init superseded
        getEntityManager().flush();
        getEntityManager().clear();

        channel.setSupersededByChannel(supersededBy1);
        expressionChannelService.update(channel);
        channel = getEntityManager().find(ExpressionChannel.class, channel.getId());
        assertEquals(supersededBy1, channel.getSupersededByChannel());

        // change superseded
        getEntityManager().clear();

        channel.setSupersededByChannel(supersededBy2);
        expressionChannelService.update(channel);
        channel = getEntityManager().find(ExpressionChannel.class, channel.getId());
        assertEquals(supersededBy2, channel.getSupersededByChannel());

        // clear superseded
        getEntityManager().clear();

        channel.setSupersededByChannel(null);
        expressionChannelService.update(channel);
        channel = getEntityManager().find(ExpressionChannel.class, channel.getId());
        assertNull(channel.getSupersededByChannel());
    }

    @Test
    public void testCreateAudienceChannelExpression() {
        Account account = internalAccountTF.createPersistent();
        AudienceChannel audienceChannel = audienceChannelTF.createPersistent(account);

        ExpressionChannel expressionChannel = expressionChannelTF.create(account);
        expressionChannel.setExpression(audienceChannel.getId().toString());
        expressionChannelTF.persist(expressionChannel);

        String expectedCdml = "[" + account.getName() + "|" + audienceChannel.getName() + "]";
        String actualCdml = null;
        try {
            actualCdml = expressionService.convertToHumanReadable(expressionChannel.getExpression());
        } catch (ExpressionConversionException ece) {

        }

        assertEquals(expectedCdml, actualCdml);
    }

    private <T> T[] array(T... array) {
        return array;
    }

    private void testDisplayStatus(ExpressionChannel channel, DisplayStatus ds, Status[] statuses, ApproveStatus[] approveStatuses) {
        for (Status status : statuses) {
            for (ApproveStatus approveStatus : approveStatuses) {
                channel.setStatus(status);
                channel.setQaStatus(approveStatus);
                displayStatusServiceBean.update(channel);
                entityManager.flush();
                entityManager.refresh(channel);
                assertEquals("Expected: " + ds + " for Status: " + status + " QaStatus: " + approveStatus, ds, channel.getDisplayStatus());
            }
        }
    }
}
