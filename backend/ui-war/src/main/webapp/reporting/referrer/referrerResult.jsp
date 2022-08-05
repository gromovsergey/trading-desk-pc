<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>


<ui:errorsBlock>
    <s:actionerror/>
    <s:fielderror/>
</ui:errorsBlock>

<c:set var="preparedParameters" value="${pageScope.data.preparedParameters}"/>
<c:if test="${not empty preparedParameters}">
    <ui:section>
        <ui:fieldGroup>
            <c:forEach var="prm" items="${preparedParameters}">
                <c:if test="${not empty prm.valueText}">
                    <c:choose>
                        <c:when test="${prm.id == 'siteUrl'}">
                            <ui:field label="${prm.name}">
                                <a href='${prm.valueText}' target="_blank">${prm.valueText}</a>
                            </ui:field>
                        </c:when>
                        <c:otherwise>
                            <ui:simpleField label="${prm.name}" value="${prm.valueText}"/>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </c:forEach>
        </ui:fieldGroup>
    </ui:section>
</c:if>

<s:if test="data">
    <h2><fmt:message key="report.domains"/></h2>

    <ui:report id="result" data="${data}"/>

    <c:if test="${not empty data.rows}">
        <ui:reportExportLinks/>
    </c:if>
</s:if>
