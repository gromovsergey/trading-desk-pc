package app.programmatic.ui.audienceresearch.validation;

import com.foros.rs.client.model.advertising.channel.ExpressionChannel;
import org.springframework.beans.factory.annotation.Autowired;
import app.programmatic.ui.account.dao.model.AccountEntity;
import app.programmatic.ui.account.dao.model.AccountRole;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.audienceresearch.dao.AudienceResearchRepository;
import app.programmatic.ui.audienceresearch.dao.model.AudienceResearch;
import app.programmatic.ui.audienceresearch.dao.model.AudienceResearchChannel;
import app.programmatic.ui.channel.dao.ChannelRepository;
import app.programmatic.ui.channel.dao.model.ChannelEntity;
import app.programmatic.ui.channel.service.ChannelService;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.common.validation.ValidateMethod;
import app.programmatic.ui.common.validation.strategy.CreateValidationStrategy;
import app.programmatic.ui.common.validation.strategy.NullForbiddenValidationStrategy;
import app.programmatic.ui.common.validation.strategy.UpdateValidationStrategy;
import app.programmatic.ui.common.validation.strategy.ValidationStrategy;

import javax.validation.*;
import java.util.HashSet;
import java.util.Set;

import static app.programmatic.ui.channel.dao.model.ChannelDisplayStatus.LIVE;
import static app.programmatic.ui.channel.dao.model.ChannelDisplayStatus.LIVE_CHANNELS_NEED_ATT;
import static app.programmatic.ui.channel.dao.model.ChannelDisplayStatus.LIVE_TRIGGERS_NEED_ATT;
import static app.programmatic.ui.common.validation.ValidateMethod.CREATE;

public class AudienceResearchValidationServiceImpl implements ConstraintValidator<ValidateAudienceResearch, Object> {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static final ValidationStrategy createValidationStrategy = new CreateValidationStrategy(new NullForbiddenValidationStrategy());
    private static final ValidationStrategy updateValidationStrategy = new UpdateValidationStrategy(new NullForbiddenValidationStrategy());

    @Autowired
    private AudienceResearchRepository audienceResearchRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private AccountService accountService;

    private ValidateMethod validateMethod;

    @Override
    public final void initialize(ValidateAudienceResearch constraintAnnotation) {
        validateMethod = constraintAnnotation.value();
    }

    @Override
    public final boolean isValid(Object value, ConstraintValidatorContext context) {
        return validate((AudienceResearch)value, context, validateMethod);
    }

    private boolean validate(AudienceResearch audienceResearch, ConstraintValidatorContext context, ValidateMethod validateMethod) {
        ConstraintViolationBuilder<AudienceResearch> builder = new ConstraintViolationBuilder<>();

        switch (validateMethod) {
            case CREATE:
                createValidationStrategy.checkBean(audienceResearch.getId(), builder, id -> audienceResearchRepository.findById(id).orElse(null));
                break;
            case UPDATE:
                updateValidationStrategy.checkBean(audienceResearch.getId(), builder, id -> audienceResearchRepository.findById(id).orElse(null));
                break;
        }

        if (validateMethod == CREATE) {
            validateTargetChannel(audienceResearch, builder.buildSubNode("targetChannel"));
        }

        validateChannels(audienceResearch, builder);
        validateAdvertisers(audienceResearch, builder);

        return builder.buildAndPushToContext(context).isValid();
    }

    private void validateTargetChannel(AudienceResearch audienceResearch, ConstraintViolationBuilder<ChannelEntity> builder) {
        if (audienceResearch.getTargetChannel() == null) {
            builder.addViolationDescription("", "entity.field.error.mandatory");
            return;
        }

        ChannelEntity targetChannel = updateValidationStrategy.checkBean(
                audienceResearch.getTargetChannel().getId(),
                builder,
                id -> channelRepository.findById(id).orElse(null)
        );

        if (targetChannel != null && targetChannel.getDisplayStatus() != LIVE &&
                targetChannel.getDisplayStatus() != LIVE_CHANNELS_NEED_ATT &&
                targetChannel.getDisplayStatus() != LIVE_TRIGGERS_NEED_ATT) {
            builder.addViolationDescription("", "audienceResearch.error.targetChannel.status");
        }
    }

    private void validateChannels(AudienceResearch audienceResearch, ConstraintViolationBuilder<AudienceResearch> builder) {
        ConstraintViolationBuilder<AudienceResearchChannel> channelBuilder = builder.buildSubNode("channels");

        if (audienceResearch.getChannels() == null || audienceResearch.getChannels().isEmpty()) {
            channelBuilder.addViolationDescription("", "entity.field.error.mandatory");
            return;
        }

        Set<Long> channelIds = new HashSet<>();
        for (AudienceResearchChannel audienceResearchChannel : audienceResearch.getChannels()) {
            if (audienceResearchChannel.getChannel() == null) {
                channelBuilder.addViolationDescription("", "entity.field.error.invalidValue");
                return;
            }

            ConstraintViolationBuilder<ExpressionChannel> expressionChannelBuilder = channelBuilder.buildSubNode("");
            ExpressionChannel expressionChannel = updateValidationStrategy.checkBean(
                    audienceResearchChannel.getChannel().getId(),
                    expressionChannelBuilder,
                    id -> channelService.findExpressionUnchecked(id)
            );
            
            if (expressionChannel != null) {
                ChannelEntity channelEntity = channelRepository.findById(expressionChannel.getId()).orElse(null);
                if (channelEntity.getDisplayStatus() != LIVE &&
                        channelEntity.getDisplayStatus() != LIVE_CHANNELS_NEED_ATT &&
                        channelEntity.getDisplayStatus() != LIVE_TRIGGERS_NEED_ATT) {
                    expressionChannelBuilder.addViolationDescription("", "audienceResearch.error.channel.status", channelEntity.getName());
                }
                
                for (String s : expressionChannel.getExpression().split("\\|")) {
                    try {
                        Long.parseLong(s);
                    } catch (NumberFormatException e) {
                        expressionChannelBuilder.addViolationDescription("", "audienceResearch.error.channels.expression", channelEntity.getName());
                    }
                }

                if (!channelIds.add(expressionChannel.getId())) {
                    expressionChannelBuilder.addViolationDescription("", "entity.field.error.duplicates");
                }

                if (expressionChannel.getId().equals(audienceResearch.getTargetChannel().getId())) {
                    expressionChannelBuilder.addViolationDescription("", "audienceResearch.error.channels.sameAsTarget");
                }
            }
        }
    }

    private void validateAdvertisers(AudienceResearch audienceResearch, ConstraintViolationBuilder<AudienceResearch> builder) {
        ConstraintViolationBuilder<AdvertisingAccount> advertisersBuilder = builder.buildSubNode("advertisers");

        if (audienceResearch.getAdvertisers() == null || audienceResearch.getAdvertisers().isEmpty()) {
            return;
        }

        Set<Long> advertiserIds = new HashSet<>();
        for (AccountEntity account : audienceResearch.getAdvertisers()) {
            AdvertisingAccount advertisingAccount = updateValidationStrategy.checkBean(
                    account.getId(),
                    advertisersBuilder,
                    id -> accountService.findAdvertisingUnchecked(id)
            );

            if (advertisingAccount.getRole() == AccountRole.ADVERTISER && advertisingAccount.getAgencyId() != null) {
                advertisersBuilder.addViolationDescription("id", "audienceResearch.error.advertisers.invalid", advertisingAccount.getName());
            }

            if (!advertiserIds.add(account.getId())) {
                advertisersBuilder.addViolationDescription("id", "entity.field.error.duplicates");
            }
        }
    }
}
