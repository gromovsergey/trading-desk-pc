package com.foros.web.taglib;

import com.foros.model.DisplayStatus;
import com.foros.model.account.Account;
import com.foros.session.ServiceLocator;
import com.foros.session.account.AccountService;
import com.foros.util.StringUtil;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The tag's purpose is to display account display status by id.
 */
public class AccountDisplayStatusTag extends TagSupport {

    private String accountId;

    public AccountDisplayStatusTag() {
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * Method called at start of tag.
     *
     * @return SKIP_BODY
     */
    @Override
    public int doStartTag() throws JspException {
        try {
            Long accountId;
            DisplayStatus displayStatus;
            JspWriter out = pageContext.getOut();

            if (this.accountId == null || this.accountId.length() == 0) {
                JspException je = new JspException("Account id was not specified");

                throw je;
            }

            try {
                accountId = Long.parseLong(this.accountId);
            } catch (NumberFormatException e) {
                JspException je = new JspException("Wrong format in accountId attribute", e);
                throw je;
            }

            AccountService accountService = ServiceLocator.getInstance().lookup(AccountService.class);
            Account account = accountService.find(accountId);
            displayStatus = account.getDisplayStatus();

            String color;
            switch (displayStatus.getMajor()) {
                case LIVE:
                    color = "Green";
                    break;
                case NOT_LIVE:
                    color = "Red";
                    break;
                case INACTIVE:
                    color = "Gray";
                    break;
                case DELETED:
                    color = "Gray";
                    break;
                default:
                    color = "";
            }

            if (account.getTestFlag()) {
                color += "Test";
            }
            
            StringBuffer buf = new StringBuffer("<div class=\"displayStatus ");
            buf.append(color).append("\" title=\"");
            buf.append(StringUtil.getLocalizedString(displayStatus.getDescription()));
            buf.append("\"></div>");

            out.print(buf.toString());
        } catch (JspException je) {
            throw je;
        } catch (Exception e) {
            JspException je = new JspException(e);
            throw je;
        }

        return SKIP_BODY;
    }

    /**
     * Method called at end of tag.
     *
     * @return EVAL_PAGE
     */
    @Override
    public int doEndTag() {
        return EVAL_PAGE;
    }
}