package com.foros.service.mock;

import com.phorm.oix.service.mock.AdServer.ChannelSearchSvcs.ChannelSearchPOA;
import com.phorm.oix.service.mock.AdServer.ChannelSearchSvcs.ChannelSearchResult;
import com.phorm.oix.service.mock.AdServer.ChannelSearchSvcs.MatchInfo;
import com.phorm.oix.service.mock.AdServer.ChannelSearchSvcs.WMatchInfo;
import com.phorm.oix.service.mock.AdServer.ChannelSearchSvcs.ChannelSearchPackage.ImplementationException;

/**
 *
 * @author oleg_roshka
 */
public class ChannelSearchServiceMock extends ChannelSearchPOA {
    public ChannelSearchServiceMock() {
    }

    @Override
    public ChannelSearchResult[] search(String phrase) {
        ChannelSearchResult[] results = new ChannelSearchResult[5];

        results[0] = new ChannelSearchResult(24272, (short)1);
        results[1] = new ChannelSearchResult(24273, (short)2);
        results[2] = new ChannelSearchResult(24274, (short)1);
        results[3] = new ChannelSearchResult(24275, (short)5);
        results[4] = new ChannelSearchResult(24277, (short)7);

        return results;
    }

    @Override
    public ChannelSearchResult[] wsearch(String phrase) throws ImplementationException {
        return search(phrase);
    }

    @Override
    public MatchInfo match(String url, String phrase, int channelsCount) throws ImplementationException {
        return null;
    }

    @Override
    public WMatchInfo wmatch(String url, String phrase, int channelsCount) throws ImplementationException {
        return null;
    }
}
