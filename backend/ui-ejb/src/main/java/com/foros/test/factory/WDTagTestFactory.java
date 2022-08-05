package com.foros.test.factory;

import com.foros.model.Status;
import com.foros.model.site.Site;
import com.foros.model.site.WDTag;
import com.foros.model.site.WDTagOptionValue;
import com.foros.model.site.WDTagOptionValuePK;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.Option;
import com.foros.session.site.WDTagService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class WDTagTestFactory extends TestFactory<WDTag> {
    @EJB
    private  SiteTestFactory siteTF;

    @EJB
    private WDTagService wdTagService;

    @EJB
    private DiscoverTemplateTestFactory discoverTemplateTF;

    private void populate(WDTag wdTag) {
        wdTag.setName(getTestEntityRandomName());
        wdTag.setWidth(500L);
        wdTag.setHeight(100L);
        wdTag.setOptedInOption(WDTag.FeedOption.A);
        wdTag.setOptedOutOption(WDTag.FeedOption.A);
        wdTag.setStatus(Status.ACTIVE);

    }

    @Override
    public WDTag create() {
        Site site = siteTF.createPersistent();
        return create(site);
    }

    @Override
    public void persist(WDTag wdTag) {
        wdTagService.create(wdTag);
    }

    @Override
    public void update(WDTag wdTag) {
        wdTagService.update(wdTag);
    }

    public WDTag create(Site persisted) {
        WDTag wdTag = new WDTag();
        wdTag.setSite(persisted);
        populate(wdTag);
        return wdTag;
    }

    @Override
    public WDTag createPersistent() {
        WDTag wdTag = create();
        DiscoverTemplate discoverTemplate = discoverTemplateTF.createPersistent();
        wdTag.setTemplate(discoverTemplate);
        try {
        wdTagService.create(wdTag);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        clearContext();

        wdTag = findById(wdTag.getId());
        return wdTag;
    }

    public WDTag findById(Long id) {
        WDTag wdTag = wdTagService.view(id);
        return wdTag;
    }

    public WDTagOptionValue createWDTagOption(Option o, String value) {
        WDTagOptionValuePK valueId = new WDTagOptionValuePK();
        valueId.setOptionId(o.getId());
        WDTagOptionValue optionValue = new WDTagOptionValue(valueId);
        optionValue.setOption(o);
        optionValue.setValue(value);
        return optionValue;
    }
}
