package com.foros.action.admin.keywordChannel;

import com.foros.action.bulk.CsvRow;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersUnits;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.reporting.Row;
import com.foros.reporting.rowsource.RowSource;
import com.foros.session.channel.KeywordChannelCsvTO;

import java.util.Iterator;

public class KeywordChannelRowSource implements RowSource, Iterator<Row> {
    private Iterator<KeywordChannelCsvTO> channelIterator;

    public KeywordChannelRowSource(Iterator<KeywordChannelCsvTO> channelIterator) {
        this.channelIterator = channelIterator;
    }

    @Override
    public Iterator<Row> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return channelIterator.hasNext();
    }

    @Override
    public Row next() {
        KeywordChannelCsvTO to = channelIterator.next();
        CsvRow row = new CsvRow(KeywordChannelFieldCsv.TOTAL_COLUMNS_COUNT);
        write(row, to);
        return row;
    }

    @Override
    public void remove() {
        channelIterator.remove();
    }

    private void write(CsvRow record, KeywordChannelCsvTO channel) {
        record.set(KeywordChannelFieldCsv.InternalAccount, channel.getAccountName());
        record.set(KeywordChannelFieldCsv.Keyword, channel.getName());
        record.set(KeywordChannelFieldCsv.Type, KeywordTriggerType.byLetter(channel.getType()).getName());
        record.set(KeywordChannelFieldCsv.Country, channel.getCountryCode());
        Iterator<BehavioralParameters> parametersIterator = channel.getBehavioralParameters().iterator();
        if (parametersIterator.hasNext()) {
            BehavioralParameters parameters = parametersIterator.next();
            BehavioralParametersUnits unit = BehavioralParametersUnits.calculate(
                    parameters.getTimeFrom(), parameters.getTimeTo());
            Long timeFrom = parameters.getTimeFrom() / unit.getMultiplier();
            Long timeTo = parameters.getTimeTo() / unit.getMultiplier();
            record.set(KeywordChannelFieldCsv.BPFrequency, parameters.getMinimumVisits());
            record.set(KeywordChannelFieldCsv.BPFrom, timeFrom);
            record.set(KeywordChannelFieldCsv.BPTo, timeTo);
            record.set(KeywordChannelFieldCsv.BPScale, unit.getName());
        }
        if (channel.getFrequencyCap() != null) {
            record.set(KeywordChannelFieldCsv.FCPeriod, channel.getFrequencyCap().getPeriodSpan());
            record.set(KeywordChannelFieldCsv.FCWindowLimit, channel.getFrequencyCap().getWindowCount());
            record.set(KeywordChannelFieldCsv.FCWindowLength, channel.getFrequencyCap().getWindowLengthSpan());
            record.set(KeywordChannelFieldCsv.FCLife, channel.getFrequencyCap().getLifeCount());
        }
    }
}
