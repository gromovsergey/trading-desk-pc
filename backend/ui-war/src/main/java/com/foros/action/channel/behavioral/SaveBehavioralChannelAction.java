package com.foros.action.channel.behavioral;

import static com.foros.session.channel.service.BehavioralChannelService.REMOVED_TRIGGERS;
import com.foros.action.channel.ChannelBreadcrumbsElement;
import com.foros.action.channel.ChannelsBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.account.Account;
import com.foros.model.account.GenericAccount;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.trigger.TriggerBase;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.security.AccountRole;
import com.foros.session.channel.service.BehavioralChannelService;
import com.foros.util.StringUtil;
import com.foros.validation.annotation.Validate;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.struts2.interceptor.SessionAware;

@Validations(
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "behavioralParameters('U').minimumVisits", key = "errors.field.number"),
                @ConversionErrorFieldValidator(fieldName = "behavioralParameters('S').minimumVisits", key = "errors.field.number"),
                @ConversionErrorFieldValidator(fieldName = "behavioralParameters('P').minimumVisits", key = "errors.field.number"),
                @ConversionErrorFieldValidator(fieldName = "behavioralParameters('R').minimumVisits", key = "errors.field.number"),
                @ConversionErrorFieldValidator(fieldName = "channelRateValue", key = "errors.field.number")
        }
)
public class SaveBehavioralChannelAction extends EditChannelSupport<BehavioralChannel> implements BreadcrumbsSupport, SessionAware {

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("urls.positive[(#index)]", "'urls.positive'", "urlError(violation.invalidValue, violation.message)")
            .add("urls.negative[(#index)]", "'urls.negative'", "urlError(violation.invalidValue, violation.message)")
            .add("pageKeywords.positive[(#index)]", "'pageKeywords.positive'", "violation.message")
            .add("pageKeywords.negative[(#index)]", "'pageKeywords.negative'", "violation.message")
            .add("searchKeywords.positive[(#index)]", "'searchKeywords.positive'", "violation.message")
            .add("searchKeywords.negative[(#index)]", "'searchKeywords.negative'", "violation.message")
            .add("urlKeywords.positive[(#index)]", "'urlKeywords.positive'", "violation.message")
            .add("urlKeywords.negative[(#index)]", "'urlKeywords.negative'", "violation.message")
            .add("behavioralParameters[(#key)](#path)", "'behavioralParameters(' + quote(groups[0]) + ')' + groups[1]", "violation.message")
            .add("channelRate.value", "'channelRateValue'", "violation.message")
            .rules();
    @EJB
    private BehavioralChannelService behavioralChannelService;

    private Account existingAccount;
    private Map<String, Object> session;

    public SaveBehavioralChannelAction() {
        model = new BehavioralChannel();
        model.setAccount(new GenericAccount());
    }

    @Validate(validation = "BehavioralChannel.create", parameters = "#target.prepareModel()")
    public String create() {
        prepareModel();
        behavioralChannelService.create(model);
        setSessionParameters();
        return SUCCESS;
    }

    @Validate(validation = "BehavioralChannel.update", parameters = "#target.prepareModel()")
    public String update() {
        prepareModel();
        behavioralChannelService.update(model);
        setSessionParameters();
        return SUCCESS;
    }

    @Validate(validation = "BehavioralChannel.submitToCmp", parameters = "#target.prepareCmpModel()")
    public String submitToCmp() {
        prepareCmpModel();
        behavioralChannelService.submitToCmp(model);
        return SUCCESS;
    }

    @Validate(validation = "BehavioralChannel.update", parameters = "#target.prepareCmpModel()")
    public String updateCmp() {
        prepareCmpModel();
        BehavioralChannel existing = behavioralChannelService.view(model.getId());
        model.setPageKeywords(existing.getPageKeywords());
        model.setSearchKeywords(existing.getSearchKeywords());
        model.setUrls(existing.getUrls());
        model.setUrlKeywords(existing.getUrlKeywords());
        behavioralChannelService.update(model);
        return SUCCESS;
    }

    @Override
    public Account getExistingAccount() {
        if (existingAccount == null) {
            existingAccount = accountService.find(model.getAccount().getId());
        }
        return existingAccount;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    // Initialize session parameters to use them later on View page
    private void setSessionParameters() {
        setRemovedTriggersParameters(TriggerType.PAGE_KEYWORD, "removedPageKeywords", "removedPageKeywordsNumber");
        setRemovedTriggersParameters(TriggerType.SEARCH_KEYWORD, "removedSearchKeywords", "removedSearchKeywordsNumber");
        setRemovedTriggersParameters(TriggerType.URL, "removedUrls", "removedUrlsNumber");
        setRemovedTriggersParameters(TriggerType.URL_KEYWORD, "removedUrlKeywords", "removedUrlKeywordsNumber");
    }

    private void setRemovedTriggersParameters(TriggerType triggerType, String textParameterName, String numberParameterName) {
        Map<TriggerType, Collection<? extends TriggerBase>> removedByType = model.getProperty(REMOVED_TRIGGERS);

        StringBuilder buf = new StringBuilder();
        Collection<? extends TriggerBase> removed = removedByType.get(triggerType);
        for (TriggerBase trigger : removed) {
            if (!trigger.isNegative()) {
                buf.append(trigger.getOriginal()).append('\n');
            }
        }
        session.put(textParameterName, buf.toString());
        session.put(numberParameterName, removed.size());
    }

    public BehavioralChannel prepareModel() {
        model.registerChange("behavioralParameters");
        flushKeywordsToModel();
        return model;
    }

    public String urlError(String value, String message) {
        return StringUtil.getLocalizedString("errors.urlError", value, message);
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = null;
        if (model.getId() != null) {
            BehavioralChannel persistent = behavioralChannelService.find(model.getId());
            breadcrumbs = ChannelBreadcrumbsElement.getChannelBreadcrumbs(persistent).add(ActionBreadcrumbs.EDIT);
        } else {
            if (getExistingAccount().getRole() == AccountRole.INTERNAL) {
                breadcrumbs = new Breadcrumbs().add(new ChannelsBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
            }
        }

        return breadcrumbs;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
