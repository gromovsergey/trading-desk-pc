package com.foros.service.timed;

import java.util.Scanner;
import javax.ejb.ScheduleExpression;

public class CronStyleScheduleParseUtil {
    public static ScheduleExpression parse(String schedule) {
        Scanner scanner = new Scanner(schedule).useDelimiter("\\s+");
        try {
            ScheduleExpression scheduleExpression = new ScheduleExpression();
            scheduleExpression.minute(scanner.next());
            scheduleExpression.hour(scanner.next());
            scheduleExpression.dayOfMonth(scanner.next());
            scheduleExpression.month(scanner.next());
            scheduleExpression.dayOfWeek(scanner.next());
            return scheduleExpression;
        } finally {
            scanner.close();
        }
    }
}