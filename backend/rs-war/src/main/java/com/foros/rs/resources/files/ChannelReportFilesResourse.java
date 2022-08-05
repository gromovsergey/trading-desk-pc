package com.foros.rs.resources.files;

import com.foros.config.ConfigService;
import com.foros.model.fileman.FileInfo;
import com.foros.model.fileman.FileList;
import com.foros.session.account.AccountService;
import com.foros.session.fileman.*;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.foros.config.ConfigParameters.DEFAULT_MAX_TEXT_AD_IMAGE_DIMENSION;
import static com.foros.config.ConfigParameters.UPLOAD_MAX_DIR_LEVELS;

@RequestScoped
@Path("/files/channelReport/")
public class ChannelReportFilesResourse {

    @EJB
    private AccountService accountService;

    @EJB
    private ConfigService configService;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/listDir/")
    public FileList listDir(@QueryParam("account.id") Long accountId) {
        FileManager fileManager = getFileManager(accountId);
        List<FileInfo> files;
        try {
            files = fileManager.getFileList("");
        } catch (IOException e) {
            throw fail("errors.file.badFolderName")
                    .build();
        }

        List<String> result = new ArrayList<>(files.size());
        for (FileInfo fileInfo : files) {
            if (!fileInfo.isDirectory()) {
                result.add(fileInfo.getName());
            }
        }

        return new FileList(result);
    }


    @POST
    @Consumes("application/zip")
    public void upload(@QueryParam("account.id") Long accountId, InputStream zip) {
        FileManager fileManager = getFileManager(accountId);

        try {
            for (FileInfo file : fileManager.getFileList("")) {
                fileManager.delete("", file.getName());
            }

            fileManager.unpackStream("", "", zip, new UnpackOptions(false, UnpackOptions.MergeMode.ADD_UPDATE));
        } catch (TooManyEntriesZipException e) {
            throw fail("errors.file.archiveTooManyEntries").build();
        } catch (BadZipException e) {
            throw fail("errors.file.badArchive").build();
        } catch (FileSizeException e) {
            throw fail("errors.file.archiveSizeExceeded")
                    .withParameters(e.getThreshold())
                    .build();
        } catch (AccountSizeExceededException e) {
            throw fail("errors.file.archiveAccSizeExceeded").build();
        } catch (BadFileNameException e) {
            throw fail("errors.file.badFileName")
                    .withValue(e.getFileName())
                    .build();
        } catch (BadFolderNameException e) {
            throw fail("errors.file.badFolderName")
                    .withValue(e.getFolderName())
                    .build();
        } catch (TooManyDirLevelsException e) {
            Integer maxDirLevels = configService.get(UPLOAD_MAX_DIR_LEVELS);
            throw fail("errors.file.tooManyLevels")
                    .withParameters(maxDirLevels.toString())
                    .build();
        } catch (FileContentException e) {
            throw fail("errors.file.invalidContent")
                    .build();
        } catch (ImageDimensionException e) {
            Integer maxDimension = configService.get(DEFAULT_MAX_TEXT_AD_IMAGE_DIMENSION);
            throw fail("errors.file.imageDimensionsExceeded")
                    .withParameters(maxDimension.toString())
                    .build();
        } catch (IOException e) {
            throw fail("errors.file.uploadAgain").build();
        }
    }

    @GET
    @Path("/download/")
    public Response download(@QueryParam("account.id") Long accountId,
                             @QueryParam("path") String path) {
        FileManager fileManager = getFileManager(accountId);
        try {
            File file = fileManager.getFile(path);
            return Response.ok(file, FileUtils.getMimeTypeByExtension(file.getName()))
                    .build();
        } catch (IOException e) {
            throw fail("errors.file.badFileName")
                    .withValue(path)
                    .build();
        }
    }

    private FileManager getFileManager(Long accountId) {
        checkQueryParamNotNull(accountId, "account.id");
        return accountService.getChannelReportFileManager(accountId);
    }

    private void checkQueryParamNotNull(Object param, String name) {
        if (param == null) {
            throw fail("errors.field.required")
                    .withPath(name)
                    .build();
        }
    }

    private ConstraintViolationException.Builder fail(String messageKey) {
        return ConstraintViolationException.newBuilder(messageKey);
    }
}
