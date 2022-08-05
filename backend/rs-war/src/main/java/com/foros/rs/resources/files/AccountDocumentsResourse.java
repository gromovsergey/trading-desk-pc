package com.foros.rs.resources.files;

import com.foros.config.ConfigService;
import com.foros.model.fileman.FileInfo;
import com.foros.model.fileman.FileList;
import com.foros.model.restriction.Predicates;
import com.foros.session.CurrentUserService;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.foros.config.ConfigParameters.DEFAULT_MAX_TEXT_AD_IMAGE_DIMENSION;
import static com.foros.config.ConfigParameters.UPLOAD_MAX_DIR_LEVELS;

@RequestScoped
@Path("/files/accountDocuments/")
public class AccountDocumentsResourse {

    @EJB
    private AccountService accountService;

    @EJB
    private ConfigService configService;

    @EJB
    private CurrentUserService currentUserService;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/listDir/")
    public FileList listDir(@QueryParam("account.id") Long accountId) {
        FileManager fileManager = getFileManagerForView(accountId);
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
        FileManager fileManager = getFileManagerForUpdate(accountId);
        try {
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
                    .withValue(extractPath(fileManager, e, ""))
                    .build();
        } catch (ImageDimensionException e) {
            Integer maxDimension = configService.get(DEFAULT_MAX_TEXT_AD_IMAGE_DIMENSION);
            throw fail("errors.file.imageDimensionsExceeded")
                    .withParameters(maxDimension.toString())
                    .withValue(extractPath(fileManager, e, ""))
                    .build();
        } catch (IOException e) {
            throw fail("errors.file.uploadAgain").build();
        }
    }

    @GET
    @Path("/download/")
    public Response download(@QueryParam("account.id") Long accountId,
                             @QueryParam("path") String path) {
        FileManager fileManager = getFileManagerForView(accountId);
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

    @POST
    @Path("/delete/")
    public void delete(
            @QueryParam("account.id") Long accountId,
            @QueryParam("path") String pathAsStr) {

        FileManager fileManager = getFileManagerForUpdate(accountId);

        java.nio.file.Path path = Paths.get(pathAsStr);
        String dir = path.getParent() == null ? "" : path.getParent().toString();
        String file = path.getFileName().toString();

        try {
            fileManager.delete(dir, file);
        } catch (IOException e) {
            throw fail("errors.file.badFileName")
                    .withValue(pathAsStr)
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/checkFiles/")
    public Predicates checkFiles(@QueryParam("account.ids") List<Long> accountIds) {
        List<Boolean> result = new ArrayList<>(accountIds.size());

        for (Long accountId : accountIds) {
            FileManager fileManager = getFileManagerForView(accountId);
            try {
                result.add(checkFilesList(fileManager.getFileList("")));
            } catch (IOException e) {
                throw fail("errors.file.badFolderName")
                        .build();
            }
        }

        return new Predicates(result);
    }

    private boolean checkFilesList(List<FileInfo> files) {
        for (FileInfo fileInfo: files) {
            if (!fileInfo.isDirectory()) {
                return true;
            }
        }
        return false;
    }

    private FileManager getFileManagerForView(Long accountId) {
        checkQueryParamNotNull(accountId, "account.id");
        return accountService.getDocumentsFileManagerForView(accountId);
    }

    private FileManager getFileManagerForUpdate(Long accountId) {
        checkQueryParamNotNull(accountId, "account.id");
        return accountService.getDocumentsFileManagerForUpdate(accountId);
    }

    private void checkQueryParamNotNull(Object param, String name) {
        if (param == null) {
            throw fail("errors.field.required")
                    .withPath(name)
                    .build();
        }
    }

    private String extractPath(FileManager fileManager, FilePathException e, String root) {
        return e.getPath().replace(fileManager.getRootPath(), "").replaceFirst("[\\\\|/]" + root, "");
    }

    private ConstraintViolationException.Builder fail(String messageKey) {
        return ConstraintViolationException.newBuilder(messageKey);
    }
}
