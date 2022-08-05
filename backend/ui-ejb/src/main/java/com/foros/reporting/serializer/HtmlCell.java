package com.foros.reporting.serializer;

import com.foros.reporting.serializer.formatter.HtmlCellAccessor;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class HtmlCell implements HtmlCellAccessor {
    private List<String> styles;

    private String html;

    @Override
    public void addStyle(String style) {
        if (styles == null) {
            styles = new ArrayList<String>();
        }
        styles.add(style);
    }

    @Override
    public void setHtml(String html) {
        this.html = html;
    }

    @Override
    public String getHtml() {
        return html;
    }

    public String getCssClasses() {
        return styles == null ? "" : StringUtils.join(styles, ' ');
    }
}
