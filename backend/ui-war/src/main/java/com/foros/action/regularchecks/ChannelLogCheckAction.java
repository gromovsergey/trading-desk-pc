package com.foros.action.regularchecks;

import com.foros.framework.ReadOnly;
import com.foros.model.account.Account;
import com.foros.model.account.GenericAccount;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.channel.GenericChannel;
import com.foros.model.security.AccountType;
import com.foros.session.channel.service.BehavioralChannelService;
import com.foros.session.channel.service.ExpressionChannelService;
import com.foros.session.regularchecks.RegularReviewService;
import com.foros.util.EntityUtils;

import java.util.SortedMap;
import javax.ejb.EJB;

public class ChannelLogCheckAction extends LogChecksAction<Channel> {

    @EJB
    private RegularReviewService regularReviewService;

    @EJB
    private BehavioralChannelService behavioralChannelService;

    @EJB
    private ExpressionChannelService expressionChannelService;

    private Channel model = new GenericChannel();


    @ReadOnly
    public String edit() {
        if ("BehavioralChannel".equals(getEntityName())) {
            model = behavioralChannelService.view(getEntityId());
        } else {
            model = expressionChannelService.view(getEntityId());
        }
        return SUCCESS;
    }

    public String updateCheck() throws Exception {
        Channel channel;
        if ("BehavioralChannel".equals(getEntityName())) {
            channel = new BehavioralChannel();
        } else {
            channel = new ExpressionChannel();
        }
        EntityUtils.copy(channel, getModel());
        this.model = channel;
        regularReviewService.updateChannelCheck(channel);
        return SUCCESS;
    }

    @Override
    public Channel getModel() {
        return model;
    }

    protected SortedMap<Integer, String> getAvailableIntervals(AccountType at, Integer lastCheckInterval) {
        return getAvailableIntervals(at.getChannelFirstCheck(), at.getChannelSecondCheck(), at.getChannelThirdCheck(), lastCheckInterval);
    }

    public Account getAccount() {
        Account result = model.getAccount();
        if (result == null) {
            result = new GenericAccount();
            model.setAccount(result);
        }
        return result;
    }
}
