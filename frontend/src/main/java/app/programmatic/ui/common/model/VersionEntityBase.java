package app.programmatic.ui.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class VersionEntityBase<T> extends EntityBase<T> {
    @Column(name = "VERSION")
    @Version
    private Timestamp version;

    @JsonIgnore
    public Timestamp getVersion() {
        return version;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }
}
