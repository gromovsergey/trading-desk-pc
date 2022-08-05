package com.foros.session;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

public class TimeExecutionLoggingInterceptor {

    private static final Logger logger = Logger.getLogger(TimeExecutionLoggingInterceptor.class.getName());

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        String methodName = context.getMethod().getName();
        boolean isLogging = (methodName.startsWith("query") || methodName.startsWith("execute") || methodName.startsWith("update"))
                && logger.isLoggable(Level.INFO);

        DateTime start = new DateTime();
        String uuid = UUID.randomUUID().toString();
        if (isLogging) {
            StringBuilder msg = new StringBuilder(512);
            msg.append("Query ").append(uuid).append(" started at ").append(start).append("\n");
            Object[] contextParameters = context.getParameters();

            printInvoker(context, msg);

            Object query = contextParameters[0];
            msg.append(query.toString());

            printParameters(context, msg);
            logger.log(Level.INFO, msg.toString());
        }

        boolean isError = true;
        try {
            Object result = context.proceed();
            isError = false;
            return result;
        } finally {
            if (isLogging) {
                String duration = DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - start.getMillis());
                String status = (isError ? " with an ERROR" : "");
                logger.log(
                        Level.INFO,
                        "Query " + uuid + " started at {0} is finished{1}. Elapsed time: {2}",
                        new Object[] { start, status, duration }
                );
            }
        }
    }

    private void printInvoker(InvocationContext context, StringBuilder msg) {
        for (Object parameter : context.getParameters()) {
            if (parameter instanceof ResultSetExtractor ||
                    parameter instanceof RowCallbackHandler ||
                    parameter instanceof RowMapper) {
                msg.append('(').append(parameter.getClass().getName()).append(") ");
            }
        }
    }


    private void printParameters(InvocationContext context, StringBuilder msg) {
        for (Object obj : context.getParameters()) {
            if (obj instanceof Object[]) {
                msg.append("\nParameters:\n");
                Object[] params = (Object[]) obj;
                for (Object parameter : params) {
                    if (parameter instanceof Calendar) {
                        Calendar cal = (Calendar) parameter;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        sdf.setTimeZone(cal.getTimeZone());
                        parameter = sdf.format(cal.getTime());
                    }

                    String value = (parameter == null ? "null" : parameter.toString());
                    if (value.length() > 1000) {
                        value = value.substring(0, 1000) + " ... , " + value.length() + " chars";
                    }
                    msg.append("    ").append(value).append('\n');
                }
            }
        }
    }
}
