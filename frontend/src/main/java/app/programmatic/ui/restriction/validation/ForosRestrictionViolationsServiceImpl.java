package app.programmatic.ui.restriction.validation;

import com.foros.rs.client.model.ConstraintViolation;
import com.foros.rs.client.result.RsConstraintViolationException;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationProcessor;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.common.validation.exception.EntityNotFoundException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForosRestrictionViolationsServiceImpl implements ForosApiViolationProcessor {
    private static final Integer ENTITY_NOT_FOUND_ERR_CODE = 301001;
    private static Pattern ID_REGEX = Pattern.compile("[^#]+#(\\d+).*");

    public ConstraintViolationBuilder process(RsConstraintViolationException e, Object[] methodArgs) {
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            if (ENTITY_NOT_FOUND_ERR_CODE.equals(violation.getCode())) {
                Matcher idMatcher = ID_REGEX.matcher(violation.getValue());
                if (idMatcher.matches()) {
                    throw new EntityNotFoundException(idMatcher.group(1));
                }
            }
        }
        // All other cases: let's internal error
        throw e;
    }
}
