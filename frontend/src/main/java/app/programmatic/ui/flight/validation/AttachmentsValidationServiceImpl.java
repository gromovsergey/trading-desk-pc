package app.programmatic.ui.flight.validation;

import static app.programmatic.ui.common.validation.ConstraintViolationBuilder.GENERAL_ERROR_FIELD_NAME;

import com.foros.rs.client.model.ConstraintViolation;
import com.foros.rs.client.result.RsConstraintViolationException;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationProcessor;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

public class AttachmentsValidationServiceImpl implements ForosApiViolationProcessor {

    @Override
    public ConstraintViolationBuilder process(RsConstraintViolationException e, Object[] methodArgs) {
        ConstraintViolationBuilder<Void> builder = new ConstraintViolationBuilder<>();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            builder.addViolationMessage(GENERAL_ERROR_FIELD_NAME, violation.getMessage());
        }
        return builder;
    }
}
