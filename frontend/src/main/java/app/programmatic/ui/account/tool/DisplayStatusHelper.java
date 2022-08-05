package app.programmatic.ui.account.tool;

import app.programmatic.ui.account.dao.model.AccountDisplayStatus;

import java.util.HashMap;
import java.util.Map;

public class DisplayStatusHelper {
    private static final Map<Integer, String> displayStatuses = initAccountDisplayStatusMap();

    private static Map<Integer, String> initAccountDisplayStatusMap() {
        Map<Integer, String> result = new HashMap<>(AccountDisplayStatus.values().length);
        for (AccountDisplayStatus status : AccountDisplayStatus.values()) {
            result.put(status.getId(), status.getMajorStatus().toString() + "|" + status.getDescriptionKey());
        }
        return result;
    }

    public static Map<Integer, String> getStatusMap() {
        return displayStatuses;
    }
}
