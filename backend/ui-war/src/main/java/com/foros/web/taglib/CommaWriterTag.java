package com.foros.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.taglibs.standard.tag.rt.core.ForEachTag;

public class CommaWriterTag extends ForEachTag {
    private String label;
    private String separator = ", ";
    private boolean useLabel;
    private boolean escape = true;

    public boolean isEscape() {
        return escape;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public void release() {
        super.release();
        label = null;
    }

    @Override
    protected void prepare() throws JspTagException {
        super.prepare();
        useLabel = items.hasNext();
    }

    @Override
    public int doAfterBody() throws JspException {
        // If tag has a body comma-separated list
        useLabel = false;
        try {
            writeSeparator();
        } catch (IOException e) {
            throw new JspTagException(e);
        }
        return super.doAfterBody();
    }

    @Override
    public int doEndTag() throws JspException {
        if (useLabel && label != null) {
            writeUsingLabel();
        }
        return super.doEndTag();
    }

    private String retrieveName(Object element) throws JspTagException {
        try {
            return BeanUtils.getProperty(element, label);
        } catch (Exception e) {
            throw new JspTagException(e);
        }
    }

    private void writeSeparator() throws JspTagException, IOException {
        if (hasNext()) {
            pageContext.getOut().write(separator);
        }
    }

    private void writeUsingLabel() throws JspTagException {
        JspWriter out = pageContext.getOut();
        try {
            Object o = getCurrent();
            writeObject(out, o);
            while (hasNext()) {
                o = next();
                writeObject(out, o);
            }
        } catch (IOException e) {
            throw new JspTagException(e);
        }
    }

    private void writeObject(JspWriter out, Object o) throws JspTagException, IOException {
        if (o != null) {
            String text = retrieveName(o);
            if (escape) {
                out.write(StringEscapeUtils.escapeHtml(text));
            } else {
                out.write(text);
            }
        }
        writeSeparator();
    }
}
