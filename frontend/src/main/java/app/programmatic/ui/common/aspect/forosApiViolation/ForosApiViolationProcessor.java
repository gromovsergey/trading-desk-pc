package app.programmatic.ui.common.aspect.forosApiViolation;

import com.foros.rs.client.result.RsConstraintViolationException;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

public interface ForosApiViolationProcessor {
    ConstraintViolationBuilder process(RsConstraintViolationException e, Object[] methodArgs);
}
