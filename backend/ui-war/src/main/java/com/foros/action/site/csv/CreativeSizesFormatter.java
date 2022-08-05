package com.foros.action.site.csv;

import com.foros.model.creative.CreativeSize;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;

import java.util.Collection;
import java.util.Iterator;

public class CreativeSizesFormatter extends ValueFormatterSupport<Collection<CreativeSize>> {

    @Override
    public String formatText(Collection<CreativeSize> sizes, FormatterContext context) {
        StringBuilder buffer = new StringBuilder();
        if (sizes.isEmpty()) {
            return SiteCsvProcessor.ALL_SIZES;
        } else {
            for (Iterator iterator = sizes.iterator(); iterator.hasNext();) {
                CreativeSize creativeSize = (CreativeSize) iterator.next();
                buffer.append(creativeSize.getProtocolName());
                if (iterator.hasNext()) {
                    buffer.append(",");
                }
            }
        }
        return buffer.toString();
    }
}
