package com.foros.session.channel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;

import group.Unit;

import java.util.Set;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class DiscoverChannelUtilsTest extends AbstractServiceBeanIntegrationTest{

    @Test
    public void applyKeywordPattern() {
        assertEquals("pattern", DiscoverChannelUtils.applyKeywordPattern("pattern", "keyword"));
        assertEquals("pattern keyword", DiscoverChannelUtils.applyKeywordPattern("pattern ##KEYWORD##", "keyword"));
        assertEquals("pattern keyword keyword", DiscoverChannelUtils.applyKeywordPattern("pattern ##KEYWORD## ##KEYWORD##", "keyword"));
        assertEquals("pattern keyword##KEYWORD## keyword##KEYWORD##", DiscoverChannelUtils.applyKeywordPattern("pattern ##KEYWORD## ##KEYWORD##", "keyword##KEYWORD##"));
        assertEquals("pattern \rkey\n\"word  \rkey\n\"word ", DiscoverChannelUtils.applyKeywordPattern("pattern ##KEYWORD## ##KEYWORD##", "\rkey\n\"word "));
    }

    @Test
    public void assertParsedDiscoverChannel() {
        DiscoverChannelUtils.assertParsed(new DiscoverChannel());
    }

    @Test
    public void assertParsedDiscoverChannelList() {
        DiscoverChannelUtils.assertParsed(new DiscoverChannelList());
    }

    @Test
    public void getKeywordText() {
        DiscoverChannel discoverChannel = new DiscoverChannel();
        discoverChannel.setName("name");
        discoverChannel.setDiscoverQuery("query");
        assertEquals("name,,,,,query", DiscoverChannelUtils.getKeywordText(discoverChannel));

        //Positive part
        discoverChannel.getPageKeywords().clear();
        assertEquals("name,,,,,query", DiscoverChannelUtils.getKeywordText(discoverChannel));
        discoverChannel.getPageKeywords().setPositive("test");
        assertEquals("name,test,,,,query", DiscoverChannelUtils.getKeywordText(discoverChannel));
        discoverChannel.getPageKeywords().setPositive("test test");
        assertEquals("name,test test,,,,query", DiscoverChannelUtils.getKeywordText(discoverChannel));
        discoverChannel.getPageKeywords().setPositive("test test", "test");
        assertEquals("name,\"test test\r\ntest\",,,,query", DiscoverChannelUtils.getKeywordText(discoverChannel));
        discoverChannel.getSearchKeywords().setPositive("test");
        assertEquals("name,\"test test\r\ntest\",,test,,query", DiscoverChannelUtils.getKeywordText(discoverChannel));

        //Negative part
        discoverChannel.getSearchKeywords().clear();
        discoverChannel.getPageKeywords().clear();
        assertEquals("name,,,,,query", DiscoverChannelUtils.getKeywordText(discoverChannel));
        discoverChannel.getPageKeywords().setNegative("test");
        assertEquals("name,,test,,,query", DiscoverChannelUtils.getKeywordText(discoverChannel));
        discoverChannel.getPageKeywords().setNegative("test test");
        assertEquals("name,,test test,,,query", DiscoverChannelUtils.getKeywordText(discoverChannel));
        discoverChannel.getPageKeywords().setNegative("test test", "test");
        assertEquals("name,,\"test test\r\ntest\",,,query", DiscoverChannelUtils.getKeywordText(discoverChannel));
        discoverChannel.getSearchKeywords().setNegative("test");
        assertEquals("name,,\"test test\r\ntest\",,test,query", DiscoverChannelUtils.getKeywordText(discoverChannel));
    }

    @Test
    public void unmacro() {
        assertEquals("aa", DiscoverChannelUtils.unmacroKeyword("aa", "aa"));
        assertEquals("aa", DiscoverChannelUtils.unmacroKeyword("##KEYWORD##", "aa"));
        assertEquals("aa", DiscoverChannelUtils.unmacroKeyword("##KEYWORD##", "aa"));
        assertEquals("", DiscoverChannelUtils.unmacroKeyword("a##KEYWORD##b", "ab"));
        assertEquals("a", DiscoverChannelUtils.unmacroKeyword("a##KEYWORD##b", "aab"));
        assertEquals("__", DiscoverChannelUtils.unmacroKeyword("a##KEYWORD##b a##KEYWORD##basd", "a__b a__basd"));
    }

    @Test
    public void testChildChannelsFromKeywordList() {
        String firstKeyword = "test-WDChannels-duplicateKwdsIgnored-<test_date>,\"\"\"ONLY THIS ONE SHOULD BE SAVED\"\"\",,\"Audi AND ANY A4 A5 RS6\",";
        String keywordList = firstKeyword + "\n" +
            "test-WDChannels-duplicateKwdsIgnored-<test_date>,\"\"\"THIS ONE WILL NOT BE SAVED\"\"\",,\"not to be saved\",\n" +
            "test-WDChannels-duplicateKwdsIgnored-<test_date>,,\"\"\"THIS ONE WILL NOT BE SAVED TOO\"\"\",,\n" +
            "test-WDChannels-duplicateKwdsIgnored-<test_date>";

        DiscoverChannelList dcList = new DiscoverChannelList();
        dcList.setKeywordList(keywordList);

        DiscoverChannelUtils.createChildChannelsFromKeywordList(dcList);
        Set<DiscoverChannel> childChannels = dcList.getChildChannels();

        //Only first keyword should be retained
        assertEquals(childChannels.size(), 1);
        assertTrue(childChannels.iterator().next().getBaseKeyword().equals(firstKeyword));
    }
}
