package com.foros.util;

import java.util.Map;

public class ThreadUtil {

    public static String dumpThreads() {
        Map<Thread, StackTraceElement[]> dump = Thread.getAllStackTraces();

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Thread, StackTraceElement[]> row : dump.entrySet()) {
            Thread thread = row.getKey();
            if (thread.getState() != Thread.State.RUNNABLE) {
                continue;
            }
            sb.append(thread.getName()).append("(").append(thread.getId()).append(")").append(":\n");
            for (StackTraceElement element : row.getValue()) {
                sb.append("at ").append(element.getClassName()).append(".").append(element.getMethodName());
                sb.append("(").append(element.getFileName()).append(":").append(element.getLineNumber()).append(")");
                sb.append("\n");
            }
            sb.append("\n\n");
        }
        return sb.toString();
    }
}
