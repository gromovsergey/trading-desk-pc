package app.programmatic.ui.creative.tool;

import app.programmatic.ui.common.tool.converter.XmlDateTimeConverter;
import app.programmatic.ui.common.tool.foros.ForosHelper;
import app.programmatic.ui.common.tool.foros.StatusHelper;
import app.programmatic.ui.creative.dao.model.Creative;
import app.programmatic.ui.creative.dao.model.CreativeCategory;
import app.programmatic.ui.creative.dao.model.CreativeDisplayStatus;
import app.programmatic.ui.creative.dao.model.CreativeStat;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class CreativeBuilder {
    public static com.foros.rs.client.model.advertising.campaign.Creative buildForosCreative(Creative src) {
        com.foros.rs.client.model.advertising.campaign.Creative result =
                new com.foros.rs.client.model.advertising.campaign.Creative();

        result.setId(src.getId());
        result.setAccount(ForosHelper.createAdvertiserLink(src.getAccountId()));
        result.setName(src.getName());
        result.setSize(ForosHelper.createEntityLink(src.getSizeId()));
        result.setTemplate(ForosHelper.createEntityLink(src.getTemplateId()));
        result.setOptions(src.getOptions());
        result.setWidth(src.getWidth());
        result.setHeight(src.getHeight());
        result.setStatus(StatusHelper.getRsStatusByMajorStatus(src.getDisplayStatus()));
        result.setUpdated(XmlDateTimeConverter.convertEpoch(src.getVersion(), "GMT"));

        ArrayList<CreativeCategory> categories = new ArrayList<>(src.getContentCategories().size() + src.getVisualCategories().size());
        categories.addAll(src.getContentCategories());
        categories.addAll(src.getVisualCategories());

        result.setCategories(
                categories.stream()
                    .map( category -> ForosHelper.createEntityLink(category.getId()) )
                    .collect(Collectors.toList())
        );

        return result;
    }

    public static CreativeStat fillCreativeStatFields(CreativeStat target, com.foros.rs.client.model.advertising.campaign.Creative src,
                                                      String sizeName, String templateName) {
        target.setId(src.getId());
        target.setName(src.getName());
        target.setDisplayStatus(CreativeDisplayStatus.valueOf(src.getDisplayStatusId().intValue()).getMajorStatus());
        target.setAccountId(src.getAccount().getId());
        if (src.getAccount().getAgency() != null) {
            target.setAgencyId(src.getAccount().getAgency().getId());
        }
        target.setWidth(src.getWidth());
        target.setHeight(src.getHeight());

        target.setTemplateId(src.getTemplate().getId());
        target.setTemplateName(templateName);

        target.setSizeId(src.getSize().getId());
        target.setSizeName(sizeName);

        return target;
    }
}
