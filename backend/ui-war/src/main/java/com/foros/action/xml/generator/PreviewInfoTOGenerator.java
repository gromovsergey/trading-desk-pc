package com.foros.action.xml.generator;

import com.foros.session.creative.PreviewInfoTO;
import com.foros.util.xml.XmlUtil;

public class PreviewInfoTOGenerator  implements Generator<PreviewInfoTO> {

    @Override
    public String generate(PreviewInfoTO model) {
        StringBuilder  xml = new StringBuilder(Constants.XML_HEADER);
        xml.append("<previewInfo>");
        if (model.getErrors() != null && !model.getErrors().isEmpty()) {
            xml.append("<errors>");
            for (String error : model.getErrors()) {
                xml.append("<error>").append(error).append("</error>");
            }
            xml.append("</errors>");
        }
        xml.append(XmlUtil.Generator.tag("width", model.getWidth())).
            append(XmlUtil.Generator.tag("height", model.getHeight())).
            append(XmlUtil.Generator.tag("path", model.getPath()));
        xml.append("</previewInfo>");
        return xml.toString();
    }
}
