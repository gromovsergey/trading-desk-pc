<%@ tag language="java" body-content="empty" description="Renders page title" %>
<%@ attribute name="attributeName" required="true"%>
<%@ attribute name="isSimpleText" required="false"%>
<%@ attribute name="isEscape" required="false"%>
<%@ attribute name="attribute" required="false" type="java.lang.String"%>
<%@ attribute name="colorStatus" required="false" type="com.foros.model.DisplayStatus" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

    <c:if test="${empty pageScope.isSimpleText || pageScope.isSimpleText == 'false'}"><fmt:message key="${pageScope.attributeName}"/></c:if><c:if test="${not empty pageScope.isSimpleText && pageScope.isSimpleText == 'true'}">
    <c:choose><c:when test="${not empty pageScope.isEscape && pageScope.isEscape == 'false'}">${pageScope.attributeName}</c:when><c:otherwise><c:out value="${pageScope.attributeName}"/></c:otherwise></c:choose></c:if><c:if test="${not empty pageScope.attribute && pageScope.attribute !=''}">:</c:if>
    <c:if test="${not empty pageScope.attribute && pageScope.attribute !=''}">${pageScope.attribute}</c:if>

    <c:set scope="request" var="foros_title_generated_heading">
        <ui:pageHeading attributeName="${pageScope.attributeName}" attribute="${pageScope.attribute}" isEscape="${pageScope.isEscape}" isSimpleText="${pageScope.isSimpleText}" colorStatus="${pageScope.colorStatus}"/>
    </c:set>
 - <fmt:message key="systemTitle"/>