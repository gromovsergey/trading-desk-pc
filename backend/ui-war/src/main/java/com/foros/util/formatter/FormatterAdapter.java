package com.foros.util.formatter;

import javax.servlet.jsp.PageContext;
import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;

public class FormatterAdapter implements DisplaytagColumnDecorator {
    private FieldFormatter formatter;

    public FormatterAdapter(FieldFormatter formatter) {
        this.formatter = formatter;
    }

    public Object decorate(Object columnValue, PageContext pageContext, MediaTypeEnum media) throws DecoratorException {
        return formatter.getString(columnValue);
    }

    public FieldFormatter getFormatter() {
        return formatter;
    }
}
