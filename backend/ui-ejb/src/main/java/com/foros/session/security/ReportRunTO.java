package com.foros.session.security;

import com.foros.model.security.ObjectType;
import com.foros.model.security.ResultType;
import com.foros.session.reporting.ReportType;
import com.foros.util.NameValuePair;

import java.util.Collection;
import java.util.LinkedList;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ReportRunTO {
    private Long id;
    private ObjectType objectType;
    private String name;
    private String outputType;
    private Long rowsCount;
    private Long size;
    private Long executionTime;
    private Collection<NameValuePair<String, Object>> params;
    private Collection<String> columns;
    private String errorMessage;
    private ResultType resultType = ResultType.SUCCESS;

    public ReportRunTO(Long id, ObjectType objectType) {
        this.id = id;
        this.objectType = objectType;
    }

    public Long getId() {
        return id;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public Long getRowsCount() {
        return rowsCount;
    }

    public void setRowsCount(Long rowsCount) {
        this.rowsCount = rowsCount;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public Collection<NameValuePair<String, Object>> getParams() {
        return params;
    }

    public void setParams(Collection<NameValuePair<String, Object>> params) {
        this.params = params;
    }

    public void addParam(String name, Object value) {
        if (this.params == null) {
            this.params = new LinkedList<NameValuePair<String, Object>>();
        }
        this.params.add(new NameValuePair<String, Object>(name, value));
    }

    public Collection<String> getColumns() {
        return columns;
    }

    public void setColumns(Collection<String> columns) {
        this.columns = columns;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.resultType = ResultType.FAILURE;
        this.errorMessage = errorMessage;
    }

    public void clearError() {
        this.resultType = ResultType.SUCCESS;
        this.errorMessage = null;
    }

    public ResultType getResultType() {
        return resultType;
    }

    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setUseClassName(false);
        style.setUseIdentityHashCode(false);
        style.setContentStart("");
        style.setFieldSeparator("\n");
        style.setContentEnd("");

        return new ToStringBuilder(this, style)
                .append("type", id == null ? null : ReportType.byId(id))
                .append("objectType", objectType)
                .append("columns", columns)
                .append("outputType", outputType)
                .append("params", params)
                .toString();
    }
}