package com.foros.session.channel;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.channel.Channel;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.CmpAccountTestFactory;
import com.foros.test.factory.InternalAccountTestFactory;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractChannelServiceBeanIntegrationTest<C extends Channel> extends AbstractServiceBeanIntegrationTest {
    @Autowired
    protected InternalAccountTestFactory internalAccountTF;

    @Autowired
    protected AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    protected CmpAccountTestFactory cmpAccountTF;

    protected void insertIntoChannelInventory(Long channelId, int count) {
        jdbcTemplate.update(
            "INSERT INTO CHANNELINVENTORY " +
                    " (CHANNEL_ID, SDATE, TOTAL_USER_COUNT, COLO_ID)" +
                    " VALUES(?, now(), " + count + ", 0)",
            channelId
            );

        jdbcTemplate.execute("select displaystatus.update_channel_status_by_stats()");
    }
}
