package app.programmatic.ui.changetrack.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "changestracker.changes")
public class ChangeTrack {

    @Id // It is not true, but we must complement JPA spec
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "table_name", updatable = false, nullable = false)
    private String tableName;

    public ChangeTrack() {
    }

    public ChangeTrack(Long id, String tableName) {
        this.id = id;
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
