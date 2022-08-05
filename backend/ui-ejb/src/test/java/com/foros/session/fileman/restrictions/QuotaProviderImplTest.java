package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.AbstractFileAwareTest;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category(Unit.class)
public class QuotaProviderImplTest extends AbstractFileAwareTest {
    @Test
    public void testGet() throws IOException {
        int maxFolderSize = 300;
        QuotaProviderImpl quotaProvider = new QuotaProviderImpl(maxFolderSize);
        Quota quota1 = quotaProvider.get(pathProvider);
        assertNotNull(quota1);
        assertEquals(maxFolderSize, quota1.getFileSizesAvailable());

        createTestFile("test1", 10);
        createTestFile("test2", 20);
        createTestFile("dir/test11", 30);
        createTestFile("dir/dir/test111", 40);
        // 4 file and 2 dir created. Total size is 100 bytes.

        createTestFile(".test~forostmp", 50);
        // temp file shouldn't be counted

        Quota quota2 = quotaProvider.get(pathProvider);
        assertNotNull(quota2);
        assertEquals(maxFolderSize - 100 , quota2.getFileSizesAvailable());
    }
}
