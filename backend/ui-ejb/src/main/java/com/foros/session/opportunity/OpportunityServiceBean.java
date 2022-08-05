package com.foros.session.opportunity;

import static com.foros.model.opportunity.Probability.AWAITING_GO_LIVE;
import static com.foros.model.opportunity.Probability.BRIEF_RECEIVED;
import static com.foros.model.opportunity.Probability.FIRST_CONTACT;
import static com.foros.model.opportunity.Probability.IO_SIGNED;
import static com.foros.model.opportunity.Probability.LIVE;
import static com.foros.model.opportunity.Probability.LOST;
import static com.foros.model.opportunity.Probability.PROPOSAL_SENT;
import static com.foros.model.opportunity.Probability.TARGET;
import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CampaignAllocation;
import com.foros.model.campaign.CampaignAllocationSumTO;
import com.foros.model.fileman.FileInfo;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.opportunity.Probability;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.account.AccountService;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.fileman.ContentSource;
import com.foros.session.fileman.FileContentException;
import com.foros.session.fileman.FileManager;
import com.foros.session.fileman.FileUtils;
import com.foros.session.fileman.PathProvider;
import com.foros.session.security.AuditService;
import com.foros.tx.TransactionCallback;
import com.foros.tx.TransactionSupportService;
import com.foros.util.CollectionTransformer;
import com.foros.util.StringUtil;
import com.foros.util.VersionCollisionException;
import com.foros.util.mapper.Converter;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.util.ValidationUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

@Stateless(name = "OpportunityService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class OpportunityServiceBean implements OpportunityService {

    @EJB
    private AccountService accountService;

    @EJB
    private AuditService auditService;

    @EJB
    private TransactionSupportService transactionSupportService;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    private static final Logger logger = Logger.getLogger(OpportunityServiceBean.class.getName());

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "Opportunity.create", parameters = "find('Account', #opportunity.account.id)")
    @Validate(validation = "Opportunity.create", parameters = {"#opportunity", "#ioFiles"})
    public Long create(Opportunity opportunity, Map<String, File> ioFiles) {
        prePersist(opportunity);
        em.persist(opportunity);
        if (ioFiles != null && !ioFiles.isEmpty() && OpportunityHelper.canCreateIOFile(opportunity)) {
            updateIOFiles(opportunity, ioFiles);
            opportunity.setProperty(ADDED_IO_FILES, ioFiles.keySet());
        }
        auditService.audit(opportunity, ActionType.CREATE);
        return opportunity.getId();
    }

    @Override
    @Interceptors({CaptureChangesInterceptor.class, AutoFlushInterceptor.class})
    @Restrict(restriction = "Opportunity.update", parameters = "find('Opportunity', #opportunity.id)")
    @Validate(validation = "Opportunity.update", parameters = {"#opportunity", "#ioFiles"})
    public Opportunity update(Opportunity opportunity, Map<String, File> ioFiles) {
        prePersist(opportunity);
        Opportunity existingOpportunity = view(opportunity.getId());
        if (OpportunityHelper.canUpdateIOFiles(opportunity)) {
            updateIOFiles(opportunity, ioFiles);
            opportunity.setProperty(ADDED_IO_FILES, CollectionUtils.subtract(ioFiles.keySet(), getIOFileNames(opportunity)));
            opportunity.setProperty(REMOVED_IO_FILES, getFileNamesToDelete(opportunity, ioFiles));
        }

        opportunity = em.merge(opportunity);
        auditService.audit(opportunity, ActionType.UPDATE);
        return opportunity;
    }

    @Override
    @Restrict(restriction = "Opportunity.view", parameters = "find('Opportunity', #id)")
    public Opportunity view(Long id) {
        return find(id);
    }

    @Override
    @Restrict(restriction = "Opportunity.view", parameters = "find('Account', #accountId)")
    public Collection<Opportunity> findOpportunitiesForAccount(Long accountId) {
        return em.createNamedQuery("Opportunity.findByAccountId")
                .setParameter("accountId", accountId)
                .getResultList();
    }

    @Override
    public Opportunity find(Long id) {
        Opportunity res = em.find(Opportunity.class, id);
        if (res == null) {
            throw new EntityNotFoundException("Opportunity with id=" + id + " not found");
        }
        return res;
    }

    private void updateIOFiles(final Opportunity opportunity, final Map<String, File> ioFiles) {
        transactionSupportService.onTransaction(new TransactionCallback() {
            Collection<String> deletedIos = getFileNamesToDelete(opportunity, ioFiles);
            FileManager fm = accountService.getOpportunitiesFileManager(opportunity);
            String uuid = UUID.randomUUID().toString();

            @Override
            public void onBeforeCommit() {
                ValidationContext validationContext = null;

                //Addition of files
                for (Map.Entry<String, File> attachment : ioFiles.entrySet()) {
                    String realFileName = attachment.getKey();
                    try {
                        if (!fm.checkExist("", realFileName)) {
                            if (attachment.getValue().getAbsolutePath().startsWith(fm.getRootPath()) && !attachment.getValue().exists()) {
                                //File was deleted from FM during editing of opportunity
                                throw new VersionCollisionException();
                            }
                            writeFile(getFileName("create", uuid, StringUtil.trimFileName(realFileName)), attachment.getValue(), fm);
                        }
                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    } catch (FileContentException fce) {
                        if (validationContext == null) {
                            validationContext = ValidationUtil.validationContext().build();
                        }
                        validationContext.addConstraintViolation("opportunity.invalid.fileContent")
                                         .withError(BusinessErrors.FILE_ERROR)
                                         .withParameters(realFileName);
                    }
                }

                if (validationContext != null) {
                    validationContext.throwIfHasViolations();
                }

                if (!deletedIos.isEmpty()) {
                    try {
                        fm.createFolder("", getFolderName("delete", uuid));
                        for (String fileName : deletedIos) {
                            fm.renameTO(fileName, getFileName("delete", uuid, fileName));
                        }
                    } catch (Exception io) {
                        throw new RuntimeException(io);
                    }
                }
            }

            @Override
            public void onCommit() {
                if (!ioFiles.isEmpty()) {
                    for (Map.Entry<String, File> attachment : ioFiles.entrySet()) {
                        String realFileName = StringUtil.trimFileName(attachment.getKey());
                        String tempFileName = getFileName("create", uuid, realFileName);
                        try {
                            fm.renameTO(tempFileName, realFileName);
                        } catch (Exception io) {
                            logger.log(Level.WARNING, "Failed to move the file from " + tempFileName + " to " + realFileName, io);
                        }
                    }
                    deleteFolder("create", uuid);
                }

                if (!deletedIos.isEmpty()) {
                    for (String fileName : deletedIos) {
                        String tempFileName = getFileName("delete", uuid, fileName);
                        try {
                            FileUtils.deleteFile(fm.getFile(tempFileName));
                        } catch (IOException io) {
                            logger.log(Level.WARNING, "Failed to delete the file " + tempFileName, io);
                        }
                    }
                    deleteFolder("delete", uuid);
                }
            }

            @Override
            public void onRollback() {
                // reverting added files
                if (!ioFiles.isEmpty()) {
                    for (Map.Entry<String, File> attachment : ioFiles.entrySet()) {
                        String tempFileName = getFileName("create", uuid, StringUtil.trimFileName(attachment.getKey()));
                        try {
                            // Delete created tmp file
                            FileUtils.deleteFile(fm.getFile(tempFileName));
                        } catch (Exception io) {
                            logger.log(Level.WARNING, "Failed to delete the file " + tempFileName, io);
                        }
                    }
                    deleteFolder("create", uuid);
                }

                //reverting deleted files
                if (!deletedIos.isEmpty()) {
                    for (String fileName : deletedIos) {
                        String tempFileName = getFileName("delete", uuid, fileName);
                        try {
                            fm.renameTO(tempFileName, fileName);
                        } catch (Exception io) {
                            logger.log(Level.WARNING, "Failed to move the file from " + tempFileName + " to " + fileName, io);
                        }
                    }
                    deleteFolder("delete", uuid);
                }
            }

            private void writeFile(String realFileName, File file, FileManager fm) throws IOException {
                fm.createFile("", realFileName, new FileInputStream(file));
            }

            private void deleteFolder(String prefix, String uuid) {
                String folderName = getFolderName(prefix, uuid);
                try {
                    fm.delete("", folderName);
                } catch (IOException io) {
                    logger.log(Level.WARNING, "Failed to remove the folder " + folderName, io);
                }
            }

            private String getFileName(String prefix, String uuid, String fileName) {
                return getFolderName(prefix, uuid) + PathProvider.PATH_SEPARATOR + fileName;
            }

            private String getFolderName(String prefix, String uuid) {
                return prefix + "_" + uuid;
            }
        });
    }

    private void prePersist(Opportunity opportunity) {
        AdvertiserAccount account;
        if (opportunity.getId() == null) {
            if (!OpportunityHelper.canCreateIOFile(opportunity)) {
                removeIOFields(opportunity);
            }
            account = em.getReference(AdvertiserAccount.class, opportunity.getAccount().getId());
        } else {
            Opportunity existingOpportunity = find(opportunity.getId());
            account = existingOpportunity.getAccount();
            if (!OpportunityHelper.canUpdateIOFiles(opportunity)) {
                opportunity.unregisterChange("poNumber", "ioNumber");
                if (opportunity.getProbability() != Probability.LOST && existingOpportunity.getAccount().getAccountType().getIoManagement()) {
                    opportunity.unregisterChange("notes");
                }
            } else if (!OpportunityHelper.isIOFileRequired(opportunity) && OpportunityHelper.isIOFileRequired(existingOpportunity)) {
                removeIOFields(opportunity);
            }
        }
        opportunity.setAccount(account);
        opportunity.unregisterChange("account");
    }

    private void removeIOFields(Opportunity opportunity) {
        opportunity.setIoNumber(null);
        opportunity.setPoNumber(null);
    }

    private Collection<String> getFileNamesToDelete(Opportunity opportunity, Map<String, File> ioFiles) {
        Collection<String> existingIOFiles = getIOFileNames(opportunity);
        if (!existingIOFiles.isEmpty()) {
            Collection<String> ioFileNames = com.foros.util.CollectionUtils.convert(new Converter<Map.Entry<String, File>, String>() {
                @Override
                public String item(Map.Entry<String, File> value) {
                    return value.getKey();
                }
            }, ioFiles.entrySet());
            return CollectionUtils.subtract(existingIOFiles, ioFileNames);
        }
        return new HashSet<String>();
    }

    @Override
    public Set<String> getIOFileNames(Opportunity opportunity) {
        List<FileInfo> ioFiles;
        try {
            ioFiles = accountService.getOpportunitiesFileManager(opportunity).getFileList(".");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CollectionUtils.filter(ioFiles, new Predicate() {
            @Override
            public boolean evaluate(Object obj) {
                FileInfo file = (FileInfo) obj;
                return !file.isDirectory();
            }
        });
        Collections.sort(ioFiles, new Comparator<FileInfo>() {
            public int compare(FileInfo o1, FileInfo o2) {
                return new Long(o2.getTime()).compareTo(o1.getTime());
            }
        });
        return new HashSet<String>(new IOCollectionTransformer().transform(ioFiles));
    }

    @Override
    @Restrict(restriction = "Opportunity.view", parameters = "find('Opportunity', #opportunityId)")
    public ContentSource getIOFileContent(Long opportunityId, String fileName) {
        FileManager fm = accountService.getOpportunitiesFileManager(find(opportunityId));
        return fm.readFile(fileName);
    }

    private class IOCollectionTransformer extends CollectionTransformer<FileInfo, String> {
        @Override
        public String item(FileInfo value) {
            return value.getName();
        }
    }

    @Restrict(restriction = "Entity.view", parameters = "find('Account', #accountId)")
    public Collection<Probability> getAvailableProbabilities(Long accountId, Probability existingProbability) {
        boolean isIODisabled = !accountService.find(accountId).getAccountType().getIoManagement();

        Set<Probability> probabilities = new TreeSet<Probability>(new Comparator<Probability>() {
            @Override
            public int compare(Probability p1, Probability p2) {
                return p1.ordinal() - p2.ordinal();
            }
        });

        if (existingProbability == null) {
            // new opportunity
            probabilities.add(TARGET);
            probabilities.add(FIRST_CONTACT);
            probabilities.add(BRIEF_RECEIVED);
            probabilities.add(PROPOSAL_SENT);
            probabilities.add(IO_SIGNED);
            probabilities.add(LOST);
            return probabilities;
        }

        //  existing value always allowed
        probabilities.add(existingProbability);

        switch (existingProbability) {
            case TARGET:
                probabilities.add(FIRST_CONTACT);
                probabilities.add(BRIEF_RECEIVED);
                probabilities.add(PROPOSAL_SENT);
                probabilities.add(IO_SIGNED);
                probabilities.add(LOST);
                break;
            case FIRST_CONTACT:
                probabilities.add(BRIEF_RECEIVED);
                probabilities.add(PROPOSAL_SENT);
                probabilities.add(IO_SIGNED);
                probabilities.add(LOST);
                break;
            case BRIEF_RECEIVED:
                probabilities.add(FIRST_CONTACT);
                probabilities.add(PROPOSAL_SENT);
                probabilities.add(IO_SIGNED);
                probabilities.add(LOST);
                break;
            case PROPOSAL_SENT:
                probabilities.add(FIRST_CONTACT);
                probabilities.add(BRIEF_RECEIVED);
                probabilities.add(IO_SIGNED);
                probabilities.add(LOST);
                break;
            case IO_SIGNED:
                probabilities.add(BRIEF_RECEIVED);
                probabilities.add(PROPOSAL_SENT);
                probabilities.add(LOST);
                if (isIODisabled) {
                    probabilities.add(AWAITING_GO_LIVE);
                    probabilities.add(LIVE);
                }
                break;
            case AWAITING_GO_LIVE:
                if (isIODisabled) {
                    probabilities.add(IO_SIGNED);
                    probabilities.add(LOST);
                    probabilities.add(LIVE);
                }
                break;
            case LIVE:
                break;
            case LOST:
                break;
        }

        return probabilities;
    }

    @Override
    @Restrict(restriction = "Opportunity.viewIO", parameters = "find('Opportunity', #id)")
    public Opportunity viewIO(Long id) {
        return view(id);
    }

    @Override
    @Restrict(restriction = "Opportunity.view", parameters = "find('Opportunity', #opportunityId)")
    public List<CampaignAllocation> getCampaignAllocations(Long opportunityId) {
        Query q = em.createQuery("select a from CampaignAllocation a " +
                " where a.opportunity.id = :opportunityId");
        q.setParameter("opportunityId", opportunityId);
        return q.getResultList();
    }

    @Override
    public List<CampaignAllocationSumTO> findCampaignAllocationSum(Long opportunityId) {
        String query = "select new com.foros.model.campaign.CampaignAllocationSumTO(" +
                "   camAll.campaign.id, " +
                "   camAll.campaign.name, " +
                "   sum(camAll.amount), " +
                "   sum(camAll.utilizedAmount)) " +
                " from CampaignAllocation camAll " +
                " where camAll.opportunity.id = :opportunityId " +
                " group by camAll.campaign.id, camAll.campaign.name " +
                " order by sum(camAll.amount) desc ";

        return em.createQuery(query, CampaignAllocationSumTO.class)
                .setParameter("opportunityId", opportunityId)
                .getResultList();
    }
}
