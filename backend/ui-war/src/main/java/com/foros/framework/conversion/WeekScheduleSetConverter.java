package com.foros.framework.conversion;

import com.opensymphony.xwork2.conversion.TypeConversionException;
import com.foros.action.WeekScheduleSet;
import com.foros.model.campaign.WeekSchedule;
import com.foros.util.CollectionUtils;
import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;
import com.foros.util.mapper.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeekScheduleSetConverter extends SingleValueBaseTypeConverter {
//    private static Pattern BEGIN_END_PATTERN = Pattern.compile(".*begin : ([0-9]+), end : ([0-9]+).*");
    private static Pattern BEGIN_END_PATTERN = Pattern.compile("([0-9]+):([0-9]+)");

    @Override
    public Object convertFromString(Map<String, Object> context, String value, Class toClass) {
        Collection<WeekSchedule> schedules = new ArrayList<WeekSchedule>();

        if (StringUtil.isPropertyEmpty(value)) {
            return new WeekScheduleSet(schedules);
        }

//        String[] rangeSlots = value.split("\\}\\s*,\\s*\\{");
        String[] rangeSlots = value.split(",");

        for (String rangeSlot : rangeSlots) {
            Matcher matcher = BEGIN_END_PATTERN.matcher(rangeSlot);

            if (matcher.matches() && matcher.groupCount() == 2) {
                schedules.add(WeekScheduleSet.schedule(NumberUtil.parseLong(matcher.group(1)), NumberUtil.parseLong(matcher.group(2))));
            }
        }
        return new WeekScheduleSet(schedules);
    }

    @Override
    public String convertToString(Map<String, Object> context, Object o) {
        if (!(o instanceof WeekScheduleSet)) {
            throw new TypeConversionException("Object o is not WeekScheduleSet");
        }

        WeekScheduleSet scheduleBean = (WeekScheduleSet) o;

        StringBuilder builder = new StringBuilder("");
        if (scheduleBean.size() > 0) {
            builder.append(CollectionUtils.join(scheduleBean.getSchedules(), ",", new Converter<WeekSchedule, String>() {
                @Override
                public String item(WeekSchedule value) {
                    return value.getTimeFrom() + ":" + value.getTimeTo();
                }
            }));
        }
        return builder.toString();
    }
}
