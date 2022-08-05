package app.programmatic.ui.common.aspect.prePersistProcessor;

import app.programmatic.ui.common.tool.javabean.emptyValues.EmptyValuesStrategy;

public interface PrePersistProcessorContext {

    EmptyValuesStrategy getEmptyValuesStrategy();

    void setEmptyValuesStrategy(EmptyValuesStrategy emptyValuesStrategy);
}
