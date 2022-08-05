package app.programmatic.ui.creative.dao.model;

public class FileUrl {
    private String url;
    private String name;

    public FileUrl(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }
}
