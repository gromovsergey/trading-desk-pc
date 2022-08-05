package com.foros.action.channel.bulk;

import static com.foros.action.channel.bulk.ChannelFieldCsv.PageKeyword;
import static com.foros.action.channel.bulk.ChannelFieldCsv.PageKeywordCount;
import static com.foros.action.channel.bulk.ChannelFieldCsv.PageKeywordFrom;
import static com.foros.action.channel.bulk.ChannelFieldCsv.PageKeywordNegative;
import static com.foros.action.channel.bulk.ChannelFieldCsv.PageKeywordTo;
import static com.foros.action.channel.bulk.ChannelFieldCsv.PageKeywordUnit;
import static com.foros.action.channel.bulk.ChannelFieldCsv.SearchKeyword;
import static com.foros.action.channel.bulk.ChannelFieldCsv.SearchKeywordCount;
import static com.foros.action.channel.bulk.ChannelFieldCsv.SearchKeywordFrom;
import static com.foros.action.channel.bulk.ChannelFieldCsv.SearchKeywordNegative;
import static com.foros.action.channel.bulk.ChannelFieldCsv.SearchKeywordTo;
import static com.foros.action.channel.bulk.ChannelFieldCsv.SearchKeywordUnit;
import static com.foros.action.channel.bulk.ChannelFieldCsv.Url;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlCount;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlFrom;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlNegative;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlTo;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlUnit;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlKeyword;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlKeywordCount;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlKeywordFrom;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlKeywordNegative;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlKeywordTo;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlKeywordUnit;

import com.foros.action.bulk.CsvNodeWriter;
import com.foros.action.bulk.CsvRow;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersUnits;
import com.foros.model.channel.trigger.TriggerType;

public class BehavioralChannelCsvNodeWriter extends AbstractChannelCsvNodeWriter implements CsvNodeWriter<BehavioralChannel> {

    @Override
    public void write(CsvRow row, BehavioralChannel channel) {
        super.write(row, channel);

        row.set(Url, channel.getUrls().getPositiveString());
        BehavioralParameters urlParameters = TriggerType.findBehavioralParameters(channel.getBehavioralParameters(), TriggerType.URL);
        writeBehavioralParameters(row, UrlCount, UrlFrom, UrlTo, UrlUnit, urlParameters);
        row.set(UrlNegative, channel.getUrls().getNegativeString());

        row.set(SearchKeyword, channel.getSearchKeywords().getPositiveString());
        BehavioralParameters skParameters = TriggerType.findBehavioralParameters(channel.getBehavioralParameters(), TriggerType.SEARCH_KEYWORD);
        writeBehavioralParameters(row, SearchKeywordCount, SearchKeywordFrom, SearchKeywordTo, SearchKeywordUnit, skParameters);
        row.set(SearchKeywordNegative, channel.getSearchKeywords().getNegativeString());

        row.set(PageKeyword, channel.getPageKeywords().getPositiveString());
        BehavioralParameters pkParameters = TriggerType.findBehavioralParameters(channel.getBehavioralParameters(), TriggerType.PAGE_KEYWORD);
        writeBehavioralParameters(row, PageKeywordCount, PageKeywordFrom, PageKeywordTo, PageKeywordUnit, pkParameters);
        row.set(PageKeywordNegative, channel.getPageKeywords().getNegativeString());

        row.set(UrlKeyword, channel.getUrlKeywords().getPositiveString());
        BehavioralParameters ukParameters = TriggerType.findBehavioralParameters(channel.getBehavioralParameters(), TriggerType.URL_KEYWORD);
        writeBehavioralParameters(row, UrlKeywordCount, UrlKeywordFrom, UrlKeywordTo, UrlKeywordUnit, ukParameters);
        row.set(UrlKeywordNegative, channel.getUrlKeywords().getNegativeString());
    }

    private void writeBehavioralParameters(
            CsvRow row,
            ChannelFieldCsv countCol, ChannelFieldCsv fromCol, ChannelFieldCsv toCol, ChannelFieldCsv unitCol,
            BehavioralParameters parameters) {

        if (parameters == null) {
            return;
        }

        BehavioralParametersUnits unit = BehavioralParametersUnits.calculate(parameters.getTimeFrom(), parameters.getTimeTo());

        long timeFrom = 0L;
        long timeTo = 0L;
        if (unit != null){
            timeFrom = parameters.getTimeFrom() / unit.getMultiplier();
            timeTo = parameters.getTimeTo() / unit.getMultiplier();
        }

        row.set(countCol, parameters.getMinimumVisits());
        row.set(fromCol, timeFrom);
        row.set(toCol, timeTo);
        if (unit != null)   {
            row.set(unitCol, unit.getName());
        }
    }
}
