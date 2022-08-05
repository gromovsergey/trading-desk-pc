package app.programmatic.ui.reporting.view;

import app.programmatic.ui.common.i18n.MessageInterpolator;
import app.programmatic.ui.reporting.model.ReportColumn;
import app.programmatic.ui.reporting.model.Report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReportMeta {
    private static final MessageInterpolator MESSAGE_INTERPOLATOR = MessageInterpolator.getDefaultMessageInterpolator();

    private List<ReportColumnView> columnsInfo;
    private List<ReportColumn> available;
    private List<ReportColumn> defaults;
    private List<ReportColumn> required;

    public ReportMeta(Report report) {
        columnsInfo = initColumnsInfo();
        available = new ArrayList<>(report.getAvailable());
        defaults = new ArrayList<>(report.getDefaults());
        required =  new ArrayList<>(report.getRequired());
    }

    public List<ReportColumnView> getColumnsInfo() {
        return columnsInfo;
    }

    public List<ReportColumn> getAvailable() {
        return available;
    }

    public List<ReportColumn> getDefaults() {
        return defaults;
    }

    public List<ReportColumn> getRequired() {
        return required;
    }

    private List<ReportColumnView> initColumnsInfo() {
        return Arrays.asList(ReportColumn.values()).stream()
                .map(p -> new ReportColumnView(p.toString(), MESSAGE_INTERPOLATOR.interpolate(p.getKey()), p.getLocation()))
                .collect(Collectors.toList());
    }
}
