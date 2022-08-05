package com.foros.action.site;

import com.foros.action.IdNameBean;
import com.foros.action.admin.option.CachedOptionValue;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.Status;
import com.foros.model.template.Template;
import com.foros.session.LocalizableNameEntityComparator;
import com.foros.session.site.SiteService;
import com.foros.session.template.TemplateService;
import com.foros.util.LocalizableNameEntityHelper;
import com.foros.util.context.RequestContexts;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;


public class EditWDTagActionBase extends PreviewWDTagActionBase implements RequestContextsAware {

    @EJB
    protected SiteService siteService;

    @EJB
    protected TemplateService templateService;

    private Long accountId;

    private Collection<CachedOptionValue> cachedOptionValues = new LinkedList<CachedOptionValue>();

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Collection<CachedOptionValue> getCachedOptionValues() {
        return cachedOptionValues;
    }

    public void setCachedOptionValues(Collection<CachedOptionValue> cachedOptionValues) {
        this.cachedOptionValues = cachedOptionValues;
    }

    public List<IdNameBean> getTemplates() {
        if (wdTag.getSite() == null) {
            return Collections.emptyList();
        }

        if (wdTag.getAccount() == null) {
            wdTag.setSite(siteService.view(wdTag.getSite().getId()));
        }

        List<Template> result = templateService.findByAccountType(wdTag.getSite().getAccount().getAccountType());
        if (wdTag.getTemplate() != null && wdTag.getTemplate().getId() != null) {
            Template template = templateService.findById(wdTag.getTemplate().getId());
            if (template.getStatus() == Status.DELETED) {
                if(wdTag.getId() != null) {
                    result.add(templateService.findById(wdTag.getTemplate().getId()));
                } else {
                    wdTag.setTemplate(null);
                    getOptionValues().clear();
                }
            }
        }

        Collections.sort(result, new LocalizableNameEntityComparator());

        return LocalizableNameEntityHelper.convertToIdNameBeans(result);
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getPublisherContext().switchTo(wdTag.getAccount().getId());
    }
}
