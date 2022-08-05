package com.foros.model.site;

import com.foros.session.NamedTO;

import java.sql.Timestamp;

public class CategoryTO extends NamedTO {
    private Timestamp version;
    private boolean dependencyExists;

    public CategoryTO() {
    }

    public CategoryTO(Long id, String name, Timestamp version, boolean used) {
        super(id, name);
        this.version = version;
        this.dependencyExists = used;
    }

    public Timestamp getVersion() {
        return version;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }

    public boolean isDependencyExists() {
        return dependencyExists;
    }

    public void setDependencyExists(boolean dependencyExists) {
        this.dependencyExists = dependencyExists;
    }    
}
