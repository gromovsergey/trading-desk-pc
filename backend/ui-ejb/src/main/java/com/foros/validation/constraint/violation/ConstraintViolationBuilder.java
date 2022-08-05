package com.foros.validation.constraint.violation;

import com.foros.validation.code.ForosError;
import com.foros.validation.interpolator.MessageInterpolator;
import com.foros.validation.interpolator.MessageTemplate;
import com.foros.validation.interpolator.ForosErrorResolver;

public class ConstraintViolationBuilder<T extends ConstraintViolationBuilder> {

    private Path basePath;

    private String template;
    private Path path = Path.empty();
    private Object[] parameters;
    private Object value;
    private ForosError error;

    public ConstraintViolationBuilder(String template, Path basePath) {
        this.template = template;
        this.basePath = basePath;
    }

    public T withValue(Object value) {
        this.value = value;
        return self();
    }

    public T withParameters(Object... parameters) {
        this.parameters = parameters;
        return self();
    }

    public T withPath(String... path) {
        this.path = Path.fromArray(path);
        return self();
    }

    public T withError(ForosError error) {
        this.error = error;
        return self();
    }

    protected T self() {
        //noinspection unchecked
        return (T) this;
    }

    public ConstraintViolation build(ForosErrorResolver errorResolver, MessageInterpolator interpolator) {
        MessageTemplate messageTemplate = new MessageTemplate(template, parameters);

        if (error == null) {
            error = errorResolver.resolve(messageTemplate);
        }
        String message = interpolator.interpolate(messageTemplate);

        return new ConstraintViolationImpl(error, message, basePath.add(path), value, template);
    }
}
