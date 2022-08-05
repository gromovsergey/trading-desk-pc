package app.programmatic.ui.common.tool.foros;

import com.foros.rs.client.model.advertising.AdvertiserLink;
import com.foros.rs.client.model.entity.EntityLink;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.entity.StatusEntityBase;
import app.programmatic.ui.common.model.StatusOperation;

public class ForosHelper {

    public static EntityLink createEntityLink(Long id) {
        EntityLink entityLink = new EntityLink();
        entityLink.setId(id);
        return entityLink;
    }

    public static AdvertiserLink createAdvertiserLink(Long id) {
        AdvertiserLink entityLink = new AdvertiserLink();
        entityLink.setId(id);
        return entityLink;
    }

    public static StatusOperation isChangeStatusOperation(Enum operation) {
        return StatusOperation.valueOf(operation.name());
    }

    public static <T extends StatusEntityBase> T changeEntityStatus(T entity, StatusOperation operation) {
        switch (operation) {
            case ACTIVATE:
                entity.setStatus(Status.ACTIVE);
                return entity;
            case INACTIVATE:
                entity.setStatus(Status.INACTIVE);
                return entity;
            case DELETE:
                entity.setStatus(Status.DELETED);
                return entity;
            default:
                throw new IllegalArgumentException("Unexpected StatusOperation: " + operation);
        }
    }
}
