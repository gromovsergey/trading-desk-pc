package app.programmatic.ui.common.validation.forosApiViolation;

import static app.programmatic.ui.common.config.ApplicationConstants.RS_API_SINGLE_OPERATION_PREFIX_REGEX;

import com.foros.rs.client.model.ConstraintViolation;
import com.foros.rs.client.result.RsConstraintViolationException;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationProcessor;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


public class ForosApiViolationMappingProcessor implements ForosApiViolationProcessor {
    private final Pattern CUTTER;
    private final List<ForosApiPathMapper> mappers;

    protected ForosApiViolationMappingProcessor(String entityName) {
        this(entityName, Collections.emptyList());
    }

    protected ForosApiViolationMappingProcessor(String entityName, List<ForosApiPathMapper> mappers) {
        String regexSuffix = entityName != null && !entityName.isEmpty() ? entityName + "[.]" : "";
        CUTTER = Pattern.compile(RS_API_SINGLE_OPERATION_PREFIX_REGEX + regexSuffix);
        this.mappers = mappers;
    }

    @Override
    public ConstraintViolationBuilder process(RsConstraintViolationException e, Object[] methodArgs) {
        ConstraintViolationBuilder<Void> builder = new ConstraintViolationBuilder<>();

        for (ConstraintViolation violation : e.getConstraintViolations()) {
            if (ignoreViolation(violation.getPath(), violation.getMessage())) {
                continue;
            }

            String path = mapPath(violation.getPath());
            for (ForosApiPathMapper mapper : mappers) {
                PathMapperResult result = mapper.map(path, methodArgs);
                if (result.isChanged()) {
                    path = result.getNewPath();
                    break;
                }
            }
            builder.addViolationMessage(path, mapMessage(violation.getMessage()));
        }

        return builder;
    }

    protected boolean ignoreViolation(String originalPath, String originalMessage) {
        return false;
    }

    protected String mapPath(String originalPath) {
        return CUTTER.matcher(originalPath).replaceFirst("");
    }

    protected String mapMessage(String originalMessage) {
        return originalMessage;
    }
}
