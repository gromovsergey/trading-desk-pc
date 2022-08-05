package com.foros.util.preview.token;

import com.foros.model.template.Option;
import com.foros.model.template.OptionValue;
import com.foros.session.creative.PreviewException;
import com.foros.util.Function;
import com.foros.util.preview.PreviewContext;
import com.foros.util.preview.SubstitutionTemplateParser;
import com.foros.util.preview.TokenDefinition;

import java.util.Set;

public abstract class OptionTokenDefinitionSupport implements TokenDefinition {
    protected final Option option;
    protected final Set<String> substitutions;

    public OptionTokenDefinitionSupport(Option option) {
        this(option, option.buildSubstitutionTokens());
    }

    protected OptionTokenDefinitionSupport(Option option, Set<String> substitutions) {
        this.substitutions = substitutions;
        this.option = option;
    }

    public String evaluate(PreviewContext context) {
        String value;
        OptionValue optionValue = context.getOptionValue(option.getId());
        if (optionValue != null) {
            value = optionValue.getValue();
        } else {
            value = option.getDefaultValue();
        }

        if (value == null || value.isEmpty()) {
            return "";
        }

        try {
            value = performSubstitution(context, value);
        } catch (PreviewException e) {
            // invalid values should not be saved
            value = "";
        }

        return value;
    }

    private String performSubstitution(final PreviewContext context, String value) {
        return SubstitutionTemplateParser.parse(value).render(new Function<String, String>() {
            @Override
            public String apply(String token) {
                if (!substitutions.contains(token)) {
                    return "";
                } else {

                    return context.evaluateToken(token);
                }
            }
        });
    }

    public Option getOption() {
        return option;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + option.getId() + ", " + option.getToken() + "]";
    }
}
