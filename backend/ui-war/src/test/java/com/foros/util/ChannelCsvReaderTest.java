package com.foros.util;

import com.foros.AbstractUnitTest;
import com.foros.action.channel.bulk.ChannelCsvReader;
import com.foros.action.channel.bulk.MetaDataBuilder;
import com.foros.model.channel.Channel;
import com.foros.session.UploadContext;
import com.foros.session.channel.service.AdvertisingChannelType;
import com.foros.util.bulk.BulkReader;
import com.foros.util.bulk.csv.CsvBulkReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import group.Bulk;
import group.Unit;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.TimeZone;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Unit.class, Bulk.class} )
public class ChannelCsvReaderTest extends AbstractUnitTest {
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    @Test
    public void testInvalidExpressionChannelCsv() throws IOException {
        InputStream inputStream = openFile("invalid/expressionChannel.csv");
        Reader reader = new InputStreamReader(inputStream, DEFAULT_CHARSET);
        UploadContext uploadStatus = mockUploadExpressionChannelCsv(reader);
        assertEquals(1, uploadStatus.getErrors().size());
        assertTrue(uploadStatus.getErrors().get(0).getMessage().contains("Invalid row format"));
    }

    @Test
    public void testValidExpressionChannelCsv() throws IOException {
        InputStream inputStream = openFile("valid/expressionChannel.csv");
        Reader reader = new InputStreamReader(inputStream, DEFAULT_CHARSET);
        UploadContext uploadStatus = mockUploadExpressionChannelCsv(reader);
        assertFalse(uploadStatus.hasErrors());
    }

    private InputStream openFile(String fileName) throws IOException {
        URL resource = getResource(fileName);
        return resource.openStream();
    }

    private URL getResource(String fileName) {
        String prefix = "channel/csv/";
        URL fileLocation = getClass().getResource(prefix + fileName);
        if (fileLocation == null) {
            fail("File " + fileName + " not found");
        }
        URL resource = getClass().getResource(prefix + fileName);
        return resource;
    }

    private UploadContext mockUploadExpressionChannelCsv(Reader reader) {
        try {
            BulkReader bulkReader = new CsvBulkReader(reader, ',', TimeZone.getTimeZone("GMT"));

            MetaDataBuilder builder = new MetaDataBuilder(AdvertisingChannelType.EXPRESSION, false);
            ChannelCsvReader channelCsvReader = new ChannelCsvReader(bulkReader, builder);
            List<Channel> channels = channelCsvReader.parse();
            assertEquals(1, channels.size());

            return UploadUtils.getUploadContext(channels.get(0));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
