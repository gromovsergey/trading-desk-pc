package app.programmatic.ui.flight.dao.model.chart;

import java.util.ArrayList;
import java.util.List;

public class YAxis {
    private String label;
    private List<Number> points = new ArrayList<>();

    public YAxis(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public List<Number> getPoints() {
        return points;
    }

    public void addPoint(Number point) {
        this.points.add(point);
    }
}
