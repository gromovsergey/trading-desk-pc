package com.foros.validation.strategy;

import com.foros.validation.ValidationContext;
import com.foros.validation.constraint.violation.Path;

import java.util.Collection;
import java.util.HashSet;

public class ExcludeValidationStrategy implements SubContextSupportStrategy {


    private Collection<String> absoluteExclusions = new HashSet<String>();

    private Collection<String> localExclusions = new HashSet<String>();

    public ExcludeValidationStrategy(Collection<String> absoluteExclusions) {
        this(Path.empty(), absoluteExclusions);
    }

    private ExcludeValidationStrategy(Path path, Collection<String> exclusions) {
        this.absoluteExclusions.addAll(exclusions);
        for (String absoluteExclusion : exclusions) {
            String localExclusion = Path.fromString(absoluteExclusion).subtract(path).toString();
            localExclusions.add(localExclusion);
        }
    }

    @Override
    public boolean isReachable(ValidationContext context, String fieldName) {
        return !absoluteExclusions.contains(fieldName)
                && !localExclusions.contains(fieldName);
    }

    @Override
    public ValidationStrategy subContextStrategy(Path path) {
        return new ExcludeValidationStrategy(path, absoluteExclusions);
    }

}
