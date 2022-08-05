package app.programmatic.ui.channel.dao;

import org.springframework.data.repository.CrudRepository;
import app.programmatic.ui.channel.dao.model.ChannelEntity;

public interface ChannelRepository extends CrudRepository<ChannelEntity, Long> {
}