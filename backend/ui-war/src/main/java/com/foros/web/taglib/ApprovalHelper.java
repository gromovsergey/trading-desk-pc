package com.foros.web.taglib;

import com.foros.model.ApprovableEntity;
import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.model.security.User;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;
import com.foros.util.DateHelper;
import com.foros.util.StringUtil;
import com.foros.util.xml.QADescription;
import com.foros.util.xml.QADescriptionError;

import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApprovalHelper {
    private static Logger logger = Logger.getLogger(ApprovalHelper.class.getName());

    public static ApprovalInfo getApprovalInfo(ApprovableEntity entity) {
        if (entity.getStatus() == Status.DELETED) {
            return null;
        }

        ApprovalInfo ai = null;

        if (SecurityContext.isInternal()) {
            ai = new ApprovalInfo();
            switch (entity.getQaStatus()) {
                case APPROVED:
                case DECLINED:
                    TimeZone timeZone = CurrentUserSettingsHolder.getTimeZone();
                    Locale locale = CurrentUserSettingsHolder.getLocale();
                    ai.setStatusMessage(getApprovalStatusMessage(entity, DateFormat.SHORT, timeZone, locale));
                    ai.setReason(fromXml(entity));
                    break;
                default:
                    ai.setStatusMessage(getStatusMessage(entity.getQaStatus()));
                    break;
            }
        } else {
            if(entity.getQaStatus() == ApproveStatus.DECLINED) {
                ai = new ApprovalInfo();
                ai.setReason(fromXml(entity));
            }
        }

        return  ai;
    }

    private static QADescription fromXml(ApprovableEntity entity) {
        try {
            return entity.getQaDescriptionObject();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Can't parse QADescription from: " + entity.getQaDescription(), e);
            return QADescriptionError.INSTANCE;
        }
    }

    private static String getApprovalStatusMessage(ApprovableEntity entity, int dateStyle, TimeZone timeZone, Locale locale) {
        String status = getStatusMessage(entity.getQaStatus());
        if (entity.getQaUser() == null && entity.getQaDate() == null) {
            return status;
        }

        String name = getName(entity.getQaUser());
        String date = DateHelper.formatDate(entity.getQaDate(), dateStyle, timeZone, locale);

        return StringUtil.getLocalizedString("approval.description", new String[]{status, name, date});
    }


    private static String getName(User qaUser) {
        return qaUser != null ? qaUser.getFirstName() + " " + qaUser.getLastName() : "Unknown";
    }

    private static String getStatusMessage(ApproveStatus approveStatus) {
        return StringUtil.getLocalizedString("approval.description.status." + approveStatus.getLetter());
    }

    public static class ApprovalInfo {
        private String statusMessage;
        private QADescription reason;

        public String getStatusMessage() {
            return statusMessage;
        }

        public void setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
        }

        public QADescription getReason() {
            return reason;
        }

        public void setReason(QADescription reason) {
            this.reason = reason;
        }
    }
}
