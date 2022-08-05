package com.foros.model.security;

import com.foros.model.account.Account;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@Category(Unit.class)
public class NotManagedEntityTest {
    @Test
    public void util() {
        // managed
        assertTrue(NotManagedEntity.Util.isManaged(Foo.class));
        assertTrue(NotManagedEntity.Util.isManaged(Foo.class, Foo2.class));

        // not managed
        assertFalse(NotManagedEntity.Util.isManaged(Bar.class));
        assertFalse(NotManagedEntity.Util.isManaged(Bar.class, Bar2.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void error1() {
        NotManagedEntity.Util.isManaged(Foo.class, Bar.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void error2() {
        NotManagedEntity.Util.isManaged(Bar.class, Foo.class);
    }

    public static class Foo implements OwnedEntity {
        @Override
        public Account getAccount() {
            return null;
        }
    }

    public static class Foo2 implements OwnedEntity {
        @Override
        public Account getAccount() {
            return null;
        }
    }

    public static class Bar implements OwnedEntity, NotManagedEntity {
        @Override
        public Account getAccount() {
            return null;
        }
    }

    public static class Bar2 implements OwnedEntity, NotManagedEntity {
        @Override
        public Account getAccount() {
            return null;
        }
    }
}
