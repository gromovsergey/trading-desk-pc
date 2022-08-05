package app.programmatic.ui.account.validation;

import app.programmatic.ui.common.validation.forosApiViolation.ForosApiPathMapper;
import app.programmatic.ui.common.validation.forosApiViolation.ForosApiViolationMappingProcessor;
import app.programmatic.ui.common.validation.forosApiViolation.mappers.EntityIdMapper;

import java.util.Arrays;
import java.util.List;


public class ForosAccountViolationsServiceImpl extends ForosApiViolationMappingProcessor {
    private static final List<ForosApiPathMapper> MAPPERS = initMappers();

    public ForosAccountViolationsServiceImpl() {
        super("account", MAPPERS);
    }

    private static List<ForosApiPathMapper> initMappers() {
        return Arrays.asList(new EntityIdMapper());
    }
}
