package app.programmatic.ui.flight.dao.model.chart;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartData {
    private final List<Timestamp> x;
    private final YAxis[] y;

    public ChartData(String... graphLabels) {
        x = new ArrayList<>();

        y = new YAxis[graphLabels.length];
        for (int i = 0; i < graphLabels.length; i++) {
            y[i] = new YAxis(graphLabels[i]);
        }
    }

    public List<Timestamp> getX() {
        return x;
    }

    public List<YAxis> getY() {
        return Arrays.asList(y);
    }

    public void addRow(Timestamp xVal, Number... yAxes) {
        if(yAxes.length != y.length) {
            throw new IllegalArgumentException(String.format("%d y points expected, but %d received", y.length, yAxes.length));
        }

        x.add(xVal);

        for (int i = 0; i < y.length; i++) {
            y[i].addPoint(yAxes[i]);
        }
    }
}
