package com.foros.test;

import com.foros.AbstractRestrictionsBeanTest.PermissionsSet;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountTypeCCGType;
import com.foros.model.security.InternalAccessType;
import com.foros.model.security.PolicyEntry;
import com.foros.model.security.User;
import com.foros.model.template.CreativeTemplate;
import com.foros.security.AccountRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserDefinition {
    private AccountRole accountRole;
    private User user;
    private PermissionsSet permissionsSet;
    private boolean ispManager = false;
    private boolean advertiserManager = false;
    private boolean cmpManager = false;
    private boolean publisherManager = false;
    private UserDefinition managedBy;
    private Collection<PolicyEntry> customPermissions = new ArrayList<PolicyEntry>();
    private Collection<PolicyEntry> removedPermissions = new ArrayList<PolicyEntry>();
    private boolean persistent = false;
    private Collection<AccountTypeCCGType> ccgTypes = new HashSet<AccountTypeCCGType>();
    private CreativeTemplate creativeTemplate;
    private CreativeSize creativeSize;
    private InternalAccessType accessType;
    private Set<Long> accessAccountIds = new HashSet<Long>();
    
    private UserDefinitionFactory udf;
    
    public UserDefinition(UserDefinitionFactory factory, boolean persistent) {
        this.persistent = persistent;
        this.udf = factory;
    }
    
    public AccountRole getAccountRole() {
        return accountRole;
    }
    
    public void setAccountRole(AccountRole accountRole) {
        this.accountRole = accountRole;
    }
    
    public PermissionsSet getPermissionsSet() {
        return permissionsSet;
    }
    
    public void setPermissionsSet(PermissionsSet permissionsSet) {
        this.permissionsSet = permissionsSet;
    }
    
    public Collection<PolicyEntry> getCustomPermissons() {
        return Collections.unmodifiableCollection(customPermissions);
    }
    
    public UserDefinition addCustomPermission(PolicyEntry descriptor) {
        customPermissions.add(descriptor);
        return this;
    }

    public Collection<PolicyEntry> getRemovedPermissions() {
        return Collections.unmodifiableCollection(removedPermissions);
    }
    
    public UserDefinition removePermission(PolicyEntry descriptor) {
        removedPermissions.add(descriptor);
        return this;
    }
    
    public boolean isCreated() {
        return user != null;
    }
    
    public boolean isPersistent() {
        return persistent;
    }

    public CreativeTemplate getCreativeTemplate() {
        return creativeTemplate;
    }
    
    public void setCreativeTemplate(CreativeTemplate creativeTemplate) {
        this.creativeTemplate = creativeTemplate;
    }
    
    public CreativeSize getCreativeSize() {
        return creativeSize;
    }
    
    public void setCreativeSize(CreativeSize creativeSize) {
        this.creativeSize = creativeSize;
    }
    
    public void addCcgType(CCGType ccgType, TGTType tgtType, RateType rateType) {
        AccountTypeCCGType res = new AccountTypeCCGType();
        res.setCcgType(ccgType);
        res.setTgtType(tgtType);
        res.setRateType(rateType);
        ccgTypes.add(res);
    }

    public Collection<AccountTypeCCGType> getCcgTypes() {
        return Collections.unmodifiableCollection(ccgTypes);
    }
    
    public User getUser() {
        user = udf.createOrLoadUser(this, user);
        return user;
    }

    public UserDefinition ispManager() {
        ispManager = true;
        return this;
    }

    public UserDefinition cmpManager() {
        cmpManager = true;
        return this;
    }
    
    public UserDefinition advertiserManager() {
        advertiserManager = true;
        return this;
    }

    public UserDefinition publisherManager() {
        publisherManager = true;
        return this;
    }

    public UserDefinition managedBy(UserDefinition ud) {
        managedBy = ud;
        return this;
    }
    
    public UserDefinition getManagerBy() {
        return managedBy;
    }

    public boolean isIspManager() {
        return ispManager;
    }

    public boolean isCmpManager() {
        return cmpManager;
    }

    public boolean isAdvertiserManager() {
        return advertiserManager;
    }

    public boolean isPublisherManager() {
        return publisherManager;
    }

    public InternalAccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(InternalAccessType accessType) {
        this.accessType = accessType;
    }

    public Set<Long> getAccessAccountIds() {
        return accessAccountIds;
    }

    public void setAccessAccountIds(Set<Long> accessAccountIds) {
        this.accessAccountIds = accessAccountIds;
    }
}
