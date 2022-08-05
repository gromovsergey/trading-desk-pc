package com.foros.reporting.meta;

import com.foros.session.reporting.parameters.ColumnOrderTO;
import com.foros.session.reporting.parameters.Order;
import com.foros.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

public class ReportMetaDataImpl<C extends DependentColumn<C>> implements ReportMetaData<C> {

    private String nameKey;
    private ReportColumnsMetaData<C> metrics;
    private ReportColumnsMetaData<C> output;
    private ReportColumnsMetaData<C> all;
    private List<ColumnOrder<C>> sortColumns;
    private Object context;

    ReportMetaDataImpl(
            String nameKey,
            List<C> metricsColumns,
            List<C> outputColumns,
            List<ColumnOrder<C>> sortColumns,
            Object context) {
        this(nameKey,
                new ReportColumnsMetaDataImpl<>(outputColumns, context),
                new ReportColumnsMetaDataImpl<>(metricsColumns, context),
                sortColumns,
                context);
    }

    private ReportMetaDataImpl(
            String nameKey,
            ReportColumnsMetaData<C> output,
            ReportColumnsMetaData<C> metrics,
            List<ColumnOrder<C>> sortColumns,
            Object context) {
        this(nameKey,
                output,
                metrics,
                new ReportColumnsMetaDataImpl<>(merge(output.getColumns(), metrics.getColumns(), new ArrayList<C>()), context),
                sortColumns,
                context);
    }

    private ReportMetaDataImpl(
            String nameKey,
            ReportColumnsMetaData<C> output,
            ReportColumnsMetaData<C> metrics,
            ReportColumnsMetaData<C> all,
            List<ColumnOrder<C>> sortColumns,
            Object context) {
        this.nameKey = nameKey;
        this.output = output;
        this.metrics = metrics;
        this.all = all;
        this.sortColumns = sortColumns(sortColumns);
        this.context = context;
    }

    public ReportMetaDataImpl(String nameKey, List<C> metricsColumns, List<C> outputColumns, Object context) {
        this(nameKey, metricsColumns, outputColumns, defaultSortColumns(outputColumns), context);
    }

    private ReportMetaDataImpl<C> copyWithOrder(List<ColumnOrder<C>> sortColumns) {
        return new ReportMetaDataImpl<>(nameKey, output, metrics, all, sortColumns, context);
    }

    private ReportMetaDataImpl<C> copyWithColumns(
            ReportColumnsMetaData<C> output,
            ReportColumnsMetaData<C> metrics) {
        return new ReportMetaDataImpl<>(nameKey, output, metrics, sortColumns, context);
    }

    private static <I, T extends Collection<I>> T merge(T list1, T list2, T result) {
        result.addAll(list1);
        result.addAll(list2);
        return result;
    }

    private static <C extends Column> List<ColumnOrder<C>> defaultSortColumns(List<C> outputColumns) {
        ArrayList<ColumnOrder<C>> result = new ArrayList<>();

        for (C metricsColumn : outputColumns) {
            result.add(new ColumnOrder<>(metricsColumn, Order.ASC));
        }

        return result;
    }

    private List<ColumnOrder<C>> sortColumns(List<ColumnOrder<C>> sortColumns) {
        List<ColumnOrder<C>> result = new ArrayList<>();

        for (ColumnOrder<C> columnOrder : sortColumns) {
            if (this.all.contains(columnOrder.getColumn())) {
                result.add(columnOrder);
            }
        }

        return result;
    }

    @Override
    public String getName(Locale locale) {
        return StringUtil.getLocalizedString(nameKey, locale);
    }

    @Override
    public ReportColumnsMetaData<C> getOutputColumnsMeta() {
        return output;
    }

    @Override
    public ReportColumnsMetaData<C> getMetricsColumnsMeta() {
        return metrics;
    }

    @Override
    public ReportColumnsMetaData<C> getColumnsMeta() {
        return all;
    }

    @Override
    public List<C> getColumns() {
        return getColumnsMeta().getColumns();
    }

    @Override
    public List<C> getOutputColumns() {
        return getOutputColumnsMeta().getColumns();
    }

    @Override
    public List<C> getMetricsColumns() {
        return getMetricsColumnsMeta().getColumns();
    }

    @Override
    public ReportMetaData<C> retainById(Collection<String> outputColumns, Collection<String> metricsColumns) {
        return copyWithColumns(output.retainById(outputColumns), metrics.retainById(metricsColumns));
    }

    @Override
    public ReportMetaData<C> retainById(Collection<String> columns) {
        return retainById(columns, columns);
    }

    @Override
    public ReportMetaData<C> retain(C... columns) {
        return retain(Arrays.asList(columns));
    }

    @Override
    public ReportMetaData<C> retain(Collection<C> columns) {
        return copyWithColumns(output.retain(columns), metrics.retain(columns));
    }

    @Override
    public ReportMetaData<C> exclude(C... columns) {
        return exclude(Arrays.asList(columns));
    }

    @Override
    public ReportMetaData<C> exclude(Collection<C> columns) {
        return copyWithColumns(output.exclude(columns), metrics.exclude(columns));
    }

    @Override
    public List<ColumnOrder<C>> getSortColumns() {
        return Collections.unmodifiableList(sortColumns);
    }

    @Override
    public String toString() {
        return "MetaData: " + getColumns().toString();
    }

    @Override
    public C find(String id) {
        C column = all.findById(id);

        if (column == null) {
            throw new IllegalArgumentException(id + " column not found");
        }

        return column;
    }

    @Override
    public boolean contains(String id) {
        return getColumnsMeta().contains(id);
    }

    @Override
    public boolean contains(C column) {
        return getColumnsMeta().contains(column);
    }

    @Override
    public ReportMetaData<C> metricsOnly() {
        return copyWithColumns(new ReportColumnsMetaDataImpl<C>(Collections.<C>emptyList(), context), metrics);
    }

    @Override
    public void replace(C from, C to) {
        metrics.replace(from, to);
        output.replace(from, to);
        all.replace(from, to);
    }

    @Override
    public ReportMetaData<C> order(List<ColumnOrder<C>> orderColumns) {
        return copyWithOrder(orderColumns);
    }

    @Override
    public ReportMetaData<C> order(ColumnOrder<C>... columnOrder) {
        return order(Arrays.asList(columnOrder));
    }

    @Override
    public ReportMetaData<C> orderById(ColumnOrderTO... columnOrderTOs) {
        return orderById(Arrays.asList(columnOrderTOs));
    }

    @Override
    public ReportMetaData<C> orderById(List<ColumnOrderTO> columnOrderTOs) {
        List<ColumnOrder<C>> orders = new ArrayList<>();

        for (ColumnOrderTO orderTO : columnOrderTOs) {
            if (StringUtils.isNotBlank(orderTO.getColumn())) {
                orders.add(new ColumnOrder<>(find(orderTO.getColumn()), orderTO.getOrder()));
            }
        }

        return copyWithOrder(orders);
    }

    @Override
    public ReportMetaData<C> include(Collection<C> columns) {
        return copyWithColumns(output.include(columns), metrics.include(columns));
    }

    @Override
    public ReportMetaData<C> include(C... columns) {
        return include(Arrays.asList(columns));
    }
}
