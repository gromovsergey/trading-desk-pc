package app.programmatic.ui.common.model;

public interface Statusable {

    MajorDisplayStatus getMajorStatus();

    // ToDo: get rid of default implementation in #404
    default MajorDisplayStatus getInheritedMajorStatus() {
        return getMajorStatus();
    }
}
