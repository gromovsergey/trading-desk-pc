package app.programmatic.ui.reporting.segments;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.validation.GenericReportValidationServiceImpl;
import org.springframework.util.CollectionUtils;

public class SegmentsReportValidationServiceImpl extends GenericReportValidationServiceImpl<SegmentsReportParameters> {

    @Override
    protected Report getReport() {
        return Report.SEGMENTS;
    }

    @Override
    protected void validate(SegmentsReportParameters value, ConstraintViolationBuilder<SegmentsReportParameters> builder) {
        validateAdvertisingAccount(value.getAccountId(), builder);

        if (!CollectionUtils.isEmpty(value.getFlightIds())
                && !CollectionUtils.isEmpty(value.getLineItemIds())) {
            builder.addViolationDescription("lineItemIds", "entity.id.error.excessive");
        }
    }
}
