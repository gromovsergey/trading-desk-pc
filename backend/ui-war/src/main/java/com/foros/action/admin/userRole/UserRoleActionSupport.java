package com.foros.action.admin.userRole;

import com.foros.action.BaseActionSupport;
import com.foros.model.IdNameEntity;
import com.foros.model.security.UserRole;
import com.foros.security.AccountRole;
import com.foros.session.NamedTO;
import com.foros.session.account.AccountService;
import com.foros.session.admin.userRole.UserRoleService;
import com.foros.util.CollectionUtils;
import com.foros.util.mapper.NamedTOConverter;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class UserRoleActionSupport extends BaseActionSupport implements ModelDriven<UserRole> {

    @EJB
    protected UserRoleService service;
    
    @EJB
    protected AccountService accountService;

    protected UserRole entity = new UserRole();
    protected List<NamedTO> availableAccountIdList;
    protected List<NamedTO> selectedAccountIdList = new ArrayList<NamedTO>();

    @Override
    public UserRole getModel() {
        return entity;
    }

    public UserRole getEntity() {
        return entity;
    }

    public AccountRole[] getAvailableAccountRoles() {
        return AccountRole.values();
    }

    public boolean isAdvertiserAgencyChangeRoleAllowed() {
        if (getModel().getId() != null) {
            return !service.hasManagedAccounts(getModel(), AccountRole.ADVERTISER) && !service.hasManagedAccounts(getModel(), AccountRole.AGENCY) ;
        }
        return true;
    }

    public boolean isPublisherChangeRoleAllowed() {
        if (getModel().getId() != null) {
            return !service.hasManagedAccounts(getModel(), AccountRole.PUBLISHER);
        }
        return true;
    }

    public boolean isIspChangeRoleAllowed() {
        if (getModel().getId() != null) {
            return !service.hasManagedAccounts(getModel(), AccountRole.ISP);
        }
        return true;
    }

    public boolean isCmpChangeRoleAllowed() {
        if (getModel().getId() != null) {
            return !service.hasManagedAccounts(getModel(), AccountRole.CMP);
        }
        return true;
    }

    public List<? extends IdNameEntity> getAvailableAccountIdList(){
        if (availableAccountIdList == null) {
            availableAccountIdList = new ArrayList<NamedTO>();
            availableAccountIdList.addAll(accountService.getInternalAccountsWithoutRestricted(true));
        }
        return availableAccountIdList;
    }
    
    public void setSelectedAccountIdList(List<NamedTO> selectedAccountsList){
        this.selectedAccountIdList.addAll(selectedAccountsList);
    }
    
    public List<? extends IdNameEntity> getSelectedAccountIdList(){
        if (selectedAccountIdList.isEmpty() && !getModel().getAccessAccountIds().isEmpty()) {
            selectedAccountIdList.addAll(CollectionUtils.convert(new NamedTOConverter(), getModel().getAccessAccountIds()));
        }
        return selectedAccountIdList;
    }
}
