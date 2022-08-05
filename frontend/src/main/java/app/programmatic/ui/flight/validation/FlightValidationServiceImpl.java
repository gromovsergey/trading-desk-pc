package app.programmatic.ui.flight.validation;

import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.common.validation.pathalias.ValidationPathAliasesService;
import app.programmatic.ui.common.validation.strategy.CreateValidationStrategy;
import app.programmatic.ui.common.validation.strategy.NullForbiddenValidationStrategy;
import app.programmatic.ui.common.validation.strategy.UpdateValidationStrategy;
import app.programmatic.ui.common.validation.strategy.ValidationStrategy;
import app.programmatic.ui.device.service.DeviceService;
import app.programmatic.ui.flight.dao.FlightRepository;
import app.programmatic.ui.flight.dao.LineItemRepository;
import app.programmatic.ui.flight.dao.model.DeliveryPacing;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.Opportunity;

import org.springframework.beans.factory.annotation.Autowired;
import app.programmatic.ui.flight.service.FlightService;
import app.programmatic.ui.geo.service.GeoService;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FlightValidationServiceImpl extends FlightBaseValidationServiceImpl<ValidateFlight, Flight> {
    private static final Logger logger = Logger.getLogger(FlightValidationServiceImpl.class.getName());
    private static final ValidationStrategy createValidationStrategy = new CreateValidationStrategy(new NullForbiddenValidationStrategy());
    private static final ValidationStrategy updateValidationStrategy = new UpdateValidationStrategy(new NullForbiddenValidationStrategy());

    private final AccountService accountService;
    private final FlightRepository flightRepository;
    private final FlightService flightService;

    @Autowired
    public FlightValidationServiceImpl(ValidationPathAliasesService validationPathAliasesService,
                                       DeviceService deviceService,
                                       AccountService accountService,
                                       GeoService geoService,
                                       FlightRepository flightRepository,
                                       LineItemRepository lineItemRepository,
                                       FlightService flightService) {
        super(validationPathAliasesService, deviceService, accountService, geoService, lineItemRepository);
        this.accountService = accountService;
        this.flightRepository = flightRepository;
        this.flightService = flightService;
    }

    @Override
    protected String getValidateMethod(ValidateFlight constraintAnnotation) {
        return constraintAnnotation.value();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected Flight findEntity(Long id) {
        return flightRepository.findById(id).orElse(null);
    }

    @Override
    protected ValidationStrategy createValidationStrategy() {
        return createValidationStrategy;
    }

    @Override
    protected ValidationStrategy updateValidationStrategy() {
        return updateValidationStrategy;
    }

    @Override
    protected FlightBaseValidator newValidator(Flight flight, ValidationStrategy strategy) {
        return new FlightValidator(flight,
                                   flight.getOpportunity() == null ? null : flight.getOpportunity().getAccountId(),
                                   strategy);
    }

    private class FlightValidator extends FlightBaseValidator {

        private FlightValidator(Flight validated, Long accountId, ValidationStrategy strategy) {
            super(validated, accountId, strategy);
        }

        @Override
        public void validate(ConstraintViolationBuilder<Flight> builder) {
            super.validate(builder);
            validateFlightBudget(builder);
            validateOpportunity(validated.getOpportunity(), builder.buildSubNode("opportunity"));
            validateFlightDeliveryPacing(validated, builder);
            validateFlightImpressionsPacing(validated, builder);
            validateFlightClicksPacing(validated, builder);
        }

        private void validateOpportunity(Opportunity validated, ConstraintViolationBuilder<Opportunity> builder) {
            strategy.checkBean(validated.getId(), builder, (Long id) -> getExistingOpportunity());

            AdvertisingAccount account = null;
            try {
                account = validated.getAccountId() == null ? null : accountService.findAdvertisingUnchecked(validated.getAccountId());
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "'Find account' method should not throw exceptions", e);
            }
            if (account == null) {
                builder.addViolationDescription("accountId", "entity.error.notFound", String.valueOf(validated.getAccountId()));
            } else {
                strategy.checkValueNotChanged(validated.getAccountId(), account.getId(), "accountId", builder);
            }
        }

        private void validateFlightDeliveryPacing(Flight validated, ConstraintViolationBuilder<Flight> builder) {
            if (validated.getDeliveryPacing() == null) {
                builder.addViolationDescription("deliveryPacing", "entity.field.error.mandatory");
                return;
            }

            if (validated.getDeliveryPacing() == DeliveryPacing.F) {
                // Error will be signaled in base validator if null
                if (validated.getDailyBudget() != null) {
                    BigDecimal flightBudget = validated.getOpportunity().getAmount();

                    if (validated.getDailyBudget().compareTo(flightBudget) > 0) {
                        builder.addViolationDescription("dailyBudget", "entity.field.error.maxValueExceeded", flightBudget);
                    }
                }
            }

            super.validateDeliveryPacing(validated, builder);
        }

        private void validateFlightImpressionsPacing(Flight validated, ConstraintViolationBuilder<Flight> builder) {
            if (validated.getImpressionsPacing() == null) {
                builder.addViolationDescription("impressionsPacing", "entity.field.error.mandatory");
                return;
            }

            super.validateImpressionsPacing(validated, builder);
        }

        private void validateFlightClicksPacing(Flight validated, ConstraintViolationBuilder<Flight> builder) {
            if (validated.getClicksPacing() == null) {
                builder.addViolationDescription("clicksPacing", "entity.field.error.mandatory");
                return;
            }

            super.validateClicksPacing(validated, builder);
        }

        private void validateFlightBudget(ConstraintViolationBuilder<Flight> builder) {
            validateBudget(validated.getOpportunity().getAmount(), builder);
            if (validated.getOpportunity().getAmount() != null &&
                    !validated.getOpportunity().getAmount().equals(validated.getBudget())) {
                builder.addViolationDescription("budget", "entity.field.error.mandatory");
            }
        }

        private Opportunity getExistingOpportunity() {
            return existing == null ? null : existing.getOpportunity();
        }

        @Override
        protected BigDecimal getSpentBudget() {
            return validated.getId() == null ? BigDecimal.ZERO : flightService.getStat(validated.getId()).getSpentBudget();
        }
    }
}
