package app.programmatic.ui.file.validation;

import com.foros.rs.client.model.ConstraintViolation;
import com.foros.rs.client.result.RsConstraintViolationException;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationProcessor;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

public class ForosFileViolationsServiceImpl implements ForosApiViolationProcessor {

    @Override
    public ConstraintViolationBuilder process(RsConstraintViolationException e, Object[] methodArgs) {
        ConstraintViolationBuilder<Void> builder = new ConstraintViolationBuilder<>();

        for (ConstraintViolation violation : e.getConstraintViolations()) {
            builder.addGeneralViolationMessage(violation.getMessage());
        }
        return builder;
    }
}