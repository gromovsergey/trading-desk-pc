package app.programmatic.ui.common.validation.pathalias;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;


@Service
public class ValidationPathAliasesServiceImpl implements ValidationPathAliasesService, ValidationPathAliasesServiceConfigurator {
    private static final ThreadLocal<Collection<ValidationPathAlias>> pathAliases = new ThreadLocal<>();

    public void configure(Collection<ValidationPathAlias> pathAliases) {
        this.pathAliases.set(pathAliases);
    }

    public void clear() {
        pathAliases.remove();
    }

    public Collection<ValidationPathAlias> getAliases() {
        Collection<ValidationPathAlias> result = pathAliases.get();
        return result != null ? result : Collections.emptyList();
    }
}
