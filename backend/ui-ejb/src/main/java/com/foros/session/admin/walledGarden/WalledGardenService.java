package com.foros.session.admin.walledGarden;

import com.foros.model.admin.WalledGarden;
import com.foros.model.security.AccountType;
import com.foros.service.ByIdLocatorService;
import com.foros.session.security.AccountTO;

import java.util.List;
import javax.ejb.Local;

@Local
public interface WalledGardenService extends ByIdLocatorService<WalledGarden> {
    void create(WalledGarden walledGarden) throws IllegalWalledGardenAgencyTypeException;
    
    WalledGarden update(WalledGarden walledGarden);
    
    List<WalledGarden> findAll();
    WalledGarden findByAdvertiser(Long advertiserAccountId);
    WalledGarden findByPublisher(Long publisherAccountId);
    List<WalledGarden> findAllWithDependencies();
    List<WalledGarden> findWithDependancesByCountryCode(String countryCode);

    List<AccountTO> findFreeAgencyAccounts(String countryCode);
    List<AccountTO> findFreePublisherAccounts(String countryCode);
    
    boolean isPublisherWalledGarden(Long publisherAccountId);
    boolean isAdvertiserWalledGarden(Long advertiserAccountId);
    boolean isAgencyWalledGarden(Long agencyAccountId);
    boolean isAgencyAccountTypeWalledGarden(Long agencyAccountTypeId);
    
    boolean validateAgencyAccountType(AccountType agencyAccountType);
}
