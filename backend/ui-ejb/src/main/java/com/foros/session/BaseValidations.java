package com.foros.session;

import com.foros.model.VersionEntityBase;
import com.foros.validation.ValidationContext;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.apache.commons.lang.ObjectUtils;

@LocalBean
@Stateless
public class BaseValidations {

    public void validateVersion(ValidationContext context, VersionEntityBase entity, VersionEntityBase existing) {
        if (existing == null) {
            return;
        }
        if (context.isReachable("version") && !ObjectUtils.equals(entity.getVersion(), existing.getVersion())) {
            context.addConstraintViolation("errors.version")
                .withValue(entity.getVersion())
                .withPath("version");
        }
    }

}