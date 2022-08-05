package app.programmatic.ui.device.dao.model;

import java.util.Collections;
import java.util.Set;

public class RootDevices {
    private boolean isAllAvailable;
    private Set<Device> rootDevices;

    public RootDevices() {
        this.isAllAvailable = true;
        this.rootDevices = Collections.emptySet();
    }

    public RootDevices(Set<Device> rootDevices) {
        this.isAllAvailable = false;
        this.rootDevices = Collections.unmodifiableSet(rootDevices);
    }

    public boolean isAllAvailable() {
        return isAllAvailable;
    }

    public Set<Device> getAvailable() {
        return rootDevices;
    }
}
