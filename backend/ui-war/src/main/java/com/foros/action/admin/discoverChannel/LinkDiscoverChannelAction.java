package com.foros.action.admin.discoverChannel;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.Trim;
import com.foros.model.channel.DiscoverChannel;
import com.foros.session.channel.service.DiscoverChannelListService;
import com.foros.session.channel.service.DiscoverChannelService;
import com.foros.session.channel.service.DiscoverChannelUtils;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;

@Trim(exclude = {"singleBaseKeyword"})
public class LinkDiscoverChannelAction extends BaseActionSupport {

    public static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add(
                    "channelsToLink[(#index)].keywords[(#index)]",
                    "'keywords'",
                    "channelToLinkError(groups[0], 'keywords', violation.message)"
            )
            .add(
                    "channelsToLink[(#index)].baseKeyword",
                    "'keywords'",
                    "channelToLinkError(groups[0], violation.message)"
            )
            .add(
                    "channelsToLink[(#index)].(#property)",
                    "'keywords'",
                    "channelToLinkError(groups[0], groups[1], violation.message)"
            )
            .add(
                    "channelsToLink[(#index)]",
                    "'keywords'",
                    "channelToLinkError(groups[0], violation.message)"
            )
            .add(
                    "childChannels[(#index)]",
                    "'keywords'",
                    "channelToLinkError(groups[0], violation.message)"
            )
            .rules();

    private Long discoverChannelListId;
    private List<DiscoverChannel> discoverChannels;
    private boolean fromDiscoverChannelList;
    private String singleBaseKeyword;

    @EJB
    private DiscoverChannelService discoverChannelService;
    @EJB
    private DiscoverChannelListService discoverChannelListService;

    @ReadOnly
    public String edit() {
        if (isSingle()) {
            DiscoverChannel discoverChannel = getFirstDiscoverChannel();
            if (isLinked(discoverChannel)) {
                setSingleBaseKeyword(discoverChannel.getBaseKeyword());
            } else {
                setSingleBaseKeyword(DiscoverChannelUtils.getKeywordText(discoverChannel));
            }
        }
        return SUCCESS;
    }

    private boolean isLinked(DiscoverChannel discoverChannel) {
        return discoverChannel.getChannelList() != null;
    }

    @Validations(
            conversionErrorFields = {
                    @ConversionErrorFieldValidator(key = "errors.discoverList.notFound", fieldName = "discoverChannelListId", shortCircuit = true)
            },
            requiredFields = {
                    @RequiredFieldValidator(key = "errors.discoverList.notFound", fieldName = "discoverChannelListId")
            }
    )
    public String save() throws Exception {
        if(isSingle()){
            discoverChannelListService.link(discoverChannelListId, discoverChannels.get(0), singleBaseKeyword);
        } else {
            discoverChannelListService.link(discoverChannelListId, discoverChannels);
        }
        return SUCCESS;
    }

    public String getSuccessLocation() {
        return fromDiscoverChannelList ?
               "view.action?id=" + getDiscoverChannelListId() :
               "view.action?id=" + getFirstDiscoverChannel().getId();
    }

    public Long getDiscoverChannelListId() {
        return discoverChannelListId;
    }

    public void setDiscoverChannelListId(Long discoverChannelListId) {
        this.discoverChannelListId = discoverChannelListId;
    }

    public List<DiscoverChannel> getDiscoverChannels() {
        return discoverChannels;
    }

    public void setDiscoverChannels(List<DiscoverChannel> discoverChannels) {
        this.discoverChannels = discoverChannels;
    }

    public boolean isSingle() {
        return getDiscoverChannels() != null && getDiscoverChannels().size() == 1;
    }

    private DiscoverChannel getFirstDiscoverChannel() {
        DiscoverChannel existingChannel = discoverChannelService.view(discoverChannels.get(0).getId());
        return existingChannel;
    }

    public boolean isFromDiscoverChannelList() {
        return fromDiscoverChannelList;
    }

    public void setFromDiscoverChannelList(boolean fromDiscoverChannelList) {
        this.fromDiscoverChannelList = fromDiscoverChannelList;
    }

    public String getSingleBaseKeyword() {
        return singleBaseKeyword;
    }

    public void setSingleBaseKeyword(String singleBaseKeyword) {
        this.singleBaseKeyword = singleBaseKeyword;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    public String channelToLinkError(int index, String message) {
        if (isSingle()) {
            return message;
        } else {
            String name = discoverChannels.get(index).getName();
            return getText("DiscoverChannelList.errors.channelToLinkError", Arrays.asList(name, message));
        }
    }

    public String channelToLinkError(int index, String property, String message) {
        String propertyName = getText("channel." + property);
        if (isSingle()) {
            return getText("DiscoverChannel.errors.fieldError", Arrays.asList(propertyName, message));
        } else {
            String name = discoverChannels.get(index).getName();
            return getText("DiscoverChannelList.errors.channelToLinkFieldError", Arrays.asList(name, propertyName, message));
        }
    }
}
