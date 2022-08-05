package com.foros.session.channel;

import com.foros.AbstractValidationsTest;
import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.campaign.RateType;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.ExpressionChannel;
import com.foros.session.channel.service.ExpressionChannelService;
import com.foros.test.factory.BehavioralChannelTestFactory;
import com.foros.test.factory.CmpAccountTestFactory;
import com.foros.test.factory.ExpressionChannelTestFactory;
import com.foros.test.factory.InternalAccountTestFactory;

import java.math.BigDecimal;

import group.Db;
import group.Validation;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class ExpressionChannelValidationsTest extends AbstractValidationsTest {

    @Autowired
    private ExpressionChannelTestFactory expressionChannelTF;

    @Autowired
    private BehavioralChannelTestFactory behavioralChannelTF;

    @Autowired
    private InternalAccountTestFactory internalAccountTF;

    @Autowired
    private CmpAccountTestFactory cmpAccountTF;

    @Autowired
    private ExpressionChannelService expressionChannelService;

    @Test
    public void testValidateExpression() {
        ExpressionChannel channel = expressionChannelTF.createPersistent();
        channel.setDisplayStatus(Channel.LIVE);
        expressionChannelTF.update(channel);

        ExpressionChannel usedChannelWithCycle = expressionChannelTF.createPersistent(channel.getAccount());
        usedChannelWithCycle.setDisplayStatus(Channel.LIVE);
        usedChannelWithCycle.setExpression(channel.getId().toString());
        expressionChannelTF.update(usedChannelWithCycle);

        BehavioralChannel wrongUsedChannel = behavioralChannelTF.createPersistent();

        BehavioralChannel channelNotLive = behavioralChannelTF.createPersistent();
        behavioralChannelTF.inactivate(channelNotLive);

        clearContext();

        BehavioralChannel usedChannel = behavioralChannelTF.createPersistent(channel.getAccount());
        usedChannel.setDisplayStatus(Channel.LIVE);
        behavioralChannelTF.update(usedChannel);

        // null expression
        channel.setExpression(null);
        validateExpressionUpdate(channel, 1);

        // empty expression
        channel.setExpression("");
        validateExpressionUpdate(channel, 1);

        // too long expression
        String expression = StringUtils.repeat(usedChannel.getId().toString() + "|", 500);
        channel.setExpression(expression.substring(0, expression.length() - 1));
        validateExpressionUpdate(channel, 1);

        // wrong expression
        channel.setExpression("aaa|bbb^ccc");
        validateExpressionUpdate(channel, 1);

        channel.setExpression(usedChannel.getId().toString() + "|");
        validateExpressionUpdate(channel, 1);

        // self link loop
        channel.setExpression(channel.getId().toString());
        validateExpressionUpdate(channel, 1);

        // channel not found
        channel.setExpression("0");
        validateExpressionUpdate(channel, 1);

        // invalid channel
        channel.setExpression(wrongUsedChannel.getId().toString());
        validateExpressionUpdate(channel, 1);

        // not-live channel of another account
        channel.setExpression(channelNotLive.getId().toString());
        validateExpressionUpdate(channel, 1);

        // has cycle
        channel.setExpression(usedChannelWithCycle.getId().toString());
        validateExpressionUpdate(channel, 1);

        // live pending from same account
        usedChannel.setDisplayStatus(Channel.LIVE_PENDING_INACTIVATION);
        channel.setExpression(usedChannel.getId().toString());
        validateExpressionUpdate(channel, 1);

        // good
        usedChannel.setDisplayStatus(Channel.LIVE);
        channel.setExpression(usedChannel.getId().toString());
        validateExpressionUpdate(channel, 0);
    }

    @Test
    public void testValidateExpressionInCreate() {
        ExpressionChannel channel = expressionChannelTF.create();
        channel.setDisplayStatus(Channel.LIVE);

        BehavioralChannel wrongUsedChannel = behavioralChannelTF.createPersistent();

        BehavioralChannel channelNotLive = behavioralChannelTF.createPersistent();
        behavioralChannelTF.inactivate(channelNotLive);

        clearContext();

        BehavioralChannel usedChannel = behavioralChannelTF.createPersistent(channel.getAccount());
        usedChannel.setDisplayStatus(Channel.LIVE);
        behavioralChannelTF.update(usedChannel);

        // null expression
        channel.setExpression(null);
        validateExpressionCreate(channel, 1);

        // empty expression
        channel.setExpression("");
        validateExpressionCreate(channel, 1);

        // too long expression
        String expression = StringUtils.repeat(usedChannel.getId().toString() + "|", 500);
        channel.setExpression(expression.substring(0, expression.length() - 1));
        validateExpressionCreate(channel, 1);

        // wrong expression
        channel.setExpression("aaa|bbb^ccc");
        validateExpressionCreate(channel, 1);

        channel.setExpression(usedChannel.getId().toString() + "|");
        validateExpressionCreate(channel, 1);

        // channel not found
        channel.setExpression("0");
        validateExpressionCreate(channel, 1);

        // invalid channel
        channel.setExpression(wrongUsedChannel.getId().toString());
        validateExpressionCreate(channel, 1);

        // not-live channel of another account
        channel.setExpression(channelNotLive.getId().toString());
        validateExpressionCreate(channel, 1);

        // live pending from same account
        usedChannel.setDisplayStatus(Channel.LIVE_PENDING_INACTIVATION);
        channel.setExpression(usedChannel.getId().toString());
        validateExpressionCreate(channel, 1);

        // good
        usedChannel.setDisplayStatus(Channel.LIVE);
        channel.setExpression(usedChannel.getId().toString());
        validateExpressionCreate(channel, 0);
    }

    @Test
    public void testExpressionChannelWithExpressions() throws Exception {
        //Internal
        InternalAccount account = internalAccountTF.createPersistent();
        ExpressionChannel internalChannel = expressionChannelTF.createPersistent(account);
        updateChannelWithExp(internalChannel, ChannelVisibility.PUB, false, false);
        updateChannelWithExp(internalChannel, ChannelVisibility.PRI, true, false);
        updateChannelWithExp(internalChannel, ChannelVisibility.PRI, false, true);
        updateChannelWithExp(internalChannel, ChannelVisibility.CMP, true, false);

        //Advertiser
        ExpressionChannel advertiserChannel = expressionChannelTF.createPersistent();
        updateChannelWithExp(advertiserChannel, ChannelVisibility.PUB, false, false);
        updateChannelWithExp(advertiserChannel, ChannelVisibility.PRI, true, false);
        updateChannelWithExp(advertiserChannel, ChannelVisibility.PRI, false, true);
        updateChannelWithExp(advertiserChannel, ChannelVisibility.CMP, false, false);

        //CMP
        CmpAccount cmpAccount = cmpAccountTF.createPersistent();
        ExpressionChannel cmpChannel = getPublicChannel(cmpAccount);
        updateChannelWithExp(cmpChannel, ChannelVisibility.PUB, false, false);
        updateChannelWithExp(cmpChannel, ChannelVisibility.CMP, true, false);
        updateChannelWithExp(cmpChannel, ChannelVisibility.PRI, true, false);
    }

    private void updateChannelWithExp(ExpressionChannel existing, ChannelVisibility visibility, boolean fail, boolean isSameAccount) throws Exception {
        ExpressionChannel channel = new ExpressionChannel();
        channel.setId(existing.getId());
        ExpressionChannel used = createChannelWithVisibility(visibility, isSameAccount ? existing.getAccount() : null);
        channel.setExpression(used.getId().toString());
        validate("ExpressionChannel.update", channel);
        if (fail) {
            assertViolationsCount(1);
            assertHasViolation("expression");
        } else {
            assertViolationsCount(0);
        }
    }

    private ExpressionChannel getPublicChannel(CmpAccount cmpAccount) throws Exception {
        ExpressionChannel cmpChannelPub = expressionChannelTF.createPersistent(cmpAccount);
        expressionChannelService.makePublic(cmpChannelPub.getId(), cmpChannelPub.getVersion());
        return cmpChannelPub;
    }

    private ExpressionChannel createChannelWithVisibility(ChannelVisibility visibility, Account sameAccount) throws Exception {
        if (sameAccount == null) {
            sameAccount = cmpAccountTF.createPersistent();
        }

        ExpressionChannel channel = expressionChannelTF.createPersistent(sameAccount);
        channel.setDisplayStatus(Channel.LIVE);

        if (visibility == ChannelVisibility.PUB) {
            expressionChannelService.makePublic(channel.getId(), channel.getVersion());
        } else if (visibility == ChannelVisibility.CMP) {
            channel.setStatus(Status.ACTIVE);
            channel.setQaStatus(ApproveStatus.APPROVED);
            channel.setChannelRate(expressionChannelTF.createChannelRate(channel, RateType.CPC, new BigDecimal(1)));
            expressionChannelService.submitToCmp(channel);
        }

        return channel;
    }

    private void validateExpression(String validationMethod, Channel channel, int violationsCount) {
        validate(validationMethod, channel);
        assertViolationsCount(violationsCount);
        if (violationsCount > 0) {
            assertHasViolation("expression");
        }
    }

    private void validateExpressionCreate(Channel channel, int violationsCount) {
        validateExpression("ExpressionChannel.create", channel, violationsCount);
    }

    private void validateExpressionUpdate(Channel channel, int violationsCount) {
        validateExpression("ExpressionChannel.update", channel, violationsCount);
    }
}
