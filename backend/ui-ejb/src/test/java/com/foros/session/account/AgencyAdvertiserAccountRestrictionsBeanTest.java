package com.foros.session.account;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class AgencyAdvertiserAccountRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AgencyAdvertiserAccountRestrictions agencyAdvertiserAccountRestrictions;

    private AdvertiserAccount agencyAdvertiserAccount;
    private AgencyAccount agencyAccount;

    @Override @org.junit.Before public void setUp() throws Exception {
        super.setUp();
        agencyAccount = agencyAccountTF.createPersistent();
        agencyAdvertiserAccount = advertiserAccountTF.createPersistentAdvertiserInAgency(agencyAccount);
    }

    @Test
    public void testCreate() throws Exception {
        Callable call = new Callable() {
            @Override
            public boolean call() {
                return agencyAdvertiserAccountRestrictions.canCreate(agencyAccount);
            }
        };
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, false);

        doCheck(call);
    }

    @Test
    public void testView() throws Exception {
        Callable call = new Callable() {
            @Override
            public boolean call() {
                return agencyAdvertiserAccountRestrictions.canView(agencyAdvertiserAccount);
            }
        };
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, false);
        doCheck(call);
    }

    @Test
    public void testUpdate() throws Exception {
        Callable call = new Callable() {
            @Override
            public boolean call() {
                return agencyAdvertiserAccountRestrictions.canUpdate(agencyAdvertiserAccount);
            }
        };
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, false);
        doCheck(call);
    }
}
