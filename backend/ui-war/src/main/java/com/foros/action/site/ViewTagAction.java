package com.foros.action.site;

import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.LocalizableName;
import com.foros.model.account.AccountAuctionSettings;
import com.foros.model.creative.CreativeSize;
import com.foros.model.site.CategoryExclusionApproval;
import com.foros.model.site.TagAuctionSettings;
import com.foros.model.site.TagsCreativeCategoryExclusion;
import com.foros.session.CurrentUserService;
import com.foros.session.auctionSettings.AuctionSettingsService;
import com.foros.session.creative.CreativeSizeService;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.StringUtil;

import java.util.Collection;
import javax.ejb.EJB;

public class ViewTagAction extends TagSupportAction implements RequestContextsAware, BreadcrumbsSupport {
    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AuctionSettingsService auctionSettingsService;

    @EJB
    protected CreativeSizeService creativeSizeService;

    private String inventoryEstimationTagView;
    private String tagView;
    private String iframeTagView;
    private String browserPassbackTagView;
    private Long id;

    private TagAuctionSettings tagAuctionSettings;
    private AccountAuctionSettings defaultAuctionSettings;

    public static class OptionEntry {
        private LocalizableName name;
        private String value;

        public OptionEntry(LocalizableName name, String value) {
            this.name = name;
            this.value = value;
        }

        public LocalizableName getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    @ReadOnly
    public String view() throws Exception {
        setTag(tagsService.viewFetched(id));

        prepareView();

        tagAuctionSettings = auctionSettingsService.findByTagId(id);
        defaultAuctionSettings = auctionSettingsService.findDefaultByTagId(id);

        return SUCCESS;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private void prepareView() throws Exception {
        populatePassback();
    }

    public String getInventoryEstimationTagView() {
        if (inventoryEstimationTagView == null) {
            setInventoryEstimationTagView(tagsService.generateInventoryEstimationTagHtml(getTag()));
        }
        return inventoryEstimationTagView;
    }

    public void setInventoryEstimationTagView(String inventoryEstimationTagView) {
        this.inventoryEstimationTagView = inventoryEstimationTagView;
    }

    public String getTagView() {
        if (tagView == null) {
            setTagView(tagsService.generateTagHtml(getTag()));
        }

        return tagView;
    }

    public void setTagView(String tagView) {
        this.tagView = tagView;
    }

    public String getIframeTagView() {
        if (iframeTagView == null) {
            setIframeTagView(tagsService.generateIframeTagHtml(getTag()));
        }

        return iframeTagView;
    }

    public void setIframeTagView(String iframeTagView) {
        this.iframeTagView = iframeTagView;
    }

    public String getBrowserPassbackTagView() {
        if (browserPassbackTagView == null) {
            setBrowserPassbackTagView(tagsService.generateBrowserPassbackTagHtml(getTag()));
        }

        return browserPassbackTagView;
    }

    public void setBrowserPassbackTagView(String browserPassbackTagView) {
        this.browserPassbackTagView = browserPassbackTagView;
    }

    public boolean isShowIframeTag() {
        CreativeSize size = getTag().getOnlySizeOrNull();
        return (currentUserService.isInternal() || getTag().getAccount().getAccountType().getShowIframeTag())
                && size != null && !isSpecialTagSize();
    }

    public boolean isShowBrowserPassbackTag() {
        CreativeSize size = getTag().getOnlySizeOrNull();
        return (currentUserService.isInternal() || getTag().getAccount().getAccountType().getShowBrowserPassbackTag())
                && size != null && !isSpecialTagSize();
    }

    // https://confluence.ocslab.com/display/TDOCDRAFT/REQ-2099+Browser+Passback+and+iframe+Tag+Support+in+UI
    // bq. tag size width + height > 2 (so that we exclude special 1x1 sizes)
    private boolean isSpecialTagSize() {
        Long tagWidth = tagsService.getTagWidth(getTag());
        Long tagHeight = tagsService.getTagHeight(getTag());
        return ((tagWidth == null ? 0 : tagWidth) + (tagHeight == null ? 0 : tagHeight)) <= 2;
    }

    public TagAuctionSettings getTagAuctionSettings() {
        return tagAuctionSettings;
    }

    public AccountAuctionSettings getDefaultAuctionSettings() {
        return defaultAuctionSettings;
    }

    private String getCategories(Collection<TagsCreativeCategoryExclusion> categories, CategoryExclusionApproval approval, boolean isShort) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        boolean allEquals = true;
        for (TagsCreativeCategoryExclusion c: categories) {
            if (c.getApproval() == approval) {
                index++;
                if (!isShort || index <= 10) {
                    if (index > 1) {
                        sb.append(", ");
                    }
                    sb.append(LocalizableNameUtil.getLocalizedValue(c.getCreativeCategory().getName()));
                }
            } else {
                allEquals = false;
            }
        }
        if (allEquals) {
            return StringUtil.getLocalizedString("site.allCategories");
        }
        return sb.toString();
    }

    public String getVisualCategoriesAcceptShort() {
        return getCategories(getAvailableCreativeCategoryExclusions(), CategoryExclusionApproval.ACCEPT, true);
    }

    public String getVisualCategoriesAcceptFull() {
        return getCategories(getAvailableCreativeCategoryExclusions(), CategoryExclusionApproval.ACCEPT, false);
    }

    public String getVisualCategoriesRejectShort() {
        return getCategories(getAvailableCreativeCategoryExclusions(), CategoryExclusionApproval.REJECT, true);
    }

    public String getVisualCategoriesRejectFull() {
        return getCategories(getAvailableCreativeCategoryExclusions(), CategoryExclusionApproval.REJECT, false);
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new SiteBreadcrumbsElement(getTag().getSite())).add(new TagBreadcrumbsElement(getTag()));
    }

    public boolean isAvailableCreativeCustomization() {
        return tagsService.findSizesWithPublisherOptions(getTag()).size() > 0 ||
                templateService.findTemplatesWithPublisherOptions(getTag()).size() > 0;
    }
}
