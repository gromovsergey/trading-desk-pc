package app.programmatic.ui.flight.validation;

import static app.programmatic.ui.common.config.ApplicationConstants.RS_API_SINGLE_OPERATION_PREFIX_REGEX;

import com.foros.rs.client.result.RsConstraintViolationException;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationProcessor;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

import java.util.regex.Pattern;


public class ForosFlightViolationsServiceImpl implements ForosApiViolationProcessor {
    private static final Pattern CAMPAIGN_PATH_MATCHER = Pattern.compile(RS_API_SINGLE_OPERATION_PREFIX_REGEX + "campaign[.].+");
    private static final Pattern CCG_PATH_MATCHER = Pattern.compile(RS_API_SINGLE_OPERATION_PREFIX_REGEX + "campaignCreativeGroup[.].+");

    private ForosCampaignViolationsServiceImpl campaignViolationsService = new ForosCampaignViolationsServiceImpl() {
        @Override
        protected boolean ignoreViolation(String originalPath, String originalMessage) {
            return !CAMPAIGN_PATH_MATCHER.matcher(originalPath).matches();
        }
    };
    private ForosCcgViolationsServiceImpl ccgViolationsService = new ForosCcgViolationsServiceImpl() {
        @Override
        protected boolean ignoreViolation(String originalPath, String originalMessage) {
            return !CCG_PATH_MATCHER.matcher(originalPath).matches();
        }
    };

    @Override
    public ConstraintViolationBuilder process(RsConstraintViolationException e, Object[] methodArgs) {
        ConstraintViolationBuilder campaignBuilder = campaignViolationsService.process(e, methodArgs);
        ConstraintViolationBuilder ccgBuilder = ccgViolationsService.process(e, methodArgs);

        campaignBuilder.addViolationDescription(ccgBuilder);
        return campaignBuilder;
    }
}
