package com.foros.profiling;

import com.foros.profiling.request.RequestHolder;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class ProfilingInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        if (profilingEnabled()) {
            return processWithProfiling(context);
        } else {
            return process(context);
        }
    }

    private boolean profilingEnabled() {
        return ProfilingStatisticHolder.isEnabled();
    }

    private Object process(InvocationContext context) throws Exception {
        return context.proceed();
    }

    private Object processWithProfiling(InvocationContext context) throws Exception {
        ProfilingContext profilingContext = start(context.getMethod());
        try {
            return context.proceed();
        } finally {
            stop(profilingContext);
        }
    }

    private ProfilingContext start(Method method) {
        ProfilingContext parentProfilingContext = ProfilingContextHolder.get();

        HttpServletRequest request = RequestHolder.get();

        StatisticKey key = new StatisticKey(request != null ? request.getServletPath() : null,
                method, parentProfilingContext != null ? parentProfilingContext.getKey().getMethod() : null);

        ProfilingContext profilingContext = new ProfilingContext(key, parentProfilingContext);

        ProfilingContextHolder.set(profilingContext);

        return profilingContext;
    }

    private void stop(ProfilingContext profilingContext) {
        profilingContext.end();

        ProfilingContextHolder.set(profilingContext.getParent());

        if (profilingContext.getParent() == null) {
            save(profilingContext);
        }
    }

    private void save(ProfilingContext profilingContext) {
        ProfilingStatisticHolder.accumulate(profilingContext);

        for (ProfilingContext child : profilingContext.getChildren()) {
            save(child);
        }
    }

}
