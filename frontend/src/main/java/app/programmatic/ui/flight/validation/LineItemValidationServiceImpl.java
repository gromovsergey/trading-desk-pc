package app.programmatic.ui.flight.validation;

import static app.programmatic.ui.common.validation.ConstraintViolationBuilder.GENERAL_ERROR_FIELD_NAME;

import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.channel.service.ChannelService;
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
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.service.LineItemService;
import app.programmatic.ui.flight.tool.EffectiveLineItemTool;
import app.programmatic.ui.geo.service.GeoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidatorContext;


public class LineItemValidationServiceImpl extends FlightBaseValidationServiceImpl<ValidateLineItem, LineItem> {
    private static final Logger logger = Logger.getLogger(FlightValidationServiceImpl.class.getName());
    private static final ValidationStrategy createValidationStrategy = new CreateValidationStrategy(new NullForbiddenValidationStrategy());
    private static final ValidationStrategy updateValidationStrategy = new UpdateValidationStrategy(new NullForbiddenValidationStrategy());

    private final ChannelService channelService;
    private final LineItemRepository lineItemRepository;
    private final LineItemService lineItemService;
    private final FlightRepository flightRepository;

    @Autowired
    public LineItemValidationServiceImpl(ValidationPathAliasesService validationPathAliasesService,
                                         DeviceService deviceService,
                                         AccountService accountService,
                                         GeoService geoService,
                                         ChannelService channelService,
                                         LineItemRepository lineItemRepository,
                                         LineItemService lineItemService,
                                         FlightRepository flightRepository) {
        super(validationPathAliasesService, deviceService, accountService, geoService, lineItemRepository);
        this.channelService = channelService;
        this.lineItemRepository = lineItemRepository;
        this.lineItemService = lineItemService;
        this.flightRepository = flightRepository;
    }

    @Override
    protected String getValidateMethod(ValidateLineItem constraintAnnotation) {
        return constraintAnnotation.value();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected LineItem findEntity(Long id) {
        return lineItemRepository.findById(id).orElse(null);
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
    protected FlightBaseValidator newValidator(LineItem lineItem, ValidationStrategy strategy) {
        Flight owner = lineItem.getFlightId() == null ? null : flightRepository.findById(lineItem.getFlightId()).orElse(null);
        Long accountId = owner == null ? null : owner.getOpportunity().getAccountId();
        LineItem effectiveLineItem =  EffectiveLineItemTool.buildEffective(lineItem, owner);
        return new LineItemValidator(effectiveLineItem, accountId, strategy);
    }

    @Override
    protected boolean validateActivate(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof Collection) {
            return validateChangeStatusAll((Collection<Long>)obj, context);
        }
        return super.validateActivate(obj, context);
    }

    @Override
    protected boolean validateInactivate(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof Collection) {
            return validateChangeStatusAll((Collection<Long>)obj, context);
        }
        return super.validateInactivate(obj, context);
    }

    @Override
    protected boolean validateDelete(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof Collection) {
            return validateChangeStatusAll((Collection<Long>)obj, context);
        }
        return super.validateDelete(obj, context);
    }

    private boolean validateChangeStatusAll(Collection<Long> ids, ConstraintValidatorContext context) {
        Iterable<LineItem> lineItems = lineItemRepository.findAllById(ids);

        Set<Long> fetchedIds = new HashSet<>(ids.size());
        Long flightId = null;
        for (LineItem lineItem : lineItems) {
            if (flightId == null) {
                flightId = lineItem.getFlightId();
            }
            if (!flightId.equals(lineItem.getFlightId())) {
                return multipleFlightsError(flightId, lineItem.getFlightId(), context);
            }

            fetchedIds.add(lineItem.getId());
        }

        if (ids.size() != fetchedIds.size() || !fetchedIds.containsAll(ids)) {
            return notFoundError(ids, fetchedIds, context);
        }

        return true;
    }

    private boolean multipleFlightsError(Long flightId1, Long flightId2, ConstraintValidatorContext context) {
        ConstraintViolationBuilder<LineItem> builder = new ConstraintViolationBuilder<>();
        builder.addViolationDescription(GENERAL_ERROR_FIELD_NAME, "lineItem.bulk.error.multipleFlights", flightId1, flightId2);
        builder.buildAndPushToContext(context);
        return false;
    }

    private boolean notFoundError(Collection<Long> ids, Set<Long> fetchedIds, ConstraintValidatorContext context) {
        HashSet<Long> notFoundIds = new HashSet<>(ids);
        notFoundIds.removeAll(fetchedIds);

        ConstraintViolationBuilder<LineItem> builder = new ConstraintViolationBuilder<>();
        builder.addViolationDescription(GENERAL_ERROR_FIELD_NAME, "entity.error.notFound",
                notFoundIds.stream()
                        .map( id -> String.valueOf(id) )
                        .collect(Collectors.joining(", "))
        );
        builder.buildAndPushToContext(context);

        return false;
    }

    private class LineItemValidator extends FlightBaseValidator {
        private final BigDecimal MIN_BUDGET = BigDecimal.valueOf(0);
        private final BigDecimal MAX_BUDGET = BigDecimal.valueOf(1000000000);
        private final int MAX_NAME_LENGTH = 200;

        private LineItemValidator(LineItem validated, Long accountId, ValidationStrategy strategy) {
            super(validated, accountId, strategy);
        }

        @Override
        public void validate(ConstraintViolationBuilder<LineItem> builder) {
            super.validate(builder);

            validateName(validated, builder);
            validateLineItemBudget(validated, builder);
            validateLineItemDeliveryPacing(validated, builder);
            validateLineItemImpressionsPacing(validated, builder);
            validateLineItemClicksPacing(validated, builder);
            validateWithFlightDates(validated, builder);
            validateSpecialChannel(validated, existing, builder);
        }

        private void validateLineItemBudget(LineItem validated, ConstraintViolationBuilder<LineItem> builder) {
            if (!strategy.checkNotNull(validated.getBudget(), "budget", builder)) {
                return;
            }

            if (validated.getBudget().compareTo(MIN_BUDGET) <= 0 ||
                    validated.getBudget().compareTo(MAX_BUDGET) >= 0) {
                builder.addViolationDescription("budget", "entity.field.error.notInRangeExclusive", MIN_BUDGET, MAX_BUDGET);
            }

            BigDecimal scaledBudget = validated.getBudget().stripTrailingZeros();
            if (scaledBudget.scale() > 5) {
                builder.addViolationDescription("budget", "entity.field.error.tooMuchAccuracy", ".#####");
            }

            validateBudget(validated.getBudget(), builder);
        }

        private void validateName(LineItem validated, ConstraintViolationBuilder<LineItem> builder) {
            if (validated.getName() == null || validated.getName().trim().isEmpty()) {
                builder.addViolationDescription("name", "entity.field.error.mandatory");
                return;
            }

            if (validated.getName().length() > 200) {
                builder.addViolationDescription("name", "entity.field.error.maxLengthExceeded", MAX_NAME_LENGTH);
            }
        }

        private void validateWithFlightDates(LineItem validated, ConstraintViolationBuilder<LineItem> builder) {
            if (validated.getFlightId() == null || validated.getDateStart() == null && validated.getDateEnd() == null) {
                // Flight Id not set - error will be recorded elsewhere
                // LI dates not set -> OK
                return;
            }

            Flight owner = flightRepository.findById(validated.getFlightId()).orElse(null);

            if (validated.getDateStart() != null) {
                if (owner.getDateStart().isAfter(validated.getDateStart())) {
                    builder.addViolationDescription("dateStart", "flight.dateStart.error.beforeFlightStart");
                }
            }

            if (validated.getDateEnd() == null || owner.getDateEnd() == null) {
                // LI end date not set or Flight date not set -> OK
                return;
            }

            if (validated.getDateEnd().isAfter(owner.getDateEnd())) {
                builder.addViolationDescription("dateEnd", "flight.dateEnd.error.afterFlightEnd");
            }
        }

        private void validateLineItemDeliveryPacing(LineItem validated, ConstraintViolationBuilder<LineItem> builder) {
            if (validated.getDeliveryPacing() == null) {
                return;
            }

            if (validated.getDeliveryPacing() == DeliveryPacing.F) {
                if (validated.getDailyBudget() != null) {
                    BigDecimal lineItemBudget = validated.getBudget();
                    if (lineItemBudget == null) {
                        if (validated.getFlightId() == null) {
                            // Error will be signaled elsewhere
                            return;
                        }
                        Flight owner = flightRepository.findById(validated.getFlightId()).orElse(null);
                        lineItemBudget = owner.getOpportunity().getAmount();
                    }

                    if (validated.getDailyBudget().compareTo(lineItemBudget) > 0) {
                        builder.addViolationDescription("dailyBudget", "entity.field.error.maxValueExceeded", lineItemBudget);
                    }
                }
            }

            super.validateDeliveryPacing(validated, builder);
        }

        private void validateLineItemImpressionsPacing(LineItem validated, ConstraintViolationBuilder<LineItem> builder) {
            if (validated.getImpressionsPacing() == null) {
                builder.addViolationDescription("impressionsPacing", "entity.field.error.mandatory");
                return;
            }

            super.validateImpressionsPacing(validated, builder);
            super.validateClicksPacing(validated, builder);
        }

        private void validateLineItemClicksPacing(LineItem validated, ConstraintViolationBuilder<LineItem> builder) {
            if (validated.getClicksPacing() == null) {
                builder.addViolationDescription("clicksPacing", "entity.field.error.mandatory");
                return;
            }

            super.validateClicksPacing(validated, builder);
        }

        private void validateSpecialChannel(LineItem validated, LineItem existing, ConstraintViolationBuilder<LineItem> builder) {
            Long validatedChannelId = validated.getSpecialChannelId();
            Long existingChannelId = existing == null ? null : existing.getSpecialChannelId();

            if (validatedChannelId != null && existingChannelId == null) {
                boolean isValid = channelService.checkSpecialChannelConstraints(validatedChannelId, validated.getId());
                isValid = isValid && lineItemRepository.countBySpecialChannelIdOrChannelIds(
                                        validatedChannelId, validatedChannelId) == 0;
                if (!isValid) {
                    builder.addViolationDescription("specialChannelId", "entity.field.error.changeForbidden");
                }
                return;
            }
            if (!ObjectUtils.nullSafeEquals(existingChannelId, validatedChannelId)) {
                builder.addViolationDescription("specialChannelId", "entity.field.error.changeForbidden");
            }
        }

        @Override
        protected BigDecimal getSpentBudget() {
            return validated.getId() == null ? BigDecimal.ZERO : lineItemService.getStat(validated.getId()).getSpentBudget();
        }
    }
}
