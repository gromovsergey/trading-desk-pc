package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.data.ContentSource;
import com.foros.rs.client.data.ContentSourceEntity;
import com.foros.rs.client.data.OutputStreamResponseHandler;
import com.foros.rs.client.model.file.FileList;
import com.foros.rs.client.model.file.RootLocation;
import com.foros.rs.client.model.restriction.Predicates;
import com.foros.rs.client.util.UrlBuilder;

import org.apache.http.HttpEntity;

import java.io.OutputStream;
import java.util.List;

public class FilesService {

    private final RsClient rsClient;

    public FilesService(RsClient rsClient) {
        this.rsClient = rsClient;
    }

    @Deprecated
    /**
     * This method uploads to Creatives root, please use @upload method with expanded argument list
     */
    public void upload(Long accountId, String root, ContentSource zipSource) {
        upload(accountId, root, new ContentSourceEntity(zipSource));
    }

    @Deprecated
    /**
     * This method uploads to Creatives root, please use @upload method with expanded argument list
     */
    public void upload(Long accountId, String root, HttpEntity entity) {
        upload(accountId, null, root, RootLocation.CREATIVES, entity);
    }

    public void upload(Long accountId, Long entityId, String root, RootLocation rootLocation, HttpEntity entity) {
        String uri = UrlBuilder.path("/files")
                .setQueryParameter("account.id", accountId)
                .setQueryParameter("entity.id", entityId)
                .setQueryParameter("root", root)
                .setQueryParameter("rootLocation", rootLocation)
                .build();

        rsClient.post(uri, entity);
    }

    public void upload(Long accountId, Long entityId, String root, RootLocation rootLocation, ContentSource zipSource) {
        upload(accountId, entityId, root, rootLocation, new ContentSourceEntity(zipSource));
    }

    public FileList listDir(Long accountId, Long entityId, String root, RootLocation rootLocation) {
        String uri = UrlBuilder.path("/files/listDir")
                .setQueryParameter("account.id", accountId)
                .setQueryParameter("entity.id", entityId)
                .setQueryParameter("root", root)
                .setQueryParameter("rootLocation", rootLocation)
                .build();

        return rsClient.get(uri);
    }

    public Predicates checkExist(String fileName, Long accountId, String root, RootLocation rootLocation) {
        String uri = UrlBuilder.path("/files/checkExist")
                .setQueryParameter("fileName", fileName)
                .setQueryParameter("account.id", accountId)
                .setQueryParameter("root", root)
                .setQueryParameter("rootLocation", rootLocation)
                .build();

        return rsClient.get(uri);
    }

    public void delete(Long accountId, Long entityId, String path, RootLocation rootLocation) {
        String uri = UrlBuilder.path("/files/delete")
                .setQueryParameter("account.id", accountId)
                .setQueryParameter("entity.id", entityId)
                .setQueryParameter("path", path)
                .setQueryParameter("rootLocation", rootLocation)
                .build();

        rsClient.post(uri, null);
    }

    public void download(Long accountId, Long entityId, String path, RootLocation rootLocation, OutputStream stream) {
        String uri = UrlBuilder.path("/files/download")
                .setQueryParameter("account.id", accountId)
                .setQueryParameter("entity.id", entityId)
                .setQueryParameter("path", path)
                .setQueryParameter("rootLocation", rootLocation)
                .build();

        rsClient.get(uri, new OutputStreamResponseHandler(stream));
    }

    public void uploadAccountDocument(Long accountId, HttpEntity entity) {
        String uri = UrlBuilder.path("/files/accountDocuments/")
                .setQueryParameter("account.id", accountId)
                .build();

        rsClient.post(uri, entity);
    }

    public FileList accountDocumentsList(Long accountId) {
        String uri = UrlBuilder.path("/files/accountDocuments/listDir")
                .setQueryParameter("account.id", accountId)
                .build();

        return rsClient.get(uri);
    }

    public void downloadAccountDocument(Long accountId, String path, OutputStream stream) {
        String uri = UrlBuilder.path("/files/accountDocuments/download")
                .setQueryParameter("account.id", accountId)
                .setQueryParameter("path", path)
                .build();

        rsClient.get(uri, new OutputStreamResponseHandler(stream));
    }

    public void deleteAccountDocument(Long accountId, String path) {
        String uri = UrlBuilder.path("/files/accountDocuments/delete")
                .setQueryParameter("account.id", accountId)
                .setQueryParameter("path", path)
                .build();

        rsClient.post(uri, null);
    }

    public Predicates checkAccountDocuments(List<Long> accountIds) {
        String uri = UrlBuilder.path("/files/accountDocuments/checkFiles").setQueryParameter("account.ids", accountIds).build();
        return rsClient.get(uri);
    }

    public void uploadChannelReport(Long accountId, HttpEntity entity) {
        String uri = UrlBuilder.path("/files/channelReport/")
                .setQueryParameter("account.id", accountId)
                .build();

        rsClient.post(uri, entity);
    }

    public void downloadChannelReport(Long accountId, String path, OutputStream stream) {
        String uri = UrlBuilder.path("/files/channelReport/download")
                .setQueryParameter("account.id", accountId)
                .setQueryParameter("path", path)
                .build();

        rsClient.get(uri, new OutputStreamResponseHandler(stream));
    }

    public FileList channelReportList(Long accountId) {
        String uri = UrlBuilder.path("/files/channelReport/listDir")
                .setQueryParameter("account.id", accountId)
                .build();

        return rsClient.get(uri);
    }
}
