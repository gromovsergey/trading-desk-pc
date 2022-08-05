package com.foros.session.channel;

import com.foros.model.Country;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.model.channel.BehavioralParametersListChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DiscoverChannel;
import com.foros.session.BeanValidations;
import com.foros.util.StringUtil;
import com.foros.util.TriggerUtil;
import com.foros.validation.ValidationContext;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
public class BaseDiscoverChannelValidations {

    @EJB
    private BeanValidations beanValidations;

    public void validateCountry(ValidationContext context, Channel channel) {
        if (!context.isReachable("country")) {
             return;
        }

        beanValidations.linkValidator(context, Country.class)
            .withPath("country")
            .withRequired(true)
            .validate(channel.getCountry());
    }

    public void validateBehavioralParameters(ValidationContext context, BehavioralParametersListChannel channel) {
        if (context.isReachable("behavParamsList")) {
            BehavioralParametersList behavParamsList = channel.getBehavParamsList();

            beanValidations.linkValidator(context, BehavioralParametersList.class)
                .withRequired(false)
                .withPath("behavParamsList")
                .validate(behavParamsList);
        }
    }

    public void validateDiscoverQuery(ValidationContext context, DiscoverChannel channel) {
        String query = channel.getDiscoverQuery();
        if (!context.isReachable("discoverQuery") || StringUtil.isPropertyEmpty(query)) {
            return;
        }

        if (TriggerUtil.NEWLINE_PATTERN.matcher(query).find()) {
            context.addConstraintViolation("errors.field.invalid")
                .withValue(query)
                .withPath("discoverQuery");
        }

    }
}
