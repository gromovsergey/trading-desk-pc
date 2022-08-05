package com.foros.session.creative;

import com.foros.model.DisplayStatus;
import com.foros.model.LocalizableName;
import com.foros.model.Status;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Template;
import com.foros.session.status.ApprovableEntityTO;

import java.util.Date;

@SuppressWarnings("serial")
public final class CreativeTO extends ApprovableEntityTO {
    private String accountName;
    private Long accountId;
    private LocalizableName sizeName;
    private Long sizeId;
    private LocalizableName templateName;
    private Long templateId;
    private char accountStatus;
    private Date version;
    private DisplayStatus templateDisplayStatus;
    private DisplayStatus sizeDisplayStatus;

    public static CreativeTOBuilder createBuilder(Long id, String name, char status, char qaStatus,
            DisplayStatus displayStatus) {
        CreativeTO instance = new CreativeTO(id, name, status, qaStatus, displayStatus);
        return instance.new CreativeTOBuilder();
    }

    public class CreativeTOBuilder {

        private CreativeTO instance = CreativeTO.this;

        private CreativeTOBuilder() {
        }

        public CreativeTOBuilder withAccountName(String accountName) {
            instance.setAccountName(accountName);
            return this;
        }

        public CreativeTOBuilder withAccountId(Long id) {
            instance.setAccountId(id);
            return this;
        }

        public CreativeTOBuilder withSize(Long sizeId, String sizeDefName) {
            instance.setSizeName(new LocalizableName(sizeDefName, "CreativeSize." + sizeId));
            instance.setSizeId(sizeId);
            return this;
        }

        public CreativeTOBuilder withTemplate(Long templateId, String templateDefName) {
            instance.setTemplateName(new LocalizableName(templateDefName, "CreativeTemplate." + templateId));
            instance.setTemplateId(templateId);
            return this;
        }

        public CreativeTOBuilder withSizeDisplayStatus(char sizeStatus) {
            instance.setSizeDisplayStatus(CreativeSize.getDisplayStatus(Status.valueOf(sizeStatus)));
            return this;
        }

        public CreativeTOBuilder withTemplateDisplayStatus(char templateStatus) {
            instance.setTemplateDisplayStatus(Template.getDisplayStatus(Status.valueOf(templateStatus)));
            return this;
        }

        public CreativeTOBuilder withAccountStatus(char accountStatus) {
            instance.setAccountStatus(accountStatus);
            return this;
        }

        public CreativeTOBuilder withVersion(Date version) {
            instance.setVersion(version);
            return this;
        }

        public CreativeTO build() {
            return this.instance;
        }

    }

    private CreativeTO(Long id, String name, char status, char qaStatus, DisplayStatus displayStatus) {
        super(id, name, status, qaStatus, displayStatus);
    }

    public CreativeTO(Long id, String name, Long accountId, String accountName, char status, char qaStatus,
            String sizeDefName, Long sizeId, String templateDefName, Long templateId, Long displayStatusId) {
        super(id, name, status, qaStatus, Creative.getDisplayStatus(displayStatusId));
        this.accountName = accountName;
        this.accountId = accountId;        
        this.sizeName = new LocalizableName(sizeDefName, "CreativeSize." + sizeId);
        this.templateName = new LocalizableName(templateDefName, "CreativeTemplate." + templateId);
    }

    public CreativeTO(Long id, String name,
                      Long accountId, String accountName,
            char status, char qaStatus,
            String sizeDefName, Long sizeId, char sizeStatus,
            String templateDefName, Long templateId, char templateStatus,
            Long displayStatusId,char accountStatus, Date version) {
        this(id,name,accountId,accountName,status,qaStatus,sizeDefName,sizeId,templateDefName,templateId,displayStatusId);
        this.accountStatus = accountStatus;
        this.version = version;
        this.templateDisplayStatus = Template.getDisplayStatus(Status.valueOf(templateStatus));
        this.sizeDisplayStatus = CreativeSize.getDisplayStatus(Status.valueOf(sizeStatus));
    }

    public Long getAccountId() {
        return accountId;
    }

    private void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public LocalizableName getSizeName() {
        return sizeName;
    }

    private void setSizeName(LocalizableName sizeName) {
        this.sizeName = sizeName;
    }

    public LocalizableName getTemplateName() {
        return templateName;
    }

    private void setTemplateName(LocalizableName templateName) {
        this.templateName = templateName;
    }

    public String getAccountName() {
        return accountName;
    }

    private void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public boolean isTextCreative() {
        return CreativeSize.isTextName(getSizeName()) && CreativeTemplate.isTextName(getTemplateName());
    }

    public char getAccountStatus() {
        return accountStatus;
    }

    private void setAccountStatus(char accountStatus) {
        this.accountStatus = accountStatus;
    }

    public Date getVersion() {
        return version;
    }

    private void setVersion(Date version) {
        this.version = version;
    }

    public DisplayStatus getTemplateDisplayStatus() {
        return templateDisplayStatus;
    }

    private void setTemplateDisplayStatus(DisplayStatus templateDisplayStatus) {
        this.templateDisplayStatus = templateDisplayStatus;
    }

    public DisplayStatus getSizeDisplayStatus() {
        return sizeDisplayStatus;
    }

    private void setSizeDisplayStatus(DisplayStatus sizeDisplayStatus) {
        this.sizeDisplayStatus = sizeDisplayStatus;
    }
}
