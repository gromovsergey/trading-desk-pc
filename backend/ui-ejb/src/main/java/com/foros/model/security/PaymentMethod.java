package com.foros.model.security;

import java.util.ArrayList;
import java.util.List;

import com.foros.security.AccountRole;

/**
 * @author oleg_pervyshov
 */
public enum PaymentMethod {
    BACS("BACS"),
    Swift("Swift");

    private final String name;

    private PaymentMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<PaymentMethod> getListForRole(AccountRole accountRole) {
        List<PaymentMethod> result = new ArrayList<PaymentMethod>();

        if (accountRole == AccountRole.ISP || accountRole == AccountRole.PUBLISHER) {
            result.add(PaymentMethod.BACS);
            result.add(PaymentMethod.Swift);
        }

        return result;
    }
}
