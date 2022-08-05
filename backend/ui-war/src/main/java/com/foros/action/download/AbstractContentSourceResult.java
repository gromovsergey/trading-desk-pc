package com.foros.action.download;

import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FilenameUtils;
import com.opensymphony.xwork2.ActionInvocation;
import com.foros.session.fileman.ContentSource;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStream;

public abstract class AbstractContentSourceResult extends StrutsResultSupport {
    private static final String DEFAULT_CONTENT_TYPE = "application/binary";
    private static final Logger logger = Logger.getLogger(FileDownloadResult.class.getName());
    private String targetFile = "${targetFile}";
    private String contentSourceName = "contentSource";
    private String contentType;
    private ContentSource contentSource;

    public String getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentSourceName() {
        return contentSourceName;
    }

    public void setContentSourceName(String contentSourceName) {
        this.contentSourceName = contentSourceName;
    }

    public ContentSource getContentSource() {
        return contentSource;
    }

    public void setContentSource(ContentSource contentSource) {
        this.contentSource = contentSource;
    }

    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {

        if (contentSourceName != null) {
            contentSource = (ContentSource) invocation.getStack().findValue(conditionalParse(contentSourceName, invocation));
        }

        if (contentSource == null) {
            String msg = ("Can not find a PathProvider with the name [" + contentSourceName + "] in the invocation stack. " +
                    "Check the <param name=\"pathProviderName\"> tag specified for this action.");
            logger.log(Level.SEVERE, msg);
            throw new IllegalArgumentException(msg);
        }

        targetFile = conditionalParse(targetFile, invocation);

        if (StringUtils.isBlank(targetFile)) {
            // use source file name as default value
            targetFile = FilenameUtils.getName(contentSource.getName());
        }

        contentType = conditionalParse(contentType, invocation);
        if (StringUtils.isBlank(contentType)) {
            contentType = DEFAULT_CONTENT_TYPE;
        }

        doDownload(invocation);
    }

    private void doDownload(ActionInvocation invocation) throws Exception {
        // Find Request & Response in context
        HttpServletResponse response = (HttpServletResponse) invocation.getInvocationContext().get(HTTP_RESPONSE);
        HttpServletRequest request = (HttpServletRequest) invocation.getInvocationContext().get(HTTP_REQUEST);

        response.setContentLength((int) getContentSource().getLength());
        response.setContentType(getContentType());

        initResponse(request, response);

        // Write file
        ServletOutputStream os = null;
        InputStream is = null;
        try {
            os = response.getOutputStream();
            is = contentSource.getStream();
            IOUtils.copy(is, os);
        } catch (IOException ex) {
            logger.info(ex.getMessage());
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
        }
    }

    protected abstract void initResponse(HttpServletRequest request, HttpServletResponse response);
}
