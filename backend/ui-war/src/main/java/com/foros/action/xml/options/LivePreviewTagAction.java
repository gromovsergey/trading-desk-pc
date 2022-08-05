package com.foros.action.xml.options;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.action.xml.model.TagPreviewTO;
import com.foros.model.creative.CreativeSize;
import com.foros.model.site.Tag;
import com.foros.model.site.TagOptionValue;
import com.foros.session.site.TagsPreviewService;
import com.foros.session.site.TagsService;
import com.foros.validation.ValidationService;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class LivePreviewTagAction extends AbstractXmlAction<TagPreviewTO> implements ModelDriven<Tag> {

    @EJB
    private TagsService tagsService;

    @EJB
    private TagsPreviewService previewService;

    @EJB
    private ValidationService validationService;

    private Tag tag = new Tag();

    private Map<Long, TagOptionValue> optionValues;


    public Map<Long, TagOptionValue> getOptionValues() {
        if (optionValues == null) {
            optionValues = new HashMap<Long, TagOptionValue>();
            for (TagOptionValue v : tag.getOptions()) {
                optionValues.put(v.getOption().getId(), v);
            }
        }
        return optionValues;
    }

    public void setOptionValues(Map<Long, TagOptionValue> tagOptionValues) {
        this.optionValues = tagOptionValues;
    }

    private void preparePreview() {
        tag = tagsService.viewFetched(tag.getId());
        tag.setOptions(new LinkedHashSet<TagOptionValue>(getOptionValues().values()));
        validationService.validate("Tag.livePreview", getTag()).throwIfHasViolations();
    }

    @Override
    protected TagPreviewTO generateModel() throws ProcessException {
        preparePreview();

        ByteArrayOutputStream stream = previewService.getLivePreview(getTag());
        CreativeSize size = tag.getOnlySize();
        return new TagPreviewTO(size.getWidth(), size.getHeight(), stream.toString());
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Override
    public Tag getModel() {
        return tag;
    }
}
