package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.PaymentMethodConverter;
import com.foros.model.security.PaymentMethod;
import com.foros.security.AccountRole;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import java.util.Collection;

public class PaymentMethodsXmlAction extends AbstractOptionsAction<PaymentMethod> {

    private String roleId;

    public PaymentMethodsXmlAction() {
        super(new PaymentMethodConverter());
    }

    @RequiredStringValidator(key = "errors.required", message = "roleId")
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    protected Collection<? extends PaymentMethod> getOptions() throws ProcessException {
        return PaymentMethod.getListForRole(AccountRole.byName(getRoleId()));
    }

}
