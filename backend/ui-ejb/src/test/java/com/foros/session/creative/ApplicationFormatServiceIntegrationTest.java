package com.foros.session.creative;

import com.foros.test.factory.ApplicationFormatTestFactory;
import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.template.ApplicationFormat;
import com.foros.session.template.ApplicationFormatService;

import java.util.List;

import group.Db;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class ApplicationFormatServiceIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    public ApplicationFormatService applicationFormatService;

    @Autowired
    public ApplicationFormatTestFactory applicationFormatTF;

    @Test
    public void testFindAll() {
        List<ApplicationFormat> all = applicationFormatService.findAll();

        int rowCount = jdbcTemplate.queryForInt("SELECT COUNT(0) FROM APPFORMAT");
        assertEquals("JDBC query must show the same number of CreativeOptions", rowCount, all.size());
    }

    @Test
    public void testCreateEntity() {
        ApplicationFormat format = applicationFormatTF.createPersistent();

        ApplicationFormat found = applicationFormatService.findById(format.getId());
        assertSame("Entity is not created properly", format, found);
    }
}
