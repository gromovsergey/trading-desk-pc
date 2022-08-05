package com.foros.session.admin.walledGarden;

import com.foros.session.BusinessException;
import com.foros.util.StringUtil;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
@Deprecated // Use com.foros.validation.constraint.violation.ConstraintViolationException
public class IllegalWalledGardenAgencyTypeException extends BusinessException {

    public IllegalWalledGardenAgencyTypeException() {
        super(StringUtil.getLocalizedString("WalledGarden.validation.agency.accounttype"));
    }
}
