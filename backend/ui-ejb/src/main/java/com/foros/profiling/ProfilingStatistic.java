package com.foros.profiling;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProfilingStatistic {

    private Date beginTime = new Date();
    private Date endTime = null;

    public static class Statistic {
        private long totalTime = 0L;
        private Long minTime = null;
        private Long maxTime = null;
        private long count = 0L;

        public long getTotalTime() {
            return totalTime;
        }

        public Long getMinTime() {
            return minTime;
        }

        public Long getMaxTime() {
            return maxTime;
        }

        public long getCount() {
            return count;
        }

        void addTime(long time) {
            count++;
            totalTime += time;
            if (maxTime == null || time > maxTime) {
                maxTime = time;
            }
            if (minTime == null || time < minTime) {
                minTime = time;
            }
        }
    }

    private Map<StatisticKey, Statistic> statistic = new HashMap<StatisticKey, Statistic>();

    public synchronized void accumulate(ProfilingContext profilingContext) {
        StatisticKey key = profilingContext.getKey();

        Statistic stat = statistic.get(key);
        if (stat == null) {
            stat = new Statistic();
            statistic.put(key, stat);
        }

        stat.addTime(profilingContext.getEndTime() - profilingContext.getBeginTime());
    }

    public Map<StatisticKey, Statistic> getStatistic() {
        return Collections.unmodifiableMap(statistic);
    }

    public ProfilingStatistic end() {
        this.endTime = new Date();
        return this;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}
