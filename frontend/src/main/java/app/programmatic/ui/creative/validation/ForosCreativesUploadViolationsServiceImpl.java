package app.programmatic.ui.creative.validation;

import com.foros.rs.client.model.ConstraintViolation;
import com.foros.rs.client.result.RsConstraintViolationException;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationProcessor;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.common.validation.forosApiViolation.ForosApiPathMapper;
import app.programmatic.ui.common.validation.forosApiViolation.PathMapperResult;
import app.programmatic.ui.creative.dao.model.Creative;

import java.util.regex.Pattern;

public class ForosCreativesUploadViolationsServiceImpl implements ForosApiViolationProcessor {
    private final Pattern CUTTER = Pattern.compile("operations\\[[0-9]+\\][.]creative[.]");
    private final ForosApiPathMapper optionsMapper = new ApiOptionsMapper();

    @Override
    public ConstraintViolationBuilder process(RsConstraintViolationException e, Object[] methodArgs) {
        ConstraintViolationBuilder<Creative> builder = new ConstraintViolationBuilder<>();

        for (ConstraintViolation violation : e.getConstraintViolations()) {
            if (CUTTER.matcher(violation.getPath()).find()) {
                String path = CUTTER.matcher(violation.getPath()).replaceFirst("");

                PathMapperResult result = optionsMapper.map(path, methodArgs);
                if (result.isChanged()) {
                    path = result.getNewPath();
                }

                builder.addViolationMessage(path, violation.getMessage());

            } else if (violation.getPath() != null && !violation.getPath().isEmpty()) {
                builder.addGeneralViolationMessage(violation.getPath() + ": " + violation.getMessage());
            } else {
                builder.addGeneralViolationMessage(violation.getMessage());
            }
        }
        return builder;
    }
}