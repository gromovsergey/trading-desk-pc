package com.foros.session;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationBuilder;
import com.foros.validation.constraint.violation.Path;
import com.foros.validation.interpolator.MessageInterpolator;
import com.foros.validation.interpolator.StringUtilsMessageInterpolator;
import com.foros.validation.util.ValidationUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class UploadContext implements Serializable {

    private UploadStatus status;
    private boolean fatal = false;
    private List<ConstraintViolation> errors = new LinkedList<ConstraintViolation>();
    private transient List<ConstraintViolationBuilder> builders = new LinkedList<ConstraintViolationBuilder>();

    public UploadContext() {
    }

    public UploadContext(UploadStatus status) {
        this.status = status;
    }

    public UploadStatus getStatus() {
        return status;
    }

    public boolean isFatal() {
        return fatal;
    }

    public void setFatal() {
        fatal = true;
    }

    public ConstraintViolationBuilder addFatal(String key) {
        setFatal();
        return addError(key);
    }

    public void addFatal(ConstraintViolation error) {
        setFatal();
        addError(error);
    }

    public ConstraintViolationBuilder addError(String key) {
        return addError(key, new Object[]{});
    }

    public ConstraintViolationBuilder addError(String key, Object ... params) {
        if (builders == null) {
            builders = new LinkedList<ConstraintViolationBuilder>();
        }

        mergeStatus(UploadStatus.REJECTED);
        ConstraintViolationBuilder builder = new ConstraintViolationBuilder(key, Path.empty());
        if (params != null && params.length > 0){
            builder = builder.withParameters(params);
        }
        builders.add(builder);
        return builder;
    }

    public void addError(ConstraintViolation error) {
        mergeStatus(UploadStatus.REJECTED);
        errors.add(error);
    }

    public void addErrors(Collection<ConstraintViolation> constraintViolations) {
        checkFlushed();
        if (constraintViolations == null || constraintViolations.isEmpty()) {
            return;
        }

        mergeStatus(UploadStatus.REJECTED);
        errors.addAll(constraintViolations);
    }

    public void mergeStatus(UploadStatus status) {
        if (this.status != UploadStatus.REJECTED && this.status != UploadStatus.LINK) {
            this.status = status;
        }
    }

    public Collection<String> getWrongPaths() {
        checkFlushed();
        Set<String> wrongs = new HashSet<String>();
        for (ConstraintViolation error : errors) {
            if (error.getPropertyPath() != null) {
                wrongs.add(error.getPropertyPath().toString());
            }
        }
        return wrongs;
    }

    public List<ConstraintViolation> getErrors() {
        checkFlushed();
        return Collections.unmodifiableList(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty() || (builders != null && !builders.isEmpty());
    }

    public void flush(MessageInterpolator interpolator) {
        if (builders == null) {
            return;
        }

        for (ConstraintViolationBuilder builder : builders) {
            errors.add(builder.build(ValidationUtil.getDefaultCodesResolver(), interpolator));
        }
        builders.clear();
    }

    private void checkFlushed() {
        if (builders != null && !builders.isEmpty()) {
            // flush with default interpolator
            flush(new StringUtilsMessageInterpolator(CurrentUserSettingsHolder.getLocale()));
        }
    }
}
