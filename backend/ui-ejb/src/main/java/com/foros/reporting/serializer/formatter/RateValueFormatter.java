package com.foros.reporting.serializer.formatter;

import com.foros.model.campaign.RateType;
import com.foros.reporting.serializer.xlsx.Styles;
import com.foros.util.StringUtil;

import java.math.BigDecimal;

public class RateValueFormatter extends ValueFormatterSupport<BigDecimal> {

    private ValueFormatterSupport foramtter;

    public RateValueFormatter(ValueFormatterSupport currencyFormatter) {
        this.foramtter = currencyFormatter;
    }

    @Override
    public String formatText(BigDecimal value, FormatterContext context) {
        String text = foramtter.formatText(value, context);
        if (StringUtil.isPropertyNotEmpty(text)) {
            text += " " + StringUtil.getLocalizedString("ccg." + RateType.CPC.name().toLowerCase(), context.getLocale());
        }

        return text;
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, BigDecimal value, FormatterContext context) {
        cellAccessor.setString(formatText(value, context));
        cellAccessor.addStyle(Styles.textAlignRight());
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, BigDecimal value, FormatterContext context) {
        cellAccessor.addStyle("number");
        super.formatHtml(cellAccessor, value, context);
    }
}