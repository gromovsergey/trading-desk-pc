package com.foros.action.admin.country.placementsBlacklist;

import com.opensymphony.xwork2.util.CreateIfNull;
import com.foros.action.BaseActionSupport;
import com.foros.action.bulk.BulkMetaData;
import com.foros.action.bulk.CsvRow;
import com.foros.action.download.FileDownloadResult;
import com.foros.framework.ReadOnly;
import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;
import com.foros.reporting.RowTypes;
import com.foros.reporting.rowsource.RowSource;
import com.foros.reporting.serializer.BulkFormat;
import com.foros.reporting.serializer.CsvSerializer;
import com.foros.reporting.serializer.ResultSerializerSupport;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.xlsx.ExcelStyles;
import com.foros.reporting.serializer.xlsx.ExcelStylesRegistry;
import com.foros.reporting.serializer.xlsx.XlsxSerializer;
import com.foros.reporting.tools.query.strategy.IterationStrategy;
import com.foros.reporting.tools.query.strategy.SimpleIterationStrategy;
import com.foros.session.channel.ValidationResultTO;
import com.foros.session.channel.service.PlacementsBlacklistService;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DownloadPlacementsBlacklistAction extends BaseActionSupport implements ServletResponseAware, ServletRequestAware {
    protected BulkFormat format = BulkFormat.CSV;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @CreateIfNull
    private ValidationResultTO validationResult;

    @EJB
    protected PlacementsBlacklistService placementsBlacklistService;

    @EJB
    private ExcelStylesRegistry excelStylesRegistry;

    @ReadOnly
    public String export() throws IOException {
        Collection<PlacementBlacklist> placements = placementsBlacklistService.getValidatedResults(validationResult.getId());

        PlacementsBlacklistRowSource rowSource = new PlacementsBlacklistRowSource(
                placements.iterator(), new ReviewPlacementsBlacklistCsvNodeWriter());
        serialize(MetaDataBuilder.REVIEW_COLUMNS, rowSource);

        return null;
    }

    @ReadOnly
    public String template() throws IOException {
        Iterator<PlacementBlacklist> iterator = new ArrayList<PlacementBlacklist>().iterator();
        PlacementsBlacklistRowSource rowSource = new PlacementsBlacklistRowSource(iterator, new PlacementsBlacklistCsvNodeWriter());

        serialize(MetaDataBuilder.UPLOAD_COLUMNS, rowSource);

        return null;
    }

    public ValidationResultTO getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResultTO validationResult) {
        this.validationResult = validationResult;
    }

    protected void serialize(BulkMetaData<PlacementBlacklistFieldCsv> metaData, RowSource rowSource) throws IOException {
        FileDownloadResult.setDownloadHeaders(request, response, "PlacementsBlacklist" + format.getFormat().getExtension());
        response.setHeader("Content-type", format.getFormat().getMime());

        ResultSerializerSupport serializer;
        switch (format) {
            case XLSX:
                ExcelStyles excelStyles = excelStylesRegistry.get(getLocale());
                serializer = new XlsxSerializer(response.getOutputStream(), null, excelStyles);
                break;

            default:
                serializer = new CsvSerializer(response.getOutputStream(), getLocale(), Integer.MAX_VALUE, format);
                break;
        }

        serializer.registry(ValueFormatterRegistries.bulkDefaultAnd(null), RowTypes.data());
        serializer.registry(ValueFormatterRegistries.defaultUnparsedRowRegistry(), CsvRow.UNPARSED_ROW_TYPE);
        IterationStrategy iterationStrategy = new SimpleIterationStrategy(metaData);
        iterationStrategy.process(rowSource, serializer);
    }

    public BulkFormat getFormat() {
        return format;
    }

    public void setFormat(BulkFormat format) {
        this.format = format;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
}
