<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<ui:fieldGroup>
    <c:forEach items="${addressFields}" var="field">
        <c:if test="${field.OFFieldName != 'Country' && field.enabled}">
            <c:set var="addressFieldName" value="${ad:localizeName(field.name)}"/>
            <c:choose>
                <c:when test="${field.OFFieldName == 'City'}">
                    <c:set var="fieldValue" value="${addressEntity.city}"/>
                </c:when>
                <c:when test="${field.OFFieldName == 'Line1'}">
                    <c:set var="fieldValue" value="${addressEntity.line1}"/>
                </c:when>
                <c:when test="${field.OFFieldName == 'Line2'}">
                    <c:set var="fieldValue" value="${addressEntity.line2}"/>
                </c:when>
                <c:when test="${field.OFFieldName == 'Line3'}">
                    <c:set var="fieldValue" value="${addressEntity.line3}"/>
                </c:when>
                <c:when test="${field.OFFieldName == 'Province'}">
                    <c:set var="fieldValue" value="${addressEntity.province}"/>
                </c:when>
                <c:when test="${field.OFFieldName == 'State'}">
                    <c:set var="fieldValue" value="${addressEntity.state}"/>
                </c:when>
                <c:when test="${field.OFFieldName == 'Zip'}">
                    <c:set var="fieldValue" value="${addressEntity.zip}"/>
                </c:when>
            </c:choose>
            <c:if test="${not empty fieldValue}">
                <ui:simpleField label="${addressFieldName}" value="${fieldValue}"/>
            </c:if>
        </c:if>
    </c:forEach>
</ui:fieldGroup>

