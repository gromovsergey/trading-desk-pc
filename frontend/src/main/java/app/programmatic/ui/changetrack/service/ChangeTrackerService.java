package app.programmatic.ui.changetrack.service;

import app.programmatic.ui.changetrack.dao.model.TableName;

import java.util.Collection;
import java.util.Map;

public interface ChangeTrackerService {

    void saveChange(TableName tableName, Long pk);

    void saveChanges(TableName tableName, Collection<Long> pks);

    void saveChanges(Map<TableName, Collection<Long>> pksByTables);
}
