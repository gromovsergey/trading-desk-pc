package com.foros.session.creative;

import com.foros.model.creative.CreativeCategoryType;
import com.foros.validation.constraint.RequiredConstraint;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CreativeCategoryEditTO {

    @RequiredConstraint
    private Timestamp version;

    private List<CreativeCategoryTO> categories = new ArrayList<CreativeCategoryTO>();

    @RequiredConstraint
    private CreativeCategoryType type;

    public Timestamp getVersion() {
        return version;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }

    public List<CreativeCategoryTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CreativeCategoryTO> categories) {
        this.categories = categories;
    }

    public CreativeCategoryType getType() {
        return type;
    }

    public void setType(CreativeCategoryType type) {
        this.type = type;
    }
}
