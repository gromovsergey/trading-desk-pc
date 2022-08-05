package com.foros.session.account.yandex;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.TnsAdvertiser;
import com.foros.session.account.yandex.advertiser.YandexTnsAdvertiserService;
import com.foros.test.factory.TestFactory;
import com.foros.test.factory.TnsAdvertiserTestFactory;
import com.foros.util.CollectionUtils;
import com.foros.util.bean.Filter;

import group.Db;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class YandexTnsAdvertiserServiceIntegrationBean extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private YandexTnsAdvertiserService yandexTnsAdvertiserService;

    @Autowired
    private TnsAdvertiserTestFactory tnsAdvertiserTestFactory;

    @Test
    public void testSearch() {
        final TnsAdvertiser persistent = tnsAdvertiserTestFactory.createPersistent();

        List<TnsAdvertiser> advertisers = yandexTnsAdvertiserService.searchAdvertisers(TestFactory.OUI_TEST_ENTITY_NAME_PREFIX, 20);

        assertTrue(!advertisers.isEmpty());

        CollectionUtils.filter(advertisers, new Filter<TnsAdvertiser>() {
            @Override
            public boolean accept(TnsAdvertiser element) {
                return persistent.getName().equals(element.getName());
            }
        });

        assertEquals(advertisers.size(), 1);
    }

}
