package app.programmatic.ui.reporting.validation;

import org.springframework.beans.factory.annotation.Autowired;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.common.validation.strategy.EntityFetcher;
import app.programmatic.ui.common.validation.strategy.NullForbiddenValidationStrategy;
import app.programmatic.ui.common.validation.strategy.UpdateValidationStrategy;
import app.programmatic.ui.common.validation.strategy.ValidationStrategy;
import app.programmatic.ui.reporting.model.Report;
import app.programmatic.ui.reporting.model.ReportColumn;
import app.programmatic.ui.reporting.model.ReportParameters;
import app.programmatic.ui.reporting.tools.ColumnAvailabilityHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.EnumSet;

import static app.programmatic.ui.common.validation.ConstraintViolationBuilder.GENERAL_ERROR_FIELD_NAME;
import static app.programmatic.ui.reporting.model.ReportColumnLocation.*;

public abstract class GenericReportValidationServiceImpl<P extends ReportParameters> implements ConstraintValidator<ValidateReportParameters, P> {
    private static final ValidationStrategy updateValidationStrategy = new UpdateValidationStrategy(new NullForbiddenValidationStrategy());

    @Autowired
    private AccountService accountService;

    @Autowired
    private ColumnAvailabilityHelper columnAvailabilityHelper;

    @Override
    public final void initialize(ValidateReportParameters constraintAnnotation) {
    }

    @Override
    public boolean isValid(P value, ConstraintValidatorContext context) {
        ConstraintViolationBuilder<P> builder = new ConstraintViolationBuilder<>();

        validateDates(value, builder);
        validateColumns(value, builder);
        validateUnacceptableColumns(value, builder);
        validate(value, builder);

        return builder.buildAndPushToContext(context).isValid();
    }

    protected void validateDates(P value, ConstraintViolationBuilder<P> builder) {
        LocalDateTime dateStart = value.getDateStart();
        LocalDateTime dateEnd = value.getDateEnd();

        if (dateStart == null) {
            builder.addViolationDescription("dateStart", "entity.field.error.mandatory");
        }

        if (dateEnd == null) {
            builder.addViolationDescription("dateEnd", "entity.field.error.mandatory");
        }

        if (dateStart != null && dateEnd != null && dateStart.isAfter(dateEnd)) {
            builder.addViolationDescription("dateEnd", "flight.dateEnd.error.endAfterStart");
        }
    }

    protected void validateColumns(P value, ConstraintViolationBuilder<P> builder) {
        boolean allRequiredPresent = value.getSelectedColumns().stream()
                .filter(col -> getReport().getRequired().contains(col))
                .count() == getReport().getRequired().size();
        if (!allRequiredPresent) {
            builder.addViolationDescription(GENERAL_ERROR_FIELD_NAME, "report.error.requiredColumnsNotSelected");
        }

        boolean settingsDefined = value.getSelectedColumns().stream()
                .filter( col -> col.getLocation() == SETTINGS )
                .findAny()
                .isPresent();
        if (!settingsDefined) {
            builder.addViolationDescription(GENERAL_ERROR_FIELD_NAME, "report.error.settingsColumnsNotSelected");
        }

        boolean statisticDefined = value.getSelectedColumns().stream()
                .filter( col -> col.getLocation() == STATISTIC
                        || col.getLocation() == VIDEO_STATISTIC
                        || col.getLocation() == TIME_STATISTIC)
                .findAny()
                .isPresent();
        if (!statisticDefined) {
            builder.addViolationDescription(GENERAL_ERROR_FIELD_NAME, "report.error.statisticColumnsNotSelected");
        }
    }

    protected void validateAdvertisingAccount(Long accountId, ConstraintViolationBuilder<P> builder) {
        validateAccount(accountId, builder, id -> accountService.findAdvertisingUnchecked(id));
    }

    protected void validatePublisherAccount(Long accountId, ConstraintViolationBuilder<P> builder) {
        validateAccount(accountId, builder, id -> accountService.findPublisherUnchecked(id));
    }

    private void validateAccount(Long accountId, ConstraintViolationBuilder<P> builder, EntityFetcher<?, Long> fetcher) {
        updateValidationStrategy.checkBean(accountId, "accountId", builder.cast(), fetcher);
    }

    protected void validateUnacceptableColumns(P parameters, ConstraintViolationBuilder<P> builder) {
        EnumSet<ReportColumn> unacceptableColumns = columnAvailabilityHelper.getUnacceptableColumns(parameters);
        unacceptableColumns.retainAll(parameters.getSelectedColumns());
        unacceptableColumns.stream()
                .forEach(
                    c -> builder.addViolationDescription(GENERAL_ERROR_FIELD_NAME, "report.error.notAllowedColumn", c));
    }

    protected abstract Report getReport();

    protected abstract void validate(P value, ConstraintViolationBuilder<P> builder);
}
