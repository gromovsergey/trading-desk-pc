<%@ tag description="UI Tab" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<%@ attribute name="data" required="true" rtexprvalue="true" type="java.lang.Object" %>

<ui:errorsBlock>
    <s:actionerror/>
    <s:fielderror/>
</ui:errorsBlock>

<ui:constraintViolations/>

<c:set var="preparedParameters" value="${pageScope.data.preparedParameters}"/>
<c:if test="${not empty preparedParameters}">
    <ui:section>
        <ui:fieldGroup>
            <c:forEach var="prm" items="${preparedParameters}">
                <c:if test="${not empty prm.valueText}">
                    <ui:simpleField label="${prm.name}" value="${prm.valueText}"/>
                </c:if>
            </c:forEach>
        </ui:fieldGroup>
    </ui:section>
</c:if>

