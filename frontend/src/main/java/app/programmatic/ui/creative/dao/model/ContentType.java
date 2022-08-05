package app.programmatic.ui.creative.dao.model;

public class ContentType {
    private int id;
    private String name;

    public ContentType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
