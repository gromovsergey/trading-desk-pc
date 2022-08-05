package app.programmatic.ui.flight.service;

import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.dao.model.chart.ChartData;
import app.programmatic.ui.flight.dao.model.chart.ChartMetric;
import app.programmatic.ui.flight.dao.model.chart.ChartObject;
import app.programmatic.ui.flight.dao.model.chart.ChartType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class FlightChartServiceImpl implements FlightChartService {
    private static final String CHART_X_PROC_PARAM_NAME = "adv_sdate";
    private static final int MAX_RESULTS = 10;

    @Autowired
    protected AccountService accountService;

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Autowired
    protected LineItemServiceInternal lineItemService;

    @Autowired
    protected FlightServiceInternal flightService;


    @Override
    public ChartData buildChart(Long flightBaseId, ChartObject object, ChartMetric metric, ChartType type,
                                LocalDateTime dateStart, LocalDateTime dateEnd) {
        validate(object, metric);

        ChartDataBuilder builder;
        ArrayList<Object> params;

        if (type == ChartType.TOTAL) {
            builder = new ChartDataBuilder(object, metric, 1);
            params = new ArrayList<>(1);
            params.add(flightBaseId);
        } else {
            LocalDateTime effectiveDateEnd = calcDateEnd(object, dateEnd, flightBaseId);
            builder = new ChartDataBuilder(object, metric, dateStart, effectiveDateEnd);
            params = new ArrayList<>(3);
            params.add(flightBaseId);
            params.add(dateStart == null ? null : Timestamp.valueOf(dateStart));
            params.add(Timestamp.valueOf(effectiveDateEnd));
        }

        if (object != ChartObject.FLIGHT && object != ChartObject.LINE_ITEM) {
            params.add(metric.getProcParamName());
        }

        jdbcOperations.query(
                "select * from " + (type == ChartType.TOTAL ? object.getTotalProcedureSignature() : object.getDailyProcedureSignature()),
                params.toArray(),
                (ResultSet rs, int ind) -> {
                    builder.addPoint(type == ChartType.TOTAL ? Timestamp.from(Instant.now()) : rs.getTimestamp(CHART_X_PROC_PARAM_NAME),
                                     rs);
                    return null;
                });
        return builder.build(flightBaseId, object, type);
    }

    private static void validate(ChartObject object, ChartMetric metric) {
        if (metric == ChartMetric.POST_IMP_CONV || metric == ChartMetric.POST_CLICK_CONV) {
            if (object != ChartObject.FLIGHT && object != ChartObject.LINE_ITEM) {
                ConstraintViolationBuilder.throwExpectedException("chart.error.unexpectedConvMetrics");
            }
        }
    }

    private LocalDateTime calcDateEnd(ChartObject object, LocalDateTime userDateTime, Long flightBaseId) {
        LocalDateTime result;
        LocalDate objectDateEnd;
        switch (object) {
            case FLIGHT:
            case FLIGHT_CHANNEL:
            case FLIGHT_SITE:
            case FLIGHT_DEVICE:
            case FLIGHT_GEO:
                objectDateEnd = flightService.find(flightBaseId).getDateEnd();
                result = objectDateEnd == null ? null : objectDateEnd.atStartOfDay();
                break;
            case LINE_ITEM:
            case LINE_ITEM_CHANNEL:
            case LINE_ITEM_SITE:
            case LINE_ITEM_DEVICE:
            case LINE_ITEM_GEO:
                LineItem lineItem = lineItemService.find(flightBaseId);
                objectDateEnd = lineItemService.find(flightBaseId).getDateEnd();
                if (objectDateEnd == null) {
                    objectDateEnd = flightService.find(lineItem.getFlightId()).getDateEnd();
                }
                result = objectDateEnd == null ? null : objectDateEnd.atStartOfDay();
                break;
            default:
                result = userDateTime;
                break;
        }

        LocalDateTime now = LocalDateTime.now();
        if (result == null) {
            return now;
        }
        return result.isBefore(now) ? result : now;
    }

    private class ChartDataBuilder {
        private ChartObject chartObject;
        private ChartMetric chartMetric;
        private LocalDate minDate = LocalDate.MAX;
        private LocalDate maxDate = LocalDate.MIN;
        private HashMap<LocalDate, HashMap<Long, Number>> chartMap;
        private int chartMapEntrySize = 2;
        private LinkedHashSet<Long> ids = new LinkedHashSet<>(MAX_RESULTS);
        private ArrayList<String> labels = new ArrayList<>(MAX_RESULTS);

        public ChartDataBuilder(ChartObject chartObject, ChartMetric chartMetric, LocalDateTime start, LocalDateTime end) {
            init(chartObject, chartMetric);
            minDate = start != null ? start.toLocalDate() : LocalDate.MAX;
            maxDate = end != null ? end.toLocalDate() : LocalDate.MIN;
            chartMap = start == null || end == null ? new HashMap<>(256, 0.75f) :
                    new HashMap<>((int)((end.toEpochSecond(ZoneOffset.UTC) - start.toEpochSecond(ZoneOffset.UTC)) / 60 / 60 / 24) + 1);
        }

        public ChartDataBuilder(ChartObject chartObject, ChartMetric chartMetric, int size) {
            init(chartObject, chartMetric);
            minDate = LocalDate.MAX;
            maxDate = LocalDate.MIN;
            chartMap = new HashMap<>(size);
        }

        private void init(ChartObject chartObject, ChartMetric chartMetric) {
            this.chartObject = chartObject;
            this.chartMetric = chartMetric;
        }

        public void addPoint(Timestamp date, ResultSet rs) throws SQLException {
            Long id = chartObject.getIdFetcher().fetchId(rs);
            Number value = chartMetric.fetch(rs);

            LocalDate localDate = date.toLocalDateTime().toLocalDate();
            HashMap<Long, Number> dateValues = chartMap.get(localDate);
            if (dateValues == null) {
                dateValues = new HashMap<>(chartMapEntrySize);
                chartMap.put(localDate, dateValues);
            } else if (chartMapEntrySize < dateValues.size()) {
                chartMapEntrySize = dateValues.size();
            }

            Number prevValue = dateValues.put(id, value);
            if (prevValue != null) {
                throw new IllegalArgumentException(String.format("Duplicate value for entity id %d and date %S", id, localDate.toString()));
            }

            if (ids.add(id)) {
                labels.add(chartObject.getNameFetcher().fetchName(rs));
            }

            if (minDate.isAfter(localDate)) {
                minDate = localDate;
            }
            if (maxDate.isBefore(localDate)) {
                maxDate = localDate;
            }
        }

        public ChartData build(Long flightBaseId, ChartObject chartObject, ChartType chartType) {
            if (ids.isEmpty()) {
                return null;
            }

            if (maxDate.isBefore(minDate)) {
                throw new IllegalStateException();
            }

            ValuesFetcher valuesFetcher = chartType != ChartType.RUNNING_TOTAL ? new SimpleValuesFetcher(chartMap, ids, chartMetric) :
                    new RunningTotalValuesFetcher(chartMap, ids, chartObject, chartMetric, flightBaseId, minDate);

            ValuesChartDataMapper valuesChartDataMapper = chartType == ChartType.TOTAL ? new TotalValuesChartDataMapper(chartMetric) :
                    new CommonValuesChartDataMapper();
            return valuesChartDataMapper.map(valuesFetcher, labels, minDate, maxDate);
        }
    }

    private interface ValuesFetcher {
        Number[] fetch(LocalDate date);
    }

    private class SimpleValuesFetcher implements ValuesFetcher {
        private Map<LocalDate, HashMap<Long, Number>> chartMap;
        private Set<Long> ids;
        private ChartMetric chartMetric;

        public SimpleValuesFetcher(Map<LocalDate, HashMap<Long, Number>> chartMap, Set<Long> ids, ChartMetric chartMetric) {
            this.chartMap = chartMap;
            this.ids = ids;
            this.chartMetric = chartMetric;
        }

        public Number[] fetch(LocalDate date) {
            Number[] chartRow = new Number[ids.size()];
            Map<Long, Number> dateValues = chartMap.get(date);
            if (dateValues == null) {
                Arrays.fill(chartRow, chartMetric.getZero());
            } else {
                int i = 0;
                for (Long id : ids) {
                    chartRow[i++] = dateValues.getOrDefault(id, chartMetric.getZero());
                }
            }

            return chartRow;
        }
    }

    private class RunningTotalValuesFetcher implements ValuesFetcher {
        private Map<LocalDate, HashMap<Long, Number>> chartMap;
        private LinkedHashSet<Long> ids;
        private ChartMetric chartMetric;
        Number[] chartRow;


        public RunningTotalValuesFetcher(Map<LocalDate, HashMap<Long, Number>> chartMap, LinkedHashSet<Long> ids,
                                         ChartObject chartObject, ChartMetric chartMetric, Long flightBaseId, LocalDate minDate) {
            this.chartMap = chartMap;
            this.ids = ids;
            this.chartMetric = chartMetric;
            this.chartRow = initValues(ids, chartObject, chartMetric, flightBaseId, minDate);
        }

        public Number[] fetch(LocalDate date) {
            Map<Long, Number> dateValues = chartMap.get(date);
            if (dateValues != null) {
                int i = 0;
                for (Long id : ids) {
                    chartRow[i] = chartMetric.add(chartRow[i], dateValues.getOrDefault(id, chartMetric.getZero()));
                    i++;
                }
            }

            return chartRow;
        }

        private Number[] initValues(Set<Long> ids, ChartObject chartObject, ChartMetric chartMetric,
                                         Long flightBaseId, LocalDate minDate) {
            HashMap<Long, Number> initialValues = new HashMap<>();
            jdbcOperations.query(
                    "select * from " + chartObject.getTotalToDateProcedureSignature(),
                    new Object[] { flightBaseId, Timestamp.valueOf(minDate.minusDays(1).atStartOfDay()) },
                    (ResultSet rs, int ind) -> {
                        initialValues.put(chartObject.getIdFetcher().fetchId(rs),
                                          chartMetric.fetch(rs));
                        return null;
                    });

            Number[] result = new Number[ids.size()];
            int i = 0;
            for (Long id : ids) {
                Number value = initialValues.get(id);
                result[i++] = value != null ? value : chartMetric.getZero();
            };
            return result;
        }
    }

    private interface ValuesChartDataMapper {
        ChartData map(ValuesFetcher valuesFetcher, ArrayList<String> labels, LocalDate minDate, LocalDate maxDate);
    }

    private class CommonValuesChartDataMapper implements ValuesChartDataMapper {

        @Override
        public ChartData map(ValuesFetcher valuesFetcher, ArrayList<String> labels, LocalDate minDate, LocalDate maxDate) {
            ChartData result = new ChartData(labels.toArray(new String[labels.size()]));

            LocalDate date = LocalDate.from(minDate);
            while (!date.isAfter(maxDate)) {
                Number[] chartRow = valuesFetcher.fetch(date);
                result.addRow(Timestamp.valueOf(LocalDateTime.of(date, LocalTime.MIDNIGHT)), chartRow);

                date = date.plusDays(1l);
            }

            return result;
        }
    }

    private class TotalValuesChartDataMapper implements ValuesChartDataMapper {
        private ChartMetric chartMetric;

        public TotalValuesChartDataMapper(ChartMetric chartMetric) {
            this.chartMetric = chartMetric;
        }

        @Override
        public ChartData map(ValuesFetcher valuesFetcher, ArrayList<String> labels, LocalDate minDate, LocalDate maxDate) {
            if (!minDate.equals(maxDate)) {
                throw new RuntimeException("Totals can be count for a single day only");
            }

            ListIterator<Number> chartRowIt = Arrays.asList(valuesFetcher.fetch(maxDate)).listIterator();
            List<LabelValue> labelValues = labels.stream()
                    .map( l -> new LabelValue(l, chartRowIt.next()) )
                    .sorted( (lv1, lv2) -> chartMetric.compare(lv2.getValue(), lv1.getValue()) )
                    .filter( lv -> chartMetric.compare(chartMetric.getZero(), lv.getValue()) != 0 )
                    .collect(Collectors.toList());

            ChartData result = new ChartData(labelValues.stream()
                    .map(lv -> lv.getLabel())
                    .collect(Collectors.toList())
                    .toArray(new String[labelValues.size()]));

            result.addRow(Timestamp.valueOf(LocalDateTime.of(maxDate, LocalTime.MIDNIGHT)),
                          labelValues.stream()
                            .map(lv -> lv.getValue())
                            .collect(Collectors.toList())
                            .toArray(new Number[labelValues.size()]));

            return result;
        }

        private class LabelValue {
            private String label;
            private Number value;

            public LabelValue(String label, Number value) {
                this.label = label;
                this.value = value;
            }

            public String getLabel() {
                return label;
            }

            public Number getValue() {
                return value;
            }
        }
    }
}
