package com.foros.action.admin.keywordChannel;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.session.channel.service.DefaultKeywordSettingsTO;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.List;

@Validations(
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "behavioralParameters('S').minimumVisits", key = "errors.field.positiveNumber"),
                @ConversionErrorFieldValidator(fieldName = "behavioralParameters('P').minimumVisits", key = "errors.field.positiveNumber")
        }
)
public class DefaultKeywordSettingsAction extends KeywordChannelActionSupport<DefaultKeywordSettingsTO> implements BreadcrumbsSupport {

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
        .add("behavioralParameters[(#key)](#path)", "'behavioralParameters(' + quote(groups[0]) + ')' + groups[1]", "violation.message")
        .rules();

    private DefaultKeywordSettingsTO model = new DefaultKeywordSettingsTO();

    @ReadOnly
    public String edit() throws Exception {
        model = keywordChannelService.findDefaultKeywordSettings();
        return SUCCESS;
    }

    public String save() throws Exception {
        keywordChannelService.updateDefaultParameters(model);
        return SUCCESS;
    }

    @Override
    public DefaultKeywordSettingsTO getModel() {
        return model;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new KeywordChannelsBreadcrumbsElement()).add(new DefaultKeywordSettingsBreadcrumbsElement());
    }
}
