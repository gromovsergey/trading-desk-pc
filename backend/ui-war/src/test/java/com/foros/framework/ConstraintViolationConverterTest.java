package com.foros.framework;

import com.foros.action.BaseActionSupport;
import com.foros.validation.NullErrorResolver;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.code.ForosError;
import com.foros.validation.constraint.convertion.StrutsConstraintViolationConverter;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationBuilder;
import com.foros.validation.constraint.violation.ConstraintViolationFactory;
import com.foros.validation.constraint.violation.Path;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;
import com.foros.validation.validator.NullInterpolator;

import static org.junit.Assert.assertEquals;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.XWorkJUnit4TestCase;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.ognl.OgnlValueStackFactory;
import com.opensymphony.xwork2.util.ValueStack;
import group.Struts2;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Struts2.class)
public class ConstraintViolationConverterTest extends XWorkJUnit4TestCase {
    private static final ConstraintViolationFactory FACTORY = new ConstraintViolationFactory(NullErrorResolver.INSTANCE, NullInterpolator.INSTANCE);

    private ValidationAware action;
    private StrutsConstraintViolationConverter converter;

    @Before
    public final void init() {
        action = new BaseActionSupport();
        ValueStack valueStack = createStack();
        converter = new StrutsConstraintViolationConverter(action, valueStack);

    }

    @Test
    public void converter() {

        ConstraintViolationRule rule = new ConstraintViolationRulesBuilder()
                .match("channels[(#index)].(#property)").apply("'Line ' + groups[0] + ' - ' + groups[1] + ' : ' + violation.message")
                .rule();

        ConstraintViolation violation = createViolation("channels[2].name", "message", null);
        converter.applyRule(rule, violation);

        assertEquals(1, action.getActionErrors().size());
        assertEquals("Line 2 - name : message", action.getActionErrors().iterator().next());
    }

    @Test
    public void testCodes() {
        List<ConstraintViolationRule> rules = new ConstraintViolationRulesBuilder()
                .match("options[(#index)]", BusinessErrors.CREATIVE_INVALID_OPTION_ERROR).apply("violation.message")
                .match("options[(#index)]").apply("'optionValues[' + groups[0] + ']'", "violation.message")
                .rules();

        List<ConstraintViolation> violations = Arrays.asList(
                createViolation("options[2]", "Required", BusinessErrors.FIELD_IS_REQUIRED),
                createViolation("options[2]", "Invalid Option", BusinessErrors.CREATIVE_INVALID_OPTION_ERROR)
        );


        converter.applyRules(rules, violations);

        assertEquals(1, action.getActionErrors().size());
        assertEquals(1, action.getFieldErrors().get("optionValues[2]").size());
    }

    private ValueStack createStack() {
        try {
            OgnlValueStackFactory factory = new OgnlValueStackFactory();
            factory.setContainer(container);
            factory.setXWorkConverter(container.getInstance(XWorkConverter.class));
            return factory.createValueStack();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private ConstraintViolation createViolation(String path, String message, ForosError error) {
        return FACTORY.create(new ConstraintViolationBuilder(message, Path.fromString(path)).withError(error));
    }
}
