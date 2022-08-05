package com.foros.session.campaign;

import com.foros.model.campaign.WeekSchedule;

import java.util.Comparator;

public class ScheduleComparator implements Comparator<WeekSchedule> {
    @Override
    public int compare(WeekSchedule o1, WeekSchedule o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }
        return Long.compare(o1.getTimeFrom(), o2.getTimeFrom());
    }
}
