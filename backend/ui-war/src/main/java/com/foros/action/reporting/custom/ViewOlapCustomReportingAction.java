package com.foros.action.reporting.custom;

import com.foros.action.BaseActionSupport;
import com.foros.cache.NamedCO;
import com.foros.cache.application.CountryCO;
import com.foros.framework.ReadOnly;
import com.foros.model.account.Account;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.account.AccountService;
import com.foros.session.creative.CreativeSizeTO;
import com.foros.session.reporting.custom.olap.CustomOlapMeta;
import com.foros.session.security.AccountTO;
import com.foros.util.CountryHelper;
import com.foros.util.helper.IndexHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;

public class ViewOlapCustomReportingAction extends BaseActionSupport {
    @EJB
    CurrentUserService currentUserService;

    @EJB
    AccountService accountService;

    private String defaultCountryCode;

    private Collection<NamedCO<Long>> timeZones;
    private List<CountryCO> countries;
    private List<AccountTO> accounts;
    private List<AccountTO> isps;
    private List<AccountTO> publishers;
    private List<CreativeSizeTO> sizes;

    private List<ColumnAdapter> metricsColumns;
    private List<ColumnAdapter> outputColumns;
    private List<ColumnAdapter> selectedMetricsColumns;

    @ReadOnly
    @Restrict(restriction = "Report.run", parameters = "'custom'")
    public String view() throws Exception {
        timeZones = IndexHelper.getTimezonesList();
        Set<Long> accessAccountIds = currentUserService.getAccessAccountIds();
        if (accessAccountIds == null) {
            countries = CountryHelper.sort(IndexHelper.getCountryList());
        } else {
            Account account = accountService.find(currentUserService.getAccountId());
            defaultCountryCode = account.getCountry().getCountryCode();
            countries = CountryHelper.sort(IndexHelper.getCountryList(accessAccountIds));
        }
        accounts = IndexHelper.getAccountsList(defaultCountryCode, AccountRole.AGENCY);
        isps = IndexHelper.getAccountsList(defaultCountryCode, AccountRole.ISP);
        publishers = IndexHelper.getAccountsList(defaultCountryCode, AccountRole.PUBLISHER);
        sizes = IndexHelper.getSizesList();
        metricsColumns = wrap(CustomOlapMeta.INSTANCE.getMetricsColumns());
        outputColumns = wrap(CustomOlapMeta.INSTANCE.getOutputColumns());
        selectedMetricsColumns = wrap(Arrays.asList(CustomOlapMeta.IMPS, CustomOlapMeta.CLICKS, CustomOlapMeta.CTR));
        return "success";
    }

    public Collection<NamedCO<Long>> getTimeZones() {
        return timeZones;
    }

    public String getDefaultCountryCode() {
        return defaultCountryCode;
    }

    public List<CountryCO> getCountries() {
        return countries;
    }

    public List<AccountTO> getAccounts() {
        return accounts;
    }

    public List<AccountTO> getIsps() {
        return isps;
    }

    public List<AccountTO> getPublishers() {
        return publishers;
    }

    public List<CreativeSizeTO> getSizes() {
        return sizes;
    }

    public List<ColumnAdapter> getMetricsColumns() {
        return metricsColumns;
    }

    public List<ColumnAdapter> getOutputColumns() {
        return outputColumns;
    }

    public List<ColumnAdapter> getSelectedMetricsColumns() {
        return selectedMetricsColumns;
    }

    public List<ColumnAdapter> wrap(List<OlapColumn> list) {
        List<ColumnAdapter> res = new ArrayList<>(list.size());
        for (OlapColumn column : list) {
            res.add(new ColumnAdapter(column));
        }
        return res;
    }

    public class ColumnAdapter {
        private OlapColumn column;

        public ColumnAdapter(OlapColumn column) {
            this.column = column;
        }

        public String getId() {
            return column.getNameKey();
        }

        public String getName() {
            return getText(column.getNameKey());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ColumnAdapter that = (ColumnAdapter) o;

            if (!column.equals(that.column)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return column.hashCode();
        }
    }
}
