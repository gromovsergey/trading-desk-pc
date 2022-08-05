package com.foros.action.admin;

import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;
import com.foros.action.BaseActionSupport;
import com.foros.action.SearchForm;
import com.foros.util.helper.IndexHelper;
import com.foros.cache.application.CountryCO;
import com.foros.framework.ReadOnly;
import com.foros.session.account.AccountService;
import com.foros.session.query.PartialList;
import com.foros.session.regularchecks.RegularReviewService;
import com.foros.session.regularchecks.ReviewEntityTO;
import com.foros.util.CountryHelper;
import com.foros.util.RegularChecksUtil;

public class RegularReviewDashboardAction extends BaseActionSupport implements ModelDriven<SearchForm> {

    @EJB
    private RegularReviewService regularReviewService;

    @EJB
    protected AccountService accountService;

    private PartialList<ReviewEntityTO> reviewEntities;

    private String countryCode = null;

    private String entityType = "channels";

    private SearchForm searchParams = new SearchForm();

    private List<CountryCO> countries;

    @ReadOnly
    public String main() throws Exception {
        if (countryCode == null) {
            countryCode = accountService.getMyAccount().getCountry().getCountryCode();
        }
        countries = CountryHelper.sort(IndexHelper.getCountryList());
        return SUCCESS;
    }

    @ReadOnly
    public String search() throws Exception {
        if ("campaigns".equals(getEntityType())) {
            reviewEntities = regularReviewService.searchCCGsForReview(countryCode, searchParams.getFirstResultCount(), searchParams.getPageSize());
        } else {
            reviewEntities = regularReviewService.searchChannelsForReview(countryCode, searchParams.getFirstResultCount(), searchParams.getPageSize());
        }
        for (ReviewEntityTO to: reviewEntities) {
            to.setDueCaption(RegularChecksUtil.getDueCaption(to.getHoursAgo(), to.isHourlyCheck()));
        }
        searchParams.setTotal((long)reviewEntities.getTotal());
        return SUCCESS;
    }

    public List<CountryCO> getCountries() {
        return countries;
    }

    public SearchForm getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(SearchForm searchParams) {
        this.searchParams = searchParams;
    }

    @Override
    public SearchForm getModel() {
        return searchParams;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<ReviewEntityTO> getReviewEntities() {
        return reviewEntities;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
}
