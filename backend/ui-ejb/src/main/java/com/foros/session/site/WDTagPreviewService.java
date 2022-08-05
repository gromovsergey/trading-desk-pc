package com.foros.session.site;

import com.foros.model.site.WDTag;
import com.foros.model.template.DiscoverTemplate;
import com.foros.session.fileman.ContentSource;

import javax.ejb.Local;
@Local
public interface WDTagPreviewService {
    ContentSource getTagContentHtml(Long tagId);

    ContentSource getLiveTagContentHtml(WDTag tag);

    void generateDiscoverCustomization(WDTag tag);

    void generatePreview(WDTag tag);

    void updateObsoletePreviews();

    void deletePreview(DiscoverTemplate template);

    String getHTMLCode(WDTag tag);
}
