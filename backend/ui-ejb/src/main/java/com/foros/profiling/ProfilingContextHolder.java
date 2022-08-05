package com.foros.profiling;

public class ProfilingContextHolder {

    private static ThreadLocal<ProfilingContext> profilingContext = new ThreadLocal<ProfilingContext>();

    public static ProfilingContext get() {
        return profilingContext.get();
    }

    public static void set(ProfilingContext context) {
        profilingContext.set(context);
    }

}
