package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.action.Invalidable;
import com.foros.model.Status;
import com.foros.model.channel.DeviceChannel;
import com.foros.session.campaign.ccg.bulk.AddDevicesOperation;
import com.foros.session.campaign.ccg.bulk.RemoveDevicesOperation;
import com.foros.session.campaign.ccg.bulk.SetDevicesOperation;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SaveBulkDeviceTargetingAction extends BulkDeviceTargetingActionSupport implements Invalidable {
    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("operation.devices.(#path)", "violation.message")
            .add("operation.(#path)", "violation.message")
            .add("operation.(#path)", "groups[0]", "violation.message")
            .add("groups[(#index)].devices(#path)", "addGroupError(groups[0])", "violation.message")
            .add("groups[(#index)](#path)", "addGroupError(groups[0])", "violation.message")
            .rules();

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    private Set<Long> getNonDeletedDeviceChannels(Set<Long> selectedIds) {
        Set<Long> res = new HashSet<>();
        if (selectedIds == null) {
            return res;
        }

        for (Long id: selectedIds) {
            DeviceChannel dc = deviceChannelService.findById(id);
            if (dc.getStatus() != Status.DELETED) {
                res.add(id);
            }
        }
        return res;
    }

    @Override
    public void invalid() throws Exception {
        deviceHelper = null;
        populateTargeting();
    }

    public String save() {
        populateTargeting();
        switch (editMode) {
            case Add:
                perform(new AddDevicesOperation(
                        getNonDeletedDeviceChannels(addIds),
                        deviceChannelService));
                break;
            case Remove:
                perform(new RemoveDevicesOperation(
                        getNonDeletedDeviceChannels(removeIds),
                        deviceChannelService));
                break;
            case Set:
                perform(new SetDevicesOperation(
                        getNonDeletedDeviceChannels(setIds),
                        deviceChannelService));
                break;
            default:
                return INPUT;
        }
        return SUCCESS;
    }
}
