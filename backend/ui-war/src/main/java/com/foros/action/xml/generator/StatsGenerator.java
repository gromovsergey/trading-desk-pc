package com.foros.action.xml.generator;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.campaign.ChartStats;
import com.foros.util.DateHelper;
import com.foros.util.StringUtil;
import com.foros.web.taglib.NumberFormatter;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class StatsGenerator implements Generator<ChartStats> {

    private static final String CHART_HEADER = "<chart>";
    private static final String CHART_FOOTER = "</chart>";

    private static final String X_HEADER = "<categories>";
    private static final String Y1_HEADER = "<dataset renderAs=\"line\" color=\"3300cc\" lineThickness=\"2\" seriesName=\"{0}\">";
    private static final String Y2_HEADER = "<dataset renderAs=\"line\" color=\"ff3300\" lineThickness=\"2\" seriesName=\"{0}\" parentYAxis=\"S\">";
    private static final String X_FOOTER = "</categories>";
    private static final String Y_FOOTER = "</dataset>";

    private static final String X_ENTRY = "<category label=\"{0}\"/>";
    private static final String Y_ENTRY = "<set value=\"{0}\" tooltext=\"{1}\"/>";

    @Override
    public String generate(ChartStats model) {
        String y1Name = getLocalizedAxisName(model.getY1Type());
        String y2Name = getLocalizedAxisName(model.getY2Type());

        StringBuilder xml = new StringBuilder(Constants.XML_HEADER);
        List<ChartStats.ChartEntry> rows = model.getRows();

        xml.append(CHART_HEADER);

        StringBuilder xEntries = new StringBuilder(X_HEADER);
        StringBuilder y1Entries = new StringBuilder(MessageFormat.format(Y1_HEADER, y1Name));
        StringBuilder y2Entries = new StringBuilder(MessageFormat.format(Y2_HEADER, y2Name));

        TimeZone timeZone = model.getTimeZone();
        Locale locale = CurrentUserSettingsHolder.getLocale();

        int i = 0;
        for (ChartStats.ChartEntry row : rows) {
            String date = DateHelper.formatDate(row.getDate(), DateFormat.SHORT, timeZone, locale);

            xEntries.append(MessageFormat.format(X_ENTRY, Long.toString(row.getDate().getTime())));

            String y1 = formatValue(row.getY1(), model.getY1Type(), model.getCurrencyCode());
            String y2 = formatValue(row.getY2(), model.getY2Type(), model.getCurrencyCode());

            String toolText = StringUtil.getLocalizedString("chart.date") + ": " + date + "{br}" + y1Name + ": " + y1 +
                    "{br}" + y2Name + ": " + y2;
            y1Entries.append(MessageFormat.format(Y_ENTRY, row.getY1().toString(), toolText));
            y2Entries.append(MessageFormat.format(Y_ENTRY, row.getY2().toString(), toolText));
            i++;
        }

        xEntries.append(X_FOOTER);
        y1Entries.append(Y_FOOTER);
        y2Entries.append(Y_FOOTER);

        xml.append(xEntries).append(y1Entries).append(y2Entries);

        xml.append(CHART_FOOTER);

        return xml.toString();
    }

    private String getLocalizedAxisName(String dataType) {
        if (dataType.equalsIgnoreCase("imps")) {
            return StringUtil.getLocalizedString("chart.impressions.daily");
        } else if (dataType.equalsIgnoreCase("imps_rt")) {
            return StringUtil.getLocalizedString("chart.impressions.runningTotal");
        } else if (dataType.equalsIgnoreCase("clicks")) {
            return StringUtil.getLocalizedString("chart.clicks.daily");
        } else if (dataType.equalsIgnoreCase("clicks_rt")) {
            return StringUtil.getLocalizedString("chart.clicks.runningTotal");
        } else if (dataType.equalsIgnoreCase("ctr")) {
            return StringUtil.getLocalizedString("chart.CTR.daily");
        } else if (dataType.equalsIgnoreCase("ctr_rt")) {
            return StringUtil.getLocalizedString("chart.CTR.runningTotal");
        } else if (dataType.equalsIgnoreCase("uniq")) {
            return StringUtil.getLocalizedString("chart.uniqueUsers.daily");
        } else if (dataType.equalsIgnoreCase("uniq_rt")) {
            return StringUtil.getLocalizedString("chart.uniqueUsers.runningTotal");
        } else if (dataType.equalsIgnoreCase("inv_cost")) {
            return StringUtil.getLocalizedString("chart.inv_cost.daily");
        } else if (dataType.equalsIgnoreCase("inv_cost_rt")) {
            return StringUtil.getLocalizedString("chart.inv_cost.runningTotal");
        } else if (dataType.equalsIgnoreCase("tgt_cost")) {
            return StringUtil.getLocalizedString("chart.tgt_cost.daily");
        } else if (dataType.equalsIgnoreCase("tgt_cost_rt")) {
            return StringUtil.getLocalizedString("chart.tgt_cost.runningTotal");
        } else if (dataType.equalsIgnoreCase("total_cost")) {
            return StringUtil.getLocalizedString("chart.total_cost.daily");
        } else if (dataType.equalsIgnoreCase("total_cost_rt")) {
            return StringUtil.getLocalizedString("chart.total_cost.runningTotal");
        } else {
            throw new IllegalArgumentException("Data type " + dataType + " is invalid");
        }
    }

    private String formatValue(Number value, String dataType, String currencyCode) {
        if (dataType.contains("cost")) {
            return NumberFormatter.formatCurrency(value, currencyCode);
        } else if (dataType.startsWith("ctr")) {
            return NumberFormatter.formatNumber(value, 2) + "%";
        }

        return value.toString();
    }
}
