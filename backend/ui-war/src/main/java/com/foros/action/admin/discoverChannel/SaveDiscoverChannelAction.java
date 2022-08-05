package com.foros.action.admin.discoverChannel;

import com.foros.action.Invalidable;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.Trim;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.channel.service.DiscoverChannelListService;
import com.foros.util.AccountUtil;
import com.foros.util.StringUtil;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import org.apache.commons.lang.StringUtils;

@Trim(exclude = {"baseKeyword"})
public class SaveDiscoverChannelAction extends DiscoverChannelActionSupport implements Invalidable, BreadcrumbsSupport {

    @EJB
    private DiscoverChannelListService channelListService;

    private boolean isLinked;

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("urls.positive[(#index)]", "'urls.positive'", "urlError(violation.invalidValue, violation.message)")
            .add("urls.negative[(#index)]", "'urls.negative'", "urlError(violation.invalidValue, violation.message)")
            .add("pageKeywords.positive[(#index)]", "'pageKeywords.positive'", "violation.message")
            .add("pageKeywords.negative[(#index)]", "'pageKeywords.negative'", "violation.message")
            .add("searchKeywords.positive[(#index)]", "'searchKeywords.positive'", "violation.message")
            .add("searchKeywords.negative[(#index)]", "'searchKeywords.negative'", "violation.message")
            .rules();

    private static final List<ConstraintViolationRule> LINKED_RULES = new ConstraintViolationRulesBuilder()
            .add("urls[(#index)]", "'urls'", "violation.message")
            .add("pageKeywords.positive[(#index)]", "'keywords'", "fieldError('pageKeywords.positive', violation.message)")
            .add("pageKeywords.negative[(#index)]", "'keywords'", "fieldError('pageKeywords.negative', violation.message)")
            .add("searchKeywords.positive[(#index)]", "'keywords'", "fieldError('searchKeywords.positive', violation.message)")
            .add("searchKeywords.negative[(#index)]", "'keywords'", "fieldError('searchKeywords.negative', violation.message)")
            .add("baseKeyword", "'keywords'", "violation.message")
            .add("(#property)", "'keywords'", "fieldError(groups[0], violation.message)")
            .add("(#path)", "'keywords'", "violation.message")
            .rules();

    public SaveDiscoverChannelAction() {
        model = new DiscoverChannel();
    }

    public String fieldError(String property, String message) {
        String propertyName = StringUtil.getLocalizedString("channel." + property);
        return StringUtil.getLocalizedString("DiscoverChannel.errors.fieldError", propertyName, message);
    }

    public String urlError(String value, String message) {
        return StringUtil.getLocalizedString("errors.urlError", value, message);
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        if (isLinked) {
            return LINKED_RULES;
        }
        return RULES;
    }

    @Restrict(restriction = "DiscoverChannel.update")
    public String save() throws Exception {
        boolean isNewChannel = getModel().getId() == null;
        if ((getModel().getBehavParamsList() != null) && (getModel().getBehavParamsList().getId() == null)) {
            getModel().setBehavParamsList(null);
        }
        try {
            if (isNewChannel) {
                create();
            } else {
                DiscoverChannel oldChannel = discoverChannelService.view(getModel().getId());
                DiscoverChannelList channelList = oldChannel.getChannelList();
                isLinked = channelList != null &&  channelList.getId() != null;
                if (isLinked) {
                    updateLinked();
                } else {
                    update(oldChannel);
                }
            }
        // FIXME use PersistenceExceptionInterceptor
        } catch (EJBException e) {
            if (isNewChannel) {
                getModel().setId(null);
            }
        }
        if (hasFieldErrors()) {
            invalid();
            return INPUT;
        }
        return SUCCESS;
    }

    private void create() {
        getModel().setAccount(AccountUtil.extractAccountById(getAccountId()));
        getModel().getAccount().unregisterChange("id");
        flushKeywordsToModel();
        discoverChannelService.create(getModel());
    }

    private void update(DiscoverChannel oldChannel) {
        getModel().setAccount(oldChannel.getAccount());
        flushKeywordsToModel();
        discoverChannelService.update(getModel());
    }

    private void updateLinked() {
        if (StringUtils.isBlank(getModel().getBaseKeyword())) {
            addFieldError("keywords", getText("errors.field.required"));
            return;
        }
        channelListService.updateLinkedChannel(getModel());
    }

    @Override
    public void invalid() throws Exception {
        populateDependenciesForSave();
        if (getModel().getId() == null) {
            getModel().setAccount(AccountUtil.extractAccountById(getAccountId()));
        } else {
            DiscoverChannel oldChannel = discoverChannelService.view(getModel().getId());
            getModel().setAccount(oldChannel.getAccount());
            getModel().setChannelList(oldChannel.getChannelList());
            if (isLinked) {
                getModel().setName(oldChannel.getName());
            }
        }
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs;
        if (model.getId() != null) {
            DiscoverChannel persistent = discoverChannelService.find(model.getId());
            breadcrumbs = new Breadcrumbs().add(new DiscoverChannelsBreadcrumbsElement()).add(new DiscoverChannelBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs = new Breadcrumbs().add(new DiscoverChannelsBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        }

        return breadcrumbs;
    }
}
