package com.foros.restriction;

import com.foros.aspect.registry.AspectDeclarationDescriptor;
import com.foros.aspect.registry.AspectDeclarationRegistry;
import com.foros.restriction.annotation.Restriction;
import com.foros.session.ServiceLocator;
import com.foros.validation.ValidationContext;
import com.foros.validation.util.ValidationUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless(name = "RestrictionService")
public class RestrictionServiceBean implements RestrictionService {

    private static final Logger logger = Logger.getLogger(RestrictionServiceBean.class.getName());
    
    @EJB
    private AspectDeclarationRegistry aspectDeclarationRegistry;

    @Override
    public boolean isPermitted(String restrictionName, Object... params) {
        return isPermittedImpl(aspectDeclarationRegistry.getDescriptor(Restriction.class, restrictionName), params);
    }

    @Override
    public boolean isPermitted(String restrictionName, Object param) {
        return isPermittedImpl(aspectDeclarationRegistry.getDescriptor(Restriction.class, restrictionName), new Object[] { param });
    }

    @Override
    public boolean isPermitted(String restrictionName) {
        return isPermittedImpl(aspectDeclarationRegistry.getDescriptor(Restriction.class, restrictionName), new Object[0]);
    }

    @Override
    public void validateRestriction(ValidationContext context, String restrictionName, Object... params) {
        AspectDeclarationDescriptor descriptor
                = aspectDeclarationRegistry.getDescriptor(Restriction.class, restrictionName);

        validateRestriction(context, descriptor, params);
    }

    private void validateRestriction(ValidationContext context, AspectDeclarationDescriptor descriptor, Object[] params) {
        Object service = ServiceLocator.getInstance().lookup(descriptor.getServiceClass());

        Object[] paramsWithContext = addContext(params, context);

        if (isMethodFound(descriptor, paramsWithContext)) {
            invoke(service, descriptor.getMethod(paramsWithContext), paramsWithContext);
        } else {
            Boolean result = (Boolean) invoke(service, descriptor.getMethod(params), params);
            if (!result) {
                context.addConstraintViolation("errors.forbidden");
            }
        }
    }

    private boolean isPermittedImpl(AspectDeclarationDescriptor aspectDeclarationDescriptor, Object[] params) {
        ValidationContext context = ValidationUtil.createContext();
        validateRestriction(context, aspectDeclarationDescriptor, params);
        return context.ok();
    }

    private boolean isMethodFound(AspectDeclarationDescriptor aspectDeclarationDescriptor, Object[] params) {
        return aspectDeclarationDescriptor.getMethodSafe(params) != null;
    }

    private Object invoke(Object service, Method method, Object[] params) {
        try {
            return method.invoke(service, params);
        } catch (IllegalAccessException e) {
            throw new RestrictionException("Can't invoke method!", e);
        } catch (InvocationTargetException e) {
            throw new RestrictionException("Can't invoke method!", e);
        }
    }

    private Object[] addContext(Object[] params, ValidationContext context) {
        Object[] fullParams = new Object[params.length + 1];
        fullParams[0] = context;
        System.arraycopy(params, 0, fullParams, 1, params.length);

        return fullParams;
    }

}
