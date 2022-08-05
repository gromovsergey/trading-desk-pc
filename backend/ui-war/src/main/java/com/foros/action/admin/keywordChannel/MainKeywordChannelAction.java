package com.foros.action.admin.keywordChannel;

import static com.foros.action.admin.channel.ChannelSearchStatus.ALL;
import static com.foros.action.admin.channel.ChannelSearchStatus.DECLINED;
import static com.foros.action.admin.channel.ChannelSearchStatus.LIVE;
import static com.foros.action.admin.channel.ChannelSearchStatus.NOT_LIVE;
import static com.foros.action.admin.channel.ChannelSearchStatus.PENDING_FOROS;

import com.foros.action.SearchForm;
import com.foros.action.admin.channel.ChannelSearchStatus;
import com.foros.action.download.FileDownloadResult;
import com.foros.cache.application.CountryCO;
import com.foros.framework.ReadOnly;
import com.foros.reporting.RowTypes;
import com.foros.reporting.serializer.BulkFormat;
import com.foros.reporting.serializer.CsvSerializer;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.tools.query.strategy.IterationStrategy;
import com.foros.reporting.tools.query.strategy.SimpleIterationStrategy;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.session.account.AccountService;
import com.foros.session.admin.country.CountryService;
import com.foros.session.channel.KeywordChannelCsvTO;
import com.foros.session.channel.KeywordChannelTO;
import com.foros.session.security.AccountTO;
import com.foros.util.CountryHelper;
import com.foros.util.EntityUtils;
import com.foros.util.jpa.DetachedList;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

public class MainKeywordChannelAction extends KeywordChannelActionSupport<SearchForm>
        implements ServletRequestAware, ServletResponseAware {

    private static final int MAX_SEARCH_RESULT_SIZE = 200;
    private static final int MAX_EXPORT_RESULT_SIZE = 65000;

    @EJB
    private AccountService accountSvc;

    @EJB
    private CountryService countrySvc;

    private List<AccountTO> accounts;
    private List<CountryCO> countries;
    private ChannelSearchStatus statuses[] = {ALL, LIVE, NOT_LIVE, DECLINED, PENDING_FOROS};

    private String name;
    private Long accountId;
    private String countryCode;
    private ChannelSearchStatus status;
    private BulkFormat format = BulkFormat.CSV;

    private DetachedList<KeywordChannelTO> result;

    private SearchForm searchForm = new SearchForm();

    private HttpServletRequest request;
    private HttpServletResponse response;

    public void prepare() {
        accounts = accountSvc.search(AccountRole.INTERNAL);
        countries = CountryHelper.sort(countrySvc.getIndex());
        EntityUtils.applyStatusRules(accounts, null, true);
    }

    @ReadOnly
    @Restrict(restriction = "KeywordChannel.view")
    public String main() {
        prepare();
        return INPUT;
    }

    @ReadOnly
    @Restrict(restriction = "KeywordChannel.view")
    public String search() {
        internalSearch();
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "KeywordChannel.view")
    public String download() throws IOException {
        Collection<KeywordChannelCsvTO> channelsForExport;

        try {
            channelsForExport = keywordChannelService.export(MAX_EXPORT_RESULT_SIZE, name, accountId, countryCode,
                    status.getDisplayStatuses());
        } catch (com.foros.session.TooManyRowsException e) {
            channelsForExport = null;
        }

        if (channelsForExport == null || channelsForExport.isEmpty()) {
            addFieldError("search", getText("errors.keywordChannel.wrongListForExport"));
            internalSearch();
            return ERROR;
        }

        FileDownloadResult.setDownloadHeaders(request, response, "KeywordChannels" + format.getFormat().getExtension());
        response.setHeader("Content-type", "text/csv");

        CsvSerializer serializer = new CsvSerializer(response.getOutputStream(), getLocale(), MAX_EXPORT_RESULT_SIZE, format);
        serializer.registry(ValueFormatterRegistries.bulkDefaultAnd(null), RowTypes.data());
        IterationStrategy iterationStrategy = new SimpleIterationStrategy(KeywordChannelFieldCsv.META_DATA);
        iterationStrategy.process(new KeywordChannelRowSource(channelsForExport.iterator()), serializer);

        return null;
    }

    private void internalSearch() {
        result = keywordChannelService.search(getModel().getFirstResultCount(),
                getModel().getPageSize(), name,
                accountId, countryCode, status.getDisplayStatuses());

        getModel().setTotal((long) result.getTotal());
        if (result.size() > MAX_SEARCH_RESULT_SIZE) {
            addActionMessage((getText("error.search.tooManyRows", new String[] {
                    String.valueOf(MAX_SEARCH_RESULT_SIZE), String.valueOf(result.getTotal())})));
        }
        prepare();
    }

    public List<KeywordChannelTO> getResult() {
        return result;
    }

    public boolean isExportEnabled() {
        return result != null && !result.isEmpty() && result.getTotal() <= MAX_EXPORT_RESULT_SIZE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public ChannelSearchStatus getStatus() {
        return status;
    }

    public void setStatus(ChannelSearchStatus status) {
        this.status = status;
    }

    public ChannelSearchStatus[] getStatuses() {
        return statuses;
    }

    public void setStatuses(ChannelSearchStatus statuses[]) {
        this.statuses = statuses;
    }

    public List<AccountTO> getAccounts() {
        return accounts;
    }

    public List<CountryCO> getCountries() {
        return countries;
    }

    public int getMaxExportResultSize() {
        return MAX_EXPORT_RESULT_SIZE;
    }

    public BulkFormat getFormat() {
        return format;
    }

    public void setFormat(BulkFormat format) {
        this.format = format;
    }

    @Override
    public SearchForm getModel() {
        return searchForm;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }
}
