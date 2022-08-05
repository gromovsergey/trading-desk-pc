<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<table class="dataView">
    <thead>
    <tr>
        <c:forEach items="${allColumns}" var="column" varStatus="headerStatus">
            <th id="column_${fn:replace(column, '.', '_')}" <s:if test="!selected(#attr.column)">style="display:none"</s:if> >
                <fmt:message key="${column}"/>
             </th>
        </c:forEach>
    </tr>
    </thead>
</table>
