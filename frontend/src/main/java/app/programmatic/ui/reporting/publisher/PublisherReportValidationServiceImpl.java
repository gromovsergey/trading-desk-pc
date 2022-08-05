package app.programmatic.ui.reporting.publisher;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.validation.GenericReportValidationServiceImpl;

public class PublisherReportValidationServiceImpl extends GenericReportValidationServiceImpl<PublisherReportParameters> {

    @Override
    protected Report getReport() {
        return Report.PUBLISHER;
    }

    @Override
    protected void validate(PublisherReportParameters value, ConstraintViolationBuilder<PublisherReportParameters> builder) {
        validatePublisherAccount(value.getAccountId(), builder);
    }
}
