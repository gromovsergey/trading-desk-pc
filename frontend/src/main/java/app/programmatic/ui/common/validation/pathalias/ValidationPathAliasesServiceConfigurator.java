package app.programmatic.ui.common.validation.pathalias;

import java.util.Collection;


public interface ValidationPathAliasesServiceConfigurator {
    void configure(Collection<ValidationPathAlias> pathAliases);

    void clear();
}
