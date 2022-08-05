package com.foros.session.account;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.security.AccountRole;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class AccountRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private AccountRestrictions accountRestrictions;

    @Test
    public void testCreate() throws Exception {
        Callable call = new Callable("internal_account", "create") {
            @Override
            public boolean call() {
                return accountRestrictions.canCreate(AccountRole.INTERNAL);
            }
        };
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, false);

        doCheck(call);

        call = new Callable("advertising_account", "create") {
            @Override
            public boolean call() {
                return accountRestrictions.canCreate(AccountRole.ADVERTISER);
            }
        };
        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, false);
        doCheck(call);

        call = new Callable("publisher_account", "create") {
            @Override
            public boolean call() {
                return accountRestrictions.canCreate(AccountRole.PUBLISHER);
            }
        };
        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, false);
        doCheck(call);

        call = new Callable("isp_account", "create") {
            @Override
            public boolean call() {
                return accountRestrictions.canCreate(AccountRole.ISP);
            }
        };
        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, false);
        doCheck(call);

        call = new Callable("cmp_account", "create") {
            @Override
            public boolean call() {
                return accountRestrictions.canCreate(AccountRole.CMP);
            }
        };
        resetExpectationsToDefault();
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(agencyAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        expectResult(ispAllAccess1, false);
        doCheck(call);
    }

    @Test
    public void testViewInternal() throws Exception {
        Callable call = new Callable("internal_account", "view") {
            @Override
            public boolean call() {
                return accountRestrictions.canView(AccountRole.INTERNAL);
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

