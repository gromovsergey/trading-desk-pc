package app.programmatic.ui.common.model;

public class ObjectType {
    private Integer objectId;
    private String remoteClassName;

    public ObjectType(Integer objectId, String remoteClassName) {
        this.objectId = objectId;
        this.remoteClassName = remoteClassName;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public String getRemoteClassName() {
        return remoteClassName;
    }
}
