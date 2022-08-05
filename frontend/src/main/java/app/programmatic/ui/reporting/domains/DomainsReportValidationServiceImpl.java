package app.programmatic.ui.reporting.domains;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.validation.GenericReportValidationServiceImpl;

public class DomainsReportValidationServiceImpl extends GenericReportValidationServiceImpl<DomainsReportParameters> {

    @Override
    protected Report getReport() {
        return Report.DOMAINS;
    }

    @Override
    protected void validate(DomainsReportParameters value, ConstraintViolationBuilder<DomainsReportParameters> builder) {
        validateAdvertisingAccount(value.getAccountId(), builder);
    }
}
