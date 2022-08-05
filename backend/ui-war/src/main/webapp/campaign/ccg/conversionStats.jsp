<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/ad/serverUI" prefix="ad"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<table class="dataView" id="conversion"
    style="border-bottom: 1px solid #ccc;">
    <thead>
        <tr>
            <th><fmt:message key="ccg.conversion.name" /></th>
            <th><fmt:message key="ccg.conversion.category" /></th>
            <th><fmt:message key="ccg.conversion.impConv" /></th>
            <th><fmt:message key="ccg.conversion.impCR" /></th>
            <th><fmt:message key="ccg.conversion.clickConv" /></th>
            <th><fmt:message key="ccg.conversion.clickCR" /></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${linkedConversions}" var="conversion"
            varStatus="setIndexID">
            <tr>
                <td><ui:displayStatus
                        displayStatus="${conversion.displayStatus}">
                        <a
                            href="${_context}/Action/view.action?id=${conversion.id}">
                            <c:out value="${conversion.name}" />
                        </a>
                    </ui:displayStatus></td>
                <td><fmt:message key="${conversion.category.nameKey}"/>
                </td>
                <td class="number"><fmt:formatNumber
                        value="${conversion.impConv}"
                        groupingUsed="true" /></td>
                <td class="number"><fmt:formatNumber
                        value="${conversion.impCR}" groupingUsed="false"
                        maxFractionDigits="2" />%</td>
                <td class="number"><fmt:formatNumber
                        value="${conversion.clickConv}"
                        groupingUsed="true" /></td>
                <td class="number"><fmt:formatNumber
                        value="${conversion.clickCR}"
                        groupingUsed="false" maxFractionDigits="2" />%</td>
            </tr>
        </c:forEach>
    </tbody>
</table>


