package com.foros.model.channel.placementsBlacklist;

import com.foros.changes.inspection.ChangeType;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.changes.inspection.FieldChangeDescriptor;
import com.foros.changes.inspection.changeNode.EntityChange;
import com.foros.changes.inspection.changeNode.FieldChange;
import com.foros.model.Identifiable;

public class PlacementsBlacklistChange extends EntityChange {

    public PlacementsBlacklistChange(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
        super(descriptor, object, changeType, new FieldChange[1]);

        PlacementsBlacklistWrapper placementsBlacklistWrapper = (PlacementsBlacklistWrapper) object;

        for (FieldChangeDescriptor fieldChangeDescriptor : descriptor.getFieldChangeDescriptors()) {
            if (fieldChangeDescriptor.getName().equals("placements")) {
                this.changes[0] = fieldChangeDescriptor.newInstance(placementsBlacklistWrapper.getOldPlacements(), placementsBlacklistWrapper.getPlacements());
                break;
            }
        }
    }

    public static class Factory extends EntityChange.Factory {
        @Override
        protected PlacementsBlacklistChange newInstanceInternal(EntityChangeDescriptor descriptor, Object object, ChangeType changeType, FieldChange[] changes) {
            return new PlacementsBlacklistChange(descriptor, object, changeType, changes);
        }
    }

    @Override
    public Object getId() {
        return ((Identifiable) object).getId();
    }
}
