package com.foros.action.admin.keywordChannel;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.account.GenericAccount;
import com.foros.model.channel.KeywordChannel;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.List;

@Validations(
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "frequencyCap.periodSpan.value", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowLengthSpan.value", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowCount", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "frequencyCap.lifeCount", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "behavioralParameters('S').minimumVisits", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "behavioralParameters('P').minimumVisits", key = "errors.field.integer")
        }
)
public class EditKeywordChannelAction extends KeywordChannelActionSupport<KeywordChannel> implements BreadcrumbsSupport {

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
        .add("behavioralParameters[(#key)](#path)", "'behavioralParameters(' + quote(groups[0]) + ')' + groups[1]", "violation.message")
        .rules();

    private KeywordChannel channel;

    public EditKeywordChannelAction() {
        channel = new KeywordChannel();
        channel.setAccount(new GenericAccount());
    }

    @ReadOnly
    public String edit() {
        channel = keywordChannelService.view(channel.getId());
        return SUCCESS;
    }

    public String save() {
        keywordChannelService.update(channel);
        return SUCCESS;
    }

    @Override
    public KeywordChannel getModel() {
        return channel;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new KeywordChannelsBreadcrumbsElement()).add(new KeywordChannelBreadcrumbsElement(channel)).add(ActionBreadcrumbs.EDIT);
    }
}
