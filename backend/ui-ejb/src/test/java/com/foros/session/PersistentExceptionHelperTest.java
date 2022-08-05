package com.foros.session;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.Country;
import com.foros.model.campaign.Campaign;
import com.foros.model.channel.AudienceChannel;
import com.foros.test.factory.AudienceChannelTestFactory;
import com.foros.test.factory.CountryTestFactory;
import com.foros.test.factory.TextCampaignTestFactory;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import group.Db;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
@Category(Db.class)
public class PersistentExceptionHelperTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private CountryTestFactory countryTF;

    @Autowired
    private AudienceChannelTestFactory audienceChannelTF;

    @Autowired
    private TextCampaignTestFactory campaignTF;

    private static Level defaultLevel;

    @Test
    public void testPKViolated() throws Exception {
        Country country = countryTF.create("GB");

        try {
            getEntityManager().persist(country);
            getEntityManager().flush();
            fail("Exception should be thrown");
        } catch (Exception e) {
            checkException(e, "countryCode");
        }
    }

    @Test
    public void testChannelNameUK() throws Exception {
        AudienceChannel channel1 = audienceChannelTF.create();
        AudienceChannel channel2 = audienceChannelTF.create(channel1.getAccount());
        channel2.setName(channel1.getName());

        try {
            audienceChannelTF.persist(channel1);
            audienceChannelTF.persist(channel2);
            fail("Exception should be thrown");
        } catch (Exception e) {
            checkException(e, "name");
        }
    }

    @Test
    public void testCampaignNameUK() throws Exception {
        Campaign campaign1 = campaignTF.create();
        Campaign campaign2 = campaignTF.create(campaign1.getAccount());
        campaign2.setName(campaign1.getName());

        try {
            campaignTF.persist(campaign1);
            campaignTF.persist(campaign2);
            fail("Exception should be thrown");
        } catch (Exception e) {
            checkException(e, "name");
        }
    }

    @BeforeClass
    public static void offHibernate() {
        Logger hibernateLogger = Logger.getLogger("org.hibernate");
        defaultLevel = hibernateLogger.getLevel();
        hibernateLogger.setLevel(Level.OFF);
    }

    @AfterClass
    public static void onHibernate() {
        Logger hibernateLogger = Logger.getLogger("org.hibernate");
        hibernateLogger.setLevel(defaultLevel);
    }

    private void checkException(Exception e, String expectedPath) {
        Exception e2 = PersistenceExceptionHelper.handle(e);
        assertTrue(e2 instanceof ConstraintViolationException);
        ConstraintViolationException cve = (ConstraintViolationException) e2;
        Set<ConstraintViolation> violations = cve.getConstraintViolations();
        assertEquals(1, violations.size());
        ConstraintViolation violation = violations.iterator().next();
        assertEquals(expectedPath, violation.getPropertyPath().toString());
    }
}
