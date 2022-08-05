package app.programmatic.ui.audienceresearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import app.programmatic.ui.audienceresearch.dao.model.AudienceResearch;
import app.programmatic.ui.audienceresearch.dao.model.AudienceResearchStat;
import app.programmatic.ui.audienceresearch.service.AudienceResearchService;
import app.programmatic.ui.audienceresearch.view.AudienceResearchView;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.channel.dao.model.ChannelEntity;
import app.programmatic.ui.channel.dao.model.ChannelType;

import java.util.List;

import static app.programmatic.ui.account.dao.model.AccountRole.INTERNAL;

@RestController
public class AudienceResearchController {
    private static final int MAX_CHANNEL_ROWS = 100;

    @Autowired
    private AudienceResearchService audienceResearchService;

    @Autowired
    private AuthorizationService authorizationService;

    @RequestMapping(method = RequestMethod.GET, path = "/rest/audienceResearch/list", produces = "application/json")
    public List<AudienceResearchView> getAudienceResearchList() {
        if (authorizationService.getAuthUser().getUserRole().getAccountRole() == INTERNAL) {
            return audienceResearchService.findAll();
        } else {
            return audienceResearchService.findForExternal(authorizationService.getAuthUser().getAccountId());
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/audienceResearch", produces = "application/json")
    public AudienceResearch getAudienceResearch(@RequestParam(value = "audienceResearchId") Long audienceResearchId) {
        return audienceResearchService.findEager(audienceResearchId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/audienceResearch/channels", produces = "application/json")
    public List<ChannelEntity> findChannels(@RequestParam(value = "text") String text,
                                            @RequestParam(value = "type", required = false) ChannelType type,
                                            @RequestParam(value = "internalOnly") boolean internalOnly) {
        return audienceResearchService.findChannels(text, type, internalOnly, MAX_CHANNEL_ROWS);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/audienceResearch", produces = "application/json")
    public Long create(@RequestBody AudienceResearch audienceResearch) {
        return audienceResearchService.create(audienceResearch);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/audienceResearch", produces = "application/json")
    public Long update(@RequestBody AudienceResearch audienceResearch) {
        return audienceResearchService.update(audienceResearch);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/rest/audienceResearch", produces = "application/json")
    public Long delete(@RequestParam(value = "audienceResearchId") Long audienceResearchId) {
        return audienceResearchService.delete(audienceResearchId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/audienceResearch/stat", produces = "application/json")
    public AudienceResearchStat getAudienceResearchStat(@RequestParam(value = "audienceResearchId") Long audienceResearchId,
                                                        @RequestParam(value = "channelId") Long channelId) {
        return audienceResearchService.getStat(audienceResearchId, channelId);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/audienceResearch/yesterdayComment", produces = "application/json")
    public Long updateYesterdayComment(@RequestParam String comment, @RequestParam Long id) {
        return audienceResearchService.updateYesterdayComment(comment, id);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/audienceResearch/totalComment", produces = "application/json")
    public Long updateTotalComment(@RequestParam String comment, @RequestParam Long id) {
        return audienceResearchService.updateTotalComment(comment, id);
    }
}
