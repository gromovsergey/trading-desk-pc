package app.programmatic.ui.creative.dao.model;

public class CreativeCategory {
    private Long id;
    private String name;

    public CreativeCategory() {
    }

    public CreativeCategory(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
