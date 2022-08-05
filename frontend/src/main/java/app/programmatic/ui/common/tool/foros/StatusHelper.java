package app.programmatic.ui.common.tool.foros;

import com.foros.rs.client.model.entity.Status;
import app.programmatic.ui.common.model.MajorDisplayStatus;

public class StatusHelper {
    public static MajorDisplayStatus getMajorStatusByRsStatus(Status objectStatus) {
        switch (objectStatus) {
            case ACTIVE:
                return MajorDisplayStatus.LIVE;
            case INACTIVE:
                return MajorDisplayStatus.INACTIVE;
            case DELETED:
                return MajorDisplayStatus.DELETED;
            case PENDING: case PENDING_INACTIVATION:
                return MajorDisplayStatus.NOT_LIVE;
        }
        throw new IllegalArgumentException("Unexpected object status: " + objectStatus);
    }

    public static Status getRsStatusByMajorStatus(MajorDisplayStatus majorDisplayStatus) {
        switch (majorDisplayStatus) {
            case LIVE: case LIVE_NEED_ATT: case NOT_LIVE:
                return Status.ACTIVE;
            case INACTIVE:
                return Status.INACTIVE;
            case DELETED:
                return Status.DELETED;
        }
        throw new IllegalArgumentException("Unexpected major display status: " + majorDisplayStatus);
    }

    public static MajorDisplayStatus getMajorStatusByObjectStatus(Character statusLetter) {
        switch (statusLetter) {
            case 'A':
                return getMajorStatusByRsStatus(Status.ACTIVE);
            case 'I':
                return getMajorStatusByRsStatus(Status.INACTIVE);
            case 'D':
                return getMajorStatusByRsStatus(Status.DELETED);
            default:
                throw new IllegalArgumentException("Unexpected object status letter: " + statusLetter);
        }
    }

    public static Status getRsStatusByDisplayStatus(String displayStatus) {
        return getRsStatusByMajorStatus(MajorDisplayStatus.valueOf(displayStatus.split("\\|")[0]));
    }

    public static MajorDisplayStatus getMajorStatusByOperation(String operation) {
        if ("DEACTIVATE".equalsIgnoreCase(operation) || "INACTIVATE".equalsIgnoreCase(operation)) {
            return MajorDisplayStatus.INACTIVE;
        } else if ("ACTIVATE".equalsIgnoreCase(operation)) {
            return MajorDisplayStatus.LIVE;
        } else if ("DELETE".equalsIgnoreCase(operation)) {
            return MajorDisplayStatus.DELETED;
        } else if ("RESTORE".equalsIgnoreCase(operation)) {
            return MajorDisplayStatus.INACTIVE;
        }

        throw new IllegalArgumentException("Unexpected operation: " + operation);
    }
}
