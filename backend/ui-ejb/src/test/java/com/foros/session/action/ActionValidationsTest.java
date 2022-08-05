package com.foros.session.action;

import com.foros.AbstractValidationsTest;
import com.foros.model.action.Action;
import com.foros.test.factory.ActionTestFactory;

import group.Db;
import group.Validation;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class ActionValidationsTest extends AbstractValidationsTest {

    @Autowired
    private ActionTestFactory actionTestFactory;

    @Test
    public void testValue() {
        Action action = actionTestFactory.create();

        validate("Action.create", action);
        assertViolationsCount(0);

        action.setValue(null);
        validate("Action.create", action);
        assertHasViolation("value");

        action.setValue(new BigDecimal(-1));
        validate("Action.create", action);
        assertHasViolation("value");

    }

}
