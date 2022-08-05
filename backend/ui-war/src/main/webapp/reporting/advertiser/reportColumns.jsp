<%@ page import="com.foros.reporting.serializer.HtmlCell" %>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<c:set var="selected" value="${reportState.selected}"/>
<c:set var="columns" value="${selected.columns}"/>
<s:set var="headerFormatterRegistry" value="%{new com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportHeaderFormatterRegistry(#attr.selected)}"/>
<s:set var="formatterContext" value="%{new com.foros.reporting.serializer.formatter.FormatterContext(locale)}"/>

<table class="dataView">
    <thead>
    <tr>
        <c:forEach items="${columns}" var="column" varStatus="headerStatus">
            <c:set var="htmlCell" value="<%=new HtmlCell()%>"/>
            <c:set var="formatter" value="${headerFormatterRegistry.get(column)}"/>
            ${formatter.formatHtml(htmlCell, column, formatterContext)}
            <th class="${htmlCell.cssClasses}">
                    ${htmlCell.html}
            </th>
        </c:forEach>
    </tr>
    </thead>
</table>
