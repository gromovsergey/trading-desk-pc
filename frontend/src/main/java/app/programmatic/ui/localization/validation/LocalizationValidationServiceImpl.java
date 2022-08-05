package app.programmatic.ui.localization.validation;

import org.springframework.beans.factory.annotation.Autowired;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.common.validation.strategy.NullForbiddenValidationStrategy;
import app.programmatic.ui.common.validation.strategy.UpdateValidationStrategy;
import app.programmatic.ui.common.validation.strategy.ValidationStrategy;
import app.programmatic.ui.localization.dao.LocalizationRepository;
import app.programmatic.ui.localization.dao.model.Localization;
import app.programmatic.ui.localization.dao.model.LocalizationId;
import app.programmatic.ui.localization.dao.model.LocalizationObjectKey;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

public class LocalizationValidationServiceImpl implements ConstraintValidator<ValidateLocalization, List<Localization>> {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static final ValidationStrategy updateValidationStrategy = new UpdateValidationStrategy(new NullForbiddenValidationStrategy());

    private static Set<String> localizationObjectKeys = Stream.of(LocalizationObjectKey.values())
            .map( l -> fetchPrefix(l.getPrefix()) )
            .collect(Collectors.toSet());

    @Autowired
    private LocalizationRepository localizationRepository;

    private LocalizationValidateMethod validateMethod;

    @Override
    public final void initialize(ValidateLocalization constraintAnnotation) {
        validateMethod = constraintAnnotation.value();
    }

    @Override
    public final boolean isValid(List<Localization> value, ConstraintValidatorContext context) {
        ConstraintViolationBuilder<?> builder = new ConstraintViolationBuilder<>();
        ConstraintViolationBuilder<Localization> listBuilder = builder.buildSubNode("localizations");

        switch (validateMethod) {
            case UPDATE:
                int index = 0;
                for (Localization localization : value) {
                    validateUpdated(localization, listBuilder.withIndex(index++));
                }

                break;
            case DELETE:
                index = 0;
                for (Localization localization : value) {
                    validateDeleted(localization, listBuilder.withIndex(index++));
                }

                break;
            default:
                throw new IllegalArgumentException("Unknown validate method: " + validateMethod);
        }

        return builder.buildAndPushToContext(context).isValid();
    }

    private void validateUpdated(Localization localization, ConstraintViolationBuilder<Localization> builder) {
        Set<ConstraintViolation<Localization>> violations = validator.validate(localization);
        builder.addConstraintViolation(violations);

        if (localization.getKey() == null || localization.getKey().isEmpty()) {
            return;
        }

        if (!localizationObjectKeys.contains(fetchPrefix(localization.getKey()))) {
            builder.addViolationDescription("key",
                                            "entity.field.error.oneOf",
                                            localizationObjectKeys.stream().collect(Collectors.joining(", ")));
        }
    }

    private void validateDeleted(Localization localization, ConstraintViolationBuilder<Localization> builder) {
        updateValidationStrategy.checkBean(new LocalizationId(localization.getKey(), localization.getLang()),
                                           builder,
                                           id -> localizationRepository.findById(id).orElse(null));
    }

    private static String fetchPrefix(String value) {
        String[] result = value.split("\\.");
        return result.length == 0 ? null : result[0];
    }
}

