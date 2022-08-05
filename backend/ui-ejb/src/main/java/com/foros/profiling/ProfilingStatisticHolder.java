package com.foros.profiling;

// todo: synchronize me!!!
public class ProfilingStatisticHolder {

    private static ProfilingStatistic statistic = null;

    public static boolean isEnabled() {
        return statistic != null;
    }

    public static void accumulate(ProfilingContext profilingContext) {
        if (isEnabled()) {
            statistic.accumulate(profilingContext);
        } else {
            // todo: exception?
        }
    }

    public static ProfilingStatistic get() {
        return statistic != null ? statistic.end() : null;
    }

    public static ProfilingStatistic reset() {
        return resetStatisticImpl(new ProfilingStatistic());
    }

    public static void start() {
        reset();
    }

    public static ProfilingStatistic stop() {
        return resetStatisticImpl(null);
    }

    private static ProfilingStatistic resetStatisticImpl(ProfilingStatistic newValue) {
        ProfilingStatistic old = statistic;
        statistic = newValue;
        return old != null ? old.end() : null;
    }

}
