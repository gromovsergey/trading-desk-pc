package com.foros.action.channel.bulk;

import com.opensymphony.xwork2.util.CreateIfNull;
import com.foros.framework.ReadOnly;
import com.foros.model.channel.Channel;
import com.foros.reporting.meta.MetaData;
import com.foros.session.channel.ValidationResultTO;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DownloadChannelAction extends ChannelExportBaseAction {

    private boolean isInternalProcessing;

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("country.countryCode", "'country'", "violation.message")
            .add("urls.positive[(#index)]", "'urls.positive'", "violation.message")
            .add("urls.negative[(#index)]", "'urls.negative'", "violation.message")
            .add("pageKeywords.positive[(#index)]", "'pageKeywords.positive'", "violation.message")
            .add("pageKeywords.negative[(#index)]", "'pageKeywords.negative'", "violation.message")
            .add("searchKeywords.positive[(#index)]", "'searchKeywords.positive'", "violation.message")
            .add("searchKeywords.negative[(#index)]", "'searchKeywords.negative'", "violation.message")
            .add("urlKeywords.positive[(#index)]", "'urlKeywords.positive'", "violation.message")
            .add("urlKeywords.negative[(#index)]", "'urlKeywords.negative'", "violation.message")
            .add("behavioralParameters[U]", "'urls.positive'", "violation.message")
            .add("behavioralParameters[P]", "'pageKeywords.positive'", "violation.message")
            .add("behavioralParameters[S]", "'searchKeywords.positive'", "violation.message")
            .add("behavioralParameters[R]", "'urlKeywords.positive'", "violation.message")
            .rules();

    @CreateIfNull
    private ValidationResultTO validationResult;

    @ReadOnly
    public String export() throws IOException {
        setChannelTypeHidden(validationResult.getChannelType());
        Collection<Channel> channels = bulkChannelToolsService.getValidatedResults(validationResult.getId());

        MetaData<ChannelFieldCsv> metaData = getMetaDataBuilder().forReview();
        ChannelRowSource rowSource = new ChannelRowSource(
                channels.iterator(), new ReviewCsvNodeWriter(createCsvNodeWriter(), RULES, getChannelTypeHidden(), isInternalProcessing));
        serialize(metaData, rowSource);

        return null;
    }

    @ReadOnly
    public String exportInternal() throws IOException {
        isInternalProcessing = true;
        return export();
    }

    @ReadOnly
    public String template() throws IOException {
        Iterator<Channel> iterator = new ArrayList<Channel>().iterator();
        ChannelRowSource rowSource = new ChannelRowSource(iterator, createCsvNodeWriter());

        serialize(getMetaDataBuilder().forUpload(), rowSource);

        return null;
    }

    @ReadOnly
    public String templateInternal() throws IOException {
        isInternalProcessing = true;
        return template();
    }

    public ValidationResultTO getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResultTO validationResult) {
        this.validationResult = validationResult;
    }

    public boolean isInternalProcessing() {
        return isInternalProcessing;
    }
}
