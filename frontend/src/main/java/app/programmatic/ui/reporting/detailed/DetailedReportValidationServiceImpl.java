package app.programmatic.ui.reporting.detailed;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.validation.GenericReportValidationServiceImpl;

public class DetailedReportValidationServiceImpl extends GenericReportValidationServiceImpl<DetailedReportParameters> {

    @Override
    protected Report getReport() {
        return Report.DETAILED;
    }

    @Override
    protected void validate(DetailedReportParameters value, ConstraintViolationBuilder<DetailedReportParameters> builder) {
        //validateAdvertisingAccount(value.getAccountId(), builder);
    }
}
