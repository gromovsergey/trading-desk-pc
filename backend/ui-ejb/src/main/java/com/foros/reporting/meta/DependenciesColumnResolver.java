package com.foros.reporting.meta;

import java.util.Set;

public interface DependenciesColumnResolver<C extends Column> {
    Set<C> resolve(Object context);
}
