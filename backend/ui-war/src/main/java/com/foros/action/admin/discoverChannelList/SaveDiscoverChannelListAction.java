package com.foros.action.admin.discoverChannelList;

import com.foros.action.IdNameVersionForm;
import com.foros.action.Invalidable;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.Trim;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.account.GenericAccount;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.model.channel.DiscoverChannelsAlreadyExistException;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessException;
import com.foros.session.BusinessException.PropertyError;
import com.foros.util.AccountUtil;
import com.foros.util.EqualsUtil;
import com.foros.util.StringUtil;
import com.foros.util.UITimestamp;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJBException;
import org.apache.commons.collections.CollectionUtils;

@Trim(exclude = {"keywordList"})
public class SaveDiscoverChannelListAction extends EditDiscoverChannelListActionBase implements Invalidable, BreadcrumbsSupport {
    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
    .add(
            "childChannels[(#index)].keywordList[(#index)]",
            "'keywords'",
            "childError(groups[0], 'keywords', violation.message)"
    )
    .add(
            "childChannels[(#index)].pageKeywords.positive[(#index)]",
            "'keywords'",
            "childError(groups[0], 'pageKeywords.positive', violation.message)"
    )
    .add(
            "childChannels[(#index)].pageKeywords.negative[(#index)]",
            "'keywords'",
            "childError(groups[0], 'pageKeywords.negative', violation.message)"
    )
    .add(
            "childChannels[(#index)].searchKeywords.positive[(#index)]",
            "'keywords'",
            "childError(groups[0], 'searchKeywords.positive', violation.message)"
    )
    .add(
            "childChannels[(#index)].searchKeywords.negative[(#index)]",
            "'keywords'",
            "childError(groups[0], 'searchKeywords.negative', violation.message)"
    )
    .add(
            "childChannels[(#index)].baseKeyword",
            "'keywords'",
            "childError(groups[0], violation.message)"
    )
    .add(
            "childChannels[(#index)].(#property)",
            "'keywords'",
            "childError(groups[0], groups[1], violation.message)"
    )
    .add(
            "childChannels[(#index)]",
            "'keywords'",
            "childError(groups[0], violation.message)"
    )
    .add(
            "channelsToLink[(#index)].name",
            "'existingChannels'",
            "channelToLinkError('name', violation.invalidValue, violation.message)"
    )
    .add(
            "channelsToLink[(#index)].baseKeyword",
            "'existingChannels'",
            "channelToLinkError('keywords', violation.invalidValue, violation.message)"
    )
    .rules();

    private String prevCountry;
    private String prevKeywordList;
    private Long prevAccountId;
    private boolean savedBefore;

    public SaveDiscoverChannelListAction() {
        model = new DiscoverChannelList();
        model.setAccount(new GenericAccount());
    }

    public String childError(int row, String message) {
        String res = StringUtil.getLocalizedString("DiscoverChannelList.errors.childError", row + 1, message);
        return res;
    }

    public String childError(int row, String property, String message) {
        String propertyName = StringUtil.getLocalizedString("channel." + property);
        String res = StringUtil.getLocalizedString("DiscoverChannelList.errors.childFieldError",
                row + 1, propertyName, message);
        return res;
    }

    public String channelToLinkError(String property, String value, String message) {
        String propertyName = StringUtil.getLocalizedString("channel." + property);
        String res = StringUtil.getLocalizedString("DiscoverChannelList.errors.channelToLinkFieldError",
                value, propertyName, message);
        return res;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    public String getPrevCountry() {
        return prevCountry;
    }

    public void setPrevCountry(String prevCountry) {
        this.prevCountry = prevCountry;
    }

    public String getPrevKeywordList() {
        return prevKeywordList;
    }

    public void setPrevKeywordList(String prevKeywordList) {
        this.prevKeywordList = prevKeywordList;
    }

    public Long getPrevAccountId() {
        return prevAccountId;
    }

    public void setPrevAccountId(Long prevAccountId) {
        this.prevAccountId = prevAccountId;
    }

    public boolean isSavedBefore() {
        return savedBefore;
    }

    public void setSavedBefore(boolean savedBefore) {
        this.savedBefore = savedBefore;
    }

    @Restrict(restriction = "DiscoverChannel.update")
    public String save() throws Exception {
        boolean isNewChannel = getModel().getId() == null;
        boolean isNewCountry = prevCountry != null
                && !EqualsUtil.equals(prevCountry, getModel().getCountry().getCountryCode());
        boolean isNewAccount = prevAccountId != null
                && !EqualsUtil.equals(prevAccountId, getAccountId());

        if (isNewCountry || isNewAccount) {
            setChannelsToLink(new ArrayList<IdNameVersionForm>());
            getModel().setKeywordList(prevKeywordList);
        }
        setPrevKeywordList(getModel().getKeywordList());

        if ((getModel().getBehavParamsList() != null) && (getModel().getBehavParamsList().getId() == null)) {
            getModel().setBehavParamsList(null);
        }
        try {
            List<DiscoverChannel> channelsToLink = populateChannelsToLink(getChannelsToLink());
            if (isNewChannel) {
                create(channelsToLink);
            } else {
                DiscoverChannelList oldChannelList = discoverChannelListService.view(getModel().getId());
                getModel().setFlags(oldChannelList.getFlags());

                for (IdNameVersionForm chVersion : getChannelsVersions()) {
                    DiscoverChannel dc = new DiscoverChannel();
                    dc.setId(Long.valueOf(chVersion.getId()));
                    dc.setVersion(new UITimestamp(chVersion.getVersion()));
                    getModel().getChildChannels().add(dc);
                }

                update(oldChannelList, channelsToLink);
            }
        } catch (EJBException e) {
            if (isNewChannel) {
                getModel().setId(null);
            }
        } catch (DiscoverChannelsAlreadyExistException ex) {
            processAlreadyExistingChannels(ex);
            invalid();
            savedBefore = true;
            return INPUT;
        } catch (BusinessException be) {
            for (PropertyError pe:  be.getPropertyErrors()) {
                addFieldError(pe.getName(), pe.getMessage());
            }
        }
    
        if (hasFieldErrors()) {
            invalid();
            return INPUT;
        }
        return SUCCESS;
    }

    private void create(List<DiscoverChannel> channelsToLink) {
        getModel().setAccount(AccountUtil.extractAccountById(getAccountId()));
        getModel().getAccount().unregisterChange("id");
        discoverChannelListService.create(getModel(), channelsToLink);
    }

    private void update(DiscoverChannelList oldChannelList, List<DiscoverChannel> channelsToLink) {
        getModel().setAccount(oldChannelList.getAccount());
        discoverChannelListService.update(getModel(), channelsToLink);
    }

    @Override
    public void invalid() throws Exception {
        populateDependenciesForSave();
        populateAlreadyExistingChannels();
        if (getModel().getId() == null) {
            getModel().setAccount(AccountUtil.extractAccountById(getAccountId()));
        } else {
            DiscoverChannelList oldChannel = discoverChannelListService.view(getModel().getId());
            getModel().setAccount(oldChannel.getAccount());
        }
    }

    private List<DiscoverChannel> populateChannelsToLink(List<IdNameVersionForm> channelsToLink) {
        List<DiscoverChannel> result = new ArrayList<DiscoverChannel>(channelsToLink.size());
        for (IdNameVersionForm channelToLink : channelsToLink) {
            DiscoverChannel dc = new DiscoverChannel();
            dc.setId(Long.valueOf(channelToLink.getId()));
            dc.setVersion(channelToLink.getVersion());
            result.add(dc);
        }
        return result;
    }

    private void processAlreadyExistingChannels(DiscoverChannelsAlreadyExistException ex) throws Exception {
        List<DiscoverChannel> existingChannels = new ArrayList<DiscoverChannel>();
        if (CollectionUtils.isEmpty(getChannelsToLink())) {
            existingChannels.addAll(ex.getExistingChannels());
        } else {
            for (IdNameVersionForm form : getChannelsToLink()) {
                DiscoverChannel existingChannel = new DiscoverChannel();
                existingChannel.setId(Long.valueOf(form.getId()));
                existingChannels.add(existingChannel);
            }
            for (PropertyError pe:  ex.getPropertyErrors()) {
                addFieldError("existingChannels", pe.getMessage());
            }
        }
        setExistingChannels(existingChannels);
        getModel().setKeywordList(ex.getUpdatedKeywordText());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs;
        if (model.getId() != null) {
            DiscoverChannelList persistent = discoverChannelListService.find(model.getId());
            breadcrumbs = new Breadcrumbs().add(new DiscoverChannelListsBreadcrumbsElement()).add(new DiscoverChannelListBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs = new Breadcrumbs().add(new DiscoverChannelListsBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
        }

        return breadcrumbs;
    }
}
