package com.foros.action.reporting.publisher;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.model.account.PublisherAccount;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.session.CurrentUserService;
import com.foros.session.account.AccountService;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.session.reporting.publisher.DetailLevel;
import com.foros.session.reporting.publisher.PublisherMeta;
import com.foros.session.reporting.publisher.PublisherReportService;
import com.foros.util.NameValuePair;
import com.foros.util.messages.MessageProvider;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ColumnsPublisherReportAction extends AbstractXmlAction<Map<String, Collection<NameValuePair<String, String>>>> {

    @EJB
    PublisherReportService publisherReportService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AccountService accountService;

    private DateRange dateRange = new DateRange();
    private Long accountId;
    private DetailLevel detailLevel;
    private boolean isWalledGarden;

    public Map<String, Collection<NameValuePair<String, String>>> generateModel() throws ProcessException {
        Map<String, Collection<NameValuePair<String, String>>> result = new HashMap<String, Collection<NameValuePair<String, String>>>();

        boolean isCreditImpsAvailable = false;
        if (currentUserService.isInternal()) {
            if (dateRange.getBegin() != null && dateRange.getEnd() != null && accountId != null) {
                isCreditImpsAvailable = publisherReportService.checkCreditedImps(dateRange, accountId);
            }
        }

        boolean hideClicksData = false;
        if (currentUserService.isExternal()) {
            PublisherAccount publisher = accountService.findPublisherAccount(currentUserService.getAccountId());
            hideClicksData = !publisher.getAccountType().isClicksDataVisibleToExternal();
        }

        // selected columns
        Set<OlapColumn> selectedColumns = new HashSet<OlapColumn>(PublisherMeta.COLUMNS_BY_LEVEL.get(detailLevel));
        if (!isWalledGarden) {
            selectedColumns.addAll(PublisherMeta.MANDATORY_NON_WG_COLUMNS);
        } else {
            selectedColumns.addAll(PublisherMeta.MANDATORY_WG_COLUMNS);
        }
        if (isCreditImpsAvailable) {
            selectedColumns.add(PublisherMeta.CREDITED_IMPRESSIONS);
        }
        if (hideClicksData) {
            selectedColumns.removeAll(PublisherMeta.CLICKS_DATA);
        }
        result.put("selectedColumns", localizeColumns(selectedColumns));

        // available columns
        Set<OlapColumn> availableColumns;
        if (!isWalledGarden) {
            availableColumns = new HashSet<OlapColumn>(PublisherMeta.ALL_NON_WG.getColumns());
        } else {
            availableColumns = new HashSet<OlapColumn>(PublisherMeta.ALL_WG.getColumns());
        }
        if (!isCreditImpsAvailable) {
            availableColumns.remove(PublisherMeta.CREDITED_IMPRESSIONS);
        }
        if (hideClicksData) {
            availableColumns.removeAll(PublisherMeta.CLICKS_DATA);
        }
        availableColumns.removeAll(selectedColumns);
        result.put("availableColumns", localizeColumns(availableColumns));

        return result;
    }

    private List<NameValuePair<String, String>> localizeColumns(Set<OlapColumn> columns) {
        MessageProvider messageProvider = MessageProvider.createMessageProviderAdapter();
        List<NameValuePair<String, String>> result = new ArrayList<NameValuePair<String, String>>();
        for (OlapColumn column : columns) {
            String columnName = messageProvider.getMessage(column.getNameKey());
            result.add(new NameValuePair<String, String>(column.getNameKey(), columnName));
        }
        return result;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setDetailLevel(DetailLevel detailLevel) {
        this.detailLevel = detailLevel;
    }

    public void setIsWalledGarden(boolean isWalledGarden) {
        this.isWalledGarden = isWalledGarden;
    }
}
