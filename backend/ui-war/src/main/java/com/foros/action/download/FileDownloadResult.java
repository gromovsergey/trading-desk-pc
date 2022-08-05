package com.foros.action.download;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FileDownloadResult extends AbstractContentSourceResult {

    protected void initResponse(HttpServletRequest request, HttpServletResponse response) {
        setDownloadHeaders(request, response, getTargetFile());
    }

    public static void setDownloadHeaders(HttpServletRequest request, HttpServletResponse response, String targetFileName) {
        response.setDateHeader("Expires", -1);

        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null || !userAgent.contains("MSIE")) {
            // Disable caching
            response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
            response.setHeader("Pragma", "no-cache");
        } else {
            // Do NOT use 'no-cache' and 'no-store' IE goes mad in that case.
            // See http://support.microsoft.com/kb/323308/ and OUI-6057
            // Also it wants Pragma: cache - I don't know why...
            response.setHeader("Cache-Control", "private");
            response.setHeader("Pragma", "cache");
        }

        // Set file related headers
        if (userAgent != null && userAgent.contains("MSIE 8")) {
            response.setHeader("Content-Disposition", "attachment; filename=" + encodeFileName(targetFileName));
        } else {
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodeFileName(targetFileName));
        }
    }

    private static String encodeFileName(String fileName) {
        try {
            return URLEncoder.encode(fileName, "utf-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
