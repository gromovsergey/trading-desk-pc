package com.foros.session.creative;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PreviewInfoTO implements Serializable {
    private List<String> errors;
    private Long width;
    private Long height;
    private String path;

    public void addError(String error) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(error);
    }

    public List<String> getErrors() {
        return errors;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
