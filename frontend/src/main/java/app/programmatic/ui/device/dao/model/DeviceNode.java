package app.programmatic.ui.device.dao.model;

import java.util.HashSet;
import java.util.Set;

public class DeviceNode extends Device {
    private Long parentId;
    private Set<DeviceNode> children = new HashSet<>();

    public DeviceNode(Long id) {
        super(id);
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Set<DeviceNode> getChildren() {
        return children;
    }

    public void addChild(DeviceNode child) {
        children.add(child);
        child.setParentId(getId());
    }
}
