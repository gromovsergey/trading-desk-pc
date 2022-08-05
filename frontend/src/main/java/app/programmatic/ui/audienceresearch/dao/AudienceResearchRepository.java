package app.programmatic.ui.audienceresearch.dao;

import org.springframework.data.repository.CrudRepository;
import app.programmatic.ui.audienceresearch.dao.model.AudienceResearch;

import java.util.List;

public interface AudienceResearchRepository extends CrudRepository<AudienceResearch, Long> {
    List<AudienceResearch> findByStatusNotOrderByTargetChannelName(Character status);
}