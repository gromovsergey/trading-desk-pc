package app.programmatic.ui.creative.dao.model;

public class CreativeSizeStat {
    private Long id;
    private String name;
    private Long width;
    private Long height;

    public CreativeSizeStat() {
    }

    public CreativeSizeStat(Long id, String name, Long width, Long height) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
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

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }
}
