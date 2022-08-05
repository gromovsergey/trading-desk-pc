package com.foros.web.resources;

import com.foros.util.web.ResponseCacheHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class ResourcesServlet extends HttpServlet {

    private static final long DELTA = 365L * 24L * 60L * 60L * 1000L;

    private AssetManager assetManager;

    public ResourcesServlet(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Asset asset = getAsset(request);

        if (asset != null) {
            if (assetCached(request)) {
                response.addHeader("Content-Length", String.valueOf(0));
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }

            setHeaders(response, asset);

            ServletOutputStream outputStream = response.getOutputStream();
            try {
                IOUtils.copy(asset.getStream(), outputStream);
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private boolean assetCached(HttpServletRequest request) {
        String header = request.getHeader("If-Modified-Since");
        return header != null && !"".equals(header);
    }

    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");

    private void setHeaders(HttpServletResponse response, Asset asset) {
        response.setContentType(asset.getContentType());
        response.setCharacterEncoding("UTF-8");

        response.setDateHeader("Last-Modified", getVersion(asset));
        ResponseCacheHelper.setCached(response, DELTA);
    }

    private long getVersion(Asset asset) {
        try {
            return format.parse(String.valueOf(asset.getVersion())).getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    private Asset getAsset(HttpServletRequest request) throws ServletException {
        String type = request.getServletPath().substring(1);

        String[] parts = request.getPathInfo().substring(1).split("/");

        if (parts.length < 2) {
            throw new ServletException();
        }

        long version = parseVersion(parts[0]);

        String name = fetchName(parts);

        return assetManager.get(type, name, version);
    }

    private long parseVersion(String version) {
        try {
            return Long.parseLong(version);
        } catch (NumberFormatException e) {
            return System.currentTimeMillis();
        }
    }

    private String fetchName(String[] parts) {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (!part.contains("..")) {
                builder.append("/").append(part);
            }
        }
        return builder.toString();
    }

}
