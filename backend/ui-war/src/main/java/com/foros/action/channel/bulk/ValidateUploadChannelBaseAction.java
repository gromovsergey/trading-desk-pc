package com.foros.action.channel.bulk;

import com.foros.model.channel.Channel;
import com.foros.session.BusinessException;
import com.foros.util.bulk.BulkReader;
import com.foros.util.bulk.csv.CsvBulkReader;
import com.foros.util.bulk.xlsx.XlsxBulkReader;
import com.foros.util.csv.CsvFormatException;
import com.foros.util.csv.FileFormatException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.TimeZone;


public class ValidateUploadChannelBaseAction  extends UploadChannelActionSupport {
    private File bulkFile;

    protected List<Channel> readBulk(boolean isInternalProcessing) {
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        MetaDataBuilder builder = new MetaDataBuilder(getChannelType(), isInternalProcessing);

        switch (format) {
            case XLSX:
                return readXlsx(builder, timeZone);
            default:
                return readCsv(builder, timeZone);
        }
    }

    protected List<Channel> readCsv(MetaDataBuilder builder, TimeZone timeZone) {
        InputStreamReader isr = null;
        try {
            FileInputStream fis = new FileInputStream(bulkFile);
            isr = new InputStreamReader(fis, format.getEncoding());
            BulkReader bulkReader = new CsvBulkReader(isr, format.getDelimiter(), timeZone);
            ChannelCsvReader channelCsvReader = new ChannelCsvReader(bulkReader, builder);
            return channelCsvReader.parse();
        } catch (CsvFormatException e) {
            throw new BusinessException(e.getMessage());
        } catch (FileFormatException e) {
            throw new BusinessException(e.getMessage());
        } catch (FileNotFoundException e) {
            throw new BusinessException("No such file or directory");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(isr);
        }
    }

    private List<Channel> readXlsx(MetaDataBuilder builder, TimeZone timeZone) {
        try (BulkReader bulkReader = new XlsxBulkReader(bulkFile.getPath(), timeZone)) {
            ChannelCsvReader channelCsvReader = new ChannelCsvReader(bulkReader, builder);
            return channelCsvReader.parse();
        } catch (FileFormatException e) {
            throw new BusinessException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File getBulkFile() {
        return bulkFile;
    }

    public void setBulkFile(File bulkFile) {
        this.bulkFile = bulkFile;
    }
}
