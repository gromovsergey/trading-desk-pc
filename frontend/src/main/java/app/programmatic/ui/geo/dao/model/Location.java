package app.programmatic.ui.geo.dao.model;

public class Location {
    private Long id;
    private String name;

    public Location(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
