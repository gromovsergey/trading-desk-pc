package com.foros.action.campaign.bulk;

import com.foros.model.account.Account;
import com.foros.session.BusinessException;
import com.foros.session.campaign.bulk.BulkParseResult;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.session.template.OptionService;
import com.foros.session.template.TemplateService;
import com.foros.util.AccountUtil;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TimeZone;
import javax.ejb.EJB;
import org.apache.commons.io.IOUtils;

public class ValidateUploadCampaignAction extends UploadCampaignActionSupport {

    @EJB
    private TemplateService templateService;
    @EJB
    private CreativeSizeService sizeService;
    @EJB
    private DisplayCreativeService creativeService;
    @EJB
    private OptionService optionService;

    private File bulkFile;

    @Validations(
            requiredFields = {
                    @RequiredFieldValidator(fieldName = "format", key = "errors.field.required"),
                    @RequiredFieldValidator(fieldName = "tgtType", key = "errors.field.required"),
                    @RequiredFieldValidator(fieldName = "bulkFile", key = "errors.field.required")
            }
    )
    public String validateBulk() {
        BulkParseResult result = readBulk();
        validationResult = bulkCampaignToolsService.validateAll(getAdvertiserId(), tgtType, result);
        return INPUT;
    }

    private BulkParseResult readBulk() {
        Account account = AccountUtil.extractAccountById(getAdvertiserId());
        TimeZone timeZone = TimeZone.getTimeZone(account.getTimezone().getKey());
        MetaDataBuilder builder = new MetaDataBuilder(account, tgtType);

        switch (format) {
            case XLSX:
                return readXlsx(builder, timeZone);
            default:
                return readCsv(builder, timeZone);
        }
    }

    private BulkParseResult readCsv(MetaDataBuilder builder, TimeZone timeZone) {
        InputStreamReader isr = null;
        try {
            FileInputStream fis = new FileInputStream(bulkFile);
            isr = new InputStreamReader(fis, format.getEncoding());
            BulkReader bulkReader = new CsvBulkReader(isr, format.getDelimiter(), timeZone);
            CampaignBulkReader campaignBulkReader = new CampaignBulkReader(
                    bulkReader, builder, tgtType, sizeService.findTextSizeId(), templateService.findTextTemplateId(),
                    creativeService, optionService);
            return campaignBulkReader.parse();
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

    private BulkParseResult readXlsx(MetaDataBuilder builder, TimeZone timeZone) {
        try (BulkReader bulkReader = new XlsxBulkReader(bulkFile.getPath(), timeZone)) {
            CampaignBulkReader campaignBulkReader = new CampaignBulkReader(
                    bulkReader, builder, tgtType, sizeService.findTextSizeId(), templateService.findTextTemplateId(),
                    creativeService, optionService);
            return campaignBulkReader.parse();
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
