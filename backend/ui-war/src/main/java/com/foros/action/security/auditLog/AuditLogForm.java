package com.foros.action.security.auditLog;

import com.foros.action.IdNameBean;
import com.foros.action.IdNameForm;
import com.foros.action.SearchForm;
import com.foros.action.site.TagTO;
import com.foros.model.AuditLogRecord;
import com.foros.model.security.ActionType;
import com.foros.model.security.ObjectType;
import com.foros.security.AccountRole;
import com.foros.util.StringUtil;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.EntityNotFoundException;

public class AuditLogForm extends SearchForm {
    private Long id;
    private String type;
    private String accountType;
    private String IP;
    private String accountName;
    private String login;
    private CampaignCreativeGroupForm groupForm;
    private UserForm userForm;
    private IdNameForm channelForm;
    private List<AuditLogRecord> logRecords;
    private TagTO tagTO;
    private String action;
    private IdNameBean countryEntity;
    private Timestamp version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TagTO getTagTO() {
        return tagTO;
    }

    public void setTagTO(TagTO tagTO) {
        this.tagTO = tagTO;
    }

    public List<AuditLogRecord> getLogRecords() {
        return logRecords;
    }

    public void setLogRecords(List<AuditLogRecord> logRecords) {
        this.logRecords = logRecords;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = StringUtil.isPropertyEmpty(type) ? null : type;
    }

    public ObjectType getObjectType() {
        try {
            return ObjectType.valueOf(StringUtil.toInt(type));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new EntityNotFoundException("Object Type with id = " + (type == null ? "null" : type) + " not found");
        }
    }

    public ActionType getActionType() {
        try {
            if (StringUtil.isPropertyEmpty(action)) {
                return null;
            }
            return ActionType.values()[StringUtil.toInt(action)];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new EntityNotFoundException("Action Type with id = " + (action == null ? "null" : action) + " not found");
        }
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String value) {
        accountName = value;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String value) {
        login = value;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String value) {
        IP = value;
    }

    public void setAccountType(String value) {
        accountType = value;
    }

    public AccountRole getAccountRole() {
        AccountRole[] roles = getAccountRoles();
        if (roles != null && roles.length == 1)
            return roles[0];
        
        return null;
    }

    public AccountRole[] getAccountRoles() {
        if (StringUtil.isPropertyEmpty(accountType))
            return null;
        
        String[] types = accountType.split(",");
        AccountRole[] roles = new AccountRole[types.length];
        for (int i = 0; i < types.length; i++) {
            roles[i] = AccountRole.values()[StringUtil.convertToInt(types[i])];
        }
        return roles;
    }
    
    public String getAccountType() {
        return accountType;
    }

    public UserForm getUserForm() {
        return userForm;
    }

    public void setUserForm(UserForm userForm) {
        this.userForm = userForm;
    }

    public CampaignCreativeGroupForm getGroupForm() {
        return groupForm;
    }

    public void setGroupForm(CampaignCreativeGroupForm groupForm) {
        this.groupForm = groupForm;
    }

    public IdNameForm getChannelForm() {
        return channelForm;
    }

    public void setChannelForm(IdNameForm channelForm) {
        this.channelForm = channelForm;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public IdNameBean getCountryEntity() {
        return countryEntity;
    }

    public void setCountryEntity(IdNameBean countryEntity) {
        this.countryEntity = countryEntity;
    }

    public Timestamp getVersion() {
        return version;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }
}
