package com.foros.profiling.management;

import com.foros.profiling.ProfilingStatistic;
import com.foros.profiling.StatisticKey;

import java.lang.reflect.Method;
import java.util.Map;

public class ProfilingStatisticCsvSerializer {

    public String serialize(ProfilingStatistic statistic) {
        if (statistic == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        CsvAppender appender = new CsvAppender(builder, ",");

        appendStatisticHeader(appender, statistic);

        for (Map.Entry<StatisticKey, ProfilingStatistic.Statistic> statisticEntry : statistic.getStatistic().entrySet()) {
            appendStatisticLine(appender, statisticEntry.getKey(), statisticEntry.getValue());
        }

        return builder.toString();
    }

    private void appendStatisticHeader(CsvAppender appender, ProfilingStatistic statistic) {
        appender.addComment("from " + statistic.getBeginTime() + " to " + statistic.getEndTime());
        appender
                .field("url")
                .field("ejb class")
                .field("method name")
                .field("method parameters")
                .field("parent ejb class")
                .field("parent method name")
                .field("parent method parameters")
                .field("count")
                .field("min time")
                .field("max time")
                .field("total time")
                .endLine();
    }

    private void appendStatisticLine(CsvAppender appender, StatisticKey key, ProfilingStatistic.Statistic statistic) {
        appender.field(key.getUrl());             // url

        appendMethod(appender, key.getMethod());// method info
        appendMethod(appender, key.getParent());// parent method info

        appender
                .field(statistic.getCount())      // count
                .field(statistic.getMinTime())    // min time
                .field(statistic.getMaxTime())    // max time
                .field(statistic.getTotalTime())  // total time
                .endLine();
    }

    private void appendMethod(CsvAppender appender, Method method) {
        if (method != null) {
            appender
                    .field(method.getDeclaringClass().getName())          // ejb class
                    .field(method.getName())                              // method name
                    .field(generateTypeList(method.getParameterTypes())); // method parameters
        } else {
            appender.delimiters(3);                                   // empty info
        }
    }

    private String generateTypeList(Class<?>[] types) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < types.length; i++) {
            builder.append(getTypeSignature(types[i]));
            if (i + 1 != types.length) {
                builder.append("|");
            }
        }

        return builder.toString();
    }

    private String getTypeSignature(Class<?> type) {
        if (type.isArray()) {
            return type.getComponentType().getName() + "[]";
        } else {
            return type.getName();
        }
    }

}
