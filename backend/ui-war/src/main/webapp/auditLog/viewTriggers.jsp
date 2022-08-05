<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>


<ui:fieldGroup>
    <c:if test="${not empty addedTriggers}">
        <ui:field label="Added">
            <textarea wrap="off" readonly="readonly" cols="" rows="" id="addedTriggers" class="middleLengthText1"><c:out value="${addedTriggers}" escapeXml="true"/></textarea>
        </ui:field>
    </c:if>
    <c:if test="${not empty removedTriggers}">
        <ui:field label="Removed">
            <textarea wrap="off" readonly="readonly" cols="" rows="" id="removedTriggers" class="middleLengthText1"><c:out value="${removedTriggers}" escapeXml="true"/></textarea>
        </ui:field>
    </c:if>
</ui:fieldGroup>
