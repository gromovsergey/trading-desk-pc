package com.foros.test.factory;

import com.foros.model.creative.SizeType;
import com.foros.session.creative.SizeTypeService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class SizeTypeTestFactory extends TestFactory<SizeType> {
    @EJB
    private SizeTypeService sizeTypeService;

    @Override
    public SizeType create() {
        SizeType sizeType = new SizeType();
        populate(sizeType);
        return sizeType;
    }

    private void populate(SizeType sizeType) {
        sizeType.setDefaultName(getTestEntityRandomName());
        sizeType.setTagTemplateFile("<script type=\"text/javascript\">" +
                "var PStid=\"##TAG_ID##\",PSsize=\"##SIZE##\";" +
                "</script> \n" +
                "<script id=\"PS_##TAG_ID##\" type=\"text/javascript\" src=\"##ADSERVER_URL##/tag/1.js\" charset=\"UTF-8\">" +
                "</script>");
        sizeType.setTagTemplateIframeFile("<iframe frameborder=\"0\" marginwidth=\"0\" marginheight=\"0\" scrolling=\"no\" style=\"padding-top: 0px;" +
                "padding-right: 0px; padding-bottom: 0px; padding-left: 0px; margin-top: 0px; margin-right: 0px;" +
                "margin-bottom: 0px; margin-left: 0px; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px;" +
                "border-left-width: 0px; border-top-style: none; border-right-style: none; border-bottom-style: none;" +
                "border-left-style: none; border-color: initial; width: ##WIDTH##px; height: ##HEIGHT##px;\"" +
                "src=\"##ADSERVER_URL##/services/nslookup?app=direct&tid=##TAG_ID##&format=html\">" +
                "</iframe>");
        sizeType.setTagTemplateBrPbFile("<script type=\"text/javascript\">" +
                "var PStid=\"##TAG_ID##\",PSsize=\"##SIZE##\",PSpb=\"##PASSBACK_URL##\",PSpt=\"##PASSBACK_TYPE##\";" +
                "</script>" +
                "<script id=\"PS_##TAG_ID##\" type=\"text/javascript\" src=\"##ADSERVER_URL##/services/adop\" charset=\"UTF-8\">" +
                "</script>");
        sizeType.setTagTemplateIEstFile("<script type=\"text/javascript\">" +
                "var PStid=\"##TAG_ID##\",PSsize=\"##SIZE##\",PSies=\"1\";" +
                "</script>" +
                "<script id=\"PS_##TAG_ID##\" type=\"text/javascript\" src=\"##ADSERVER_URL##/tag/3.js\" charset=\"UTF-8\">" +
                "</script>");
        sizeType.setTagTemplatePreviewFile("<script type=\"text/javascript\">" +
                "var PStid=\"##TAG_ID##\",PSsize=\"##SIZE##\",PStr=\",PStr=\"&testrequest=2&loc.name=gb\";" +
                "</script>" +
                "<script id=\"PS_##TAG_ID##\" type=\"text/javascript\" src=\"##ADSERVER_URL##/tag/1.js\" charset=\"UTF-8\">" +
                "</script>");
    }

    @Override
    public void persist(SizeType sizeType) {
        sizeTypeService.create(sizeType);
        entityManager.flush();
    }

    public void update(SizeType sizeType) {
        sizeTypeService.update(sizeType);
        entityManager.flush();
    }

    @Override
    public SizeType createPersistent() {
        SizeType sizeType = create();
        persist(sizeType);
        return sizeType;
    }
}
