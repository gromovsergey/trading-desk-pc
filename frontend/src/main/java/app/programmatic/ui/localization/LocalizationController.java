package app.programmatic.ui.localization;

import static app.programmatic.ui.localization.dao.model.LocalizationObjectKey.CHANNEL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import app.programmatic.ui.localization.dao.model.Localization;
import app.programmatic.ui.localization.dao.model.LocalizationObjectKey;
import app.programmatic.ui.localization.service.LocalizationService;

import java.util.List;

@RestController
public class LocalizationController {

    @Autowired
    private LocalizationService localizationService;

    @RequestMapping(method = RequestMethod.GET, path = "/rest/localization/channel", produces = "application/json")
    public List<Localization> getChannelLocalizationById(@RequestParam(value = "channelId") Long channelId) {
        return localizationService.findChannelLocalizationsById(channelId);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/localization/channel", produces = "application/json")
    public void updateChannelLocalization(@RequestParam(value = "channelId") Long channelId,
                                          @RequestBody List<Localization> localizations) {
        setKey(CHANNEL, channelId, localizations);
        localizationService.updateLocalizations(localizations);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/rest/localization/channel", produces = "application/json")
    public void deleteChannelLocalization(@RequestParam(value = "channelId") Long channelId,
                                          @RequestBody List<Localization> localizations) {
        setKey(CHANNEL, channelId, localizations);
        localizationService.deleteLocalizations(localizations);
    }

    private void setKey(LocalizationObjectKey key, Long id, List<Localization> localizations) {
        localizations.forEach(d -> d.setKey(key.getPrefix() + id));
    }
}
