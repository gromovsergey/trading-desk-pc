package com.foros.action.creative.display.upload;

import com.foros.action.BaseActionSupport;
import com.foros.action.bulk.BulkMetaData;
import com.foros.action.bulk.CsvRow;
import com.foros.action.creative.csv.CreativeCsvMetaData;
import com.foros.action.creative.csv.CreativeCsvNodeWriter;
import com.foros.action.creative.csv.CreativeFieldCsv;
import com.foros.action.creative.csv.CreativeReviewCsvNodeWriter;
import com.foros.action.creative.csv.CreativeRowSource;
import com.foros.action.creative.csv.CreativeValueFormatterRegistry;
import com.foros.action.creative.csv.MetaDataBuilder;
import com.foros.action.download.FileDownloadResult;
import com.foros.framework.ReadOnly;
import com.foros.model.creative.Creative;
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
import com.foros.session.CurrentUserService;
import com.foros.session.channel.ValidationResultTO;
import com.foros.session.creative.CreativeCsvReaderResult;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.session.template.OptionService;

import java.io.IOException;
import java.util.Collections;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.util.CreateIfNull;

public class DownloadCreativesAction extends BaseActionSupport implements ServletResponseAware, ServletRequestAware {
    protected BulkFormat format = BulkFormat.CSV;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @CreateIfNull
    private ValidationResultTO validationResult;

    @EJB
    private DisplayCreativeService displayCreativeService;

    @EJB
    private OptionService optionService;

    @EJB
    private ExcelStylesRegistry excelStylesRegistry;

    @EJB
    private CurrentUserService currentUserService;

    @ReadOnly
    public String export() throws IOException {
        CreativeCsvReaderResult readerResult = displayCreativeService.getValidatedResults(validationResult.getId());
        CreativeCsvMetaData metaData = MetaDataBuilder.buildReviewMetaData(readerResult.getOptionHeaderNames(), readerResult.getColumnTypeResolver());
        CreativeRowSource rowSource = new CreativeRowSource(
                new CreativeReviewCsvNodeWriter(metaData, optionService), readerResult.getCreatives(), metaData.getReviewColumns().size());
        serialize(metaData.getReviewBulkMetaData(), rowSource);

        return null;
    }

    @ReadOnly
    public String template() throws IOException {
        CreativeCsvMetaData metaData = MetaDataBuilder.buildMetaData(Collections.<Creative> emptyList(), currentUserService.isExternal());
        CreativeRowSource rowSource = new CreativeRowSource(
                new CreativeCsvNodeWriter(metaData), Collections.<Creative>emptyList(), metaData.getColumns().size());
        serialize(metaData.getBulkMetaData(), rowSource);
        return null;
    }

    public ValidationResultTO getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResultTO validationResult) {
        this.validationResult = validationResult;
    }

    protected void serialize(BulkMetaData<CreativeFieldCsv> metaData, RowSource rowSource) throws IOException {
        FileDownloadResult.setDownloadHeaders(request, response, "Creatives" + format.getFormat().getExtension());
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

        CreativeValueFormatterRegistry registry = new CreativeValueFormatterRegistry();
        serializer.registry(registry, RowTypes.data());
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
