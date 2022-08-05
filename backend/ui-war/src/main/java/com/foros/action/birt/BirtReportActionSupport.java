package com.foros.action.birt;

import com.foros.action.BaseActionSupport;
import com.foros.action.IdNameBean;
import com.foros.model.report.birt.BirtReport;
import com.foros.model.security.PolicyEntry;
import com.foros.model.security.UserRole;
import com.foros.security.AccountRole;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.admin.userRole.UserRoleService;
import com.foros.session.birt.BirtReportService;
import com.foros.util.BeanUtils;
import com.foros.util.PairUtil;
import com.foros.util.StringUtil;
import com.foros.util.UITimestamp;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class BirtReportActionSupport extends BaseActionSupport implements ModelDriven<BirtReportForm> {

    @EJB
    protected BirtReportService reportService;

    @EJB
    protected UserRoleService userRoleService;

    protected BirtReportForm entity = new BirtReportForm();
    private List<IdNameBean> userRolePairs = Collections.emptyList();

    public List<IdNameBean> getUserRolePairs() {
        return userRolePairs;
    }

    public void setUserRolePairs(List<IdNameBean> userRolePairs) {
        this.userRolePairs = userRolePairs;
    }

    public BirtReportForm getModel() {
        return entity;
    }

    protected BirtReport populateReport() {
        BirtReport report = reportService.findForUpdate(StringUtil.toLong(getModel().getId()));
        NumberFormat nf = CurrentUserSettingsHolder.getNumberFormat();

        try {
            BeanUtils.copyProperties(getModel(), report, nf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return report;
    }


    protected void populateDependencies() {
        List<UserRole> userRoles = userRoleService.find(AccountRole.INTERNAL);

        for (BirtReportForm.SecurityForm sf : getModel().getPermissions()) {
            if (sf != null) {
                if (sf.hasGlobalOpeation()) {
                    Long roleId = PairUtil.fetchId(sf.getUserRolePair());
                    userRoles.remove(new UserRole(roleId));
                }
            }
        }
        List<IdNameBean> userRolePairs = new ArrayList<IdNameBean>();
        userRolePairs.add(new IdNameBean("", getText("form.select.pleaseSelect")));
        for (UserRole userRole: userRoles) {
            userRolePairs.add(new IdNameBean(userRole.getId() + "_" + userRole.getVersion().getTime() + "/" + userRole.getVersion().getNanos(), userRole.getName()));
        }
        setUserRolePairs(userRolePairs);
    }

    protected BirtReport populateForm() {
        BirtReport birtReport = null;
        BirtReportForm form = getModel();

        if (form.getId() != null) {
            birtReport = populateReport();
        }

        Collection<PolicyEntry> policies = userRoleService.findPolicyEntriesIncludeGlobal("birt_report", form.getId());

        Map<UserRole, Map<String, PolicyEntry>> policyIds = new HashMap<UserRole, Map<String, PolicyEntry>>();

        for (PolicyEntry policy : policies) {
            UserRole role = policy.getUserRole();

            Map<String, PolicyEntry> policiesForRole = policyIds.get(role);

            if (policiesForRole == null) {
                policiesForRole = new HashMap<String, PolicyEntry>();
                policyIds.put(role, policiesForRole);
            }

            policiesForRole.put(policy.getAction(), policy);
        }

        form.setPermissions(new LinkedList<BirtReportForm.SecurityForm>());

        if (!policyIds.isEmpty()) {
            for (Map.Entry<UserRole, Map<String, PolicyEntry>> policiesForRole : policyIds.entrySet()) {
                UserRole role = policiesForRole.getKey();
                Map<String, PolicyEntry> actions = policiesForRole.getValue();

                PolicyEntry runAction = actions.get("run");
                PolicyEntry editAction = actions.get("edit");

                if (runAction == null && editAction == null) {
                    continue;
                }

                BirtReportForm.SecurityForm sf = new BirtReportForm.SecurityForm();
                sf.setUserRolePair(PairUtil.createAsString(role.getId().toString(), role.getVersion().getTime() + "/" + role.getVersion().getNanos()));
                sf.setUserRoleName(role.getName());

                if (runAction != null) {
                    sf.setRunPolicyId(runAction.getId().toString());
                    sf.setRunPolicyVersion(new UITimestamp(runAction.getVersion()));
                    sf.setRun(true);
                    if (runAction.getParameter() == null) {
                        sf.setRunGlobal(true);
                    }
                }

                if (editAction != null) {
                    sf.setEditPolicyId(editAction.getId().toString());
                    sf.setEditPolicyVersion(new UITimestamp(editAction.getVersion()));
                    sf.setEdit(true);
                    if (editAction.getParameter() == null) {
                        sf.setEditGlobal(true);
                    }
                }

                form.getPermissions().add(sf);
            }
        }

        Collections.sort(form.getPermissions(), new BirtReportForm.SecurityFormComparator());

        return birtReport;
    }
}
