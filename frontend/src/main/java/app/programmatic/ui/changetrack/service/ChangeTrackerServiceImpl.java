package app.programmatic.ui.changetrack.service;

import app.programmatic.ui.changetrack.dao.ChangeTrackRepository;
import app.programmatic.ui.changetrack.dao.model.ChangeTrack;
import app.programmatic.ui.changetrack.dao.model.TableName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ChangeTrackerServiceImpl implements ChangeTrackerService {

    @Autowired
    private ChangeTrackRepository changeTrackRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveChange(TableName tableName, Long pk) {
        saveChanges(tableName, Collections.singletonList(pk));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveChanges(TableName tableName, Collection<Long> pks) {
        List<ChangeTrack> changeTracks = pks.stream()
                .map( pk -> new ChangeTrack(pk, tableName.getStoredName()) )
                .collect(Collectors.toList());
        changeTrackRepository.saveAll(changeTracks);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveChanges(Map<TableName, Collection<Long>> pksByTables) {
        List<ChangeTrack> changeTracks = pksByTables.entrySet().stream()
                .flatMap(
                        e -> e.getValue().stream()
                                .map( pk -> new ChangeTrack(pk, e.getKey().getStoredName() )))
                .collect(Collectors.toList());
        changeTrackRepository.saveAll(changeTracks);
    }
}
