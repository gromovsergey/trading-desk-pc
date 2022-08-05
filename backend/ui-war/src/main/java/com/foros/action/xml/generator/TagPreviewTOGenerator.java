package com.foros.action.xml.generator;

import com.foros.action.xml.model.TagPreviewTO;
import com.foros.util.xml.XmlUtil;

public class TagPreviewTOGenerator implements Generator<TagPreviewTO> {

    public String generate(TagPreviewTO previewTO) {
        StringBuilder  xml = new StringBuilder(Constants.XML_HEADER);

        xml.append("<tagPreview>").
            append(XmlUtil.Generator.tag("width", previewTO.getWidth())).
            append(XmlUtil.Generator.tag("height", previewTO.getHeight())).
            append(XmlUtil.Generator.tag("preview", previewTO.getPerview())).
            append("</tagPreview>");

        return xml.toString();
    }

}

