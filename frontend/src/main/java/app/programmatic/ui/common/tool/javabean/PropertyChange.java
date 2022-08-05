package app.programmatic.ui.common.tool.javabean;

public interface PropertyChange {
    String getName();
    Object getNewValue();
    Object getPrevValue();
    PropertyChangeType getChangeType();
}
