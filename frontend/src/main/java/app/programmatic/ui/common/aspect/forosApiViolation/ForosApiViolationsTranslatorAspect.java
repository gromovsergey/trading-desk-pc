package app.programmatic.ui.common.aspect.forosApiViolation;

import static app.programmatic.ui.common.config.ApplicationConstants.RS_API_FORBIDDEN_CODE;
import static app.programmatic.ui.common.config.ApplicationConstants.RS_API_OPTIMISTIC_LOCK_CODE;

import com.foros.rs.client.model.ConstraintViolation;
import com.foros.rs.client.result.RsConstraintViolationException;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


@Aspect
@Order(3)
@Component
public class ForosApiViolationsTranslatorAspect {
    private static final Logger logger = Logger.getLogger(ForosApiViolationsTranslatorAspect.class.getName());

    private ConcurrentHashMap<String, ForosApiViolationProcessor> processors = new ConcurrentHashMap<>();

    @Around("execution(public * *(..)) && " +
            "@annotation(forosApiViolationsAware)")
    public Object intercept(ProceedingJoinPoint pjp, ForosApiViolationsAware forosApiViolationsAware) throws Throwable {
        try {
            return pjp.proceed();
        } catch (RsConstraintViolationException e) {
            if (isIgnore(e)) {
                throw e;
            }

            ForosApiViolationProcessor processor = findProcessor(forosApiViolationsAware.value());
            if (processor != null) {
                ConstraintViolationBuilder builder = processor.process(e, pjp.getArgs());
                builder.throwExpectedException();
            }

            throw e;
        }
    }

    private ForosApiViolationProcessor findProcessor(String processorName) {
        try {
            ForosApiViolationProcessor result = processors.get(processorName);
            if (result != null) {
                return result;
            }

            result = ((ForosApiViolationProcessor)Class.forName(processorName).newInstance());
            ForosApiViolationProcessor previous = processors.putIfAbsent(processorName, result);

            return previous != null ? previous : result;

        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Can't instantiate ForosApiViolationProcessor", t);
        }

        return null;
    }

    private boolean isIgnore(RsConstraintViolationException e) {
        for(ConstraintViolation violation : e.getConstraintViolations()){
            if (RS_API_OPTIMISTIC_LOCK_CODE.equals(violation.getCode())) {
                return true;
            } else
            if (RS_API_FORBIDDEN_CODE.equals(violation.getCode())) {
                return true;
            }
        }
        return false;
    }
}
