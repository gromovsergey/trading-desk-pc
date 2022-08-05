package com.foros.session.channel;

import com.foros.model.Country;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.campaign.RateType;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelNamespace;
import com.foros.model.channel.ChannelRate;
import com.foros.model.channel.ChannelVisibility;
import com.foros.security.AccountRole;
import com.foros.session.BaseValidations;
import com.foros.session.BeanValidations;
import com.foros.util.NumberUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.bean.BeansValidationService;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.constraint.validator.LinkValidator;
import com.foros.validation.constraint.validator.RangeValidator;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.ObjectUtils;

@LocalBean
@Stateless
public class AdvertisingChannelValidations extends CommonChannelValidations {

    @EJB
    private BeansValidationService beanValidationService;

    @EJB
    private BaseValidations baseValidations;

    @EJB
    private BeanValidations beanValidations;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    public void validate(ValidationContext context, Channel channel, Channel existing) {
        validateChannelName(context, channel);
        beanValidationService.validate(context);
        baseValidations.validateVersion(context, channel, existing);

        validateAccount(context, channel, existing);
        validateCountry(context, channel, existing);
        validateSupersededBy(context, channel, existing);
        validateRate(context, channel, existing == null ? ChannelVisibility.PRI : existing.getVisibility(), existing);
        validateVisibility(context, channel, existing);
        validateStatus(context, channel, existing);
    }

    private void validateStatus(ValidationContext context, Channel channel, Channel existing) {
        if (context.isReachable("status")) {
            if (existing != null && existing.getVisibility() == ChannelVisibility.CMP) {
                if (channel.getStatus() != Status.PENDING_INACTIVATION && channel.getStatus() != Status.ACTIVE) {
                    context.addConstraintViolation("channel.errors.invalidStatus")
                        .withPath("status")
                        .withValue(channel.getStatus());
                }
            } else {
                if (channel.getStatus() == Status.PENDING_INACTIVATION) {
                    context.addConstraintViolation("channel.errors.invalidStatus")
                        .withPath("status")
                        .withValue(channel.getStatus());
                }
            }
        }
    }

    private void validateVisibility(ValidationContext context, Channel channel, Channel existing) {
        if (!context.isReachable("visibility") || existing == null && !channel.isChanged("visibility")) {
            return;
        }

        AccountRole role;
        if (!context.props("account").reachableAndNoViolations()) {
            return;
        } else {
            role = em.find(Account.class, channel.getAccount().getId()).getRole();
        }

        boolean isAdvertisingRole = role == AccountRole.ADVERTISER || role == AccountRole.AGENCY;

        ChannelVisibility visibility = channel.getVisibility();
        ChannelVisibility existingVisibility = existing != null ? existing.getVisibility() : null;

        boolean visibilityInvalid = visibility == null || visibility == ChannelVisibility.CMP;
        visibilityInvalid = visibilityInvalid || isAdvertisingRole && visibility != ChannelVisibility.PRI;
        visibilityInvalid = visibilityInvalid || existingVisibility != null &&
            existingVisibility != visibility && (existingVisibility != ChannelVisibility.PRI || visibility != visibility.PUB);

        if (visibilityInvalid) {
            context.addConstraintViolation("channel.errors.invalidVisibility")
                    .withPath("visibility")
                    .withValue(channel.getVisibility());
        }
    }

    private void validateRate(ValidationContext context, Channel channel, ChannelVisibility visibility, Channel existing) {
        if (channel instanceof AudienceChannel || !context.isReachable("channelRate")) {
            return;
        }

        ChannelRate rate = channel.getChannelRate();

        if (existing == null) {
            // no rate allowed for new channels
            if (rate != null) {
                context.addConstraintViolation("errors.field.null")
                    .withPath("channelRate")
                    .withValue(rate);
            }
        } else {
            ChannelVisibility existingVisibility = existing.getVisibility();

            if (visibility == ChannelVisibility.CMP) {
                if (rate == null) {
                    context.addConstraintViolation("errors.field.required")
                        .withPath("channelRate")
                        .withValue(rate);
                }
            } else {
                if (rate != null) {
                    context.addConstraintViolation("errors.field.null")
                        .withPath("channelRate")
                        .withValue(rate);
                }
            }

            if (rate == null) {
                return;
            }

            context = context.createSubContext(channel.getChannelRate(), "channelRate");

            ChannelRate existingRate = existing.getChannelRate();

            if (rate.getRateType() != null) {
                Collection<RateType> allowedRates;
                if (existingVisibility != ChannelVisibility.CMP) {
                    allowedRates = ChannelRate.getAllowedTypes();
                } else {
                    // rate type can't be changed
                    allowedRates = Collections.singleton(existingRate.getRateType());
                }

                if (!allowedRates.contains(rate.getRateType())) {
                    context.addConstraintViolation("channel.errors.rateTypeIsNotAllowed")
                        .withPath("rateType")
                        .withValue(rate);
                }
            } else {
                context.addConstraintViolation("errors.field.required")
                    .withPath("rateType")
                    .withValue(rate);
            }

            // can't validate value with errors in type
            if (!context.hasViolations()) {
                if (rate.getRate() != null) {
                    int fractionDigits = existing.getAccount().getCurrency().getFractionDigits();
                    BigDecimal maxRate;
                    if (existingVisibility == ChannelVisibility.CMP) {
                        // can be decreased only
                        maxRate = existingRate.getRate();
                    } else {
                        maxRate = NumberUtil.subtractFraction(new BigDecimal("10000000"), fractionDigits);
                    }

                    context.validator(FractionDigitsValidator.class)
                        .withFraction(fractionDigits)
                        .withPath("value")
                        .validate(rate.getRate());

                    context.validator(RangeValidator.class)
                        .withMin(BigDecimal.ZERO, fractionDigits)
                        .withMax(maxRate)
                        .withPath("value")
                        .validate(rate.getRate());

                } else {
                    context.addConstraintViolation("errors.field.required")
                        .withPath("value")
                        .withValue(rate);
                }
            } else {
                context.setValidationIncomplete();
            }
        }
    }

    private void validateSupersededBy(ValidationContext context, Channel channel, Channel existing) {
        if (channel instanceof AudienceChannel || !context.isReachable("supersededByChannel")) {
            return;
        }

        if (existing == null) {
            if (channel.getSupersededByChannel() != null) {
                context.addConstraintViolation("channel.supersededByChannel.new")
                    .withPath("supersededByChannel")
                    .withValue(channel.getSupersededByChannel());
            }
        } else {
            LinkValidator<Channel> validator =
                    beanValidations.linkValidator(context, Channel.class)
                            .withCheckDeleted(existing.getSupersededByChannel())
                            .withRequired(false)
                            .withPath("supersededByChannel");

            validator
                    .validate(channel.getSupersededByChannel());

            Channel supersededBy = validator.getEntity();

            if (supersededBy != null) {
                if (!ObjectUtils.equals(existing.getAccount(), supersededBy.getAccount())) {
                    context.addConstraintViolation("channel.supersededByChannel.wrongAccount")
                        .withPath("supersededByChannel")
                        .withValue(channel.getSupersededByChannel());
                }

                if (!ObjectUtils.equals(existing.getCountry(), supersededBy.getCountry())) {
                    context.addConstraintViolation("channel.supersededByChannel.wrongCountry")
                        .withPath("supersededByChannel")
                        .withValue(channel.getSupersededByChannel());
                }

                if (ObjectUtils.equals(channel, supersededBy)) {
                    context.addConstraintViolation("channel.supersededByChannel.self")
                        .withPath("supersededByChannel")
                        .withValue(channel.getSupersededByChannel());
                }

                if (supersededBy.getNamespace() != ChannelNamespace.ADVERTISING) {
                    context.addConstraintViolation("channel.supersededByChannel.advertising")
                        .withPath("supersededByChannel")
                        .withValue(channel.getSupersededByChannel());
                }
            }
        }
    }

    private void validateAccount(ValidationContext context, Channel channel, Channel existing) {
        if (!context.isReachable("account")) {
            return;
        }

        if (existing == null) {
            Account account = channel.getAccount();

            beanValidations.linkValidator(context, Account.class)
                    .withPath("account")
                    .withRequired(true)
                    .validate(account);
        }
    }

    private void validateCountry(ValidationContext context, Channel channel, Channel existing) {
        if (!context.isReachable("country")) {
            return;
        }

        if (existing == null) {
            Country country = channel.getCountry();

            beanValidations.linkValidator(context, Country.class)
                    .withPath("country")
                    .withRequired(true)
                    .validate(country);

            if (!context.hasViolations()) {
                Account account = em.find(Account.class, channel.getAccount().getId());
                if (!account.isInternational() && !country.equals(account.getCountry())) {
                    context
                            .addConstraintViolation("errors.canNotChange")
                            .withParameters("{channel.country}", account.getCountry().getCountryCode())
                            .withPath("country");
                }
            }
        }
    }

    public void validateSubmitToCmp(ValidationContext context, Channel channel) {
        Channel existing = em.find(Channel.class, channel.getId());
        validateRate(context, channel, ChannelVisibility.CMP, existing);
    }
}