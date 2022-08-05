package com.foros.session.channel.service;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.channel.KeywordChannel;
import com.foros.model.channel.KeywordTriggerType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class KeywordChannelRestrictionsBeanTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private KeywordChannelService keywordChannelService;

    @Autowired
    private KeywordChannelRestrictions keywordChannelRestrictions;

    private KeywordChannel channel;

    @Override
    @org.junit.Before
    public void setUp() throws Exception {
        super.setUp();
        channel = new KeywordChannel();
        Set<String> keywords = new HashSet<String>();
        keywords.add(UUID.randomUUID().toString());
        Map<String, Long> channels = keywordChannelService.findOrCreate(internalAllAccess.getUser().getAccount().getId(),
                "GB", KeywordTriggerType.SEARCH_KEYWORD, keywords);
        for (Entry<String, Long> entry : channels.entrySet()) {
            channel = keywordChannelService.findById(entry.getValue());
            break;
        }
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("keyword_channel", "view") {
            @Override
            public boolean call() {
                return keywordChannelRestrictions.canView();
            }
        };
        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        doCheck(callCanView);
    }

    @Test
    public void testUpdate() throws Exception {
        Callable callCanUpdate = new Callable("keyword_channel", "edit") {
            @Override
            public boolean call() {
                return keywordChannelRestrictions.canUpdate(channel);
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);
        expectResult(publisherAllAccess1, false);
        expectResult(cmpAllAccess1, false);
        doCheck(callCanUpdate);
    }
}
