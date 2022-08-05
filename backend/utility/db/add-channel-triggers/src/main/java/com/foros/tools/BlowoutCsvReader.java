package com.foros.tools;

import com.foros.session.channel.descriptors.ChannelTriggersContainer;
import com.foros.util.csv.CsvReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlowoutCsvReader {
    private static final Pattern ID_PATTERN = Pattern.compile("(\\d+)$");

    public static Collection<ChannelTriggersContainer> read(File file) throws Exception {
        TriggersCollector collector = new TriggersCollector();
        try (
                FileInputStream fis = new FileInputStream(file);
                Reader r = new InputStreamReader(fis, "UTF-8")
        ) {
            CsvReader reader = new CsvReader(r);
            reader.readHeaders();

            while(reader.readRecord()) {
                String[] values = reader.getValues();

                String countryCode = values[0];
                Long channelId = idFromLink(values[3]);
                String triggerType = values[4];
                String originalTrigger = values[5];

                collector.add(channelId, countryCode, triggerType, originalTrigger, false);
            }
        }
        return collector.getTriggers();
    }

    private static Long idFromLink(String value) {
        Matcher matcher = ID_PATTERN.matcher(value);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Can't find id in " + value);
        }

        return Long.valueOf(matcher.group(1));
    }
}

