package com.foros.action.channel;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.breadcrumbs.SimpleTextBreadcrumbsElement;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.MailSendingFailedException;
import com.foros.session.channel.service.ChannelMessagingService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.util.RequestUtil;
import com.foros.util.StringUtil;

import com.foros.util.context.RequestContexts;
import org.apache.struts2.interceptor.ServletRequestAware;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

public class ContactChannelUsersAction extends BaseActionSupport implements ServletRequestAware, RequestContextsAware, BreadcrumbsSupport {
    @EJB
    SearchChannelService searchChannelService;

    @EJB
    ChannelMessagingService channelMessagingService;

    private HttpServletRequest httpRequest;
    private Long id;
    private String name;
    private String message;
    private boolean sendMeCopyFlag;
    private boolean messageSent = true;
    private Boolean messageSentToMe;
    private String pageTitle;
    private Channel channel;

    @ReadOnly
    @Restrict(restriction = "AdvertisingChannel.contactCMPChannelUser", parameters = "find('Channel', #target.id)")
    public String editMessage() throws Exception {
        Channel channel = getChannel();
        setName(channel.getName());
        setPageTitle(channel.getName());
        if (channelMessagingService.isMaxLimitReached(channel)) {
            return INPUT;
        }
        return SUCCESS;
    }

    public String sendMessage() throws Exception {
        try {
            channelMessagingService.sendMessage(getId(), getMessage(), RequestUtil.getBaseUrl(httpRequest), isSendMeCopyFlag());
            messageSentToMe = isSendMeCopyFlag();
        } catch (MailSendingFailedException e) {
            messageSent = false;
        }

        return SUCCESS;
    }

    @Override
    public void validate() {
        super.validate();
        if (StringUtil.isPropertyEmpty(getMessage())) {
            addFieldError("message", getText("errors.field.required"));
        }
    }

    private Channel getChannel() {
        if (channel == null) {
            channel = searchChannelService.view(getId());
        }

        return channel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSendMeCopyFlag() {
        return sendMeCopyFlag;
    }

    public void setSendMeCopyFlag(boolean sendMeCopyFlag) {
        this.sendMeCopyFlag = sendMeCopyFlag;
    }

    @Override
    public void setServletRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public boolean isMessageSent() {
        return messageSent;
    }

    public void setMessageSent(boolean messageSent) {
        this.messageSent = messageSent;
    }

    public Boolean getMessageSentToMe() {
        return messageSentToMe;
    }

    public void setMessageSentToMe(Boolean messageSentToMe) {
        this.messageSentToMe = messageSentToMe;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.switchTo(searchChannelService.find(getId()).getAccount());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Channel channel = getChannel();
        if (channel instanceof BehavioralChannel) {
            return new Breadcrumbs().add(new ChannelBreadcrumbsElement((BehavioralChannel) channel)).add(new SimpleTextBreadcrumbsElement("channel.breadcrumbs.sendMessage"));
        } else if (channel instanceof ExpressionChannel) {
            return new Breadcrumbs().add(new ChannelBreadcrumbsElement((ExpressionChannel) channel)).add(new SimpleTextBreadcrumbsElement("channel.breadcrumbs.sendMessage"));
        }

        return null;
    }
}
