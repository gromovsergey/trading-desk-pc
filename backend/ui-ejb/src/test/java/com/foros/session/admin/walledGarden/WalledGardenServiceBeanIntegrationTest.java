package com.foros.session.admin.walledGarden;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.MarketplaceType;
import com.foros.model.account.PublisherAccount;
import com.foros.model.admin.WalledGarden;
import com.foros.model.campaign.CCGType;
import com.foros.model.security.AccountType;
import com.foros.session.security.AccountTO;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.AgencyAccountTypeTestFactory;
import com.foros.test.factory.CountryTestFactory;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.test.factory.WalledGardenTestFactory;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Db.class)
public class WalledGardenServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private WalledGardenService walledGardenService;

    @Autowired
    private PublisherAccountTestFactory publisherAccountTF;

    @Autowired
    private AgencyAccountTestFactory agencyAccountTF;

    @Autowired
    protected AgencyAccountTypeTestFactory agencyAccountTypeTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private WalledGardenTestFactory walledGardenTF;

    @Autowired
    private CountryTestFactory countryTF;

    @Test
    public void testFindAll() {
        walledGardenTF.createPersistent();

        List<WalledGarden> all = walledGardenService.findAll();
        assertEquals("JDBC query must show the same number of CreativeOptions",
                jdbcTemplate.queryForInt("SELECT COUNT(0) FROM WALLEDGARDEN"),
                all.size());
    }

    @Test
    public void testFindByAdvertiser() {
        WalledGarden wg = walledGardenTF.createPersistent();
        AgencyAccount agency = agencyAccountTF.createPersistent();

        AdvertiserAccount adv1 = advertiserAccountTF.createPersistentAdvertiserInAgency(wg.getAgency());
        AdvertiserAccount adv2 = advertiserAccountTF.createPersistentAdvertiserInAgency(agency);

        assertTrue(wg.equals(walledGardenService.findByAdvertiser(adv1.getId())));

        assertNull(walledGardenService.findByAdvertiser(adv2.getId()));
    }

    @Test
    public void testFindByPublisher() {
        WalledGarden wg = walledGardenTF.createPersistent();
        PublisherAccount publisher2 = publisherAccountTF.createPersistent();

        assertTrue(wg.equals(walledGardenService.findByPublisher(wg.getPublisher().getId())));

        assertNull((walledGardenService.findByPublisher(publisher2.getId())));
    }

    @Test
    public void testFindWithDependancesByCountryCode() {
        WalledGarden wg1 = walledGardenTF.createPersistent("US");
        WalledGarden wg2 = walledGardenTF.createPersistent("US");

        WalledGarden other = walledGardenTF.createPersistent("RU");

        List<WalledGarden> found = walledGardenService.findWithDependancesByCountryCode(wg1.getPublisher().getCountry().getCountryCode());

        assertTrue("WalledGardenService wrong result from find by country code", found.containsAll(Arrays
                .asList(wg1, wg2)));
        assertFalse("WalledGardenService redundant object found by country code", found.contains(other));
    }

    @Test
    public void testCreate() {
        WalledGarden wg = walledGardenTF.createPersistent();

        WalledGarden found = walledGardenService.findById(wg.getId());
        assertEquals("WalledGarden is not created properly", wg, found);
    }

    @Test
    public void testUpdate() {
        WalledGarden wg = walledGardenTF.createPersistent();
        wg.setAgencyMarketplaceType(MarketplaceType.ALL);
        walledGardenService.update(wg);

        WalledGarden found = walledGardenService.findById(wg.getId());
        assertEquals(found.getAgencyMarketplaceType(), MarketplaceType.ALL);
    }

    @Test
    public void testFindFreeAgencyAccounts() {
        WalledGarden wg1 = walledGardenTF.createPersistent("US");
        Long wg1AgencyId = wg1.getAgency().getId();

        WalledGarden wg2 = walledGardenTF.createPersistent("US");
        Long wg2AgencyId = wg2.getAgency().getId();

        AgencyAccount agency = agencyAccountTF.createPersistent();
        AccountType agencyAccountType = agencyAccountTypeTF.create();
        agencyAccountType.setCPAFlag(CCGType.DISPLAY, false);
        agencyAccountType.setCPMFlag(CCGType.DISPLAY, true);
        agencyAccountType.setCPCFlag(CCGType.DISPLAY, true);
        agency.setAccountType(agencyAccountType);
        agencyAccountTypeTF.persist(agencyAccountType);
        agencyAccountTF.update(agency);

        agency.setCountry(countryTF.findOrCreatePersistent("US"));
        agencyAccountTF.update(agency);
        boolean found = false;

        assertTrue(walledGardenService.validateAgencyAccountType(agencyAccountType));

        List<AccountTO> freeAgencies = walledGardenService.findFreeAgencyAccounts("US");
        for (AccountTO freeAgency : freeAgencies) {
            Long freeAgencyId = freeAgency.getId();
            assertFalse(freeAgencyId.equals(wg1AgencyId));
            assertFalse(freeAgencyId.equals(wg2AgencyId));
            if (freeAgencyId.equals(agency.getId())) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testFindFreePublisherAccounts() {
        WalledGarden wg1 = walledGardenTF.createPersistent("US");
        Long wg1PublisherId = wg1.getPublisher().getId();

        WalledGarden wg2 = walledGardenTF.createPersistent("US");
        Long wg2PublisherId = wg2.getPublisher().getId();

        PublisherAccount publisher = publisherAccountTF.createPersistent();
        publisher.setCountry(countryTF.findOrCreatePersistent("US"));
        publisherAccountTF.update(publisher);
        boolean found = false;

        List<AccountTO> freePublishers = walledGardenService.findFreePublisherAccounts("US");
        for (AccountTO freePublisher : freePublishers) {
            Long freePublisherId = freePublisher.getId();
            assertFalse(freePublisherId.equals(wg1PublisherId));
            assertFalse(freePublisherId.equals(wg2PublisherId));
            if (freePublisherId.equals(publisher.getId())) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testPublisherWalledGarden() {
        WalledGarden wg = walledGardenTF.createPersistent();
        PublisherAccount publisher = publisherAccountTF.createPersistent();

        assertTrue(walledGardenService.isPublisherWalledGarden(wg.getPublisher().getId()));
        assertFalse(walledGardenService.isPublisherWalledGarden(publisher.getId()));
    }

    @Test
    public void testAgencyWalledGarden() {
        WalledGarden wg = walledGardenTF.createPersistent();
        AgencyAccount agency = agencyAccountTF.createPersistent();

        assertTrue(walledGardenService.isAgencyWalledGarden(wg.getAgency().getId()));
        assertFalse(walledGardenService.isAgencyWalledGarden(agency.getId()));

        AdvertiserAccount adv1 = advertiserAccountTF.createPersistentAdvertiserInAgency(wg.getAgency());
        AdvertiserAccount adv2 = advertiserAccountTF.createPersistentAdvertiserInAgency(agency);

        assertTrue(walledGardenService.isAdvertiserWalledGarden(adv1.getId()));
        assertFalse(walledGardenService.isAdvertiserWalledGarden(adv2.getId()));
    }

    @Test
    public void testAgencyAccountTypeWalledGarden() {
        AccountType agencyAccountType = agencyAccountTypeTF.createPersistent();

        WalledGarden wg1 = walledGardenTF.createPersistent();
        Long wg1AccountTypeId = wg1.getAgency().getAccountType().getId();

        WalledGarden wg2 = walledGardenTF.createPersistent();
        Long wg2AccountTypeId = wg2.getAgency().getAccountType().getId();

        assertFalse(walledGardenService.isAgencyAccountTypeWalledGarden(agencyAccountType.getId()));

        assertTrue(walledGardenService.isAgencyAccountTypeWalledGarden(wg1AccountTypeId));
        assertTrue(walledGardenService.isAgencyAccountTypeWalledGarden(wg2AccountTypeId));
    }
}
