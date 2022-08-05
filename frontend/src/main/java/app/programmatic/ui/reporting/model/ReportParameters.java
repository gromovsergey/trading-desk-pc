package app.programmatic.ui.reporting.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import app.programmatic.ui.common.tool.serialization.JsonDateTimeDeserializer;
import app.programmatic.ui.common.tool.serialization.JsonDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.stream.Collectors;

public abstract class ReportParameters {
    public abstract Report getReport();

    private LocalDateTime dateStart;
    private LocalDateTime dateEnd;
    private EnumSet<ReportColumn> selectedColumns;
    private Long accountId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public LocalDateTime getDateStart() {
        return dateStart;
    }

    @JsonDeserialize(using = JsonDateTimeDeserializer.class)
    public void setDateStart(LocalDateTime dateStart) {
        this.dateStart = dateStart;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public LocalDateTime getDateEnd() {
        return dateEnd;
    }

    @JsonDeserialize(using = JsonDateTimeDeserializer.class)
    public void setDateEnd(LocalDateTime dateEnd) {
        this.dateEnd = dateEnd;
    }

    public EnumSet<ReportColumn> getSelectedColumns() {
        return selectedColumns;
    }

    public void setSelectedColumns(EnumSet<ReportColumn> selectedColumns) {
        this.selectedColumns = selectedColumns;
    }

    @Override
    public String toString() {

        String collect = selectedColumns.stream().map(e -> "'" + e.getColumnName() + "'").collect(Collectors.joining(","));
        return "ReportParameters: accountId = " + accountId + "\n" +
                "dateStart = " + dateStart + "; " + "dateEnd = " + dateEnd + "\n" +
                "selectedColumns = " + collect;
    }
}
