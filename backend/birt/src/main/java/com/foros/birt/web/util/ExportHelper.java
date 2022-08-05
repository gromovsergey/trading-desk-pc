package com.foros.birt.web.util;

import com.foros.reporting.serializer.BulkFormat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.report.context.IContext;
import org.eclipse.birt.report.utility.ParameterAccessor;

public abstract class ExportHelper {

    public static void prepareExport(IContext context) throws IOException {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        String format = ParameterAccessor.getFormat(request);
        String emitterId = ParameterAccessor.getEmitterId(request);

        String fileName = ParameterAccessor.getExportFilename(context, format, emitterId);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
    }

    public static void prepareExtract(IContext context) throws IOException {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        // get extract format
        String openType = ParameterAccessor.getOpenType( request );
        String extractFormat = ParameterAccessor.getExtractFormat(request);
        String extractExtension = ParameterAccessor.getExtractExtension(request);

        if (extractExtension != null) {
            extractFormat = ParameterAccessor.getExtractFormat(extractExtension);
        }

        String fileName = ParameterAccessor.getExtractionFilename(context, extractExtension, extractFormat);

        response.setHeader("Content-Disposition", openType + "; filename=\"" + fileName + "\"");

        // set mime type
        String mimeType = ParameterAccessor.getExtractionMIMEType(extractFormat, extractExtension);

        if (mimeType != null && mimeType.length() > 0) {
            response.setContentType(mimeType);
        } else {
            response.setContentType("application/octet-stream"); //$NON-NLS-1$
        }

        String encoding = ParameterAccessor.getExportEncoding(request);
        for (BulkFormat format : BulkFormat.values()) {
            if (format.getEncoding() != null && format.getEncoding().equals(encoding)) {
                format.addUnicodeSupport(context.getResponse().getOutputStream());
                break;
            }
        }
    }

    public static void prepareDownload(IContext context) {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        String extractFileName = ParameterAccessor.getParameter(request, "__extractfilename");
        String extractExtension = ParameterAccessor.getExtractExtension(request);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + extractFileName + "." + extractExtension + "\"");
    }

}
