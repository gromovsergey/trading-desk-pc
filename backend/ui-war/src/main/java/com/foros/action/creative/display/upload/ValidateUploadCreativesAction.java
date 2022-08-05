package com.foros.action.creative.display.upload;

import com.foros.action.creative.csv.CreativeCsvReader;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.creative.Creative;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessException;
import com.foros.session.CurrentUserService;
import com.foros.session.creative.CreativeCsvReaderResult;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.template.TemplateService;
import com.foros.util.bulk.BulkReader;
import com.foros.util.bulk.csv.CsvBulkReader;
import com.foros.util.bulk.xlsx.XlsxBulkReader;
import com.foros.util.csv.CsvFormatException;
import com.foros.util.csv.FileFormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.EJB;

import org.apache.commons.io.IOUtils;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

public class ValidateUploadCreativesAction extends BaseUploadCreativesAction {
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT");

    @EJB
    private CreativeSizeService sizeService;

    @EJB
    private TemplateService templateService;

    @EJB
    private CurrentUserService currentUserService;

    private File bulkFile;

    @Validations(
            requiredFields = {
                    @RequiredFieldValidator(fieldName = "format", key = "errors.field.required"),
                    @RequiredFieldValidator(fieldName = "bulkFile", key = "errors.field.required")
            }
    )
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "#target.account")
    public String validateFile() {
        CreativeCsvReaderResult readerResult = readBulk();
        prepareCreatives(readerResult.getCreatives());
        validationResult = displayCreativeService.validateAll(readerResult, getAccount());

        return INPUT;
    }

    protected CreativeCsvReaderResult readBulk() {
        switch (getFormat()) {
            case XLSX:
                return readXlsx();
            default:
                return readCsv();
        }
    }

    protected CreativeCsvReaderResult readCsv() {
        InputStreamReader isr = null;
        try {
            FileInputStream fis = new FileInputStream(bulkFile);
            isr = new InputStreamReader(fis, getFormat().getEncoding());
            BulkReader bulkReader = new CsvBulkReader(isr, getFormat().getDelimiter(), TIME_ZONE);
            CreativeCsvReader creativeReader = new CreativeCsvReader(bulkReader, sizeService, templateService, currentUserService);
            return creativeReader.parse();
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

    private CreativeCsvReaderResult readXlsx() {
        try (BulkReader bulkReader = new XlsxBulkReader(bulkFile.getPath(), TIME_ZONE)) {
            CreativeCsvReader creativeReader = new CreativeCsvReader(bulkReader, sizeService, templateService, currentUserService);
            return creativeReader.parse();
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

    private void prepareCreatives(List<Creative> creatives) {
        AdvertiserAccount currentAccount = new AdvertiserAccount(getAdvertiserId());

        for (Creative creative : creatives) {
            creative.setAccount(currentAccount);
        }
    }
}
