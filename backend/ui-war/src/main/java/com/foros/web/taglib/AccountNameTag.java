package com.foros.web.taglib;

import com.foros.session.ServiceLocator;
import com.foros.session.account.AccountService;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The tag's purpose is to display account name by id.
 */
public class AccountNameTag extends TagSupport {

    private String accountId;

    private Boolean escapeHTML;
    
    private Logger logger;

    private boolean appendStatusSuffix;

    public AccountNameTag() {
        this.escapeHTML = true;
        this.logger = Logger.getLogger(AccountNameTag.class.getName());
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Boolean getEscapeHTML() {
        return escapeHTML;
    }

    public void setEscapeHTML(Boolean escapeHTML) {
        this.escapeHTML = escapeHTML;
    }

    public boolean getAppendStatusSuffix() {
        return appendStatusSuffix;
    }

    public void setAppendStatusSuffix(boolean appendStatusSuffix) {
        this.appendStatusSuffix = appendStatusSuffix;
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
            String accountName;
            JspWriter out = pageContext.getOut();

            if (this.accountId == null || this.accountId.length() == 0) {
                logger.log(Level.INFO, "Account id was not specified");
                return SKIP_BODY;
            }

            try {
                accountId = Long.parseLong(this.accountId);
            } catch (NumberFormatException e) {
                JspException je = new JspException("Wrong format in accountId attribute", e);
                throw je;
            }

            AccountService accountService = ServiceLocator.getInstance().lookup(AccountService.class);
            accountName = accountService.getAccountName(accountId, appendStatusSuffix);
            if (escapeHTML) {
                accountName = StringEscapeUtils.escapeHtml(accountName);
            }

            out.print(accountName);
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
