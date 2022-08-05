package com.foros.action.campaign.bulk;

import com.foros.framework.ReadOnly;
import com.foros.model.EntityBase;
import com.foros.model.campaign.Campaign;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.MetaData;
import com.foros.session.campaign.ValidationResultTO;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.opensymphony.xwork2.util.CreateIfNull;

public class DownloadCampaignAction extends CampaignExportBaseAction {

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("country.countryCode", "'country'", "violation.message")
            .add("soldToUser.id", "'soldToUser'", "violation.message")
            .add("billToUser.id", "'billToUser'", "violation.message")
            .add("options[(#path)].value", "#writer.getPath(#templateService.findTextOptionFromTextTemplate(groups[0]).getToken())", "violation.message")
            .add("deliveryPacing", "'dailyBudget'", "violation.message")
            .rules();

    private static final ReviewCsvNodeWriter REVIEW_WRITER = new ReviewCsvNodeWriter(RULES);

    @CreateIfNull
    private ValidationResultTO validationResult;

    @ReadOnly
    public String export() throws IOException {

        Collection<Campaign> campaigns = bulkCampaignToolsService.getValidatedResults(getAdvertiserId(), validationResult.getId()).getCampaigns();
        long total = validationResult.getCampaigns().getCreated()
                + validationResult.getCampaigns().getUpdated()
                + validationResult.getGroups().getCreated()
                + validationResult.getGroups().getUpdated()
                + validationResult.getAds().getCreated()
                + validationResult.getAds().getUpdated()
                + validationResult.getCreatives().getCreated()
                + validationResult.getCreatives().getUpdated()
                + validationResult.getKeywords().getCreated()
                + validationResult.getKeywords().getUpdated()
                + validationResult.getLineWithErrors();

        MetaData<Column> metaData = getMetaDataBuilder().forReview();
        ByLineCampaignTreeIterator iterator = new ByLineCampaignTreeIterator((int) total, campaigns.iterator());
        AbstractCampaignRowSource rowSource = new CampaignDownloadRowSource(iterator, REVIEW_WRITER);

        serialize(metaData, rowSource, false);

        return null;
    }

    @ReadOnly
    public String template() throws IOException {
        Iterator<EntityBase> iterator = new ArrayList<EntityBase>().iterator();
        MetaData<Column> metaData = getMetaDataBuilder().forUpload();
        AbstractCampaignRowSource rowSource = new CampaignDownloadRowSource(iterator, MAIN_WRITER);

        serialize(metaData, rowSource, true);

        return null;
    }

    public ValidationResultTO getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResultTO validationResult) {
        this.validationResult = validationResult;
    }
}
