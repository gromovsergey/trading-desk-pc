 package com.foros.action;

import com.foros.model.campaign.WeekSchedule;
import com.foros.session.campaign.ScheduleHelper;

import java.util.ArrayList;
import java.util.Collection;

public class WeekScheduleSet {
    private Collection<WeekSchedule> schedules = new ArrayList<WeekSchedule>();

    public static final WeekScheduleSet WHOLE_RANGE_SET = new WeekScheduleSet(){
        {
            getSchedules().add(schedule(0L, 10079L));
        }
    };

    public WeekScheduleSet() {
    }

    public WeekScheduleSet(Collection<? extends WeekSchedule> schedules) {
        if (schedules != null){
            this.schedules.addAll(schedules);
        }
    }

    public Collection<WeekSchedule> getSchedules() {
        return schedules;
    }

    public int size() {
        return schedules.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public static WeekSchedule schedule(Long begin, Long end) {
        return new WeekScheduleImpl(begin, end);
    }

    public boolean contains(WeekScheduleSet scheduleSet) {
        return ScheduleHelper.containsChildSchedule(getSchedules(), scheduleSet.getSchedules());
    }

    private static class WeekScheduleImpl implements WeekSchedule {
        private Long begin;
        private Long end;

        private WeekScheduleImpl(Long begin, Long end) {
            this.begin = begin;
            this.end = end;
        }

        @Override
        public Long getTimeTo() {
            return this.end;
        }

        @Override
        public Long getTimeFrom() {
            return this.begin;
        }
    }
}
