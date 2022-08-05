package com.foros.model.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import group.Unit;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.foros.security.AccountRole;

@Category( Unit.class )
public class PaymentMethodTest {
    @Ignore
    @Test
    public void getListForRole() {
        List<PaymentMethod> internalList = PaymentMethod.getListForRole(AccountRole.INTERNAL);
        assertEquals(internalList, new ArrayList());

        List<PaymentMethod> advertiserList = PaymentMethod.getListForRole(AccountRole.ADVERTISER);
        assertEquals(advertiserList, new ArrayList());

        List<PaymentMethod> ispList = PaymentMethod.getListForRole(AccountRole.ISP);
        assertTrue(ispList.size() == 2);
        assertTrue(ispList.contains(PaymentMethod.BACS));
        assertTrue(ispList.contains(PaymentMethod.Swift));

        List<PaymentMethod> publisherList = PaymentMethod.getListForRole(AccountRole.PUBLISHER);
        assertTrue(publisherList.size() == 2);
        assertTrue(publisherList.contains(PaymentMethod.BACS));
        assertTrue(publisherList.contains(PaymentMethod.Swift));
    }
}
