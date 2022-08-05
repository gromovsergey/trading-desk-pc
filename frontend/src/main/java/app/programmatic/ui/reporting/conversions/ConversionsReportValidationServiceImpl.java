package app.programmatic.ui.reporting.conversions;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.validation.GenericReportValidationServiceImpl;

public class ConversionsReportValidationServiceImpl extends GenericReportValidationServiceImpl<ConversionsReportParameters> {

    @Override
    protected Report getReport() {
        return Report.CONVERSIONS;
    }

    @Override
    protected void validate(ConversionsReportParameters value, ConstraintViolationBuilder<ConversionsReportParameters> builder) {
        validateAdvertisingAccount(value.getAccountId(), builder);
    }
}
