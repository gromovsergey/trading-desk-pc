package app.programmatic.ui.flight.service;

import app.programmatic.ui.flight.dao.model.chart.ChartData;
import app.programmatic.ui.flight.dao.model.chart.ChartMetric;
import app.programmatic.ui.flight.dao.model.chart.ChartObject;
import app.programmatic.ui.flight.dao.model.chart.ChartType;

import java.time.LocalDateTime;

public interface FlightChartService {

    ChartData buildChart(Long flightBaseId, ChartObject object, ChartMetric metric, ChartType type,
                         LocalDateTime dateStart, LocalDateTime dateEnd);
}
