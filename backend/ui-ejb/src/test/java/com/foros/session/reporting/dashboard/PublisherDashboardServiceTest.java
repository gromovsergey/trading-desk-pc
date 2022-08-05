package com.foros.session.reporting.dashboard;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TagsTestFactory;

import java.util.Calendar;
import java.util.List;

import group.Db;
import group.Report;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Report.class })
public class PublisherDashboardServiceTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private PublisherDashboardService publisherDashboardService;

    @Autowired
    private PublisherAccountTestFactory publisherAccountTestFactory;

    @Autowired
    private SiteTestFactory siteTestFactory;

    @Autowired
    private TagsTestFactory tagsTestFactory;

    @Test
    public void testGenerateSiteDashboard() {
        AccountDashboardParameters parameters = new AccountDashboardParameters();
        parameters.setAccountId(publisherAccountTestFactory.createPersistent().getId());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        parameters.setDateRange(new DateRange(LocalDate.fromDateFields(calendar.getTime()), new LocalDate()));
        parameters.setWithActivityOnly(false);

        List<SiteDashboardTO> result = publisherDashboardService.generateSiteDashboard(parameters);
        assertNotNull(result);
    }

    @Test
    public void testGenerateTagDashboard() {
        SiteDashboardParameters parameters = new SiteDashboardParameters();
        Site site = siteTestFactory.createPersistent();
        Tag tag = tagsTestFactory.createPersistent(site);
        parameters.setSiteId(tag.getSite().getId());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        parameters.setDateRange(new DateRange(LocalDate.fromDateFields(calendar.getTime()), new LocalDate()));
        parameters.setWithActivityOnly(false);

        List<TagDashboardTO> result = publisherDashboardService.generateTagDashboard(parameters);
        assertNotNull(result);
    }
}
