package com.foros.session.admin.userRole;

import com.foros.AbstractValidationsTest;
import com.foros.model.security.InternalAccessType;
import com.foros.model.security.UserRole;
import com.foros.security.AccountRole;
import com.foros.test.factory.UserRoleTestFactory;

import org.springframework.beans.factory.annotation.Autowired;

import group.Db;
import group.Validation;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Validation.class })
public class UserRoleValidationsTest extends AbstractValidationsTest {
    @Autowired
    private UserRoleTestFactory userRoleTestFactory;

    @Test
    public void testValidateCreate() {
        UserRole userRole = userRoleTestFactory.create(AccountRole.INTERNAL);

        userRole.setInternalAccessType(null);
        validate("UserRole.create", userRole);
        assertHasViolation("internalAccessType");

        violations.clear();
        userRole.setInternalAccessType(InternalAccessType.MULTIPLE_ACCOUNTS);
        validate("UserRole.create", userRole);
        assertHasViolation("accessAccountIds");

        violations.clear();
        userRole.getAccessAccountIds().add(9999L);
        validate("UserRole.create", userRole);
        assertHasViolation("accessAccountIds.id");
    }
}
