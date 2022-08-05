package app.programmatic.ui.reporting.tools;

import app.programmatic.ui.reporting.model.ReportRows;

import java.io.ByteArrayOutputStream;

public interface ReportFormatter {
    ByteArrayOutputStream formatRows(ReportRows reportRows, ReportColumnFormatter formatter);
}
