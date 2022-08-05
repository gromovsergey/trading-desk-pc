package com.foros.validation.constraint.convertion;

import com.foros.validation.NullErrorResolver;
import com.foros.validation.code.ForosError;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationBuilder;
import com.foros.validation.constraint.violation.ConstraintViolationFactory;
import com.foros.validation.constraint.violation.Path;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.validator.NullInterpolator;

import static junit.framework.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.junit.Assert;
import org.junit.Test;

public class ConstraintViolationConverterTest {

    private static final ConstraintViolationFactory FACTORY =
            new ConstraintViolationFactory(NullErrorResolver.INSTANCE, NullInterpolator.INSTANCE);

    @Test
    public void converter() {
        final List<String> errors = new ArrayList<String>();

        SimpleConstraintViolationConverter converter = new SimpleConstraintViolationConverter(
                new ErrorMessageList() {
                    @Override
                    public void add(String path, String message) {
                        errors.add(message);
                    }
                });

        ConstraintViolationRule rule = new ConstraintViolationRule(
                "channels[(#index)].(#property)",
                null,
                "null",
                "'Line ' + groups[0] + ' - ' + groups[1] + ' : ' + violation.message"
        );

        ConstraintViolation violation = createViolation("channels[2].name", "message");
        converter.applyRule(rule, violation);

        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("Line 2 - name : message", errors.iterator().next());
    }

    @Test
    public void ognl() throws OgnlException {
        Object expression = Ognl.parseExpression("substring(2, 5)");
        Object value = Ognl.getValue(expression, new OgnlContext(), "abcdefghj");
        assertEquals("cde", value);
    }


    private ConstraintViolation createViolation(String path, String message) {
        return createViolation(path, message, null);
    }

    private ConstraintViolation createViolation(String path, String message, ForosError errorCode) {
        return FACTORY.create(new ConstraintViolationBuilder(message, Path.fromString(path)).withError(errorCode));
    }

}
