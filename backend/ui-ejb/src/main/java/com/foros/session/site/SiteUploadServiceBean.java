package com.foros.session.site;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.EntityBase;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.security.principal.SecurityContext;
import com.foros.session.UploadStatus;
import com.foros.session.db.DBConstraint;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.OnNoProviderRoot;
import com.foros.session.fileman.PathProviderService;
import com.foros.util.Stats;
import com.foros.util.UploadUtils;
import com.foros.validation.ValidationService;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.PersistenceException;
import org.apache.commons.io.IOUtils;


@Stateless(name = "SiteUploadService")
public class SiteUploadServiceBean implements SiteUploadService {

    @EJB
    private SiteService siteService;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private ValidationService validationService;

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public void createOrUpdateAll(String validationResultId) {
        Long accountId = SecurityContext.getPrincipal().getAccountId();
        startProcessing(accountId, validationResultId);
        List<Site> sites = fetchValidatedSites(validationResultId);
        try {
            siteService.createOrUpdateAll(sites);
        } catch (PersistenceException e) {
            if (DBConstraint.SITE_NAME.match(e)) {
                validationService.validateInNewTransaction("Site.nameConstraintViolations", accountId, sites).throwIfHasViolations();
            }

            throw e; // just in case
        }
        postProcessing(accountId, validationResultId);
        finishProcessing(accountId, validationResultId);
    }

    @Override
    public List<Site> fetchValidatedSites(String validationResultId) {
        ObjectInputStream ois = null;
        List<Site> sites = null;

        try {
            Long accountId = SecurityContext.getPrincipal().getAccountId();
            ois = new ObjectInputStream(getFileSystemByAccount(accountId).readFile(getFileName(validationResultId, "sites")));
            //noinspection unchecked
            sites = (List<Site>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(ois);
        }

        return sites;
    }

    @Override
    public SiteUploadValidationResultTO validateAll(List<Site> sites) {
        siteService.validateAll(sites);
        String validationId = saveResults(sites);
        return fillValidationResult(sites, validationId);
    }

    private SiteUploadValidationResultTO fillValidationResult(List<Site> sites, String validationId) {
        SiteUploadValidationResultTO validationResult = new SiteUploadValidationResultTO();
        ErroneousLinesCounter erroneousLinesCounter = new ErroneousLinesCounter();
        for (Site site : sites) {
            updateStats(site, validationResult.getSites(), erroneousLinesCounter);

            boolean siteIsRejected = isEntityRejected(site);
            for (Tag tag : site.getTags()) {
                if (siteIsRejected) {
                    erroneousLinesCounter.registerErroneousEntity(tag);
                }
                updateStats(tag, validationResult.getTags(), erroneousLinesCounter);
            }
        }

        validationResult.setId(validationId);
        validationResult.setLineWithErrors(erroneousLinesCounter.getErroneousLinesNumber());

        return validationResult;
    }

    private boolean isEntityRejected(EntityBase entity) {
        return SiteUploadUtil.getUploadContext(entity).getStatus() == UploadStatus.REJECTED;
    }

    private void updateStats(EntityBase entity, Stats stats, ErroneousLinesCounter erroneousLinesCounter) {
        if (isEntityRejected(entity)) {
            erroneousLinesCounter.registerErroneousEntity(entity);
        } else {
            addStats(entity, stats);
        }
    }

    @Override
    public String saveResults(List<Site> sites) {
        Long accountId = SecurityContext.getPrincipal().getAccountId();
        ObjectOutputStream oos = null;

        try {
            UUID uuid = UUID.randomUUID();

            oos = new ObjectOutputStream(getFileSystemByAccount(accountId).openFile(getFileName(uuid, "sites")));
            oos.writeObject(sites);

            return uuid.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(oos);
        }
    }

    private String getFileName(String validationResultId, String suffix) {
        return getFileName(UUID.fromString(validationResultId), suffix);
    }

    private String getFileName(UUID uuid, String suffix) {
        return uuid.toString() + "." + suffix;
    }

    private void addStats(EntityBase entity, Stats stats) {
        switch (SiteUploadUtil.getUploadContext(entity).getStatus()) {
            case NEW:
                stats.setCreated(stats.getCreated() + 1);
                break;
            case UPDATE:
                stats.setUpdated(stats.getUpdated() + 1);
                break;
            default:
                break;
        }
    }

    private void startProcessing(Long accountId, String validationResultId) {
        FileSystem fs = getFileSystemByAccount(accountId);

        if (!fs.lock(getFileName(validationResultId, "lock"))) {
            throw ConstraintViolationException.newBuilder("site.upload.inprogress").build();
        }

        if (fs.checkExist(getFileName(validationResultId, "submitted"))) {
            throw ConstraintViolationException.newBuilder("site.upload.alreadySubmitted").build();
        }
    }

    private void postProcessing(Long accountId, String validationResultId) {
        getFileSystemByAccount(accountId).lock(getFileName(validationResultId, "submitted"));
    }

    private void finishProcessing(Long accountId, String validationResultId) {
        getFileSystemByAccount(accountId).delete(getFileName(validationResultId, "lock"));
    }

    private FileSystem getFileSystemByAccount(Long accountId) {
        return pathProviderService.getBulkUpload().getNested(accountId.toString(), OnNoProviderRoot.AutoCreate).createFileSystem();
    }

    private class ErroneousLinesCounter {
        private Set<Long> erroneousRows = new HashSet<>();
        private Long erroneousLinesNumber = 0l;

        public void registerErroneousEntity(EntityBase entity) {
            Long rowNumber = UploadUtils.getRowNumber(entity);
            if (erroneousRows.add(rowNumber)) {
                erroneousLinesNumber++;
            }
        }

        public Long getErroneousLinesNumber() {
            return erroneousLinesNumber;
        }
    }
}
