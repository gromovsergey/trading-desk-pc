package com.foros.framework;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.dispatcher.StrutsResultSupport;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class SimpleTextResult extends StrutsResultSupport {
    private String charSet = "UTF-8";
    private String contentType = "text/plain";
    private String textField = "text";

    @Override
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        String text = fetchText(invocation);
        if (text == null) {
            throw new IllegalArgumentException("Unable to get value of field '" + textField +
                    "'; action name: '" + invocation.getInvocationContext().getName() + "'");
        }

        HttpServletResponse response = (HttpServletResponse) invocation.getInvocationContext().get(HTTP_RESPONSE);
        response.setHeader("Cache-Control", "no-cache");
        if (contentType != null) {
            response.setContentType(contentType);
        }
        Charset charset = createCharset();
        if (charset != null) {
            response.setCharacterEncoding(charset.toString());
        }
        response.setContentLength(text.getBytes(charset).length);

        PrintWriter writer = response.getWriter();
        writer.write(text);

        writer.flush();
        writer.close();
    }

    private String fetchText(ActionInvocation invocation) {
        String text = null;
        if (textField != null) {
            text = (String) invocation.getStack().findValue(conditionalParse(textField, invocation));
        }
        return text;
    }

    private Charset createCharset() {
        if (charSet != null) {
            return Charset.isSupported(charSet) ? Charset.forName(charSet) : null;
        }
        return null;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
