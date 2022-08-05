package com.foros.reporting.serializer.formatter;

import com.foros.reporting.meta.Column;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportParameters;

import org.joda.time.LocalDate;

public class OlapMonthlyUniqueUsersValueFormatter extends NAValueFormatter {

    private OlapAdvertiserReportParameters.UnitOfTime unitOfTime;
    private LocalDate startDate;
    private Column timeUnitColumn;

    public OlapMonthlyUniqueUsersValueFormatter(
            ValueFormatter availableFormatter,
            OlapAdvertiserReportParameters.UnitOfTime unitOfTime,
            LocalDate startDate,
            Column timeUnitColumn) {
        super(availableFormatter);
        this.unitOfTime = unitOfTime;
        this.startDate = startDate;
        this.timeUnitColumn = timeUnitColumn;
    }

    @Override
    protected boolean isNotAvailable(Object value, FormatterContext context) {
        if (OlapAdvertiserReportParameters.UnitOfTime.MONTH.equals(unitOfTime)) {
            LocalDate currentDate = (LocalDate) context.getRow().get(timeUnitColumn);
            if (currentDate.isBefore(startDate)) {
                return true;
            }
        }
        return false;
    }
}
