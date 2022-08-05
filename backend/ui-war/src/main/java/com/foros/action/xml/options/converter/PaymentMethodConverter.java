package com.foros.action.xml.options.converter;

import com.foros.model.security.PaymentMethod;

/**
 * Author: Boris Vanin
 * Date: 27.11.2008
 * Time: 12:17:31
 * Version: 1.0
 */
public class PaymentMethodConverter extends AbstractConverter<PaymentMethod> {

    public PaymentMethodConverter() {
        super(false);
    }

    protected String getName(PaymentMethod value) {
        return value.getName();
    }

    protected String getValue(PaymentMethod value) {
        return value.getName();
    }
    
}