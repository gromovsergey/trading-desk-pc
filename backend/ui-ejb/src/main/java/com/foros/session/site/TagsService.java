package com.foros.session.site;

import com.foros.model.ExtensionProperty;
import com.foros.model.account.MarketplaceType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.model.site.TagEffectiveSizes;
import com.foros.model.site.TagPricing;
import com.foros.session.EntityTO;
import com.foros.session.bulk.Result;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Local;

@Local
public interface TagsService {
    ExtensionProperty<String> TAG_VIEW = new ExtensionProperty<String>(String.class);

    Long create(Tag tag);

    void update(Tag tag);

    Tag view(Long id);

    Tag find(Long id);

    Tag viewFetched(Long id);

    Tag viewFetchedForEdit(Long tagId);

    void refresh(Long id);

    void delete(Long id);

    void undelete(Long id);

    List<EntityTO> getList(Long siteId);

    List<Tag> findBySite(Long siteId);

    List<CreativeSize> findSizesBySite(Long siteId);

    /**
     * Returns a html content of passed tag, in case if its 'passback' value
     * is properly set.
     *
     * @param tag tag to be populated.
     * @throws IOException
     */
    String getPassbackHtml(Tag tag) throws IOException;

    /**
     * Populates passed tags within html content, if corresponding passback values are set. If not, html content
     * will not be populated.
     *
     * @param tags the set of tags to be processed
     */
    void fetchPassbackHtml(Collection<Tag> tags);

    void fetchTagsHtml(Collection<Tag> tags);

    String generateTagPreviewHtml(Tag tag);

    String generateTagHtml(Tag tag);

    String generateInventoryEstimationTagHtml(Tag tag);

    String generateIframeTagHtml(Tag tag);

    String generateBrowserPassbackTagHtml(Tag tag);

    TagPricing findTagPricing(Tag tag, TagPricing pricing);

    void validateAll(Set<Tag> tags);

    void createOrUpdateAll(Collection<Tag> tags, Site site, MarketplaceType marketplace, Map<String, CreativeSize> creativeSizes);

    void updateOptions(Tag tag);

    public List<CreativeSize> findSizesWithPublisherOptions(Tag tag);

    Long getTagWidth(Tag tag);

    Long getTagHeight(Tag tag);

    Result<Tag> get(TagSelector selector);

    TagEffectiveSizes getEffectiveSizes(Long tagId);
}
