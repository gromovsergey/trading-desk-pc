package com.foros.action.site;

import com.foros.model.site.TagOptGroupState;
import com.foros.model.site.TagOptionValue;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.List;

public class SaveCreativeCustomizationAction extends CreativeCustomizationSupportAction {

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("options[(#index)].(#path)", "'optionValues[' + groups[0] + '].' + groups[1]", "violation.message")
            .rules();

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    public String update() {
        Timestamp version = getTag().getVersion();
        setTag(tagsService.viewFetchedForEdit(getTag().getId()));
        getTag().setVersion(version);

        getTag().setOptions(new LinkedHashSet<TagOptionValue>(getOptionValues().values()));
        getTag().setGroupStates(new LinkedHashSet<TagOptGroupState>(getGroupStateValues().values()));

        tagsService.updateOptions(getTag());
        return SUCCESS;
    }
}
