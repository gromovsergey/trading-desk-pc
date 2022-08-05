package com.foros.util;

import com.foros.model.Status;
import com.foros.util.bean.Filter;

import java.util.Arrays;
import java.util.Collections;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category( Unit.class )
public class CollectionUtilsTest {
    @Test
    public void find() throws Exception {
        assertEquals(Status.DELETED, CollectionUtils.find(Arrays.asList(Status.ACTIVE, Status.DELETED), new Filter<Status>() {
            @Override
            public boolean accept(Status element) {
                return Status.DELETED.equals(element);
            }
        }));

        assertNull(CollectionUtils.find(Arrays.asList(Status.ACTIVE, Status.DELETED), new Filter<Status>() {
            @Override
            public boolean accept(Status element) {
                return Status.INACTIVE.equals(element);
            }
        }));

        assertNull(CollectionUtils.find(Collections.<Status>emptyList(), new Filter<Status>() {
            @Override
            public boolean accept(Status element) {
                return Status.INACTIVE.equals(element);
            }
        }));
    }
}
