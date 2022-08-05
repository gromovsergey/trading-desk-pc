package app.programmatic.ui.common.aspect.prePersistProcessor;

public enum ServiceOperationType {
    CREATE(0, "Create"),
    UPDATE(1, "Update"),
    DELETE(7, "Delete");

    private Integer id;
    private String name;

    ServiceOperationType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
