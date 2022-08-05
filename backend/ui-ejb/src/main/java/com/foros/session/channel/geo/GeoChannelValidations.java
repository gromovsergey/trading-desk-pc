package com.foros.session.channel.geo;

import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.Radius;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.bean.BeansValidationService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class GeoChannelValidations {
    @EJB
    private BeansValidationService beanValidationService;

    @Validation
    public void validateCreateAddress(ValidationContext context, @ValidateBean GeoChannel channel) {
        Radius radius = channel.getRadius();
        if (radius == null || radius.getDistance().scale() > 0) {
            context.addConstraintViolation("errors.field.maxFractionDigits")
                    .withPath("radius")
                    .withParameters(0);
        }

        if (radius == null) {
            return;
        }

        switch (radius.getRadiusUnit()) {
            case m :
            case yd :
            if (radius.getDistance().intValue() < 50 || radius.getDistance().intValue() > 50000) {
                    context.addConstraintViolation("errors.range")
                            .withPath("radius")
                            .withParameters("{channel.address.radius}")
                            .withParameters("50")
                            .withParameters("50000");
                }
                break;
            case km :
            if (radius.getDistance().intValue() < 1 || radius.getDistance().intValue() > 50) {
                    context.addConstraintViolation("errors.range")
                            .withPath("radius")
                            .withParameters("{channel.address.radius}")
                            .withParameters("1")
                            .withParameters("50");
                }
                break;
            case mi :
            if (radius.getDistance().intValue() < 1 || radius.getDistance().intValue() > 30) {
                    context.addConstraintViolation("errors.range")
                            .withPath("radius")
                            .withParameters("{channel.address.radius}")
                            .withParameters("1")
                            .withParameters("30");
                }
                break;
        }

    }

}
