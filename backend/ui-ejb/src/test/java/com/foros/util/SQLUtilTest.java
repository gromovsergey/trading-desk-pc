package com.foros.util;

import group.Unit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class SQLUtilTest extends Assert {

    @Test
    public void testFormatInClause() {
        assertEquals("Wrong IN clause", "", SQLUtil.formatINClause("c", Collections.<Long>emptyList()));
        Collection<Long> ids = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        String res = SQLUtil.formatINClause("c", ids);
        assertEquals("Wrong IN clause", "(c in (1,2,3,4,5))", res);

        StringBuilder expectedRes = new StringBuilder();
        expectedRes.append("(c in (");

        final int limit = 2000;

        for (int i = 1; i < limit; i++) {
            expectedRes.append(i).append(",");
        }
        expectedRes.append("2000))");

        ids = new ArrayList<>(limit);
        for (int i = 1; i <= limit; i++) {
            ids.add((long)i);
        }

        assertEquals("Wrong IN clause", expectedRes.toString(), SQLUtil.formatINClause("c", ids));
    }

    @Test
    public void testQuote() {
        assertEquals("'test'", SQLUtil.quote("test"));
        assertEquals("'t''est'", SQLUtil.quote("t'est"));
        assertEquals("'''test'''", SQLUtil.quote("'test'"));
        assertEquals("'ZZZ_XXX'", SQLUtil.quote("ZZZ_XXX"));
    }
}
