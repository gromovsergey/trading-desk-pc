package com.foros.framework;

import com.foros.action.ConstraintValidationsAware;
import com.foros.action.Invalidable;
import com.foros.action.Refreshable;
import com.foros.session.BusinessException;
import com.foros.session.PersistenceExceptionHelper;
import com.foros.util.ExceptionUtil;
import com.foros.util.VersionCollisionException;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.convertion.StrutsConstraintViolationConverter;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.constraint.violation.ConstraintViolationImpl;
import com.foros.validation.constraint.violation.Path;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.persistence.OptimisticLockException;
import javax.persistence.Parameter;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;

import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import org.jboss.cache.lock.TimeoutException;

/**
 * Handle various business logic exceptions.
 * Such as DAO exceptions: OptimisticLockException, ConstraintsViolation
 */
public class BusinessExceptionInterceptor extends AbstractInterceptor {
    protected static Logger logger = Logger.getLogger(BusinessExceptionInterceptor.class.getName());

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        if (!(action instanceof ValidationAware)) {
            return invocation.invoke();
        }

        ValidationAware vaction = (ValidationAware) action;
        String result;
        try {
            return invocation.invoke();
        } catch (BusinessException e) {
            result = handleBusinessException(vaction, e);
        } catch (VersionCollisionException e) {
            result = handleOptimisticLock(invocation);
        } catch (ConstraintViolationException e) {
            result = handleConstraintViolation(invocation, vaction, e);
        } catch (EJBException e) {
            result = handleEJBException(invocation, vaction, e);
        }
        MessageStoreInterceptor.saveErrors(invocation.getInvocationContext(), vaction);

        if (action instanceof Invalidable) {
            ((Invalidable) action).invalid();
        }

        return result == null ? Action.INPUT : result;
    }

    private String handleConstraintViolation(ActionInvocation invocation, ValidationAware action, ConstraintViolationException e) {
        // todo: add errors
        String result = Action.INPUT;
        Set<ConstraintViolation> constraintViolations = e.getConstraintViolations();

        // look for specific validation errors
        for (ConstraintViolation constraintViolation : constraintViolations) {
            String name = constraintViolation.getPropertyPath().toString();
            if (name.endsWith("version")) {
                action.getFieldErrors().clear();
                return handleOptimisticLock(invocation);
            }
        }

        if (action instanceof ConstraintValidationsAware) {
            ConstraintValidationsAware validationsAware = (ConstraintValidationsAware) action;
            StrutsConstraintViolationConverter converter = new StrutsConstraintViolationConverter(action, invocation.getStack());
            List<ConstraintViolationRule> rules = validationsAware.getConstraintViolationRules();
            converter.applyRules(rules, constraintViolations);
            validationsAware.getConstraintViolations().addAll(constraintViolations);
        } else {
            throw e;
        }
        MessageStoreInterceptor.saveErrors(invocation.getInvocationContext(), action);
        return result;
    }

    /*
     * Get localized text.
     */
    String getLText(Object action, String key) {
        if (action instanceof TextProvider) {
            return ((TextProvider) action).getText(key, key);
        }

        return key;
    }

    String getLText(Object action, String key, String defMsg) {
        if (action instanceof TextProvider) {
            return ((TextProvider) action).getText(key, defMsg);
        }

        return defMsg;
    }

    String getLText(Object action, String key, String defMsg, String[] args) {
        if (action instanceof TextProvider) {
            return ((TextProvider) action).getText(key, defMsg, args);
        }

        return defMsg;
    }

    protected String handleBusinessException(ValidationAware action, BusinessException ex) {
        for (BusinessException.PropertyError err : ex.getPropertyErrors()) {
            String field = err.getName();
            String msg = err.getMessage();
            action.addFieldError(field, msg);
            logger.log(Level.INFO, "Business exception: " + msg + ", field=" + field);
        }

        for (String msg : ex.getEntityErrors()) {
            action.addActionError(getLText(action, msg));
            logger.log(Level.INFO, "Business exception: " + msg + ", field=null");
        }
        return null;
    }

    /**
     * For Refreshable actions calls needRefresh(), for invocations with "version" result available executes it, do nothing otherwise.
     * @param invocation current invocation
     * @return null or version
     */
    protected String handleOptimisticLock(ActionInvocation invocation) {
        String result;
        ValidationAware action = (ValidationAware) invocation.getAction();

        if (action instanceof Refreshable) {
            ((Refreshable) action).needRefresh();
        }

        if (invocation.getProxy().getConfig().getResults().containsKey("version")) {
            MessageStoreInterceptor.saveErrors(invocation.getInvocationContext(), action);
            String messageKey = "errors.version";
            prepareConstraintViolation(action, messageKey, getLText(action, messageKey, "Entity was concurrently updated. Please repeat changes again."), "version");
            result = "version";
        } else {
            String messageKey = "errors.genericVersionCollision";
            prepareConstraintViolation(action, messageKey, getLText(action, messageKey, "Server is busy. Please try again."), "version");
            result = Action.INPUT;
        }

        logger.log(Level.FINE, "Optimistic Lock exception, field=version");
        return result;
    }

    private void prepareConstraintViolation(ValidationAware action, String messageKey, String msg, String fieldName) {
        action.addFieldError(fieldName, msg);
        if (action instanceof ConstraintValidationsAware) {
            ((ConstraintValidationsAware) action).getConstraintViolations().add(new ConstraintViolationImpl(
                BusinessErrors.ENTITY_VERSION_COLLISION,
                msg,
                Path.fromString(fieldName),
                null,
                messageKey
                ));
        }
    }

    protected String handleQueryTimeout(ActionInvocation invocation, QueryTimeoutException qtException) {
        ValidationAware action = (ValidationAware) invocation.getAction();
        action.addActionError(getLText(action, "errors.serverTimeout"));

        StringBuilder msg = new StringBuilder(qtException.getMessage());
        msg.append("\n\tParams: ");

        Query query = qtException.getQuery();
        if (query != null) {
            for (Parameter param : query.getParameters()) {
                msg.append("\t\n");
                msg.append(param.getName());
                msg.append(" (pos = ");
                msg.append(param.getPosition());
                msg.append(") = ");
                msg.append(query.getParameterValue(param.getPosition()));
            }
        } else {
            msg.append("null");
        }
        logger.log(Level.SEVERE, msg.toString());

        return Action.INPUT;
    }

    private Throwable findCauseException(EJBException ex) {
        Throwable ejbEx = ex;
        while (ejbEx.getCause() instanceof EJBException) {
            ejbEx = ejbEx.getCause();
        }

        ex = (EJBException) ejbEx;
        return ex.getCausedByException() != null ? ex.getCausedByException() : ex.getCause();
    }

    @SuppressWarnings({ "ThrowableResultOfMethodCallIgnored" })
    private String handleEJBException(ActionInvocation invocation, ValidationAware action, EJBException ex) {
        Throwable cause = findCauseException(ex);
        if (cause == null) {
            throw ex;
        }

        if (cause instanceof TimeoutException) {
            handleTimeoutException(action, ex);
            return null;
        } else if (cause instanceof ConstraintViolationException || cause.getCause() instanceof ConstraintViolationException) {
            Throwable rootCause = cause instanceof ConstraintViolationException ? cause : cause.getCause();
            return handleConstraintViolation(invocation, action, (ConstraintViolationException) rootCause);
        } else if (cause instanceof OptimisticLockException) {
            return handleOptimisticLock(invocation);
        } else if (ExceptionUtil.hasCause(ex, VersionCollisionException.class)) {
            return handleOptimisticLock(invocation);
        } else if (cause instanceof PersistenceException || cause.getCause() instanceof PersistenceException) {
            Exception handled = PersistenceExceptionHelper.handle(ex);
            if (handled instanceof ConstraintViolationException) {
                return handleConstraintViolation(invocation, action, (ConstraintViolationException) handled);
            }
            if (ExceptionUtil.hasCause(ex, QueryTimeoutException.class)) {
                return handleQueryTimeout(invocation, ExceptionUtil.getCause(ex, QueryTimeoutException.class));
            }
        }

        throw ex;
    }

    private void handleTimeoutException(ValidationAware action, Exception ex) {
        logger.log(Level.SEVERE, "TimeoutException occurred!", ex);
        action.addActionError("errors.serverTimeout");
    }
}
