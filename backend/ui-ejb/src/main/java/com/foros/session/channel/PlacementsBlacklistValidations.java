package com.foros.session.channel;

import com.foros.model.channel.placementsBlacklist.BlacklistAction;
import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;
import com.foros.session.UrlValidations;
import com.foros.session.creative.CreativeSizeService;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class PlacementsBlacklistValidations {
    private static final int MAX_URL_LENGTH = 255;

    @EJB
    private UrlValidations urlValidations;

    @EJB
    CreativeSizeService creativeSizeService;

    @Validation
    public void validateCreateOrDrop(ValidationContext context, PlacementBlacklist placement) {
        if (BlacklistAction.REMOVE == placement.getAction()) {
            validateDrop(context, placement);
        } else {
            validateCreate(context, placement);
        }
    }

    private void validateCreate(ValidationContext context, PlacementBlacklist placement) {
        if (placement.getId() != null) {
            context.addConstraintViolation("admin.placementsBlacklist.error.alreadyExist");
            return;
        }

        validateUrl(context, placement);

        if (placement.getSizeName() != null && creativeSizeService.findByName(placement.getSizeName()) == null) {
            context.addConstraintViolation("admin.placementsBlacklist.error.sizeNotFound")
                    .withPath("adSize")
                    .withValue(placement.getSizeName());
        }
    }

    private void validateDrop(ValidationContext context, PlacementBlacklist placement) {
        if (placement.getId() == null) {
            context.addConstraintViolation("admin.placementsBlacklist.error.notFound");
        }
    }

    private void validateUrl(ValidationContext context, PlacementBlacklist placement) {
        if (placement.getUrl() == null) {
            context.addConstraintViolation("errors.field.required")
                    .withPath("url");
            return;
        }

        urlValidations.validateUrl(context, placement.getUrl(), "url", false);
        if (placement.getUrl().length() > MAX_URL_LENGTH) {
            context.addConstraintViolation("errors.field.maxlength")
                    .withPath("url")
                    .withParameters(MAX_URL_LENGTH)
                    .withValue(placement.getUrl().length());
        }
    }
}
