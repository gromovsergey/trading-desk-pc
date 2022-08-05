package com.foros.reporting.tools.olap.query.builder;

import org.olap4j.metadata.Level;

import com.foros.session.reporting.parameters.Order;

public class LevelAndOrder {
    private Level level;
    private Order order;

    public LevelAndOrder(Level level, Order order) {
        this.level = level;
        this.order = order;
    }

    public Level getLevel() {
        return level;
    }

    public Order getOrder() {
        return order;
    }
}
