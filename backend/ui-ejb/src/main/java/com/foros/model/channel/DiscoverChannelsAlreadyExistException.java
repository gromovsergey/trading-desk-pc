package com.foros.model.channel;

import com.foros.session.BusinessException;
import com.foros.util.StringUtil;

import javax.ejb.ApplicationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationException(rollback = true)
@Deprecated // Use com.foros.validation.constraint.violation.ConstraintViolationException
public class DiscoverChannelsAlreadyExistException extends BusinessException {
    private List<DiscoverChannel> existingChannels = new ArrayList<DiscoverChannel>();
    private String updatedKeywordText;

    public DiscoverChannelsAlreadyExistException(List<DiscoverChannel> existingChannels, String updatedKeywordText) {
        super("name", StringUtil.getLocalizedString("errors.discoverList.channelAlreadyExists",
                                                    existingChannels.get(0).getName()));
        this.existingChannels = existingChannels;
        this.updatedKeywordText = updatedKeywordText;
    }

    public List<DiscoverChannel> getExistingChannels() {
        return Collections.unmodifiableList(existingChannels);
    }

    public String getUpdatedKeywordText() {
        return updatedKeywordText;
    }
}
