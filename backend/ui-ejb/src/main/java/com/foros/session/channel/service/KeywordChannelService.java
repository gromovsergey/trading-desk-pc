package com.foros.session.channel.service;

import com.foros.model.DisplayStatus;
import com.foros.model.admin.GlobalParam;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.KeywordChannel;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.service.ByIdLocatorService;
import com.foros.session.TooManyRowsException;
import com.foros.session.channel.KeywordChannelCsvTO;
import com.foros.session.channel.KeywordChannelTO;
import com.foros.util.jpa.DetachedList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Local;

@Local
public interface KeywordChannelService extends ByIdLocatorService<KeywordChannel>, CategoryOwnedChannelService {

    int MAX_ERRORS = 100;

    @Override
    KeywordChannel findById(Long id);

    @Override
    KeywordChannel view(Long id);

    Map<String, Long> findOrCreate(Long accountId, String countryCode, KeywordTriggerType triggerType, Set<String> keywords);

    Long update(KeywordChannel channel);

    DetachedList<KeywordChannelTO> search(int firstRow, int maxResults, String name, Long accountId, String countryCode, DisplayStatus... displayStatuses);

    Collection<KeywordChannelCsvTO> export(int maxResultSize, String name, Long accountId, String countryCode, DisplayStatus... displayStatuses) throws TooManyRowsException;

    GlobalParam getSearchParam();

    GlobalParam getPageParam();

    BehavioralParameters getDefaultParameters(GlobalParam param);

    DefaultKeywordSettingsTO findDefaultKeywordSettings();

    void updateDefaultParameters(DefaultKeywordSettingsTO settingsTO);

    public void updateAll(List<KeywordChannel> channels);

    @Override
    List<CategoryChannel> getCategories(Long channelId);
}
