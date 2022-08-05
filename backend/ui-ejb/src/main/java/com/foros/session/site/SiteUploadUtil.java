package com.foros.session.site;

import static com.foros.session.site.SiteUploadService.UPLOAD_CONTEXT;
import com.foros.model.EntityBase;
import com.foros.session.UploadStatus;
import com.foros.session.UploadContext;
import com.foros.validation.constraint.violation.ConstraintViolation;

import java.util.Set;


public class SiteUploadUtil {

    public static UploadContext getUploadContext(EntityBase entity) {
        UploadContext uploadContext = entity.getProperty(UPLOAD_CONTEXT);
        if (uploadContext == null) {
            uploadContext = new UploadContext();
            entity.setProperty(UPLOAD_CONTEXT, uploadContext);
        }        
        return uploadContext;
    }

    public static void checkErrors(EntityBase entity) {
        if (getUploadContext(entity).getStatus() == UploadStatus.REJECTED) {
            throw new IllegalStateException("Entity is rejected");
        }
    }

    public static void setErrors(EntityBase entity, Set<ConstraintViolation> constraintViolations) {
        getUploadContext(entity).addErrors(constraintViolations);
    }
}
