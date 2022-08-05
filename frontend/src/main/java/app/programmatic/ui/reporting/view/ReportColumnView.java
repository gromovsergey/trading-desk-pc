package app.programmatic.ui.reporting.view;

import app.programmatic.ui.reporting.model.ReportColumnLocation;

public class ReportColumnView {
    private String id;
    private String name;
    private ReportColumnLocation location;

    public ReportColumnView(String id, String name, ReportColumnLocation location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ReportColumnLocation getLocation() {
        return location;
    }
}
