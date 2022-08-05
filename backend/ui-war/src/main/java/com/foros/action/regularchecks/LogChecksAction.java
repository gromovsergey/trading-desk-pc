package com.foros.action.regularchecks;

import com.foros.action.BaseActionSupport;
import com.foros.model.Identifiable;
import com.foros.model.RegularCheckable;
import com.foros.model.account.Account;
import com.foros.model.security.AccountType;
import com.foros.model.security.OwnedEntity;
import com.foros.model.time.TimeSpan;
import com.foros.session.account.AccountService;
import com.foros.session.security.UserService;

import com.opensymphony.xwork2.ModelDriven;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.ejb.EJB;

public abstract class LogChecksAction <T extends RegularCheckable & Identifiable & OwnedEntity> extends BaseActionSupport implements ModelDriven<T> {

    private String entityName;
    private Long entityId;
    private boolean confirmation;
    private SortedMap<Integer, String> availableIntervals;
    private Integer existingInterval;

    @EJB
    private UserService userService;

    @EJB
    private AccountService accountService;


    public String getSuccessLocation() {
        return "view.action?id=" + getModel().getId();
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getUserName() {
        return userService.getMyUser().getFullName();
    }

    public SortedMap<Integer, String> getAvailableIntervals() {
        if (availableIntervals == null) {
            Account account = accountService.find(getModel().getAccount().getId());
            availableIntervals = getAvailableIntervals(account.getAccountType(), existingInterval);
        }
        return availableIntervals;
    }

    public Integer getExistingInterval() {
        if (existingInterval == null) {
            existingInterval = getModel().getInterval();
        }
        return existingInterval;
    }

    public void setExistingInterval(Integer existingInterval) {
        this.existingInterval = existingInterval;
    }

    protected abstract SortedMap<Integer, String> getAvailableIntervals(AccountType at, Integer lastCheckInterval);

    protected SortedMap<Integer, String> getAvailableIntervals(TimeSpan t1, TimeSpan t2, TimeSpan t3, Integer lastCheckInterval) {
        SortedMap<Integer, String> res = new TreeMap<Integer, String>();
        res.put(1, getTimeSpanText(t1));
        res.put(2, getTimeSpanText(t2));
        if (lastCheckInterval != null && lastCheckInterval >= 2) {
            res.put(3, getTimeSpanText(t3));
        }
        return res;
    }

    private String getTimeSpanText(TimeSpan t) {
        long hours = t.getValueInSeconds() / 3600;
        if (hours % 24 == 0) {
            return (hours / 24) + " " + getText("checks.days");
        }
        return hours + " " + getText("checks.hours");
    }

    public boolean isConfirmation() {
        return confirmation;
    }

    public void setConfirmation(boolean confirmation) {
        this.confirmation = confirmation;
    }
}