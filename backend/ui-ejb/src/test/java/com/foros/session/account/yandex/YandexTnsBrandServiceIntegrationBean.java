package com.foros.session.account.yandex;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.TnsBrand;
import com.foros.session.account.yandex.brand.YandexTnsBrandService;
import com.foros.test.factory.TestFactory;
import com.foros.test.factory.TnsBrandTestFactory;
import com.foros.util.CollectionUtils;
import com.foros.util.bean.Filter;

import group.Db;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class YandexTnsBrandServiceIntegrationBean extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private YandexTnsBrandService yandexTnsBrandService;

    @Autowired
    private TnsBrandTestFactory tnsBrandTestFactory;

    @Test
    public void testSearch() {
        final TnsBrand persistent = tnsBrandTestFactory.createPersistent();

        List<TnsBrand> brands = yandexTnsBrandService.searchBrands(TestFactory.OUI_TEST_ENTITY_NAME_PREFIX, 20);

        assertTrue(!brands.isEmpty());

        CollectionUtils.filter(brands, new Filter<TnsBrand>() {
            @Override
            public boolean accept(TnsBrand element) {
                return persistent.getName().equals(element.getName());
            }
        });

        assertEquals(brands.size(), 1);
    }

}