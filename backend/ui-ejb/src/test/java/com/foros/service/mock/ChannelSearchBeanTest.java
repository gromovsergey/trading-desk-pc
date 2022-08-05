package com.foros.service.mock;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.phorm.oix.service.AdServer.ChannelSearchSvcs.ChannelSearchPackage.ImplementationException;
import com.phorm.oix.service.AdServer.ChannelSearchSvcs.ChannelSearchResult;

import group.Db;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class ChannelSearchBeanTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private ChannelSearchBean channelSearch;

    @Test
    public void testSearch() throws ImplementationException {
        ChannelSearchResult[] results = channelSearch.wsearch("test");
        assertNotNull(results);
    }
}
