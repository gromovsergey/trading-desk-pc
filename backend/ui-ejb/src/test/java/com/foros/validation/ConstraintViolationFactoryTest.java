package com.foros.validation;

import com.foros.AbstractUnitTest;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationBuilder;
import com.foros.validation.constraint.violation.ConstraintViolationFactory;
import com.foros.validation.constraint.violation.Path;
import com.foros.validation.interpolator.StringUtilsMessageInterpolator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import group.Unit;
import group.Validation;
import java.util.Locale;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Unit.class, Validation.class })
public class ConstraintViolationFactoryTest extends AbstractUnitTest {
    @Test
    public void constraintViolation() {
        ConstraintViolationFactory cvf = createFactory();

        ConstraintViolationBuilder builder = new ConstraintViolationBuilder("template", Path.fromString("test"));

        ConstraintViolation violation;

        violation = cvf.create(builder.withPath("prop"));
        commonAssertions(violation);
        assertEquals("test.prop", violation.getPropertyPath().toString());

        builder.withPath();
        violation = cvf.create(builder.withValue("test"));
        commonAssertions(violation);
        assertEquals("test", violation.getPropertyPath().toString());

        builder.withPath("prop");
        violation = cvf.create(builder);
        commonAssertions(violation);
        assertEquals("test.prop", violation.getPropertyPath().toString());

        builder.withPath();
        violation = cvf.create(builder);
        commonAssertions(violation);
        assertEquals("test", violation.getPropertyPath().toString());
    }

    @Test
    public void illegalArguments() {
        ConstraintViolationFactory cvf = createFactory();
        ConstraintViolationBuilder builder = new ConstraintViolationBuilder("Test illegal characters {100$ 10%}", Path.empty());
        builder.withParameters("test {100$} test {$200}");
        builder.withPath("prop");
        ConstraintViolation violation = cvf.create(builder);
        commonAssertions(violation);
        assertEquals("prop", violation.getPropertyPath().toString());
    }

    @Test
    public void illegalArgumentsEscaping() {
        ConstraintViolationFactory cvf = createFactory();
        ConstraintViolationBuilder builder = new ConstraintViolationBuilder("Test Keyword a$\"b is invalid template", Path.empty());
        builder.withPath("prop");
        ConstraintViolation violation = cvf.create(builder);
        commonAssertions(violation);
        assertEquals("Test Keyword a$\"b is invalid template", violation.getMessage());
    }

    private void commonAssertions(ConstraintViolation violation) {
        assertNotNull(violation);
        assertNotNull(violation.getMessage());
        assertNotNull(violation.getPropertyPath());
    }

    private ConstraintViolationFactory createFactory() {
        return new ConstraintViolationFactory(NullErrorResolver.INSTANCE,
                new StringUtilsMessageInterpolator(Locale.getDefault()));
    }
}
