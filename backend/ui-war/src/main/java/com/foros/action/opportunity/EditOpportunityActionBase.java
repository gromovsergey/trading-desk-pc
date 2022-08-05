package com.foros.action.opportunity;

import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.AdvertiserAccount;
import com.foros.session.account.AccountService;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class EditOpportunityActionBase extends OpportunitySupportAction implements RequestContextsAware, BreadcrumbsSupport {
    @EJB
    private AccountService accountService;

    protected List<File> addedFiles = new ArrayList<File>();

    protected List<String> addedFilesFileName = new ArrayList<String>();

    protected AdvertiserAccount existingAccount;

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchToAdvertiser(opportunity.getAccount().getId());
    }

    public List<File> getAddedFiles() {
        return addedFiles;
    }

    public void setAddedFiles(List<File> addedFiles) {
        this.addedFiles = addedFiles;
    }

    public List<String> getAddedFilesFileName() {
        return addedFilesFileName;
    }

    public void setAddedFilesFileName(List<String> addedFilesFileName) {
        this.addedFilesFileName = addedFilesFileName;
    }

    public AdvertiserAccount getExistingAccount() {
        if (existingAccount == null) {
            existingAccount = accountService.findAdvertiserAccount(opportunity.getAccount().getId());
        }
        return existingAccount;
    }
}
