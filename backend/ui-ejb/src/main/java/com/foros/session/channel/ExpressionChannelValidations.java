package com.foros.session.channel;

import com.foros.model.Country;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.ExpressionChannel;
import com.foros.session.channel.service.ChannelUtils;
import com.phorm.oix.util.expression.CDMLParsingError;
import com.foros.util.expression.ExpressionHelper;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang.ObjectUtils;

@LocalBean
@Stateless
@Validations
public class ExpressionChannelValidations {

    @EJB
    private AdvertisingChannelValidations advertisingChannelValidations;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Validation
    public void validateCreate(ValidationContext context, ExpressionChannel channel) {
        validate(context.subContext(channel)
                .withMode(ValidationMode.CREATE)
                .build(), channel, null);
    }

    @Validation
    public void validateUpdate(ValidationContext context, ExpressionChannel channel) {
        ExpressionChannel existing = em.find(ExpressionChannel.class, channel.getId());
        validate(context
                .subContext(channel)
                .withMode(ValidationMode.UPDATE)
                .build(),
                channel, existing);
    }

    private void validate(ValidationContext context, ExpressionChannel channel, ExpressionChannel existing) {
        advertisingChannelValidations.validate(context, channel, existing);
        if (existing == null || existing.getVisibility() != ChannelVisibility.CMP) {
            validateExpression(context, channel, existing);
        } else if (channel.isChanged("expression")) {
            if (existing.getExpression() != null && !existing.getExpression().equalsIgnoreCase(channel.getExpression())) {
                context.addConstraintViolation("errors.field.canNotChange")
                        .withPath("expression");
            } else {
                channel.unregisterChange("expression");
            }
        }
    }

    private void validateExpression(ValidationContext context, ExpressionChannel channel, ExpressionChannel existing) {
        if (!context.isReachable("expression") || channel.getExpression() == null) {
            return;
        }

        String expression = channel.getExpression();
        Collection<Long> usedChannelsIds;

        try {
            usedChannelsIds = ExpressionHelper.parseIds(expression);
        } catch (CDMLParsingError ex) {
            context.addConstraintViolation("errors.wrong.cdml")
                    .withPath("expression")
                    .withValue(expression);
            return;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        if (existing != null) {
            if (usedChannelsIds.contains(existing.getId())) {
                context.addConstraintViolation("errors.expression.self.link.loop")
                        .withParameters(existing.getName())
                        .withPath("expression")
                        .withValue(expression);
                usedChannelsIds.remove(existing.getId());
            }
        }

        Set<Channel> usedChannels = new LinkedHashSet<Channel>();
        for (Long id : usedChannelsIds) {
            Channel ch = em.find(Channel.class, id);
            if (ch == null) {
                context.addConstraintViolation("errors.channelNotFound")
                        .withParameters(id)
                        .withPath("expression")
                        .withValue(expression);
                continue;
            }
            usedChannels.add(ch);
        }

        if (!context.props("expression", "account", "country").haveViolations() &&
                (existing == null || !expression.equals(existing.getExpression()))) {
            Account account = existing != null ? existing.getAccount() : em.find(Account.class, channel.getAccount().getId());
            Country country = existing != null ? existing.getCountry() : channel.getCountry();
            for (Channel usedChannel : usedChannels) {
                ValidationContext expressionContext = context.createSubContext(expression, "expression");
                validateChannel(expressionContext, account, country, usedChannel, "expression");
            }
        }

        checkCycleExpression(context, channel, usedChannels, new LinkedList<ExpressionChannel>());

    }

    public void validateChannel(ValidationContext context, Account account, Country country, Channel channel, String msg) {
        if (!(channel instanceof ExpressionChannel || channel instanceof BehavioralChannel || channel instanceof AudienceChannel)) {
            addExpressionViolation(context, channel, "errors." + msg + ".wrongType");
            return;
        }

        if (!ChannelUtils.isVisible(channel, account)) {
            addExpressionViolation(context, channel, "errors." + msg + ".wrongVisibility." + account.getRole().toString().toLowerCase());
            return;
        }

        if (account.equals(channel.getAccount())) {
            if (channel.getStatus() == Status.DELETED) {
                addExpressionViolation(context, channel, "errors." + msg +".deleted");
            }
            if (channel.getStatus() == Status.INACTIVE) {
                addExpressionViolation(context, channel, "errors." + msg +".inactive");
            }
            if (channel.getDisplayStatus() == Channel.LIVE_PENDING_INACTIVATION || channel.getDisplayStatus() == Channel.LIVE_AMBER_PENDING_INACTIVATION) {
                addExpressionViolation(context, channel, "errors." + msg +".livePendingInactivation");
            }
        } else {
            if (channel.getDisplayStatus() == Channel.LIVE_PENDING_INACTIVATION || channel.getDisplayStatus() == Channel.LIVE_AMBER_PENDING_INACTIVATION) {
                addExpressionViolation(context, channel, "errors." + msg +".livePendingInactivation");
            } else if (channel.getDisplayStatus() != Channel.LIVE && channel.getDisplayStatus() != Channel.LIVE_CHANNELS_NEED_ATT && channel.getDisplayStatus() != Channel.LIVE_TRIGGERS_NEED_ATT) {
                addExpressionViolation(context, channel, "errors.expression.notLive");
            }
        }

        if (!channel.getCountry().equals(country)) {
            addExpressionViolation(context, channel, "errors.expression.wrongCountry");
        }
    }

    private void addExpressionViolation(ValidationContext context, Channel channel, String template) {
        context.addConstraintViolation(template)
            .withParameters(channel.getAccount().getName() + "|" + (channel.getName()))
            .withValue(context.getBean());
    }

    private void checkCycleExpression(ValidationContext context, ExpressionChannel channel, Set<Channel> usedChannels, List<ExpressionChannel> path) {
        path.add(channel);

        for (Channel used : usedChannels) {
            // if used channel is in path then cycle detected
            for (Channel fromPath : path) {
                if (ObjectUtils.equals(used.getId(), fromPath.getId())) {
                    StringBuilder errDetails = new StringBuilder();
                    for (Channel ch : path) {
                        errDetails.append(ch.getName()).append(", ");
                    }
                    errDetails.append(used.getName());

                    context.addConstraintViolation("errors.expression.cycle")
                            .withParameters(errDetails)
                            .withPath("expression")
                            .withValue(channel.getExpression());
                }
            }
            if (used instanceof ExpressionChannel) {
                // check used expression channel
                ExpressionChannel expression = (ExpressionChannel) used;
                List<ExpressionChannel> pathCopy = new LinkedList<ExpressionChannel>(path);
                checkCycleExpression(context, expression, expression.getUsedChannels(), pathCopy);
            }
        }
    }

    @Validation
    public void validateSubmitToCmp(ValidationContext context, ExpressionChannel channel) {
        advertisingChannelValidations.validateSubmitToCmp(context, channel);
    }
}
