package com.foros.reporting.tools.olap.query;

import com.phorm.oix.olap.EmptyRangeException;
import com.phorm.oix.olap.OlapIdentifier;
import com.foros.reporting.ResultHandler;
import com.foros.reporting.meta.ColumnOrder;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.tools.query.strategy.IterationStrategy;
import com.foros.session.reporting.parameters.DateRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatCountingOlapQuery implements OlapQuery {
    private static final Logger logger = Logger.getLogger(StatCountingOlapQuery.class.getName());

    private final OlapQuery query;
    private final List<String> info;

    public StatCountingOlapQuery(OlapQuery query) {
        this.query = query;
        this.info = new ArrayList<>();
    }

    public void execute(final ResultHandler handler, final IterationStrategy iterationStrategy) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.execute(handler, iterationStrategy);
                }
            },
            handler, iterationStrategy);
    }

    public void execute(final ResultHandler handler, final MetaData metaData) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.execute(handler, metaData);
                }
            },
            handler, metaData);
    }

    public OlapQuery limit(final int limit) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.limit(limit);
                }
            },
            limit);
        return this;
    }

    public OlapQuery columns(final Collection<OlapColumn> columns) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.columns(columns);
                }
            },
            columns);
        return this;
    }

    public OlapQuery rows(final OlapIdentifier level, final DateRange dateRange) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.rows(level, dateRange);
                }
            },
            level, dateRange);
        return this;
    }

    public OlapQuery filter(final OlapIdentifier level, final DateRange dateRange) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.filter(level, dateRange);
                }
            },
            level, dateRange);
        return this;
    }

    public OlapQuery filter(final OlapIdentifier level, final String value) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.filter(level, value);
                }
            },
            level, value);
        return this;
    }

    public OlapQuery row(final OlapIdentifier level, final String value) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.row(level, value);
                }
            },
            level, value);
        return this;
    }

    public OlapQuery filter(final OlapIdentifier level, final Long value) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.filter(level, value);
                }
            },
            level, value);
        return this;
    }

    public OlapQuery row(final OlapIdentifier level, final Long value) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.row(level, value);
                }
            },
            level, value);
        return this;
    }

    public OlapQuery filter(final OlapIdentifier level, final List<Long> values) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.filter(level, values);
                }
            },
            level, values);
        return this;
    }

    public OlapQuery filter(final OlapIdentifier level, final List<Long> values, final boolean failIfNotFound) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.filter(level, values, failIfNotFound);
                }
            },
            level, values, failIfNotFound);
        return this;
    }

    public OlapQuery row(final OlapIdentifier level, final List<Long> values, final boolean failIfNotFound) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.row(level, values, failIfNotFound);
                }
            },
            level, values, failIfNotFound);
        return this;
    }

    public OlapQuery order(final List<ColumnOrder<OlapColumn>> columns) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.order(columns);
                }
            },
            columns);
        return this;
    }

    public OlapQuery order(final ColumnOrder<OlapColumn> columnOrder) {
        measureTime(new Runnable() {
                @Override
                public void run() {
                    query.order(columnOrder);
                }
            },
            columnOrder);
        return this;
    }

    private void measureTime(Runnable runnable, Object... paramsToLog) {
        try {
            StackTraceElement[] st = Thread.currentThread().getStackTrace();
            String method = st.length > 2 ? st[2].toString() : "Unknown method";
            StringBuilder msg = new StringBuilder("Method '" + method + "';\tParameters: ");

            for (Object param : paramsToLog) {
                msg.append(param).append(";\t");
            }

            long startTime = System.currentTimeMillis();
            try {
                runnable.run();
            } finally {
                long finishTime = System.currentTimeMillis();
                msg.append("Start time: ");
                msg.append(new Date(startTime));
                msg.append("\tFinish time: ");
                msg.append(new Date(finishTime));
                msg.append("\tDuration: ");
                msg.append(finishTime - startTime);
                msg.append(" ms.");

                info.add(msg.toString());
            }
        } catch (EmptyRangeException e) {
            throw e;
        } catch (RuntimeException e) {
            logger.log(Level.INFO, e.getClass() + " " + e.getMessage() + " - " + getStatMsg());
            throw e;
        }
    }

    private String getStatMsg() {
        StringBuilder infoMsg = new StringBuilder("Time statistics:");
        for (String infoEntry : info) {
            infoMsg.append("\n\t").append(infoEntry);
        }
        return infoMsg.toString();
    }
}
