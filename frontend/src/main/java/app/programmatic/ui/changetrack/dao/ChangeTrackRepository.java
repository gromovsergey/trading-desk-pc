package app.programmatic.ui.changetrack.dao;

import app.programmatic.ui.changetrack.dao.model.ChangeTrack;

import org.springframework.data.repository.CrudRepository;


public interface ChangeTrackRepository extends CrudRepository<ChangeTrack, Long> {
}
