package com.foros.action.campaign.campaignGroup.bulk;

import com.foros.model.site.Site;
import com.foros.session.EntityTO;
import com.foros.session.campaign.ccg.bulk.AddSitesOperation;
import com.foros.session.campaign.ccg.bulk.RemoveSitesOperation;
import com.foros.session.campaign.ccg.bulk.SetSitesOperation;
import com.foros.util.StringUtil;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SaveBulkSiteTargetingAction extends BulkSiteTargetingActionSupport {
    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("operation.sites(#path)", "withMode(groups[0])", "violation.message")
            .add("operation.(#path)", "groups[0]", "violation.message")
            .add("groups[(#index)].sites(#path)", "addGroupError(groups[0])", "getGroupSitesMessage(violation)")
            .add("groups[(#index)](#path)", "addGroupError(groups[0])", "violation.message")
            .rules();

    public String save() {
        switch (editMode) {
            case Add:
                perform(new AddSitesOperation(toSites(addIds)));
                break;
            case Remove:
                perform(new RemoveSitesOperation(toSites(removeIds), toSites(getAvailableSites())));
                break;
            case Set:
                Collection<Site> sites = includeSpecificSitesFlag ? toSites(selectedSites) : Collections.<Site>emptySet();
                perform(new SetSitesOperation(sites, includeSpecificSitesFlag));
                break;
            default:
                return INPUT;
        }
        return SUCCESS;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    private Collection<Site> toSites(Collection<EntityTO> sites) {
        List<Site> res = new ArrayList<>(ids.size());
        for (EntityTO to : sites) {
            res.add(new Site(to.getId()));
        }
        return res;
    }

    private Collection<Site> toSites(Set<Long> ids) {
        List<Site> res = new ArrayList<>(ids.size());
        for (Long id : ids) {
            res.add(new Site(id));
        }
        return res;
    }

    public String withMode(String path) {
        switch (editMode) {
            case Add:
                return "addSites" + path;
            case Remove:
                return "removeSites" + path;
            case Set:
                return "sites" + path;
            default:
                throw new IllegalArgumentException(path);
        }
    }

    public String getGroupSitesMessage(ConstraintViolation violation) {
        if (editMode == Mode.Remove && violation.getError() == BusinessErrors.FIELD_IS_REQUIRED) {
            return StringUtil.getLocalizedString("ccg.bulk.errors.noSitesLeft");
        } else {
            return violation.getMessage();
        }
    }
}
