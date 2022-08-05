package com.foros.session.reporting;

import com.foros.model.EntityBase;
import com.foros.model.security.User;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.ServiceLocator;
import com.foros.session.UtilityService;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.util.CollectionUtils;
import com.foros.util.DateHelper;
import com.foros.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

public class PreparedParameterBuilder {

    private UtilityService utilityService = null;

    private List<PreparedParameter> parameters = new ArrayList<PreparedParameter>();

    private Locale locale;

    // To output data to the parameters section
    private PreparedParameterBuilder(Locale locale) {
        this.locale = locale;
        this.utilityService = ServiceLocator.getInstance().lookup(UtilityService.class);
        addGenerated();
    }

    // To audit
    private PreparedParameterBuilder() {
        this.locale = Locale.getDefault();
    }

    public PreparedParameterBuilder addDateRange(DateRange dateRange, String timezoneStr) {
        if (!dateRange.isNullRange()) {
            String begin = DateHelper.formatLocalDate(dateRange.getBegin(), locale);
            String end = DateHelper.formatLocalDate(dateRange.getEnd(), locale);
            String name = (StringUtils.isEmpty(timezoneStr)) ? StringUtil.getLocalizedString("report.dateRange") : StringUtil.getLocalizedString("report.dateRangeWithTimezone", timezoneStr);
            String rangeValue = begin + " - " + end;
            parameters.add(new PreparedParameter("dateRange", name, rangeValue, rangeValue));
        }
        return this;
    }

    public PreparedParameterBuilder addDateRange(DateRange dateRange, TimeZone timeZone) {
        String timezoneStr = StringUtil.resolveGlobal("timezone", timeZone.getID(), true, locale);
        return addDateRange(dateRange, timezoneStr);
    }

    public PreparedParameterBuilder addDate(LocalDate date, TimeZone timeZone) {
        String valueText = DateHelper.formatLocalDate(date, locale);
        String timezoneStr = StringUtil.resolveGlobal("timezone", timeZone.getID(), true, locale);
        String name = StringUtil.getLocalizedString("report.dateWithTimezone", timezoneStr);
        parameters.add(new PreparedParameter("date", name, valueText, date));
        return this;
    }

    public PreparedParameterBuilder addId(String id, Class<? extends EntityBase> entityClass, Long entityId) {
        if (entityId == null) {
            return this;
        }

        String valueText = null;
        if (DatedReportParameters.NONE_ID.equals(entityId)) {
            valueText = StringUtil.getLocalizedString("form.select.none", locale);
        } else if (fetchData()) {
            valueText = utilityService.getEntityText(entityClass, entityId);
        }

        parameters.add(new PreparedParameter(id, getName(id), valueText, entityId));
        return this;
    }

    public PreparedParameterBuilder addIds(String id, Class<? extends EntityBase> entityClass, Collection<Long> entityIds) {
        return addIds(id, entityClass, entityIds, true);
    }

    public PreparedParameterBuilder addIds(String id, Class<? extends EntityBase> entityClass, Collection<Long> entityIds, boolean fetchNames) {
        if (entityIds != null) {
            String valueText = null;

            if (fetchData() && fetchNames) {
                List<String> valueTextList = utilityService.getEntityTextList(entityClass, entityIds);
                valueText = valueTextList.isEmpty() ? null : CollectionUtils.toString(valueTextList);
            }

            parameters.add(new PreparedParameter(id, getName(id), valueText, entityIds));
        }
        return this;
    }

    public List<PreparedParameter> parameters() {
        return parameters;
    }

    private PreparedParameterBuilder addGenerated() {
        String name = StringUtil.getLocalizedString("report.generated", locale);
        ApplicationPrincipal principal = SecurityContext.getPrincipal();
        Generated generated = new Generated(principal, new Date());
        TimeZone timeZone = CurrentUserSettingsHolder.getTimeZone();
        String dateTime = DateHelper.formatDateTime(generated.getGenerationTime(), timeZone, locale);
        User user = utilityService.findById(User.class, principal.getUserId());
        String text = StringUtil.getLocalizedString("report.generatedBy", locale, dateTime, user.getFullName() + " (" + user.getEmail() + ")");
        parameters.add(new PreparedParameter("report.generated", name, text, generated));
        return this;
    }

    public PreparedParameterBuilder addCountry(String countryCode) {
        return addCountry("country", countryCode);
    }

    public PreparedParameterBuilder addCountry(String id, String countryCode) {
        if (StringUtil.isPropertyNotEmpty(countryCode)) {
            String text = StringUtil.resolveGlobal("country", countryCode, false, locale);
            parameters.add(new PreparedParameter(id, getName(id), text, countryCode));
        }
        return this;
    }

    public PreparedParameterBuilder add(String id, Object object) {
        if (object != null) {
            String valueText = object.toString();
            if (StringUtil.isPropertyEmpty(valueText)) {
                valueText = null;
            }
            parameters.add(new PreparedParameter(id, getName(id), valueText, object));
        }
        return this;
    }

    public PreparedParameterBuilder add(String id, String text, Object value) {
        if (value != null) {
            parameters.add(new PreparedParameter(id, getName(id), text, value));
        }
        return this;
    }

    public Locale getLocale() {
        return locale;
    }

    private boolean fetchData() {
        return utilityService != null;
    }

    private String getName(String id) {
        return StringUtil.getLocalizedString("report.input.field." + id);
    }

    public PreparedParameterBuilder addYesNo(String id, boolean value) {
        String text = value ? StringUtil.getLocalizedString("yes", locale) : StringUtil.getLocalizedString("no", locale);
        parameters.add(new PreparedParameter(id, getName(id), text, value));
        return this;
    }

    public static abstract class Factory {

        public final PreparedParameterBuilder builder() {
            return builder(CurrentUserSettingsHolder.getLocale());
        }

        public final PreparedParameterBuilder builder(Locale locale) {
            PreparedParameterBuilder builder = new PreparedParameterBuilder(locale);
            fillParameters(builder);
            return builder;
        }

        public final PreparedParameterBuilder auditOnlyBuilder() {
            PreparedParameterBuilder builder = new PreparedParameterBuilder();
            fillParameters(builder);
            return builder;
        }

        protected abstract void fillParameters(PreparedParameterBuilder builder);
    }

    public static class Generated {
        private ApplicationPrincipal principal;
        private Date generationTime;

        public Generated(ApplicationPrincipal principal, Date generationTime) {
            this.principal = principal;
            this.generationTime = generationTime;
        }

        public ApplicationPrincipal getPrincipal() {
            return principal;
        }

        public void setPrincipal(ApplicationPrincipal principal) {
            this.principal = principal;
        }

        public Date getGenerationTime() {
            return generationTime;
        }

        public void setGenerationTime(Date generationTime) {
            this.generationTime = generationTime;
        }
    }
}
