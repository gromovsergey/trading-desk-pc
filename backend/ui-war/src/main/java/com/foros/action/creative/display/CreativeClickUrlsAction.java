package com.foros.action.creative.display;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.util.context.RequestContexts;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import org.apache.commons.lang.StringUtils;

public class CreativeClickUrlsAction extends BaseActionSupport implements AdvertiserSelfIdAware, RequestContextsAware {
    @EJB
    private DisplayCreativeService displayCreativeService;

    public static enum Mode {
        Set,
        Append,
        Replace
    }

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("clickUrl", "violation.message")
            .add("clickUrl(#path)", "violation.message")
            .rules();

    private List<Long> creativeIds = new ArrayList<>();
    private Mode editMode = Mode.Set;
    private String url;
    private String append;
    private String search;
    private String replace;
    private Long advertiserId;

    @ReadOnly
    public String edit() {
        return SUCCESS;
    }

	public Long getAdvertiserId() {
        return advertiserId;
    }

    public String getAppend() {
        return append;
    }

    public List<Long> getCreativeIds() {
        return creativeIds;
    }

    public Mode getEditMode() {
        return editMode;
    }

    public String getReplace() {
        return replace;
    }

    public String getSearch() {
        return search;
    }

    public String getUrl() {
        return url;
    }

    private void appendClickUrls() {
        if (StringUtils.isBlank(append)) {
            return;
        }
        displayCreativeService.appendClickUrl(creativeIds, append);
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    private void findReplaceClickUrls() {
        if (StringUtils.isBlank(search)) {
            return;
        }
        displayCreativeService.findReplaceClickUrl(creativeIds, search, replace);
    }

    private void setClickUrls() {
        if (StringUtils.isBlank(url)) {
            return;
        }
        displayCreativeService.setClickUrl(creativeIds, url);
    }

    public String save() {
        switch (editMode) {
            case Set:
                setClickUrls();
                break;
            case Append:
                appendClickUrls();
                break;
            case Replace:
                findReplaceClickUrls();
                break;
        }
        return SUCCESS;
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public void setAppend(String append) {
        this.append = append;
    }

    public void setCreativeIds(List<Long> creativeIds) {
        this.creativeIds = creativeIds;
    }

    public void setEditMode(Mode editMode) {
        this.editMode = editMode;
    }

    public void setReplace(String replace) {
        this.replace = replace;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(advertiserId);
    }
}
