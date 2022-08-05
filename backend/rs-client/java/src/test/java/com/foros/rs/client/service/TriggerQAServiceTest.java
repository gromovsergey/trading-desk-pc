package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.model.advertising.channel.triggerQA.TriggerQASelector;
import com.foros.rs.client.model.entity.QaStatus;
import com.foros.rs.client.model.operation.Operation;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.model.triggerQA.QATrigger;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class TriggerQAServiceTest extends AbstractUnitTest {

    @Test
    public void testGet() throws Exception {
        getByCampaign(null);

        Long groupId = longProperty("foros.test.creativeGroup.id");
        getTriggers(null, groupId, null, null);

        Long channelId = longArrayProperty("foros.test.channel.ids").get(0);
        getTriggers(null, null, channelId, null);

        checkStatus(getByCampaign(QaStatus.APPROVED), QaStatus.APPROVED);
        checkStatus(getByCampaign(QaStatus.DECLINED), QaStatus.DECLINED);
        checkStatus(getByCampaign(QaStatus.HOLD), QaStatus.HOLD);
    }

    @Test
    public void testUpdate() throws Exception {
        testUpdate(QaStatus.APPROVED);
        testUpdate(QaStatus.DECLINED);
        testUpdate(QaStatus.HOLD);
    }

    private List<QATrigger> getTriggers(Long campaignId, Long groupId, Long channelId, QaStatus status) {
        TriggerQASelector selector = new TriggerQASelector();
        selector.setCampaignId(campaignId);
        selector.setGroupId(groupId);
        selector.setChannelId(channelId);
        selector.setTriggerStatus(status);

        Result<QATrigger> result = triggerQAService.get(selector);
        assertNotNull(result);
        return result.getEntities();
    }

    private void updateStatus(List<QATrigger> triggerList, QaStatus status) {
        Operations<QATrigger> operations = generateOperations(triggerList, status);

        OperationsResult operationsResult = triggerQAService.perform(operations);
        assertNotNull(operationsResult);
    }

    private Operations<QATrigger> generateOperations(List<QATrigger> triggerList, QaStatus status) {
        List<Operation<QATrigger>> operations = new ArrayList<>();
        for (QATrigger trigger : triggerList) {
            Operation<QATrigger> operation = new Operation<>();
            trigger.setStatus(status);
            operation.setEntity(trigger);
            operation.setType(OperationType.UPDATE);
            operations.add(operation);
        }

        Operations<QATrigger> result = new Operations<>();
        result.setOperations(operations);
        return result;
    }

    private List<QATrigger> getByCampaign(QaStatus status) {
        Long campaignId = longProperty("foros.test.campaign.id");
        return getTriggers(campaignId, null, null, status);
    }

    private void checkStatus(List<QATrigger> triggers, QaStatus status) {
        for (QATrigger trigger : triggers) {
            assertEquals(status, trigger.getStatus());
        }
    }

    private void testUpdate(QaStatus status) throws Exception {
        updateStatus(getByCampaign(null), status);
        checkStatus(getByCampaign(null), status);
    }
}
