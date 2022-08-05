package app.programmatic.ui.flight.validation;

import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.common.validation.pathalias.ValidationPathAliasesService;
import app.programmatic.ui.common.validation.strategy.ValidationStrategy;
import app.programmatic.ui.common.view.TimeUnit;
import app.programmatic.ui.device.dao.model.DeviceNode;
import app.programmatic.ui.device.service.DeviceService;
import app.programmatic.ui.flight.dao.LineItemRepository;
import app.programmatic.ui.flight.dao.model.*;
import app.programmatic.ui.geo.dao.model.AddressTO;
import app.programmatic.ui.geo.service.GeoService;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;


public abstract class FlightBaseValidationServiceImpl<A extends Annotation, T extends FlightBase> implements ConstraintValidator<A, Object> {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final ValidationPathAliasesService validationPathAliasesService;
    private final DeviceService deviceService;
    private final AccountService accountService;
    private final GeoService geoService;
    private final LineItemRepository lineItemRepository;

    protected String validateMethod;

    public FlightBaseValidationServiceImpl(ValidationPathAliasesService validationPathAliasesService,
                                           DeviceService deviceService,
                                           AccountService accountService,
                                           GeoService geoService,
                                           LineItemRepository lineItemRepository) {
        this.validationPathAliasesService = validationPathAliasesService;
        this.deviceService = deviceService;
        this.accountService = accountService;
        this.geoService = geoService;
        this.lineItemRepository = lineItemRepository;
    }

    protected abstract String getValidateMethod(A constraintAnnotation);

    protected abstract Logger getLogger();

    protected abstract T findEntity(Long id);

    protected abstract ValidationStrategy createValidationStrategy();

    protected abstract ValidationStrategy updateValidationStrategy();

    protected abstract FlightBaseValidator newValidator(T flightBase, ValidationStrategy strategy);

    @Override
    public final void initialize(A constraintAnnotation) {
        validateMethod = getValidateMethod(constraintAnnotation);
    }

    @Override
    public final boolean isValid(Object value, ConstraintValidatorContext context) {
        switch (validateMethod) {
            case "create": return validateCreate(value, context);
            case "update": return validateUpdate(value, context);
            case "activate": return validateActivate(value, context);
            case "inactivate": return validateInactivate(value, context);
            case "delete": return validateDelete(value, context);
            case "linkAdvertisingChannels": return validateLinkAdvertisingChannels(value, context);
        }
        throw new IllegalArgumentException("Unknown validation method " + validateMethod);
    }

    private boolean validateCreate(Object object, ConstraintValidatorContext context) {
        return validateCreate((T)object, context);
    }

    private boolean validateUpdate(Object object, ConstraintValidatorContext context) {
        return validateUpdate((T)object, context);
    }

    protected boolean validateActivate(Object obj, ConstraintValidatorContext context) {
        return validateActivate( obj instanceof Long ? (Long)obj : ((T)obj).getId(), context);
    }

    protected boolean validateInactivate(Object obj, ConstraintValidatorContext context) {
        return validateInactivate( obj instanceof Long ? (Long)obj : ((T)obj).getId(), context);
    }

    protected boolean validateDelete(Object obj, ConstraintValidatorContext context) {
        return validateDelete( obj instanceof Long ? (Long)obj : ((T)obj).getId(), context);
    }

    private boolean validateLinkAdvertisingChannels(Object obj, ConstraintValidatorContext context) {
        List<Long> channelIds = (List<Long>)obj;
        ConstraintViolationBuilder builder = new ConstraintViolationBuilder(validationPathAliasesService.getAliases());
        validateChannels(channelIds, builder);
        return builder.buildAndPushToContext(context).isValid();
    }

    public final boolean validateCreate(T flightBase, ConstraintValidatorContext context) {
        FlightBaseValidator flightBaseValidator = newValidator(flightBase, createValidationStrategy());
        return validate(flightBaseValidator, context);
    }

    public final boolean validateUpdate(T flightBase, ConstraintValidatorContext context) {
        FlightBaseValidator flightBaseValidator = newValidator(flightBase, updateValidationStrategy());
        return validate(flightBaseValidator, context);
    }

    public final boolean validate(FlightBaseValidator flightBaseValidator, ConstraintValidatorContext context) {
        ConstraintViolationBuilder builder = new ConstraintViolationBuilder(validationPathAliasesService.getAliases());
        flightBaseValidator.validate(builder);
        return builder.buildAndPushToContext(context).isValid();
    }

    public final boolean validateActivate(Long id, ConstraintValidatorContext context) {
        T entity = findEntity(id);
        if (!checkEntityIsFound(id, entity, context)) {
            return false;
        }
        return validateStatusIsDifferent(entity, MajorDisplayStatus.LIVE, context);
    }

    public final boolean validateInactivate(Long id, ConstraintValidatorContext context) {
        T entity = findEntity(id);
        if (!checkEntityIsFound(id, entity, context)) {
            return false;
        }
        return validateStatusIsDifferent(entity, MajorDisplayStatus.INACTIVE, context);
    }

    public final boolean validateDelete(Long id, ConstraintValidatorContext context) {
        T entity = findEntity(id);
        if (!checkEntityIsFound(id, entity, context)) {
            return false;
        }
        return validateStatusIsDifferent(entity, MajorDisplayStatus.DELETED, context);
    }

    protected boolean checkEntityIsFound(Long id, T flightBase, ConstraintValidatorContext context) {
        if (flightBase != null) {
            return true;
        }

        ConstraintViolationBuilder<T> builder = new ConstraintViolationBuilder<>();
        builder.addViolationDescription("id", "entity.error.notFound", String.valueOf(id));
        builder.buildAndPushToContext(context);

        return false;
    }

    protected boolean validateStatusIsDifferent(T flightBase, MajorDisplayStatus displayStatus, ConstraintValidatorContext context) {
        // ToDo: do we need this ?
//        if (flightBase.getDisplayStatus() == displayStatus) {
//            ConstraintViolationBuilder<T> builder = new ConstraintViolationBuilder<>();
//            builder.addViolationDescription("status", "flight.status.error.nothingToChange", String.valueOf(displayStatus));
//            builder.buildAndPushToContext(context);
//            return false;
//        }

        return true;
    }

    private void validateChannels(List<Long> validated, ConstraintViolationBuilder<T> builder) {
        List<SpecialChannelIdProjection> ids = lineItemRepository.findSpecialChannelIdBySpecialChannelIdIn(validated);
        if (!ids.isEmpty()) {
            builder.addViolationDescription("channelIds",
                    "flight.channels.error.notFound",
                    ids.stream().map(p -> p.getSpecialChannelId().toString()).collect(Collectors.joining(", ")));
        }
    }

    protected abstract class FlightBaseValidator {
        protected final T validated;
        protected final ValidationStrategy strategy;
        protected T existing;
        protected Long accountId;
        private AdvertisingAccount account;

        protected FlightBaseValidator(T validated, Long accountId, ValidationStrategy strategy) {
            this.validated = validated;
            this.strategy = strategy;
            this.accountId = accountId;
        }

        public void validate(ConstraintViolationBuilder<T> builder) {
            Set<ConstraintViolation<T>> violations = validator.validate(validated);
            builder.addConstraintViolation(violations);

            existing = strategy.checkBean(validated.getId(), builder, (Long id) -> findEntity(id));

            strategy.checkInitialValue(validated.getMajorStatus(), MajorDisplayStatus.INACTIVE, "displayStatus", builder);

            validateDates(validated.getDateStart(), validated.getDateEnd(), builder);
            validateFrequencyCap(validated.getFrequencyCap(), builder.buildSubNode("frequencyCap"));
            validateDevices(validated.getDeviceChannelIds(), validated.getId(), builder.buildSubNode("deviceChannelIds"));
            validateRate(validated, builder);
            validateGeoChannels(validated.getGeoChannelIds(), "geoChannelIds", builder);
            validateGeoChannels(validated.getExcludedGeoChannelIds(), "excludedGeoChannelIds", builder);
            validateChannels(validated.getChannelIds(), builder);
        }

        protected AdvertisingAccount getAccount() {
            if (account == null && accountId != null) {
                account = accountService.findAdvertisingUnchecked(accountId);
            }
            return account;
        }

        protected void validateBudget(BigDecimal budget, ConstraintViolationBuilder<T> builder) {
            BigDecimal scaledBudget = budget == null ? null : budget.stripTrailingZeros();
            if (scaledBudget == null || scaledBudget.scale() > 5) {
                return;
            }

            if (scaledBudget.scale() > getAccount().getCurrencyAccuracy()) {
                builder.addViolationDescription("budget",
                        "entity.field.error.tooMuchAccuracy",
                        "." + ("#####".substring(0, getAccount().getCurrencyAccuracy())));
            }

            BigDecimal spent = getSpentBudget();
            if (budget.compareTo(spent) < 0) {
                builder.addViolationDescription("budget",
                        "flight.budget.error.lowerThanSpent",
                        String.valueOf(spent.setScale(getAccount().getCurrencyAccuracy(), RoundingMode.CEILING)));
            }
        }

        private void validateFrequencyCap(FrequencyCap validated, ConstraintViolationBuilder<FrequencyCap> builder) {
            if (validated == null) {
                return;
            }

            if (validated.getWindowCount() == null && validated.getWindowLength() != null) {
                builder.addViolationDescription("windowCount", "entity.field.error.mandatory");
            }
            if (validated.getWindowLength() == null && validated.getWindowCount() != null) {
                builder.addViolationDescription("windowLength", "entity.field.error.mandatory");
            }

            // Bug with xs:long in Old UI
            Long maxValueForLong = Long.valueOf(Integer.MAX_VALUE);
            if (validated.getWindowLength() != null && validated.getWindowLength().compareTo(maxValueForLong) > 0) {
                builder.addViolationDescription("windowLength",
                                                "entity.field.error.maxValueExceeded",
                                                maxValueForLong / getTimeUnitSeconds(validated.getWindowLength()));
            }
            if (validated.getPeriod() != null && validated.getPeriod().compareTo(maxValueForLong) > 0) {
                builder.addViolationDescription("period",
                                                "entity.field.error.maxValueExceeded",
                                                maxValueForLong / getTimeUnitSeconds(validated.getPeriod()));
            }

            if (validated.getId() != null) {
                strategy.checkBean(validated.getId(), builder, (Long id) -> getExistingFrequencyCap());
            }
        }

        private void validateDates(LocalDate dateStart, LocalDate dateEnd, ConstraintViolationBuilder<T> builder) {
            strategy.checkNotNull(dateStart, "dateStart", builder);

            if (dateStart != null && dateEnd != null && dateStart.isAfter(dateEnd)) {
                builder.addViolationDescription("dateEnd", "flight.dateEnd.error.endAfterStart");
            }
        }

        protected void validateDeliveryPacing(T validated, ConstraintViolationBuilder<T> builder) {
            if (validated.getDeliveryPacing() == DeliveryPacing.F) {
                if (validated.getDailyBudget() == null || validated.getDailyBudget().compareTo(BigDecimal.ZERO) <= 0) {
                    builder.addViolationDescription("dailyBudget", "flight.dailyBudget.error.mustBeSet");
                }
            } else {
                if (validated.getDailyBudget() != null) {
                    builder.addViolationDescription("dailyBudget", "flight.dailyBudget.error.mustNotBeSet");
                }
            }
        }

        protected void validateImpressionsPacing(T validated, ConstraintViolationBuilder<T> builder) {
            if (validated.getImpressionsPacing() == TargetingPacing.F) {
                if (validated.getImpressionsDailyLimit() == null || validated.getImpressionsDailyLimit().compareTo(BigDecimal.ZERO) <= 0) {
                    builder.addViolationDescription("impressionsDailyLimit", "flight.impressionsDailyLimit.error.mustBeSet");
                }
            } else {
                if (validated.getImpressionsDailyLimit() != null) {
                    builder.addViolationDescription("impressionsDailyLimit", "flight.impressionsDailyLimit.error.mustNotBeSet");
                }
            }
        }

        protected void validateClicksPacing(T validated, ConstraintViolationBuilder<T> builder) {
            if (validated.getClicksPacing() == TargetingPacing.F) {
                if (validated.getClicksDailyLimit() == null || validated.getClicksDailyLimit().compareTo(BigDecimal.ZERO) <= 0) {
                    builder.addViolationDescription("сlicksDailyLimit", "flight.сlicksDailyLimit.error.mustBeSet");
                }
            } else {
                if (validated.getClicksDailyLimit() != null) {
                    builder.addViolationDescription("сlicksDailyLimit", "flight.сlicksDailyLimit.error.mustNotBeSet");
                }
            }
        }

        private void validateDevices(Collection<Long> validated, Long flightBaseId, ConstraintViolationBuilder<Long> builder) {
            if (validated.isEmpty()) {
                return;
            }

            Collection<DeviceNode> devices = flightBaseId != null ? deviceService.getAvailableDevicesByFlightId(flightBaseId) :
                    deviceService.getAvailableDevicesByAccountId(accountId);
            HashSet<Long> selected = new HashSet<>(validated);

            for (DeviceNode device : devices) {
                Long violatedNode = isNodeViolated(device, selected);
                if (violatedNode != null) {
                    builder.addViolationDescription("[" + findPositionById(violatedNode, validated)+ "]", "flight.devices.error.childSelected");
                    return;
                }
            }

            HashSet<Long> selectedAndAvailable = new HashSet<>(selected.size());
            for (DeviceNode device : devices) {
                selectedAndAvailable(device, selected, selectedAndAvailable);
            }

            if (selected.size() != selectedAndAvailable.size() ||
                    !selected.containsAll(selectedAndAvailable)) {
                selected.removeAll(selectedAndAvailable);
                for (Long id : selected) {
                    builder.addViolationDescription("[" + findPositionById(id, validated)+ "]", "flight.devices.error.notAvailable");
                }
            }
        }

        private void selectedAndAvailable(DeviceNode device, HashSet<Long> selected, HashSet<Long> selectedAndAvailable) {
            if (selected.contains(device.getId())) {
                selectedAndAvailable.add(device.getId());
            }

            if (device.getChildren() == null || device.getChildren().isEmpty()) {
                return;
            }

            for (DeviceNode child : device.getChildren()) {
                selectedAndAvailable(child, selected, selectedAndAvailable);
            }
        }

        private Long isNodeViolated(DeviceNode device, HashSet<Long> selected) {
            if (selected.contains(device.getId())) {
                Long isChildSelected = isChildSelected(device, selected);
                if (isChildSelected != null) {
                    return isChildSelected;
                }
            }
            if (device.getChildren() == null || device.getChildren().isEmpty()) {
                return null;
            }

            for (DeviceNode child : device.getChildren()) {
                Long isNodeViolated = isNodeViolated(child, selected);
                if (isNodeViolated != null) {
                    return isNodeViolated;
                }
            }
            return null;
        }

        private Long isChildSelected(DeviceNode device, HashSet<Long> selected) {
            if (device.getChildren() == null || device.getChildren().isEmpty()) {
                return null;
            }

            for (DeviceNode child : device.getChildren()) {
                if (selected.contains(child) || isChildSelected(child, selected) != null) {
                    return child.getId();
                }
            }
            return null;
        }

        private Long findPositionById(Long id, Collection<Long> ids) {
            long position = 0;
            for(Long element : ids) {
                if (id.equals(element)) {
                    return position;
                }
                position++;
            }
            return null;
        }

        private void validateRate(T validated, ConstraintViolationBuilder<T> builder) {
            strategy.checkNotNull(validated.getRateType(),"rateType", builder);
            strategy.checkNotNull(validated.getRateValue(),"rateValue", builder);
        }

        private void validateGeoChannels(List<Long> validated, String path, ConstraintViolationBuilder<T> builder) {
            if (validated.isEmpty()) {
                return;
            }

            Collection<AddressTO> addresses = geoService.getAddresses(validated);

            String countryCode = getAccount().getCountryCode();
            for (AddressTO address : addresses) {
                if (!countryCode.equals(address.getCountryCode())) {
                    builder.addViolationDescription(path, "flight.geoChannels.errors.sameCountryAddresses");
                    return;
                }
            }
        }

        private FrequencyCap getExistingFrequencyCap() {
            return existing == null ? null : existing.getFrequencyCap();
        }

        protected abstract BigDecimal getSpentBudget();

        private int getTimeUnitSeconds(Long valueInSeconds) {
            return TimeUnit.determine(valueInSeconds).getMultiplier();
        }
    }
}
