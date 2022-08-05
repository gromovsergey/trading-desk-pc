package com.foros.web.taglib;

import com.foros.session.ServiceLocator;
import com.foros.session.security.UserService;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.logging.Logger;
import java.util.logging.Level;

public class UserNameTag extends TagSupport {

    private String userId;

    private Boolean escapeHTML;

    private Logger logger;

    public UserNameTag() {
        this.escapeHTML = true;
        this.logger = Logger.getLogger(UserNameTag.class.getName());
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getEscapeHTML() {
        return escapeHTML;
    }

    public void setEscapeHTML(Boolean escapeHTML) {
        this.escapeHTML = escapeHTML;
    }

    /**
     * Method called at start of tag.
     *
     * @return SKIP_BODY
     */
    @Override
    public int doStartTag() throws JspException {
        try {
            Long userId;

            JspWriter out = pageContext.getOut();

            if (this.userId == null || this.userId.length() == 0) {
                logger.log(Level.INFO, "User id was not specified");
                return SKIP_BODY;
            }

            try {
                userId = Long.parseLong(this.userId);
            } catch (NumberFormatException e) {
                JspException je = new JspException("Wrong format in userId attribute", e);
                throw je;
            }

            UserService userService = ServiceLocator.getInstance().lookup(UserService.class);
            String userName = userService.getUserFullName(userId);

            if (escapeHTML) {
                userName = StringEscapeUtils.escapeHtml(userName);
            }

            out.print(userName);
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
