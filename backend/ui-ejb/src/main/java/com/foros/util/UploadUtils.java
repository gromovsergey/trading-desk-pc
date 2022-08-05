package com.foros.util;

import com.foros.model.EntityBase;
import com.foros.model.ExtensionProperty;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.validation.ValidationContext;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.interpolator.StringUtilsMessageInterpolator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UploadUtils {
    public static ExtensionProperty<UploadContext> UPLOAD_CONTEXT = new ExtensionProperty<UploadContext>(UploadContext.class);
    private static ExtensionProperty<Long> ROW_NUMBER = new ExtensionProperty<Long>(Long.class);

    public static UploadContext getUploadContext(EntityBase entity) {
        UploadContext uploadStatus = entity.getProperty(UPLOAD_CONTEXT);
        if (uploadStatus == null) {
            uploadStatus = new UploadContext();
            entity.setProperty(UPLOAD_CONTEXT, uploadStatus);
        }
        return uploadStatus;
    }

    public static Long getRowNumber(EntityBase entity) {
        return entity.getProperty(ROW_NUMBER);
    }

    public static void setRowNumber(EntityBase entity, Long rowNumber) {
        entity.setProperty(ROW_NUMBER, rowNumber);
    }

    public static boolean isLink(EntityBase entity) {
        UploadContext context = entity.getProperty(UPLOAD_CONTEXT);
        return context != null && context.getStatus() == UploadStatus.LINK;
    }

    public static boolean isLinkWithErrors(EntityBase entity) {
        UploadContext context = entity.getProperty(UPLOAD_CONTEXT);
        return context != null && context.getStatus() == UploadStatus.LINK && context.hasErrors();
    }

    public static void throwIfErrors(EntityBase entity) {
        if (entity.getProperty(UPLOAD_CONTEXT).getStatus() == UploadStatus.REJECTED) {
            throw new IllegalStateException("Entity is rejected");
        }
    }

    public static void setErrors(EntityBase entity, Collection<ConstraintViolation> constraintViolations) {
        getUploadContext(entity).addErrors(constraintViolations);
    }

    public static boolean hasFatalError(EntityBase entity) {
        UploadContext uploadStatus = entity.getProperty(UPLOAD_CONTEXT);
        if (uploadStatus == null) {
            return false;
        }
        return uploadStatus.isFatal();
    }

    public static void addConstraintViolations(ValidationContext context) {
        if (!(context.getBean() instanceof EntityBase)) {
            return;
        }

        UploadContext uploadStatus = ((EntityBase) context.getBean()).getProperty(UPLOAD_CONTEXT);
        if (uploadStatus == null) {
            return;
        }

        context.addConstraintViolations(uploadStatus.getErrors());
    }

    public static void addConstraintViolations(ValidationContext context, EntityBase entity) {
        UploadContext uploadStatus = getUploadContext(entity);
        if (uploadStatus == null) {
            return;
        }
        context.addConstraintViolations(uploadStatus.getErrors());
    }

    public static void mergeContext(UploadContext context, UploadContext context2) {
        StringUtilsMessageInterpolator interpolator =
                new StringUtilsMessageInterpolator(CurrentUserSettingsHolder.getLocale());
        context.flush(interpolator);
        context2.flush(interpolator);
        context.mergeStatus(context2.getStatus());
        List<ConstraintViolation> joined = new ArrayList<>(context2.getErrors().size());
        for (ConstraintViolation violation : context2.getErrors()) {
            boolean duplicated = false;
            for (ConstraintViolation v : context.getErrors()) {
                if (v.getMessage().equals(violation.getMessage()) &&
                        (v.getPropertyPath() == null && violation.getPropertyPath() == null ||
                                v.getPropertyPath() != null && v.getPropertyPath().equals(violation.getPropertyPath()))) {
                    duplicated = true;
                    break;
                }
            }
            if (!duplicated) {
                joined.add(violation);
            }
        }
        context.addErrors(joined);
    }
}
