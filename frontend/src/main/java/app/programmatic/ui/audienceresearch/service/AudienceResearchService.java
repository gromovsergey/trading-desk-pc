package app.programmatic.ui.audienceresearch.service;
import app.programmatic.ui.audienceresearch.dao.model.AudienceResearch;
import app.programmatic.ui.audienceresearch.dao.model.AudienceResearchStat;
import app.programmatic.ui.audienceresearch.validation.ValidateAudienceResearch;
import app.programmatic.ui.audienceresearch.view.AudienceResearchView;
import app.programmatic.ui.channel.dao.model.ChannelEntity;
import app.programmatic.ui.channel.dao.model.ChannelType;

import java.util.List;

import static app.programmatic.ui.common.validation.ValidateMethod.CREATE;
import static app.programmatic.ui.common.validation.ValidateMethod.UPDATE;

public interface AudienceResearchService {

    AudienceResearch findEager(Long id);

    List<AudienceResearchView> findAll();

    List<AudienceResearchView> findForExternal(Long accountId);

    List<ChannelEntity> findChannels(String text, ChannelType type, boolean internalOnly, int maxRows);

    Long create(@ValidateAudienceResearch(CREATE) AudienceResearch audienceResearch);

    Long update(@ValidateAudienceResearch(UPDATE) AudienceResearch audienceResearch);

    Long updateYesterdayComment(String comment, Long id);

    Long updateTotalComment(String comment, Long id);

    Long delete(Long id);

    AudienceResearchStat getStat(Long audienceResearchId, Long channelId);
}
