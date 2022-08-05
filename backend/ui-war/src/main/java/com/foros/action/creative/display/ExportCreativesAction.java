package com.foros.action.creative.display;

import com.foros.action.bulk.BulkMetaData;
import com.foros.action.bulk.CsvRow;
import com.foros.action.creative.csv.CreativeCsvMetaData;
import com.foros.action.creative.csv.CreativeCsvNodeWriter;
import com.foros.action.creative.csv.CreativeFieldCsv;
import com.foros.action.creative.csv.CreativeRowSource;
import com.foros.action.creative.csv.CreativeValueFormatterRegistry;
import com.foros.action.creative.csv.MetaDataBuilder;
import com.foros.action.download.FileDownloadResult;
import com.foros.framework.MessageStoreInterceptor;
import com.foros.framework.ReadOnly;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
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
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.bulk.Paging;
import com.foros.session.campaign.bulk.CreativeSelector;
import com.foros.session.creative.CreativeCategoryService;
import com.foros.session.creative.CreativeTO;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.util.CollectionUtils;
import com.foros.util.EntityUtils;

import java.io.IOException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionContext;

public class ExportCreativesAction extends SearchCreativesBaseAction implements ServletResponseAware, ServletRequestAware {
    private static final int MAX_EXPORT_RESULT_SIZE = 65000;
    private static final int ORACLE_LIMIT = 500;
    private static final int MAX_ENTITIES_IN_ERROR_MSG = 50;

    @EJB
    private ExcelStylesRegistry excelStylesRegistry;

    @EJB
    private DisplayCreativeService displayCreativeService;

    @EJB
    CreativeCategoryService creativeCategoryService;

    @EJB
    private CurrentUserService currentUserService;

    private BulkFormat format = BulkFormat.CSV;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private List<Long> creativeIds;

    public BulkFormat getFormat() {
        return format;
    }

    public void setFormat(BulkFormat format) {
        this.format = format;
    }

    public List<Long> getCreativeIds() {
        return creativeIds;
    }

    public void setCreativeIds(List<Long> creativeIds) {
        this.creativeIds = creativeIds;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @ReadOnly
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "#target.account")
    public String export() throws IOException {
        boolean checkAccount = true;
        if (getCreativeIds() == null) {
            checkAccount = false;
            getSearchParams().setPage(1l);
            getSearchParams().setPageSize(MAX_EXPORT_RESULT_SIZE + 1);
            List<CreativeTO> creativeTos = searchCreatives();
            setCreativeIds(new ArrayList<>(EntityUtils.getEntityIds(creativeTos)));
        }

        if (getCreativeIds().isEmpty()) {
            addErrorAndSave(getText("creative.export.emptyList"));
            return INPUT;
        }
        if (getCreativeIds().size() > MAX_EXPORT_RESULT_SIZE) {
            addErrorAndSave(getText("creative.export.tooManyRows", new String[] { String.valueOf(MAX_EXPORT_RESULT_SIZE) }));
            return INPUT;
        }

        List<Creative> creatives = getCreatives();
        if (checkAccount) {
            checkAccount(creatives);
        }

        initializeCategories(creatives);
        creatives = sort(creatives);

        CreativeCsvMetaData metaData = MetaDataBuilder.buildMetaData(creatives, currentUserService.isExternal());
        if (!metaData.getDuplicateOptionNames().isEmpty()) {
            addDuplicateOptionsErrors(metaData.getDuplicateOptionNames());
            return INPUT;
        }

        CreativeRowSource rowSource = new CreativeRowSource(new CreativeCsvNodeWriter(metaData), creatives, metaData.getColumns().size());
        serialize(metaData.getBulkMetaData(), rowSource);

        return null;
    }

    private void addErrorAndSave(String actionError) {
        addActionError(actionError);
        MessageStoreInterceptor.saveErrors(ActionContext.getContext(), this);
    }

    private void serialize(BulkMetaData<CreativeFieldCsv> metaData, RowSource rowSource) throws IOException {
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

    private void initializeCategories(List<Creative> creatives) {
        Map<Long, CreativeCategory> categoriesById = new HashMap<>();
        for (Creative creative : creatives) {
            Set<CreativeCategory> categories = creative.getCategories();
            Set<CreativeCategory> modifiedCategories = new HashSet<>(categories.size());
            for (CreativeCategory category : categories) {
                CreativeCategory mappedCategory = categoriesById.get(category.getId());
                if (mappedCategory != null) {
                    modifiedCategories.add(mappedCategory);
                    continue;
                }
                modifiedCategories.add(category);
                categoriesById.put(category.getId(), category);
            }
            creative.setCategories(modifiedCategories);
        }

        List<CreativeCategory> persistentCategories = creativeCategoryService.findByIds(categoriesById.keySet());
        for (CreativeCategory category : persistentCategories) {
            CreativeCategory mappedCategory = categoriesById.get(category.getId());
            mappedCategory.setDefaultName(category.getDefaultName());
            mappedCategory.setType(category.getType());
        }
    }

    private List<Creative> sort(List<Creative> creatives) {
        Map<Long, Creative> creativesMap = new HashMap(creatives.size());
        for (Creative creative : creatives) {
            creativesMap.put(creative.getId(), creative);
        }

        List<Creative> result = new ArrayList(creatives.size());
        for (Long id : getCreativeIds()) {
            result.add(creativesMap.get(id));
        }

        return result;
    }

    private void checkAccount(List<Creative> creatives) {
        for (Creative creative : creatives) {
            if (!creative.getAccount().equals(getAccount())) {
                throw new AccessControlException("Access is forbidden");
            }
        }
    }

    private void addDuplicateOptionsErrors(Map<Creative, Set<String>> duplicates) {
        int currentRowNum = 0;
        for (Map.Entry<Creative, Set<String>> entry : duplicates.entrySet()) {
            if (MAX_ENTITIES_IN_ERROR_MSG < ++currentRowNum) {
                break;
            }

            addActionError(getText("creative.upload.error.duplicateCreativeOption",
                new String[] { entry.getKey().getName(), String.valueOf(entry.getKey().getId()), CollectionUtils.join(entry.getValue(), "; ") }));
        }

        if (MAX_ENTITIES_IN_ERROR_MSG < currentRowNum) {
            addActionError("...");
        }
        MessageStoreInterceptor.saveErrors(ActionContext.getContext(), this);
    }

    private List<Creative> getCreatives() {
        CreativeSelector selector = new CreativeSelector();
        selector.setPaging(new Paging(0, ORACLE_LIMIT));

        List<Long> creativeIds = getCreativeIds();
        if (creativeIds.size() > MAX_EXPORT_RESULT_SIZE) {
            creativeIds = creativeIds.subList(0, MAX_EXPORT_RESULT_SIZE);
            setCreativeIds(creativeIds);
        }

        List<Creative> result = new ArrayList<>();
        for (int i = 0; i < creativeIds.size(); i += ORACLE_LIMIT) {
            int upperIndex = i + ORACLE_LIMIT;
            upperIndex = upperIndex > creativeIds.size() ? creativeIds.size() : upperIndex;

            selector.setCreatives(creativeIds.subList(i, upperIndex));
            result.addAll(displayCreativeService.get(selector).getEntities());
        }
        return result;
    }
}
