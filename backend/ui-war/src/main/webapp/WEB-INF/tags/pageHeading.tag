
<%@ attribute name="attributeName" required="true"%>
<%@ attribute name="isSimpleText" required="false"%>
<%@ attribute name="isEscape" required="false"%>
<%@ attribute name="attribute" required="false" type="java.lang.String"%>
<%@ attribute name="colorStatus" required="false" type="com.foros.model.DisplayStatus" %>
<%@ attribute name="testFlag" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<h1>
    <table class="grouping">
        <tr>
            <c:if test="${not empty pageScope.colorStatus}">
                <td>
                    <ui:colorStatus displayStatus="${pageScope.colorStatus}" testFlag="${pageScope.testFlag}"/>
                </td>
            </c:if>
            <td>
                <span class="attributeName">
                    <c:set var="dilimiter"><c:if test="${not empty pageScope.attribute && pageScope.attribute !=''}">:</c:if></c:set>
                    <c:if test="${empty pageScope.isSimpleText || pageScope.isSimpleText == 'false'}">
                        <fmt:message key="${pageScope.attributeName}"/>${dilimiter}
                    </c:if>
                    <c:if test="${not empty pageScope.isSimpleText && pageScope.isSimpleText == 'true'}">
                        <c:choose>
                            <c:when test="${not empty pageScope.isEscape && pageScope.isEscape == 'false'}">
                                ${pageScope.attributeName}${dilimiter}
                            </c:when>
                            <c:otherwise>
                                <c:out value="${pageScope.attributeName}"/>${dilimiter}
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </span>
            </td>
            <c:if test="${not empty pageScope.attribute && pageScope.attribute !=''}">
                <td>
                    <span class="attribute">${pageScope.attribute}</span>
                </td>
            </c:if>
        </tr>
    </table>
</h1>
