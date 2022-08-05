package com.foros.action.account.terms;

import com.foros.action.BaseActionSupport;
import com.foros.action.site.csv.UploadSizeExceedException;
import com.foros.framework.CustomFileUploadInterceptor;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.fileman.FileInfo;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.account.AccountService;
import com.foros.session.fileman.BadFileNameException;
import com.foros.session.fileman.FileContentException;
import com.foros.util.StringUtil;
import com.foros.util.context.ContextBase;
import com.foros.util.context.RequestContexts;
import org.apache.struts2.interceptor.RequestAware;

import javax.ejb.EJB;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AddAccountTermsAction extends BaseActionSupport implements RequestAware, RequestContextsAware {
    @EJB
    private AccountService accountService;

    private Long id;
    private Collection<FileInfo> terms;
    private List<File> fileToUpload = new ArrayList<File>();
    private List<String> fileToUploadContentType = new ArrayList<String>();
    private List<String> fileToUploadFileName = new ArrayList<String>();

    private Map<String, Object> request;

    @Override
    public void setRequest(Map<String, Object> request) {
        this.request = request;
    }

    public void setFileToUpload(List<File> fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    public void setFileToUploadContentType(List<String> fileToUploadContentType) {
        this.fileToUploadContentType = fileToUploadContentType;
    }

    public void setFileToUploadFileName(List<String> fileToUploadFileName) {
        this.fileToUploadFileName = fileToUploadFileName;
    }

    public Collection<FileInfo> getTerms() throws IOException {
        if (terms != null) {
            return terms;
        }
        terms = accountService.viewExternalAccount(id).getTerms();
        

        return terms;
    }

    @ReadOnly
    @Restrict(restriction="Account.updateTerms", parameters="find('Account',#target.id)")
    public String edit() {
        return SUCCESS;
    }

    @Restrict(restriction="Account.updateTerms", parameters="find('Account',#target.id)")
    public String save() throws FileNotFoundException, IOException, UploadSizeExceedException {
        if (request.get(CustomFileUploadInterceptor.ATTRIBUTE_MAX_LENGTH_EXCEEDED) != null) {
            throw new UploadSizeExceedException(StringUtil.getLocalizedString("errors.file.sizeExceeded"));
        }
        Account account = accountService.find(id);
        for (int i = 0; i < fileToUpload.size(); i++) {
            File file = fileToUpload.get(i);
            String fileName = fileToUploadFileName.get(i); 
            FileInputStream is = new FileInputStream(file);
            try {
                try {
                    accountService.addTerm(account, fileName, is);
                } catch (BadFileNameException e) {
                    addActionError(getText("errors.file.badFileName"));
                } catch (FileContentException e) {
                    addActionError(getText("errors.file.invalidContent"));
                }
            } finally {
                is.close();
            }
        }
        if (hasErrors()) {
            return INPUT;
        }
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public void switchContext(RequestContexts contexts) {
        Account account = accountService.find(id);
        ContextBase context = contexts.getContext(account.getRole());

        if (context != null) {
            context.switchTo(account.getId());
        }
    }


}
