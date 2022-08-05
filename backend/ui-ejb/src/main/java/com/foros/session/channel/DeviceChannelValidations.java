package com.foros.session.channel;

import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.Platform;
import com.foros.session.BaseValidations;
import com.phorm.oix.util.expression.CDMLParsingError;
import com.foros.util.expression.ExpressionHelper;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import java.io.IOException;
import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Validations
public class DeviceChannelValidations extends CommonChannelValidations {

    @EJB
    private BaseValidations baseValidations;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE)  DeviceChannel channel) {
        validate(context, channel, null);
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) DeviceChannel channel) {
        DeviceChannel existing = em.find(DeviceChannel.class, channel.getId());
        if (!existing.getName().equals(channel.getName()) && isRootChannel(existing)) {
            context.addConstraintViolation("ccg.error.deviceTargeting.nameChangeNotAllowed")
                .withParameters(existing.getName())
                .withPath("name");
        }
        validate(context, channel, existing);
    }

    private void validate(ValidationContext context, DeviceChannel channel, DeviceChannel existing) {
        validateChannelName(context, channel);
        baseValidations.validateVersion(context, channel, existing);
        validateExpression(context, channel, existing);
    }

    private void validateExpression(ValidationContext context, DeviceChannel channel, DeviceChannel existing) {
        if (!context.isReachable("expression") || context.hasViolation("expression") || channel.getExpression() == null) {
            return;
        }

        String expression = channel.getExpression();
        Collection<Long> platformIds;

        try {
            platformIds = ExpressionHelper.parseIds(expression);
        } catch (CDMLParsingError ex) {
            context.addConstraintViolation("errors.wrong.cdml")
                    .withPath("expression")
                    .withValue(expression);
            return;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        for (Long id : platformIds) {
            Platform platform = em.find(Platform.class, id);
            if (platform == null) {
                context.addConstraintViolation("errors.platformNotFound")
                        .withParameters(id)
                        .withPath("expression")
                        .withValue(expression);
                continue;
            }
        }

    }

    private boolean isRootChannel(final DeviceChannel channel) {
        return channel.isApplications() ||
                channel.isBrowsers() ||
                channel.isMobilesChannel() ||
                channel.isNonMobilesChannel();
    }
}
