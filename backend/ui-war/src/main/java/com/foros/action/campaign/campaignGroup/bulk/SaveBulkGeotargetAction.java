package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.session.campaign.ccg.bulk.AddGeoTargetOperation;
import com.foros.session.campaign.ccg.bulk.RemoveGeoTargetOperation;
import com.foros.session.campaign.ccg.bulk.SetGeoTargetOperation;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.ArrayList;
import java.util.List;

public class SaveBulkGeotargetAction extends BulkGeotargetActionSupport {
    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("groups[(#index)](#path)", "addGroupError(groups[0])", "violation.message")
            .rules();

    protected List<Long> geoChannelsAdd = new ArrayList<>();
    protected List<Long> geoChannelsRemove = new ArrayList<>();
    protected List<Long> geoChannelsSet = new ArrayList<>();

    private String countryCode;

    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    public List<Long> getGeoChannelsAdd() {
        return geoChannelsAdd;
    }

    public List<Long> getGeoChannelsRemove() {
        return geoChannelsRemove;
    }

    public List<Long> getGeoChannelsSet() {
        return geoChannelsSet;
    }

    public String save() {
        Long advertiserId = getAccount().getId();
        switch (editMode) {
            case Add:
                groupService.perform(advertiserId, ids, new AddGeoTargetOperation(geoChannelsAdd));
                break;
            case Remove:
                groupService.perform(advertiserId, ids, new RemoveGeoTargetOperation(geoChannelsRemove));
                break;
            case Set:
                groupService.perform(advertiserId, ids, new SetGeoTargetOperation(geoChannelsSet));
                break;
            default:
                return INPUT;
        }
        return SUCCESS;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setGeoChannelsAdd(List<Long> geoChannelsAdd) {
        this.geoChannelsAdd = geoChannelsAdd;
    }

    public void setGeoChannelsRemove(List<Long> geoChannelsRemove) {
        this.geoChannelsRemove = geoChannelsRemove;
    }

    public void setGeoChannelsSet(List<Long> geoChannelsSet) {
        this.geoChannelsSet = geoChannelsSet;
    }
}
