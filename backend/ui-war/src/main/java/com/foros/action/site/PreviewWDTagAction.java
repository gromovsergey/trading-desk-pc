package com.foros.action.site;

import com.foros.framework.ReadOnly;
import com.foros.model.site.WDTagOptionValue;
import com.foros.session.fileman.ContentSource;
import com.foros.session.site.WDTagPreviewService;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationService;

import java.util.LinkedHashSet;

import javax.ejb.EJB;

public class PreviewWDTagAction extends PreviewWDTagActionBase {

    private ContentSource contentSource;

    @EJB
    private WDTagPreviewService previewService;

    @EJB
    private ValidationService validationService;

    @ReadOnly
    public String previewContent() {
        setContentSource(previewService.getTagContentHtml(wdTag.getId()));

        return SUCCESS;
    }

    @ReadOnly
    public String livePreview() {
        preparePreview();

        if (hasErrors()) {
            return INPUT;
        }

        wdTag.setWidth(getPreviewWidth());
        wdTag.setHeight(getPreviewHeight());

        setContentSource(previewService.getLiveTagContentHtml(wdTag));
        return SUCCESS;
    }

    public ContentSource getContentSource() {
        return contentSource;
    }

    public void setContentSource(ContentSource contentSource) {
        this.contentSource = contentSource;
    }

    private void preparePreview() {
        wdTag.setOptedInFeeds(WDTagActionHelper.convertUrls(StringUtil.splitAndTrim(getOptedInUrls())));
        wdTag.setOptedOutFeeds(WDTagActionHelper.convertUrls(StringUtil.splitAndTrim(getOptedOutUrls())));
        wdTag.setOptions(new LinkedHashSet<WDTagOptionValue>(getOptionValues().values()));

        validationService.validate("WDTag.checkFiles", wdTag).throwIfHasViolations();
    }
}
