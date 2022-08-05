package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.advertising.campaign.Campaign;
import com.foros.rs.client.model.advertising.campaign.CampaignBidStrategy;
import com.foros.rs.client.model.advertising.campaign.CampaignSelector;
import com.foros.rs.client.model.advertising.campaign.CampaignType;
import com.foros.rs.client.model.advertising.campaign.FrequencyCap;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.result.RsConstraintViolationException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.Test;

public class CampaignServiceTest extends AbstractUnitTest {

    @Test
    public void testCRUD() throws Exception {
        // Create one campaign
        Operations<Campaign> operations = new Operations<>();
        operations.setOperations(Collections.singletonList(
                operation(prepareCampaign(new Campaign()), OperationType.CREATE)
        ));

        OperationsResult operationsResult = campaignService.perform(operations);
        assertNotNull(operationsResult);
        assertEquals(1, operationsResult.getIds().size());

        // Check it created
        CampaignSelector selector = new CampaignSelector();
        selector.setCampaignIds(operationsResult.getIds());
        selector.setCampaignType(CampaignType.TEXT);

        Result<Campaign> result = campaignService.get(selector);
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());

        Campaign campaign = result.getEntities().get(0);
        assertEquals(operationsResult.getIds().get(0), campaign.getId());
        assertNotNull(campaign.getFrequencyCap());
        assertNotNull(campaign.getFrequencyCap().getId());

        Long fCapId = campaign.getFrequencyCap().getId();
        // Update campaign we just created and create one more
        campaign.setStatus(Status.ACTIVE);
        campaign.getFrequencyCap().setId(0L);
        campaign.setBidStrategy(CampaignBidStrategy.MAXIMISE_REACH);

        operations.setOperations(Arrays.asList(
                operation(prepareCampaign(new Campaign()), OperationType.CREATE),
                operation(updateRandom(campaign), OperationType.UPDATE)
        ));

        List<Long> ids = campaignService.perform(operations).getIds();

        selector = new CampaignSelector();
        selector.setCampaignIds(ids);
        result = campaignService.get(selector);

        assertEquals(2, result.getEntities().size());

        // Check that campaign was really updated
        Campaign updated = find(result, campaign.getId());
        assertNotNull(updated);
        assertEquals(campaign.getName(), updated.getName());
        assertEquals(campaign.getStatus(), updated.getStatus());

        // Frequency cap ID must not be updated
        assertEquals(fCapId, updated.getFrequencyCap().getId());

        // Check campaign type
        selector = new CampaignSelector();
        selector.setCampaignIds(operationsResult.getIds());
        selector.setCampaignType(CampaignType.DISPLAY);

        result = campaignService.get(selector);
        assertNotNull(result);
        assertEquals(0, result.getEntities().size());
    }

    private Campaign createCampaign() throws Exception {
        Operations<Campaign> operations = new Operations<Campaign>();
        operations.setOperations(Collections.singletonList(
                operation(prepareCampaign(new Campaign()), OperationType.CREATE)
        ));

        OperationsResult operationsResult = campaignService.perform(operations);
        CampaignSelector selector = new CampaignSelector();
        selector.setCampaignIds(operationsResult.getIds());

        Result<Campaign> result = campaignService.get(selector);
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());
        return result.getEntities().get(0);
    }

    @Test
    public void testUpdateInvalidVersion() throws Exception {
        Campaign persisted = createCampaign();
        Campaign forUpdate = new Campaign();
        forUpdate.setUpdated(getDateTime());
        forUpdate.setId(persisted.getId());

        Operations<Campaign> operations = new Operations<>();
        operations.setOperations(Collections.singletonList(
                operation(updateRandom(forUpdate), OperationType.UPDATE)
        ));

        try {
            campaignService.perform(operations).getIds();
            fail("Should not be reachable");
        } catch (RsConstraintViolationException e) {
            // expected
        }
    }

    @Test
    public void testUpdateEmptyVersion() throws Exception {
        Campaign persisted = createCampaign();
        Campaign forUpdate = new Campaign();
        forUpdate.setId(persisted.getId());

        Operations<Campaign> operations = new Operations<>();
        operations.setOperations(Collections.singletonList(
                operation(updateRandom(forUpdate), OperationType.UPDATE)
        ));

        try {
            campaignService.perform(operations).getIds();
        } catch (RsConstraintViolationException e) {
            fail("Entity should be updated!");
        }
    }

    private Campaign find(Result<Campaign> result, Long id) {
        for (Campaign campaign : result.getEntities()) {
            if (id.equals(campaign.getId())) {
                return campaign;
            }

        }
        return null;
    }

    private Campaign prepareCampaign(Campaign template) throws DatatypeConfigurationException {
        Campaign campaign = prepareCampaign(template, this);
        return campaign;
    }

    public static Campaign prepareCampaign(Campaign template, AbstractUnitTest test) throws DatatypeConfigurationException {
        Campaign campaign = new Campaign();
        campaign.setName("Test company " + String.valueOf(System.currentTimeMillis()));
        //campaign.setBillToUser(template.getBillToUser());
        //campaign.setSoldToUser(template.getSoldToUser());
        campaign.setBudget(BigDecimal.TEN);
        campaign.setDateStart(getDateTime());
        campaign.setCampaignType(CampaignType.TEXT);
        campaign.setAccount(template.getAccount());
        campaign.setStatus(Status.INACTIVE);
        campaign.setAccount(test.advertiserLink(test.longProperty("foros.test.advertiser.id")));
        campaign.setBillToUser(test.link(test.longProperty("foros.test.user.id")));
        campaign.setSoldToUser(test.link(test.longProperty("foros.test.user.id")));
        campaign.setBidStrategy(CampaignBidStrategy.MARGIN);

        FrequencyCap fCap = new FrequencyCap();
        fCap.setLifeCount(1L);
        fCap.setPeriod(1L);
        campaign.setFrequencyCap(fCap);

        return campaign;
    }

    private static Campaign updateRandom(Campaign campaign) {
        campaign.setName(String.valueOf(new Random().nextGaussian()));
        return campaign;
    }
}
