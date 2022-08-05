package com.foros.action.admin.bannedChannel;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.breadcrumbs.SimpleLinkBreadcrumbsElement;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.channel.BannedChannel;
import com.foros.util.StringUtil;
import com.foros.validation.annotation.Validate;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.List;

public class SaveBannedChannelAction extends BannedChannelActionSupport implements BreadcrumbsSupport {

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("urls.positive[(#index)]", "'urls'", "urlError(violation.invalidValue, violation.message)")
            .add("pageKeywords.positive[(#index)]", "'keywords'", "violation.message")
            .add("urlKeywords.positive[(#index)]", "'urlKeywords'", "violation.message")
            .rules();

    public SaveBannedChannelAction() {
        model = new BannedChannel();
    }

    @Validate(validation = "BannedChannel.update", parameters = "#target.prepareModel()")
    public String save() {
        prepareModel();
        service.update(getModel());
        return SUCCESS;
    }

    public BannedChannel prepareModel() {
        String pageSearchKeywords = StringUtil.removeRemarks(getKeywordsText());
        model.getPageKeywords().setPositiveString(pageSearchKeywords);
        model.getSearchKeywords().setPositiveString(pageSearchKeywords);
        model.getUrls().setPositiveString(StringUtil.removeRemarks(getUrlsText()));
        model.getUrlKeywords().setPositiveString(StringUtil.removeRemarks(getUrlKeywordsText()));
        return model;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    public String urlError(String value, String message) {
        return StringUtil.getLocalizedString("errors.urlError", value, message);
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        SimpleLinkBreadcrumbsElement channelElement =
                model.getId().equals(BannedChannel.NO_ADV_CHANNEL_ID) ? new NoAdvChannelBreadcrumbsElement() : new NoTrackChannelBreadcrumbsElement();
        return new Breadcrumbs().add(channelElement).add(ActionBreadcrumbs.EDIT);

    }
}
