package com.foros.restriction.invocation;

import com.foros.aspect.annotation.ElFunction;
import com.foros.aspect.el.Expression;
import com.foros.aspect.el.ExpressionException;
import com.foros.aspect.el.ExpressionLanguageService;
import com.foros.aspect.registry.AspectDescriptor;
import com.foros.aspect.registry.AspectDescriptorFactoryService;
import com.foros.aspect.registry.AspectRegistry;
import com.foros.aspect.registry.DynamicAspectRegistry;
import com.foros.aspect.registry.ElAspectDescriptor;
import com.foros.aspect.util.MethodUtil;
import com.foros.restriction.AccessRestrictedException;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Restriction;

import com.foros.validation.ValidationContext;
import com.foros.validation.util.ValidationUtil;
import java.lang.reflect.Method;
import java.security.AccessControlException;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless(name = "RestrictionInvocationService")
public class RestrictionInvocationServiceBean implements RestrictionInvocationService {

    @EJB
    private RestrictionService restrictionService;

    @EJB
    private AspectRegistry globalAspectRegistry;

    private AspectRegistry dynamicAspectRegistry = new DynamicAspectRegistry() {
        @Override
        protected AspectDescriptorFactoryService getAspectDescriptorFactoryService() {
            return aspectDescriptorFactoryService;
        }
    };

    @EJB
    private AspectDescriptorFactoryService aspectDescriptorFactoryService;

    @EJB
    private ExpressionLanguageService elService;

    @Override
    public void checkMethodRestrictions(Object target, Method method, Object[] params) {
        checkMethodRestrictions(target, method, params, globalAspectRegistry);
    }

    @Override
    public void checkWebMethodRestrictions(Object target, Method method, Object[] params) {
        checkMethodRestrictions(target, method, params, dynamicAspectRegistry);
    }

    private void checkMethodRestrictions(Object target, Method method, Object[] params, AspectRegistry registry) {
        AspectDescriptor descriptor = registry.getDescriptor(Restriction.class, method);
        if (descriptor != null) {
            if (descriptor instanceof ElAspectDescriptor) {
                ValidationContext validationContext = ValidationUtil.createContext();
                ElAspectDescriptor aspectDescriptor = (ElAspectDescriptor) descriptor;
                Map<String, Object> context = MethodUtil.createParametersMap(target, method, params);

                validateRestriction(validationContext, aspectDescriptor, context);

                if (!validationContext.ok()) {
                    throw new AccessRestrictedException("Restricted by [" + aspectDescriptor.getName() + "] restriction",
                            validationContext.getConstraintViolations());
                }
            } else {
                throw new IllegalArgumentException("Restrictions support only el-aspects");
            }
        }
    }

    private void validateRestriction(ValidationContext validationContext, ElAspectDescriptor aspectDescriptor, Map<String, Object> context) {
        Object[] restrictionParameters = new Object[aspectDescriptor.getParameterExpressions().length];

        for (int i = 0; i < aspectDescriptor.getParameterExpressions().length; i++) {
            Expression expression = aspectDescriptor.getParameterExpressions()[i];

            try {
                restrictionParameters[i] = elService.evaluate(expression, context, ElFunction.Namespace.GENERAL);
            } catch (ExpressionException e) {
                throw new IllegalArgumentException(e);
            }
        }


        restrictionService.validateRestriction(validationContext, aspectDescriptor.getName(), restrictionParameters);
    }

}
