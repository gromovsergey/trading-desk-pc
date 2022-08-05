package com.foros.reporting.tools.olap.query.builder;

import org.olap4j.metadata.Measure;

import com.foros.session.reporting.parameters.Order;

public class MeasureAndOrder {
    private Measure measure;
    private Order order;

    public MeasureAndOrder(Measure measure, Order order) {
        this.measure = measure;
        this.order = order;
    }

    public Measure getMeasure() {
        return measure;
    }

    public Order getOrder() {
        return order;
    }
}
