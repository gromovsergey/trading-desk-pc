package app.programmatic.ui.conversion.validation;

import com.foros.rs.client.model.ConstraintViolation;
import com.foros.rs.client.result.RsConstraintViolationException;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.common.validation.forosApiViolation.ForosApiViolationMappingProcessor;

public class ForosConversionViolationsServiceImpl  extends ForosApiViolationMappingProcessor {
    public ForosConversionViolationsServiceImpl() {
        super("conversion");
    }

    @Override
    public ConstraintViolationBuilder process(RsConstraintViolationException e, Object[] methodArgs) {
        ConstraintViolationBuilder<Void> builder = new ConstraintViolationBuilder<>();

        for (ConstraintViolation violation : e.getConstraintViolations()) {
            String path = mapPath(violation.getPath());
            if (!path.startsWith("conversion\\.")) {
                path = "conversion." + path;
            }
            builder.addViolationMessage(path, violation.getMessage());
        }

        return builder;
    }
}
