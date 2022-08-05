package app.programmatic.ui.creative.validation;

import app.programmatic.ui.common.validation.forosApiViolation.ForosApiPathMapper;
import app.programmatic.ui.common.validation.forosApiViolation.ForosApiViolationMappingProcessor;
import app.programmatic.ui.common.validation.forosApiViolation.mappers.EntityIdMapper;

import java.util.Arrays;
import java.util.List;


public class ForosCreativeViolationsServiceImpl extends ForosApiViolationMappingProcessor {
    private static final List<ForosApiPathMapper> MAPPERS = initMappers();

    public ForosCreativeViolationsServiceImpl() {
        super("creative", MAPPERS);
    }

    private static List<ForosApiPathMapper> initMappers() {
        return Arrays.asList(
                new ApiOptionsMapper(),
                new EntityIdMapper());
    }
}
