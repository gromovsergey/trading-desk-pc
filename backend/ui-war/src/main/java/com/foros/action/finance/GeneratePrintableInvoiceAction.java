package com.foros.action.finance;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.finance.Invoice;
import com.foros.model.report.birt.BirtReport;
import com.foros.session.finance.AdvertisingFinanceService;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.util.UriComponentsBuilder;

public class GeneratePrintableInvoiceAction extends BaseActionSupport implements ServletRequestAware, ServletResponseAware {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd");

    @EJB
    private AdvertisingFinanceService financeService;

    private Long id;

    private HttpServletRequest request;
    private HttpServletResponse response;

    @ReadOnly
    public String generate() throws Exception {
        Invoice invoice = financeService.viewInvoice(id);
        if (!invoice.isPrintable()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

            return SUCCESS;
        }

        BirtReport report = invoice.getAccount().getCountry().getInvoiceReport();

        String url = UriComponentsBuilder.newInstance()
                .pathSegment("birt", "reports", "runandrender", "{id}/")
                .queryParam("invoiceId", id)
                .queryParam("__format", "pdf")
                .queryParam("__extractfilename", formatInvoiceFileName(invoice))
                .queryParam("__extractextension", "pdf")
                .buildAndExpand(report.getId())
                .toUriString();

        response.sendRedirect(url);

        return SUCCESS;
    }

    private String formatInvoiceFileName(Invoice invoice) {
        return "invoice_" + invoice.getId() + "_" + formatDate(invoice.getInvoiceDate());
    }

    private String formatDate(Date date) {
        return FORMAT.format(date);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
}
