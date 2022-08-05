package com.foros.session.channel;

import com.foros.model.channel.ChannelVisibility;
import com.foros.util.CollectionUtils;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@Category(Unit.class)
public class ChannelVisibilityCriteriaTest {
    @Test
    public void visibility() {
        assertSame(ChannelVisibilityCriteria.PRIVATE, ChannelVisibilityCriteria.valueOf(ChannelVisibility.PRI));
        assertSame(ChannelVisibilityCriteria.PRIVATE, ChannelVisibilityCriteria.valueOf("PRI"));
        assertSame(ChannelVisibilityCriteria.ALL, ChannelVisibilityCriteria.valueOf(ChannelVisibility.values()));
    }

    @Test
    public void map() {
        Map<ChannelVisibilityCriteria,String> map = CollectionUtils
                .map(ChannelVisibilityCriteria.ALL, "form.all")
                .map(ChannelVisibilityCriteria.PUBLIC, "channel.visibility.PUB")
                .map(ChannelVisibilityCriteria.PRIVATE, "channel.visibility.PRI")
                .map(ChannelVisibilityCriteria.CMP, "channel.visibility.CMP")
                .build();

        assertNotNull(map);
    }
}
