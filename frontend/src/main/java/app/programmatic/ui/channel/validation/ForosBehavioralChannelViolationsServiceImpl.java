package app.programmatic.ui.channel.validation;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.common.validation.forosApiViolation.ForosApiPathMapper;
import app.programmatic.ui.common.validation.forosApiViolation.ForosApiViolationMappingProcessor;
import app.programmatic.ui.common.validation.forosApiViolation.PathMapperResult;

import java.util.Collections;

public class ForosBehavioralChannelViolationsServiceImpl extends ForosApiViolationMappingProcessor {
    public ForosBehavioralChannelViolationsServiceImpl() {
        super("behavioralChannel", Collections.singletonList(new Mapper()));
    }

    private static class Mapper implements ForosApiPathMapper {
        @Override
        public PathMapperResult map(String originalPath, Object[] methodArgs) {
            return new PathMapperResult(ConstraintViolationBuilder.GENERAL_ERROR_FIELD_NAME, true);
        }
    }
}

