package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.advertising.RateType;
import com.foros.rs.client.model.advertising.campaign.CCGRate;
import com.foros.rs.client.model.advertising.campaign.CCGType;
import com.foros.rs.client.model.advertising.campaign.CampaignCreativeGroup;
import com.foros.rs.client.model.advertising.campaign.CampaignCreativeGroupSelector;
import com.foros.rs.client.model.advertising.campaign.CampaignType;
import com.foros.rs.client.model.advertising.campaign.DeliveryPacing;
import com.foros.rs.client.model.advertising.campaign.TargetType;
import com.foros.rs.client.model.entity.EntityLink;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.result.RsConstraintViolationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.junit.Test;

public class CampaignCreativeGroupServiceTest extends AbstractUnitTest {

    @Test
    public void testCRUD() throws Exception {
        Operations<CampaignCreativeGroup> operations = new Operations<CampaignCreativeGroup>();
        operations.setOperations(Arrays.asList(
                operation(prepareGroup(), OperationType.CREATE)
        ));
        OperationsResult operationsResult = groupService.perform(operations);
        assertNotNull(operationsResult);
        assertEquals(1, operationsResult.getIds().size());

        // Check it created
        CampaignCreativeGroupSelector selector = new CampaignCreativeGroupSelector();
        selector.setGroupIds(operationsResult.getIds());
        selector.setCampaignType(CampaignType.TEXT);

        Result<CampaignCreativeGroup> result = groupService.get(selector);
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());
        assertEquals(operationsResult.getIds().get(0), result.getEntities().get(0).getId());

        // Update group we just created and create one more
        CampaignCreativeGroup forUpdate = result.getEntities().get(0);
        forUpdate.setStatus(Status.ACTIVE);

        operations.setOperations(Arrays.asList(
                operation(prepareGroup(), OperationType.CREATE),
                operation(updateRandom(forUpdate), OperationType.UPDATE)
        ));

        List<Long> ids = groupService.perform(operations).getIds();

        selector = new CampaignCreativeGroupSelector();
        selector.setGroupIds(ids);
        result = groupService.get(selector);

        assertEquals(2, result.getEntities().size());

        // Check that CampaignCreativeGroup was really updated
        CampaignCreativeGroup updated = find(result, forUpdate.getId());
        assertNotNull(updated);
        assertEquals(forUpdate.getName(), updated.getName());
        assertEquals(forUpdate.getStatus(), updated.getStatus());

    }

    private CampaignCreativeGroup createGroup() throws Exception {
        Operations<CampaignCreativeGroup> operations = new Operations<CampaignCreativeGroup>();
        operations.setOperations(Arrays.asList(
                operation(prepareGroup(), OperationType.CREATE)
        ));

        OperationsResult operationsResult = groupService.perform(operations);
        CampaignCreativeGroupSelector selector = new CampaignCreativeGroupSelector();
        selector.setGroupIds(operationsResult.getIds());

        Result<CampaignCreativeGroup> result = groupService.get(selector);
        assertNotNull(result);
        assertEquals(1, result.getEntities().size());
        return result.getEntities().get(0);
    }

    @Test
    public void testUpdateInvalidVersion() throws Exception {
        CampaignCreativeGroup persisted = createGroup();
        CampaignCreativeGroup forUpdate = new CampaignCreativeGroup();
        forUpdate.setUpdated(getDateTime());
        forUpdate.setId(persisted.getId());

        Operations<CampaignCreativeGroup> operations = new Operations<CampaignCreativeGroup>();
        operations.setOperations(Arrays.asList(
                operation(updateRandom(forUpdate), OperationType.UPDATE)
        ));

        try {
            groupService.perform(operations).getIds();
            fail("Should not be reachable");
        } catch (RsConstraintViolationException e) {
            // expected
        }
    }

    @Test
    public void testUpdateEmptyVersion() throws Exception {
        CampaignCreativeGroup persisted = createGroup();
        CampaignCreativeGroup forUpdate = new CampaignCreativeGroup();
        forUpdate.setId(persisted.getId());

        Operations<CampaignCreativeGroup> operations = new Operations<CampaignCreativeGroup>();
        operations.setOperations(Arrays.asList(
                operation(updateRandom(forUpdate), OperationType.UPDATE)
        ));

        try {
            groupService.perform(operations).getIds();
        } catch (RsConstraintViolationException e) {
            fail("Entity should be updated!");
        }
    }

    private CampaignCreativeGroup prepareGroup() throws DatatypeConfigurationException {
        CampaignCreativeGroup group = new CampaignCreativeGroup();
        group.setName("Test company " + String.valueOf(System.currentTimeMillis()));
        group.setCcgType(CCGType.TEXT);
        group.setCampaign(link(longProperty("foros.test.campaign.id")));
        group.setCountry("GB");

        group.setDailyBudget(BigDecimal.ONE);
        group.setDeliveryPacing(DeliveryPacing.FIXED);
        group.setTgtType(TargetType.CHANNEL);

        prepareDeviceChannels(group);

        group.setBudget(BigDecimal.TEN);
        group.setDateStart(getDateTime());
        group.setDateEnd(getDateTime());
        group.setStatus(Status.INACTIVE);
        CCGRate rate = new CCGRate();
        rate.setEffectiveDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        rate.setRateType(RateType.CPC);
        rate.setValue(BigDecimal.TEN);
        group.setCcgRate(rate);

        return group;
    }

    private void prepareDeviceChannels(CampaignCreativeGroup group) {
        List<EntityLink> deviceChannels = new ArrayList<EntityLink>();
        deviceChannels.add(link(longProperty("foros.test.device.mobileChannel.id")));
        deviceChannels.add(link(longProperty("foros.test.device.nonMobileChannel.id")));
        group.setDeviceChannels(deviceChannels);
    }

    private CampaignCreativeGroup find(Result<CampaignCreativeGroup> result, Long id) {
        for (CampaignCreativeGroup group : result.getEntities()) {
            if (id.equals(group.getId())) {
                return group;
            }
        }
        return null;
    }

    private static CampaignCreativeGroup updateRandom(CampaignCreativeGroup group) {
        group.setName(String.valueOf(new Random().nextGaussian()));
        return group;
    }
}
