package app.programmatic.ui.reporting.referrer;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.validation.GenericReportValidationServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ReferrerReportValidationServiceImpl extends GenericReportValidationServiceImpl<ReferrerReportParameters> {

    @Override
    protected Report getReport() {
        return Report.REFERRER;
    }

    @Override
    protected void validate(ReferrerReportParameters value, ConstraintViolationBuilder<ReferrerReportParameters> builder) {
        validatePublisherAccount(value.getAccountId(), builder);
        validateDateRange(value, builder);

        if (value.getTagIds().isEmpty()) {
            builder.addViolationDescription("tagIds", "entity.field.error.mandatory");
        }
    }

    private void validateDateRange(ReferrerReportParameters value, ConstraintViolationBuilder<ReferrerReportParameters> builder) {
        LocalDateTime dateStart = value.getDateStart();
        LocalDateTime dateEnd = value.getDateEnd();

        LocalDateTime minDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusMonths(5).withDayOfMonth(1);
        if (dateStart != null && dateStart.isBefore(minDate)) {
            builder.addViolationDescription("dateStart", "report.referrer.error.dateRange");
        }
        if (dateEnd != null && dateEnd.isBefore(minDate)) {
            builder.addViolationDescription("dateEnd", "report.referrer.error.dateRange");
        }
    }
}
