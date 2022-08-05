package com.foros.action.channel.bulk;

import static com.foros.action.channel.bulk.ChannelFieldCsv.Account;
import static com.foros.action.channel.bulk.ChannelFieldCsv.Country;
import static com.foros.action.channel.bulk.ChannelFieldCsv.Description;
import static com.foros.action.channel.bulk.ChannelFieldCsv.Name;
import static com.foros.action.channel.bulk.ChannelFieldCsv.Status;
import static com.foros.action.channel.bulk.ChannelFieldCsv.Visibility;

import com.foros.action.bulk.CsvRow;
import com.foros.model.channel.Channel;

public class AbstractChannelCsvNodeWriter<T extends Channel> {

    public void write(CsvRow row, T entity) {
        row.set(Account, entity.getAccount().getName());
        row.set(Name, entity.getName());
        row.set(Status, entity.getStatus());
        row.set(Description, entity.getDescription());
        row.set(Country, entity.getCountry().getCountryCode());
        row.set(Visibility, ChannelBulkHelper.channelVisibilityToString(entity.getVisibility()));
    }
}
