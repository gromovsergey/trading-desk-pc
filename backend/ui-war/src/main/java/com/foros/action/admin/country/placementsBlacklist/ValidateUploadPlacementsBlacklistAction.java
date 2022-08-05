package com.foros.action.admin.country.placementsBlacklist;

import com.foros.model.channel.placementsBlacklist.BlacklistAction;
import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;
import com.foros.model.security.User;
import com.foros.session.BusinessException;
import com.foros.session.security.UserService;
import com.foros.util.Schema;
import com.foros.util.bulk.BulkReader;
import com.foros.util.bulk.csv.CsvBulkReader;
import com.foros.util.bulk.xlsx.XlsxBulkReader;
import com.foros.util.csv.CsvFormatException;
import com.foros.util.csv.FileFormatException;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.TimeZone;
import javax.ejb.EJB;
import org.apache.commons.io.IOUtils;


public class ValidateUploadPlacementsBlacklistAction extends BaseUploadPlacementsBlacklistAction {

    @EJB
    private UserService userService;

    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT");

    private File bulkFile;

    @Validations(
            requiredFields = {
                    @RequiredFieldValidator(fieldName = "format", key = "errors.field.required"),
                    @RequiredFieldValidator(fieldName = "bulkFile", key = "errors.field.required")
            }
    )
    public String validateFile() {
        List<PlacementBlacklist> placements = readBulk();
        prepare(placements);
        validationResult = placementsBlacklistService.validateAll(placements, getCountry());

        return INPUT;
    }

    protected List<PlacementBlacklist> readBulk() {
        switch (getFormat()) {
            case XLSX:
                return readXlsx();
            default:
                return readCsv();
        }
    }

    protected List<PlacementBlacklist> readCsv() {
        InputStreamReader isr = null;
        try {
            FileInputStream fis = new FileInputStream(bulkFile);
            isr = new InputStreamReader(fis, getFormat().getEncoding());
            BulkReader bulkReader = new CsvBulkReader(isr, getFormat().getDelimiter(), TIME_ZONE);
            PlacementBlacklistCsvReader placementBlacklistCsvReader = new PlacementBlacklistCsvReader(bulkReader);
            return placementBlacklistCsvReader.parse();
        } catch (CsvFormatException | FileFormatException e) {
            throw new BusinessException(e.getMessage());
        } catch (FileNotFoundException e) {
            throw new BusinessException("No such file or directory");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(isr);
        }
    }

    private List<PlacementBlacklist> readXlsx() {
        try (BulkReader bulkReader = new XlsxBulkReader(bulkFile.getPath(), TIME_ZONE)) {
            PlacementBlacklistCsvReader placementBlacklistCsvReader = new PlacementBlacklistCsvReader(bulkReader);
            return placementBlacklistCsvReader.parse();
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

    private void prepare(List<PlacementBlacklist> placements) {
        User user = userService.getMyUser();
        for (PlacementBlacklist placement : placements) {
            placement.setUser(user);
            placement.setUrl(formatToPlacementUrl(placement.getUrl()));
            if (placement.getAction() == null) {
                placement.setAction(BlacklistAction.ADD);
            }
        }
    }

    private String formatToPlacementUrl(String url) {
        if (url == null) {
            return null;
        }
        String trimmedUrl = url.trim();
        String urlLower = trimmedUrl.toLowerCase();
        return urlLower.startsWith(Schema.HTTP.getValue()) || urlLower.startsWith(Schema.HTTPS.getValue()) ?
                trimmedUrl : Schema.HTTP.getValue() + trimmedUrl;
    }
}
