package com.foros.web.taglib;

import com.foros.action.IdNameBean;
import com.foros.model.EntityBase;
import com.foros.model.LocalizableName;
import com.foros.model.LocalizableNameEntity;
import com.foros.model.Status;
import com.foros.session.EntityTO;
import com.foros.session.fileman.FileManager;
import com.foros.session.reporting.ReportType;
import com.foros.util.AccountUtil;
import com.foros.util.DSTimeInterval;
import com.foros.util.DateHelper;
import com.foros.util.LocalizableNameEntityHelper;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.ObjectTypeUtil;
import com.foros.util.url.URLValidator;

import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class Functions {

    private static String MOBILE_AGENT_PATTERN = "(?s).*(\\bAndroid\\b|\\bwebOS\\b|\\biPhone\\b|\\biPad\\b|\\biPod\\b|\\bBlackBerry\\b|\\bIEMobile\\b|\\b(Opera Mini)\\b).*";

    public static boolean isValidURL(String url) {
        return URLValidator.isValid(url);
    }

    public static String formatTimeIntervalLong(Long timeInterval) {
        return DateHelper.formatTimeIntervalLong(timeInterval);
    }

    public static String formatDateTimeLong(Long time) {
        if (time == null) {
            return "";
        }
        return DateHelper.formatDateTimeLong(time);
    }

    public static String formatTimeInterval(DSTimeInterval interval) {
        return DateHelper.formatTimeInterval(interval);
    }

    public static String formatTimeString(String time) {
        return DateHelper.formatTimeString(time);
    }

    public static String getTimeFormatPattern() {
        return DateHelper.getTimeFormatPattern();
    }

    public static String getAmpms() {
        return DateHelper.getAmpms();
    }

    public static String getLocalizedValue(LocalizableName ln) {
        return LocalizableNameUtil.getLocalizedValue(ln);
    }

    public static String getLocalizedValueForTip(LocalizableName ln) {
        return LocalizableNameUtil.getLocalizedValue(ln, true);
    }

    public static String getLocalizedValue(LocalizableName ln, Status status) {
        return LocalizableNameUtil.getLocalizedValue(ln, status);
    }

    public static String getLocalizedValue(EntityTO entity) {
        return LocalizableNameUtil.getLocalizedValue(entity);
    }

    public static List<IdNameBean> convertToIdNameBeans(
            Collection<? extends LocalizableNameEntity> localizableCollection) {
        return LocalizableNameEntityHelper.convertToIdNameBeans(localizableCollection);
    }

    public static boolean isActiveLocale(String locale) {
        return LocalizableNameUtil.isActiveLocale(locale);
    }

    public static int getObjectType(EntityBase entity) {
        return ObjectTypeUtil.getObjectType(entity);
    }

    public static String getReportName(Long id) {
        return ReportType.byId(id).getName();
    }

    public static String getFolderName(Long id) {
        return FileManager.Folder.valueOf(id).getKey();
    }

    public static String getAccountParam(String parameterName, Long accountId) {
        return AccountUtil.getAccountParam(parameterName, accountId);
    }

    public static boolean isMobileAgent(HttpServletRequest request){
        String userAgent = request.getHeader("User-Agent");
        return userAgent!=null && userAgent.matches(MOBILE_AGENT_PATTERN);
    }

}
