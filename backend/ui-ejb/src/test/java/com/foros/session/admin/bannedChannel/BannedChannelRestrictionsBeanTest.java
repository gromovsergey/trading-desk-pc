package com.foros.session.admin.bannedChannel;

import com.foros.AbstractRestrictionsBeanTest;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Restriction;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Restriction.class })
public class BannedChannelRestrictionsBeanTest extends AbstractRestrictionsBeanTest {
    @Autowired
    private BannedChannelRestrictions bannedChannelRestrictions;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("bannedChannel", "view") {
            @Override
            public boolean call() {
                return bannedChannelRestrictions.canView();
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }
}
