package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.entity.EntityLink;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.model.siteCreative.SiteCreativeApproval;
import com.foros.rs.client.model.siteCreative.SiteCreativeApprovalOperation;
import com.foros.rs.client.model.siteCreative.SiteCreativeApprovalOperationType;
import com.foros.rs.client.model.siteCreative.SiteCreativeApprovalOperations;
import com.foros.rs.client.model.siteCreative.SiteCreativeApprovalSelector;
import com.foros.rs.client.model.siteCreative.SiteCreativeCategory;
import com.foros.rs.client.model.siteCreative.SiteCreativeRejectReason;

import java.util.Collections;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class SiteCreativeApprovalServiceTest extends AbstractUnitTest {

    private Long siteId;

    @Before
    public void setUp() throws Exception {
        siteId = longProperty("foros.test.site.id");
    }

    @Test
    public void testSearch() throws Exception {

        SiteCreativeApprovalSelector selector = new SiteCreativeApprovalSelector();

        selector.setSiteId(siteId);
        PagingSelector paging = new PagingSelector();
        paging.setCount(3L);
        paging.setFirst(2L);
        selector.setPaging(paging);
        Result<SiteCreativeApproval> result = siteCreativeApprovalService.get(selector);
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.getEntities().size());
        SiteCreativeApproval approval = result.getEntities().get(0);
        Assert.assertNotNull(approval.getCreative());
        Assert.assertNotNull(approval.getCreative().getId());
        Assert.assertNotNull(approval.getCreative().getContentCategories());
        SiteCreativeCategory contentCategory = approval.getCreative().getContentCategories().get(0);
        Assert.assertNotNull(contentCategory);
        Assert.assertNotNull(contentCategory.getName());
        Assert.assertNotNull(approval.getCreative().getVisualCategories());
        Assert.assertNotNull(approval.getCreative().getSize());
        Assert.assertNotNull(approval.getCreative().getSize().getId());
        Assert.assertNotNull(approval.getCreative().getSize().getName());
        Assert.assertNotNull(approval.getCreative().getUpdated());

        selector.setCreativeIds(Collections.singletonList(approval.getCreative().getId()));
        selector.setPaging(null);
        result = siteCreativeApprovalService.get(selector);
        Assert.assertEquals(1, result.getEntities().size());

        selector.setCreativeUpdatedSince(approval.getCreative().getUpdated());
        selector.setStatuses(Collections.singletonList(approval.getStatus()));
        selector.setSizeName(approval.getCreative().getSize().getName());
        result = siteCreativeApprovalService.get(selector);
        Assert.assertEquals(1, result.getEntities().size());
    }

    @Test
    public void testUpdate() {
        SiteCreativeApprovalSelector selector = new SiteCreativeApprovalSelector();
        selector.setSiteId(siteId);
        doIteration(selector);
        doIteration(selector);
    }

    private void doIteration(SiteCreativeApprovalSelector selector) {
        Result<SiteCreativeApproval> result = siteCreativeApprovalService.get(selector);

        SiteCreativeApprovalOperations operations = new SiteCreativeApprovalOperations();
        EntityLink site = new EntityLink();
        site.setId(siteId);
        operations.setSite(site);
        for (SiteCreativeApproval approval : result.getEntities().subList(0, 2)) {
            SiteCreativeApprovalOperation operation = new SiteCreativeApprovalOperation();
            EntityLink creative = new EntityLink();
            creative.setId(approval.getCreative().getId());
            operation.setCreative(creative);
            invert(approval, operation);
            operations.getOperations().add(operation);
        }

        siteCreativeApprovalService.perform(operations);
    }

    private void invert(SiteCreativeApproval approval, SiteCreativeApprovalOperation operation) {
        switch (approval.getStatus()) {
            case APPROVED:
            case CREATIVE_CATEGORY_APPROVED:
                // reject
                operation.setType(SiteCreativeApprovalOperationType.REJECT);
                operation.setFeedback("feedback + " + System.currentTimeMillis());
                operation.setRejectReason(SiteCreativeRejectReason.CREATIVE_HAS_INAPPROPRIATE_CONTENT);
                break;
            case REJECTED:
            case PENDING:
                // approve
                operation.setType(SiteCreativeApprovalOperationType.APPROVE);
                break;
        }
    }
}
