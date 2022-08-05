package com.foros.rs.resources.files;

import com.foros.config.ConfigService;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.fileman.FileInfo;
import com.foros.model.fileman.FileList;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.restriction.Predicates;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.account.AccountService;
import com.foros.session.fileman.AccountSizeExceededException;
import com.foros.session.fileman.BadFileNameException;
import com.foros.session.fileman.BadFolderNameException;
import com.foros.session.fileman.BadZipException;
import com.foros.session.fileman.FileContentException;
import com.foros.session.fileman.FileManager;
import com.foros.session.fileman.FilePathException;
import com.foros.session.fileman.FileSizeException;
import com.foros.session.fileman.FileUtils;
import com.foros.session.fileman.ImageDimensionException;
import com.foros.session.fileman.TooManyDirLevelsException;
import com.foros.session.fileman.TooManyEntriesZipException;
import com.foros.session.fileman.UnpackOptions;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.foros.config.ConfigParameters.DEFAULT_MAX_TEXT_AD_IMAGE_DIMENSION;
import static com.foros.config.ConfigParameters.UPLOAD_MAX_DIR_LEVELS;

@RequestScoped
@Path("/files/")
public class FilesResource {

    @EJB
    private AccountService accountService;

    @EJB
    private ConfigService configService;

    @EJB
    private CurrentUserService currentUserService;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/listDir/")
    public FileList listDir(@QueryParam("account.id") Long accountId,
                            @QueryParam("entity.id") Long entityId,
                            @QueryParam("root") String root,
                            @QueryParam("rootLocation") RootLocation rootLocation) {
        if (rootLocation != RootLocation.IO) {
            throw fail("errors.operation.not.permitted").build();
        }

        AdvertiserAccount account = getAccount(accountId);
        FileManager fileManager = getOpportunitiesFileManager(account, entityId, true);

        List<FileInfo> files;
        try {
            files = fileManager.getFileList(root);
        } catch (IOException e) {
            throw fail("errors.file.badFolderName")
                    .withValue(root)
                    .build();
        }

        List<String> result = new ArrayList<>(files.size());
        for (FileInfo fileInfo : files) {
            result.add(fileInfo.getName());
        }

        return new FileList(result);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/checkExist/")
    public Predicates checkExist(@QueryParam("fileName") String fileName,
                                 @QueryParam("account.id") Long accountId,
                                 @QueryParam("root") String root,
                                 @QueryParam("rootLocation") RootLocation rootLocation) {
        if (rootLocation != RootLocation.CREATIVES) {
            throw fail("errors.operation.not.permitted").build();
        }

        AdvertiserAccount account = getAccount(accountId);
        FileManager fileManager = accountService.getCreativesFileManager(account);

        try {
            Boolean result = fileManager.checkExist(root, fileName);
            return new Predicates(new ArrayList<>(Collections.singletonList(result)));
        } catch (IOException e) {
            throw fail("errors.file.badFolderName").build();
        }
    }

    @GET
    @Path("/download/")
    public Response download(@QueryParam("account.id") Long accountId,
                             @QueryParam("entity.id") Long entityId,
                             @QueryParam("path") String path,
                             @QueryParam("rootLocation") RootLocation rootLocation) {
        if (rootLocation != RootLocation.IO) {
            throw fail("errors.operation.not.permitted").build();
        }

        AdvertiserAccount account = getAccount(accountId);
        FileManager fileManager = getOpportunitiesFileManager(account, entityId, true);

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
    @Consumes("application/zip")
    public void upload(
            @QueryParam("account.id") Long accountId,
            @QueryParam("entity.id") Long entityId,
            @QueryParam("root") String root,
            @QueryParam("rootLocation") RootLocation rootLocation,
            InputStream zip) {
        // Default destination
        rootLocation = rootLocation != null ? rootLocation : RootLocation.CREATIVES;

        AdvertiserAccount account = getAccount(accountId);

        FileManager fileManager = null;
        try {
            switch (rootLocation) {
                case CREATIVES:
                    fileManager = accountService.getCreativesFileManager(account);
                    break;
                case IO:
                    fileManager = getOpportunitiesFileManager(account, entityId, false);
                    break;
            }

            fileManager.unpackStream(root, "", zip, new UnpackOptions(false, UnpackOptions.MergeMode.ADD_UPDATE));

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
                .withValue(extractPath(fileManager, e, root))
                    .build();
        } catch (ImageDimensionException e) {
            Integer maxDimension = configService.get(DEFAULT_MAX_TEXT_AD_IMAGE_DIMENSION);
            throw fail("errors.file.imageDimensionsExceeded")
                .withParameters(maxDimension.toString())
                .withValue(extractPath(fileManager, e, root))
                .build();
        } catch (IOException e) {
            throw fail("errors.file.uploadAgain").build();
        }
    }

    @POST
    @Path("/delete/")
    public void delete(
            @QueryParam("account.id") Long accountId,
            @QueryParam("entity.id") Long entityId,
            @QueryParam("rootLocation") RootLocation rootLocation,
            @QueryParam("path") String pathAsStr) {
        if (rootLocation != RootLocation.IO) {
            throw fail("errors.operation.not.permitted").build();
        }

        AdvertiserAccount account = getAccount(accountId);
        FileManager fileManager = getOpportunitiesFileManager(account, entityId, false);

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

    private FileManager getOpportunitiesFileManager(AdvertiserAccount account, Long entityId, boolean isReadOnly) {
        checkQueryParamNotNull(entityId, "entity.id");

        Opportunity opportunity = new Opportunity();
        opportunity.setId(entityId);
        opportunity.setAccount(account);
        return isReadOnly ? accountService.getOpportunitiesFileManagerForView(opportunity) :
                accountService.getOpportunitiesFileManagerForUpdate(opportunity);
    }

    private String extractPath(FileManager fileManager, FilePathException e, String root) {
        return e.getPath().replace(fileManager.getRootPath(), "").replaceFirst("[\\\\|/]" + root, "");
    }

    private ConstraintViolationException.Builder fail(String messageKey) {
        return ConstraintViolationException.newBuilder(messageKey);
    }

    private AdvertiserAccount getAccount(Long accountId) {
        if (accountId == null && currentUserService.getAccountRole() == AccountRole.ADVERTISER) {
            accountId = currentUserService.getAccountId();
        }

        checkQueryParamNotNull(accountId, "account.id");

        try {
            return accountService.findAdvertiserAccount(accountId);
        } catch (EntityNotFoundException e) {
            throw fail("errors.account.advertiser.notFound")
                    .withParameters(accountId)
                    .build();
        }
    }

    private void checkQueryParamNotNull(Object param, String name) {
        if (param == null) {
            throw fail("errors.field.required")
                    .withPath(name)
                    .build();
        }
    }

    public enum RootLocation {
        CREATIVES,
        IO
    }
}
