package app.programmatic.ui.flight.validation;

import app.programmatic.ui.common.validation.forosApiViolation.ForosApiPathMapper;
import app.programmatic.ui.common.validation.forosApiViolation.ForosApiViolationMappingProcessor;
import app.programmatic.ui.common.validation.forosApiViolation.mappers.EntityIdMapper;

import java.util.Arrays;
import java.util.List;


public class ForosCampaignViolationsServiceImpl extends ForosApiViolationMappingProcessor {
    private static final List<ForosApiPathMapper> MAPPERS = initMappers();

    public ForosCampaignViolationsServiceImpl() {
        super("campaign", MAPPERS);
    }

    private static List<ForosApiPathMapper> initMappers() {
        return Arrays.asList(new EntityIdMapper());
    }

    @Override
    protected String mapMessage(String originalMessage) {
        return originalMessage == null ? null : originalMessage.replaceAll("Campaign", "Flight");
    }
}
