package com.foros.session.admin.currencyExchange;

import com.foros.AbstractRestrictionsBeanTest;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class CurrencyExchangeRestrictionsBeanTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private CurrencyExchangeRestrictions currencyExchangeRestrictions;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("currencyExchange", "view") {
            @Override
            public boolean call() {
                return currencyExchangeRestrictions.canView();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        Callable callCanUpdate = new Callable("currencyExchange", "edit") {
            @Override
            public boolean call() {
                return currencyExchangeRestrictions.canUpdate();
            }
        };

        // normal account
        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);
    }
}
