package com.foros.reporting.tools;

import com.foros.reporting.tools.query.Adjuster;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.util.CollectionUtils;
import com.foros.util.mapper.Converter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.joda.time.DateTime;

@LocalBean
@Singleton(name = "CancelQueryService")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CancelQueryService {
    private static final Logger logger = Logger.getLogger(CancelQueryService.class.getName());

    private final ThreadLocal<CancelQueryContext> cancelQueryContext = new ThreadLocal<>();

    private final ConcurrentMap<String, CancelQueryContext> allContexts = new ConcurrentHashMap<>();

    public Adjuster statementAdjuster() {
        CancelQueryContext context = cancelQueryContext.get();
        if (context == null) {
            return Adjuster.NULL_ADJUSTER;
        }
        return new CancellationAdjuster(context);
    }


    public void doCancellable(String cancellationToken, Runnable block) {
        CancelQueryContext context = new CancelQueryContext();
        context.setPrincipal(SecurityContext.getPrincipal());
        try {
            logger.log(Level.INFO, "About to start report, cancellationToken: {0}", cancellationToken);
            cancelQueryContext.set(context);
            allContexts.putIfAbsent(cancellationToken, context);
            block.run();
        } finally {
            allContexts.remove(cancellationToken);
            cancelQueryContext.set(null);
            logger.log(Level.INFO, "Report has been finished, cancellationToken: {0}", cancellationToken);

        }
    }

    public void cancel(String cancellationToken) {
        CancelQueryContext context = allContexts.get(cancellationToken);
        if (context != null) {
            logger.log(Level.INFO, "Going to cancel report, cancellationToken: {0}", cancellationToken);
            context.cancel();
        } else {
            logger.log(Level.INFO, "No context to cancel report, cancellationToken: {0}", cancellationToken);
        }
    }

    @Asynchronous
    public Future<Void> cancelAsync(String id) {
        cancel(id);
        return new AsyncResult<>(null);
    }


    public void registerCancelTask(CancelTask cancelTask) {
        CancelQueryContext context = cancelQueryContext.get();
        if (context == null) {
            return;
        }

        context.registerCancelTask(cancelTask);
    }

    public void unregisterCancelTask(CancelTask cancelTask) {
        CancelQueryContext context = cancelQueryContext.get();
        if (context == null) {
            return;
        }

        context.unregisterCancelTask(cancelTask);
    }

    public void describe(String description) {
        CancelQueryContext context = cancelQueryContext.get();
        if (context == null) {
            return;
        }
        context.setDescription(description);
    }

    public List<CancelQueryTO> getAllContexts() {
        return CollectionUtils.convert(allContexts.entrySet(), new Converter<Map.Entry<String, CancelQueryContext>, CancelQueryTO>() {
            @Override
            public CancelQueryTO item(Map.Entry<String, CancelQueryContext> entry) {
                CancelQueryContext context = entry.getValue();

                CancelQueryTO to = new CancelQueryTO();
                to.setId(entry.getKey());
                ApplicationPrincipal principal = context.getPrincipal();
                if (principal != null) {
                    to.setIp(principal.getRemoteUserIP());
                    to.setUserId(principal.getUserId());
                    to.setUserName(principal.getName());
                }
                to.setDescription(context.getDescription());
                to.setStarted(context.getStarted());
                to.setWasCancelCalled(context.isWasCancelCalled());
                return to;
            }
        });
    }

    public boolean wasCancelCalled() {
        CancelQueryContext context = cancelQueryContext.get();
        if (context == null) {
            return false;
        }
        return context.wasCancelCalled();
    }

    public void checkCancelled() {
        CancelQueryContext context = cancelQueryContext.get();
        if (context == null) {
            return;
        }
        context.checkCancelled();
    }

    private static class CancellationAdjuster extends Adjuster {
        private final Class<? extends PreparedStatement> psProxy = createProxy(PreparedStatement.class);
        private final Class<? extends CallableStatement> csProxy = createProxy(CallableStatement.class);
        private final Class<? extends ResultSet> rsProxy = createProxy(ResultSet.class);


        private final CancelQueryContext context;

        public CancellationAdjuster(CancelQueryContext context) {
            this.context = context;
        }

        @Override
        public CallableStatement adjustCallableStatement(CallableStatement cs) throws SQLException {
            return adjust(csProxy, cs);
        }

        @Override
        public PreparedStatement adjustPreparedStatement(PreparedStatement ps) throws SQLException {
            return adjust(psProxy, ps);
        }

        private <T extends PreparedStatement> T adjust(Class<? extends T> proxyClass, final T statement) throws SQLException {
            final CancelTask cancelTask = new CancelTask() {

                @Override
                public void cancel() {
                    try {
                        statement.cancel();
                    } catch (SQLException e) {
                        throw new RuntimeException("Can't cancel", e);
                    }
                }
            };


            InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String name = method.getName();
                    boolean unregisterAfterInvocation = false;
                    if (name.equals("close") || name.equals("cancel")) {
                        unregisterAfterInvocation = true;
                    } else {
                        context.checkCancelled();
                    }
                    Object invoke = method.invoke(statement, args);
                    if (unregisterAfterInvocation) {
                        context.unregisterCancelTask(cancelTask);
                    }
                    return invoke;
                }
            };

            try {
                T res = proxyClass.getConstructor(new Class[]{InvocationHandler.class}).newInstance(handler);
                context.registerCancelTask(cancelTask);
                return res;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public ResultSet adjustResultSet(final ResultSet rs) throws SQLException {
            InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String name = method.getName();
                    if (name.equals("next")) {
                        context.checkCancelled();
                    }

                    return method.invoke(rs, args);
                }
            };

            try {
                return rsProxy.getConstructor(new Class[]{InvocationHandler.class}).newInstance(handler);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        private <T> Class<? extends T> createProxy(Class<T> superclass) {
            ClassLoader cl = CancelQueryService.class.getClassLoader();
            //noinspection RedundantArrayCreation,unchecked
            return (Class<? extends T>) Proxy.getProxyClass(cl, new Class[] { superclass });
        }
    }

    private static class CancelQueryContext {
        private volatile boolean wasCancelCalled = false;
        private volatile String description;
        private final DateTime started = DateTime.now();

        private final Set<CancelTask> cancelTasks = Collections.newSetFromMap(new ConcurrentHashMap<CancelTask, Boolean>());
        private volatile ApplicationPrincipal principal;

        public void cancel() {
            wasCancelCalled = true;
            for (CancelTask task : cancelTasks) {
                task.cancel();
            }
        }

        public void registerCancelTask(CancelTask task) {
            checkCancelled();
            cancelTasks.add(task);
        }

        public void unregisterCancelTask(CancelTask task) {
            cancelTasks.remove(task);
        }

        public void checkCancelled() {
            if (wasCancelCalled) {
                throw new CancellationException("Cancelled");
            }
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public ApplicationPrincipal getPrincipal() {
            return principal;
        }

        public void setPrincipal(ApplicationPrincipal principal) {
            this.principal = principal;
        }

        public DateTime getStarted() {
            return started;
        }

        public boolean isWasCancelCalled() {
            return wasCancelCalled;
        }

        public boolean wasCancelCalled() {
            return wasCancelCalled;
        }
    }
}
