package com.foros.action.opportunity;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.opportunity.Opportunity;
import com.foros.session.account.AccountService;
import com.foros.session.fileman.FileManager;
import com.foros.session.opportunity.OpportunityHelper;
import com.foros.util.AccountUtil;
import com.foros.validation.annotation.Validate;

import javax.ejb.EJB;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Validations(
    conversionErrorFields = {
        @ConversionErrorFieldValidator(fieldName = "amount", key = "errors.field.number")
    }
)

public class SaveOpportunityAction extends EditOpportunityActionBase {

    @EJB
    private AccountService accountService;

    @Validate(validation = "Opportunity.create", parameters = {"#target.model", "#target.prepareIOFiles()"})
    public String create() {
        opportunityService.create(opportunity, prepareIOFiles());
        return SUCCESS;
    }

    @Validate(validation = "Opportunity.update", parameters = {"#target.model", "#target.prepareIOFiles()"})
    public String update() {
        opportunityService.update(opportunity, prepareIOFiles());
        return SUCCESS;
    }

    public Map<String, File> prepareIOFiles() {
        getModel().setAccount((AdvertiserAccount) AccountUtil.extractAccountById(opportunity.getAccount().getId()));
        Map<String, File> ioFiles = new HashMap<String, File>();
        if (opportunity.getProbability() != null && OpportunityHelper.isIOFileRequired(opportunity)) {
            for (int i = 0; i < addedFiles.size(); i++) {
                ioFiles.put(addedFilesFileName.get(i), addedFiles.get(i));
            }
            if (existingFiles != null && opportunity.getId() != null) {
                FileManager fileManager = accountService.getOpportunitiesFileManager(opportunity);
                for (String fileName : existingFiles) {
                    try {
                        ioFiles.put(fileName, fileManager.getFile(fileName));
                    } catch (IOException io) {
                    }
                }
            } else {
                existingFiles = new ArrayList<String>();
            }
        }
        return ioFiles;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = null;
        if (opportunity.getId() != null) {
            final Opportunity persistent = opportunityService.find(opportunity.getId());
            breadcrumbs = new Breadcrumbs().add(new OpportunityBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        }
        return breadcrumbs;
    }
}
