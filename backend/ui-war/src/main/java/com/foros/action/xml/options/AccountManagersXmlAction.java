package com.foros.action.xml.options;

import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.model.Status;
import com.foros.model.security.User;
import com.foros.security.AccountRole;
import com.foros.session.EntityTO;
import com.foros.session.security.UserService;
import com.foros.util.StringUtil;
import com.foros.util.comparator.IdNameComparator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;

public class AccountManagersXmlAction extends AbstractOptionsAction<EntityTO> {
    @EJB
    private UserService userService;

    private String intAccountId;
    private String roleId;

    private Long noneValue;

    public AccountManagersXmlAction() {
        super(new NamedTOConverter(false));
    }

    @IntRangeFieldValidator(fieldName = "intAccountId", key = "errors.positiveNumber", message = "intAccountId")
    public String getIntAccountId() {
        return intAccountId;
    }

    public void setIntAccountId(String intAccountId) {
        this.intAccountId = intAccountId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public Long getNoneValue() {
        return noneValue;
    }

    public void setNoneValue(Long noneValue) {
        this.noneValue = noneValue;
    }

    public void setIncludeDeleted(boolean includeDeleted) {
        setFilter(new OptionStatusFilter(includeDeleted));
    }

    @Override
    public Collection<? extends EntityTO> getOptions() {
        List<EntityTO> managers;

        managers = userService.getAccountManagers(StringUtil.convertToLong(getIntAccountId()), AccountRole.byName(getRoleId()));
        Collections.sort(managers, new IdNameComparator());

        User currentUser = userService.getMyUser();
        if (!currentUser.getRole().isAccountManager() || managers.isEmpty()) {
            EntityTO noneTO = new EntityTO(noneValue, StringUtil.getLocalizedString("form.select.none"), Status.ACTIVE.getLetter());
            managers.add(0, noneTO);
        }

        return managers;
    }
}
