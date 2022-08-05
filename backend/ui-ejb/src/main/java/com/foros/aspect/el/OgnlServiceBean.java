package com.foros.aspect.el;

import com.foros.aspect.annotation.ElFunction;
import com.foros.aspect.registry.ElFunctionDescriptor;
import com.foros.aspect.registry.ElFunctionRegistryService;
import com.foros.restriction.RestrictionService;
import com.foros.session.ServiceLocator;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import ognl.MethodAccessor;
import ognl.MethodFailedException;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

@Startup
@Singleton(name = "ExpressionLanguageService")
public class OgnlServiceBean implements ExpressionLanguageService {
    private static final Logger logger = Logger.getLogger(RestrictionService.class.getName());

    @EJB
    private ElFunctionRegistryService elFunctionRegistry;

    @PostConstruct
    public void init() {
        // initialize custom method accessor
        OgnlRuntime.setMethodAccessor(RootContext.class, new CheckMethodAccessor());
    }

    @Override
    public Object evaluate(Expression expression, Map<String, Object> parameters, ElFunction.Namespace namespace) throws ExpressionException {
        try {
            Object result = Ognl.getValue(expression.getExpression(), parameters, new RootContext(namespace));
            return result;
        } catch (OgnlException e) {
            Throwable cause = e.getReason();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            
            throw new ExpressionException(e);
        }
    }

    @Override
    public Expression compileExpression(String expression) throws ExpressionException {
        try {
            return new Expression(Ognl.parseExpression(expression));
        } catch (OgnlException e) {
            throw new ExpressionException(e);
        }
    }

    private static class RootContext {
        private ElFunction.Namespace namespace;

        public RootContext(ElFunction.Namespace namespace) {
            this.namespace = namespace;
        }

        public ElFunction.Namespace getNamespace() {
            return namespace;
        }
    }

    private class CheckMethodAccessor implements MethodAccessor {
        @Override
        public Object callStaticMethod(Map context, Class targetClass, String methodName, Object[] args) throws MethodFailedException {
            throw new UnsupportedOperationException();  // todo: ???
        }

        @Override
        public Object callMethod(Map context, Object target, String methodName, Object[] args) throws MethodFailedException {
            RootContext root = (RootContext) target;

            ElFunctionDescriptor elFunctionDescriptor = elFunctionRegistry.getDescriptor(methodName, root.getNamespace());
            Object function = ServiceLocator.getInstance().lookup(elFunctionDescriptor.getContainerClass());

            logger.fine(MessageFormat.format("Tracing expression call: {0}({1})", methodName, Arrays.asList(args)));

            Object result = OgnlRuntime.callAppropriateMethod(
                    (OgnlContext) context, target, function, methodName, null,
                    elFunctionDescriptor.getFunctionMethods(), args
            );

            logger.fine(MessageFormat.format("Tracing expression result: {0}({1}) = {2}", methodName, Arrays.asList(args), result));

            return result;
        }
    }
}
