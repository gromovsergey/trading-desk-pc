package app.programmatic.ui.common.validation;

import app.programmatic.ui.common.i18n.MessageInterpolator;
import app.programmatic.ui.common.validation.exception.ExpectedForosViolationsException;
import app.programmatic.ui.common.validation.pathalias.ValidationPathAlias;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;


public class ConstraintViolationBuilder<T> {
    public static String GENERAL_ERROR_FIELD_NAME = "actionError";
    static String CURRENT_ENTITY_MARKER = "root";
    private static MessageInterpolator MESSAGE_INTERPOLATOR = MessageInterpolator.getDefaultMessageInterpolator();

    private final Collection<ValidationPathAlias> pathAliases;
    final Map<String, Set<String>> violationDescriptions = new HashMap<>();

    public ConstraintViolationBuilder() {
        this.pathAliases = Collections.emptyList();
    }

    public ConstraintViolationBuilder(Collection<ValidationPathAlias> pathAliases) {
        this.pathAliases = pathAliases;
    }

    public <E> ConstraintViolationBuilder<E> buildSubNode(String childPath) {
        return new ChildBuilder<E>(childPath, this);
    }

    public <P> ConstraintViolationBuilder<P> cast() {
        return new ChildBuilder<P>(this);
    }

    public <T> ConstraintViolationBuilder<T> withIndex(long index) {
        return new IndexedBuilder<T>(index, this);
    }

    public boolean hasErrors() {
        return !violationDescriptions.isEmpty();
    }

    public void addConstraintViolation(Collection<ConstraintViolation<T>> constraintViolations) {
        constraintViolations.stream()
                .forEach(violation -> addConstraintViolation(violation));
    }

    public void addConstraintViolation(ConstraintViolation<T> violation) {
        addViolationDescription(violation.getPropertyPath().toString(), violation.getMessage());
    }

    public void addViolationDescription(String path, String message, Object... parameters) {
        addViolationDescription(convertPath(path), interpolate(message, parameters), violationDescriptions);
    }

    public <E> void addViolationDescription(ConstraintViolationBuilder<E> another) {
        for (Map.Entry<String, Set<String>> anotherEntry : another.violationDescriptions.entrySet()) {
            Set<String> messages = violationDescriptions.get(anotherEntry.getKey());
            if (messages == null) {
                messages = new HashSet<>();
                violationDescriptions.put(anotherEntry.getKey(), messages);
            }

            messages.addAll(anotherEntry.getValue());
        }
    }

    public void addViolationMessage(String path, String message) {
        addViolationDescription(convertPath(path), message, violationDescriptions);
    }

    public void addGeneralViolationMessage(String message) {
        addViolationDescription(GENERAL_ERROR_FIELD_NAME, message, violationDescriptions);
    }

    private String convertPath(String path) {
        return pathAliases.stream()
                .filter( alias -> path.startsWith(alias.getName()) )
                .map( alias -> alias.getAlias() )
                .findFirst()
                .orElse(path);
    }

    private String interpolate(String message, Object... parameters) {
        return MESSAGE_INTERPOLATOR.interpolate(message, parameters);
    }

    private static void addViolationDescription(String path, String message, Map<String, Set<String>> map) {
        Set<String> messages = map.get(path);
        if (messages == null) {
            messages = new HashSet<>();
            map.put(path, messages);
        }

        messages.add(message);
    }

    public BuilderResult buildAndPushToContext(ConstraintValidatorContext context) {
        if (violationDescriptions.isEmpty()) {
            return BuilderResult.VALID;
        }

        context.disableDefaultConstraintViolation();

        for (Map.Entry<String, Set<String>> entry : violationDescriptions.entrySet()) {
            for (String message : entry.getValue()) {
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(CURRENT_ENTITY_MARKER + "." + entry.getKey())
                        .addConstraintViolation();
            }
        }

        return BuilderResult.NOT_VALID;
    }

    public void throwExpectedException() throws ExpectedForosViolationsException {
        if (!violationDescriptions.isEmpty()) {
            throw new ExpectedForosViolationsException(violationDescriptions);
        }
    }

    public static void throwExpectedException(String errorKey, Object... parameters) throws ExpectedForosViolationsException {
        HashMap<String, Set<String>> tmp = new HashMap();
        tmp.put(GENERAL_ERROR_FIELD_NAME, Collections.singleton(MESSAGE_INTERPOLATOR.interpolate(errorKey, parameters)));
        throw new ExpectedForosViolationsException(tmp);
    }

    public enum BuilderResult {
        VALID(true),
        NOT_VALID(false);

        private boolean isValid;

        BuilderResult(boolean isValid) {
            this.isValid = isValid;
        }

        public boolean isValid() {
            return isValid;
        }
    }

    private class ChildBuilder<E> extends ConstraintViolationBuilder<E> {
        private String childPath;
        private ConstraintViolationBuilder<T> parent;

        public ChildBuilder(ConstraintViolationBuilder<T> parent) {
            this(null, parent);
        }

        public ChildBuilder(String childPath, ConstraintViolationBuilder<T> parent) {
            this.childPath = childPath;
            this.parent = parent;
        }

        @Override
        public void addViolationDescription(String path, String message, Object... parameters) {
            String newPath = childPath == null ? path : (childPath + "." + path);
            parent.addViolationDescription(newPath, message, parameters);
        }
    }

    private class IndexedBuilder<E> extends ConstraintViolationBuilder<E> {
        private long index;
        private String arrayPath;
        private ConstraintViolationBuilder<T> parent;

        public IndexedBuilder(long index, ConstraintViolationBuilder<T> parent) {
            this.index = index;
            this.arrayPath = arrayPath;
            this.parent = parent;
        }

        @Override
        public void addViolationDescription(String path, String message, Object... parameters) {
            parent.addViolationDescription(String.format("rows[%d].%s", index, path), message, parameters);
        }
    }
}
