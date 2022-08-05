package app.programmatic.ui.agentreport.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.agentreport.dao.AgentReportStatRepository;
import app.programmatic.ui.agentreport.dao.model.AgentReportStatus;
import app.programmatic.ui.agentreport.dao.model.AgentReportStat;

import javax.validation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class AgentReportValidationServiceImpl implements ConstraintValidator<ValidateStats, List<AgentReportStat>> {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Value("${agentReport.currencyAccuracy}")
    private Integer scale;

    @Autowired
    private AgentReportStatRepository financialCampaignStatRepository;

    @Override
    public void initialize(ValidateStats constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<AgentReportStat> stats, ConstraintValidatorContext context) {
        ConstraintViolationBuilder<AgentReportStat> builder = new ConstraintViolationBuilder<>();

        ListIterator<AgentReportStat> it = stats.listIterator();
        while (it.hasNext()) {
            AgentReportStat stat = it.next();

            AgentReportStat existing = stat.getId() == null ? null : financialCampaignStatRepository.findById(stat.getId()).orElse(null);
            if (existing != null && existing.getStatus() == AgentReportStatus.CLOSED.getLetter().charAt(0)) {
                builder.addGeneralViolationMessage("entity.field.error.changeForbidden");
            }

            Set<ConstraintViolation<AgentReportStat>> violations = validator.validate(stat);
            ConstraintViolationBuilder<AgentReportStat> childBuilder = builder.withIndex(it.previousIndex());
            childBuilder.addConstraintViolation(violations);

            BigDecimal pubAmountConfirmed = stat.getPubAmountConfirmed();
            if (pubAmountConfirmed != null) {
                pubAmountConfirmed.stripTrailingZeros();
                if (pubAmountConfirmed.scale() > scale) {
                    childBuilder.addViolationDescription("pubAmountConfirmed", "entity.field.error.tooMuchAccuracy", scale);
                }
            }

            validateClose(stat, childBuilder);
        }

        return builder.buildAndPushToContext(context).isValid();
    }

    private void validateClose(AgentReportStat stat, ConstraintViolationBuilder<AgentReportStat> builder) {
        if (AgentReportStatus.CLOSED.getLetter().equals(stat.getStatus().toString())) {
            if (stat.getInventoryAmountConfirmed() == null) {
                builder.addViolationDescription("inventoryAmountConfirmed", "entity.field.error.mandatory");
            }

            if (stat.getInvoiceNumber() == null) {
                builder.addViolationDescription("invoiceNumber", "entity.field.error.mandatory");
            }

            if (stat.getPubAmountConfirmed() == null) {
                builder.addViolationDescription("pubAmountConfirmed", "entity.field.error.mandatory");
            }
        }
    }
}
