package com.foros.test.factory;

import com.foros.model.Country;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.MarketplaceType;
import com.foros.model.account.PublisherAccount;
import com.foros.model.admin.WalledGarden;
import com.foros.session.admin.walledGarden.WalledGardenService;

import javax.ejb.EJB;
import org.springframework.beans.factory.annotation.Autowired;

public class WalledGardenTestFactory extends TestFactory<WalledGarden> {

    @EJB
    private WalledGardenService walledGardenService;

    @Autowired
    private AgencyAccountTestFactory agencyAccountTF;
    
    @Autowired
    private PublisherAccountTestFactory publisherAccountTF;
    
    @Autowired
    private CountryTestFactory countryTF;
    
    @Override
    public WalledGarden create() {
        return create(null);
    }
    
    private WalledGarden create(InternalAccount internalAccount) {
        PublisherAccount publisher = internalAccount != null ? createPublisher(internalAccount) : createPublisher();
        AgencyAccount agency = internalAccount != null ? createAgency(internalAccount) : createAgency();

        return create(publisher, agency);
    }
    
    private WalledGarden create(PublisherAccount publisher, AgencyAccount agency) {
        Country us = countryTF.findOrCreatePersistent("US");
        publisher.setCountry(us);
        agency.setCountry(us);
        
        WalledGarden wg = new WalledGarden();
        wg.setAgency(agency);
        wg.setPublisher(publisher);
        wg.setPublisherMarketplaceType(MarketplaceType.WG);
        wg.setAgencyMarketplaceType(MarketplaceType.WG);

        return wg;
    }
    
    @Override
    public WalledGarden createPersistent() {
        return createPersistent((InternalAccount) null);
    }
    
    public WalledGarden createPersistent(InternalAccount internalAccount) {
        WalledGarden result = create(internalAccount);
        persist(result);
        return result;
    }
    
    public WalledGarden createPersistent(String countryCode) {
        Country country = countryTF.findOrCreatePersistent(countryCode);
        
        WalledGarden wg = createPersistent();
        wg.getPublisher().setCountry(country);
        wg.getAgency().setCountry(country);
        walledGardenService.update(wg);
        
        return wg;
    }
    
    public WalledGarden createPersistent(PublisherAccount publisher, AgencyAccount agency) {
        WalledGarden result = create(publisher, agency);
        persist(result);
        return result;
    }

    public void persist(WalledGarden walledGarden) {
        walledGardenService.create(walledGarden);
    }

    @Override
    public void update(WalledGarden walledGarden) {
        walledGardenService.update(walledGarden);
    }

    private PublisherAccount createPublisher() {
        return publisherAccountTF.createPersistent();
    }
    
    private PublisherAccount createPublisher(InternalAccount internalAccount) {
        return publisherAccountTF.createPersistent(internalAccount);
    }
    
    private AgencyAccount createAgency() {
        return agencyAccountTF.createPersistent();
    }
    
    private AgencyAccount createAgency(InternalAccount internalAccount) {
        return agencyAccountTF.createPersistent(internalAccount);
    }

}
