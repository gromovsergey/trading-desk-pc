package com.foros.validation;

import com.foros.AbstractUnitTest;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationBuilder;
import com.foros.validation.constraint.violation.ConstraintViolationFactory;
import com.foros.validation.constraint.violation.Path;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationMatcher;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationQuery;
import com.foros.validation.validator.NullInterpolator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import group.Unit;
import group.Validation;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Unit.class, Validation.class })
public class ConstraintViolationQueryTest extends AbstractUnitTest {
    private static final ConstraintViolationFactory FACTORY = new ConstraintViolationFactory(NullErrorResolver.INSTANCE, NullInterpolator.INSTANCE);

    @Test
    public void index() {
        ConstraintViolationQuery query = ConstraintViolationQuery.compile("channel[(#index)].property");

        assertMatched(query.matcher(violation("channel[2].property")), "2");

        assertNotMatched(query.matcher(violation("channel[2].other")));
        assertNotMatched(query.matcher(violation("channel[2]")));
        assertNotMatched(query.matcher(violation("group[2].property")));
    }

    @Test
    public void indexNoGroup() {
        ConstraintViolationQuery query = ConstraintViolationQuery.compile("channel[#index].property");

        assertMatched(query.matcher(violation("channel[2].property")));

        assertNotMatched(query.matcher(violation("channel[2].other")));
    }

    @Test
    public void key() {
        ConstraintViolationQuery query = ConstraintViolationQuery.compile("channel[(#key)].property");

        assertMatched(query.matcher(violation("channel[2].property")), "2");
        assertMatched(query.matcher(violation("channel[key].property")), "key");

        assertNotMatched(query.matcher(violation("channel[].property")));
        assertNotMatched(query.matcher(violation("channel[key].other")));
        assertNotMatched(query.matcher(violation("channel[key]")));
        assertNotMatched(query.matcher(violation("group[key].property")));

        query = ConstraintViolationQuery.compile("(#path).channel[(#key)].(#path)");
        assertMatched(query.matcher(violation("root.channel[key].property.property1")), "root", "key", "property.property1");
    }

    @Test
    public void property() {
        ConstraintViolationQuery query;
        query = ConstraintViolationQuery.compile("channel.(#property)");

        assertMatched(query.matcher(violation("channel.name")), "name");

        assertNotMatched(query.matcher(violation("channel.account.id")));
        assertNotMatched(query.matcher(violation("channel[2].property")));
        assertNotMatched(query.matcher(violation("channel")));
        assertNotMatched(query.matcher(violation("group.property")));

        query = ConstraintViolationQuery.compile("channel.(#property).id");

        assertMatched(query.matcher(violation("channel.account.id")), "account");
    }

    @Test
    public void path() {
        ConstraintViolationQuery query = ConstraintViolationQuery.compile("channel.(#path)");

        assertMatched(query.matcher(violation("channel.name")), "name");
        assertMatched(query.matcher(violation("channel.account.id")), "account.id");

        assertNotMatched(query.matcher(violation("channel[2].property")));
        assertNotMatched(query.matcher(violation("channel")));
        assertNotMatched(query.matcher(violation("group.property")));

        query = ConstraintViolationQuery.compile("channel.(#path).id");

        assertMatched(query.matcher(violation("channel.account.id")), "account");
        assertMatched(query.matcher(violation("channel.account.accountType.id")), "account.accountType");
    }

    private void assertNotMatched(ConstraintViolationMatcher matcher) {
        assertFalse(matcher.matches());
    }

    private void assertMatched(ConstraintViolationMatcher matcher, String... groups) {
        assertTrue(matcher.matches());

        String[] matchedGroups = matcher.groups();
        assertEquals(groups.length, matchedGroups.length);
        for (int i = 0; i < groups.length; i++) {
            assertEquals(groups[i], matchedGroups[i]);
        }

        assertNotNull(matcher.getConstraintViolation());
    }

    private ConstraintViolation violation(String s) {
        return FACTORY.create(new ConstraintViolationBuilder("test", Path.fromString(s)));
    }
}
