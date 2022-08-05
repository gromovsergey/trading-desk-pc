package com.foros.session.campaign;

import com.foros.model.campaign.WeekSchedule;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class ScheduleHelper {

    public static boolean containsChildSchedule(Collection<? extends WeekSchedule> parentScheduleList, Collection<? extends WeekSchedule> childScheduleList) {
        Set<Long> slicedSet = slice(parentScheduleList);   // Quite stupid implementation, may be optimized.
        Set<Long> targetSlicedSet = slice(childScheduleList);

        return slicedSet.containsAll(targetSlicedSet);
    }

    /**
     * Slices a small set of big intervals into big set of small intervals. We need it to initialize simple comparison
     * among sorted sets.
     */
    private static Set<Long> slice(Collection<? extends WeekSchedule> schedules) {
        Set<Long> slicedSet = new TreeSet<Long>();

        TreeSet<WeekSchedule> sortedSet = new TreeSet<WeekSchedule>(new ScheduleComparator());
        sortedSet.addAll(schedules);

        long start = 0;
        for (WeekSchedule schedule : sortedSet) {

            if  (schedule.getTimeFrom() > start) {
                start = schedule.getTimeFrom();
            }

            while (schedule.getTimeTo() > start) {
                slicedSet.add(start);
                start+=30;
            }

        }
        return slicedSet;
    }
}
