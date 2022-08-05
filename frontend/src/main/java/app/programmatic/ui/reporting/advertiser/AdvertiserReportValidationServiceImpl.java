package app.programmatic.ui.reporting.advertiser;

import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.validation.GenericReportValidationServiceImpl;

public class AdvertiserReportValidationServiceImpl extends GenericReportValidationServiceImpl<AdvertiserReportParameters> {

    @Override
    protected Report getReport() {
        return Report.ADVERTISER;
    }

    @Override
    protected void validate(AdvertiserReportParameters value, ConstraintViolationBuilder<AdvertiserReportParameters> builder) {
        validateAdvertisingAccount(value.getAccountId(), builder);
    }
}
