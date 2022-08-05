package com.foros.reporting.meta;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SimpleDependenciesColumnResolver<C extends AbstractDependentColumn<C>> implements DependenciesColumnResolver<C> {

    private Set<C> columns;

    @SafeVarargs
    public SimpleDependenciesColumnResolver(C... columns) {
        this(new HashSet<>(Arrays.asList(columns)));
    }

    public SimpleDependenciesColumnResolver(Set<C> columns) {
        this.columns = Collections.unmodifiableSet(columns);
    }

    @Override
    public Set<C> resolve(Object context) {
        return columns;
    }

}
