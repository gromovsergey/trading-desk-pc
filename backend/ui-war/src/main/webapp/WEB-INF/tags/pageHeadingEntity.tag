<%@ tag language="java" body-content="empty" description="Renders page heading for entity" %>

<%@ attribute name="name" required="false" %>
<%@ attribute name="entityName" required="true"%>
<%@ attribute name="id" required="false"%>
<%@ attribute name="colorStatus" required="false" type="com.foros.model.DisplayStatus" %>
<%@ attribute name="testFlag" required="false" type="java.lang.Boolean" %>

<%@ attribute name="isViewPage" required="false"%>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<c:choose>
    <c:when test="${empty pageScope.isViewPage || pageScope.isViewPage == 'false'}">
        <c:choose>
            <c:when test="${empty pageScope.id}">
                <h1><span class="attributeName"><fmt:message key="${pageScope.entityName}.entityName.new"/></span></h1>
            </c:when>
            <c:otherwise>
                <h1>
                    <c:if test="${empty pageScope.name}">
                        <span class="attributeName"><fmt:message key="${pageScope.entityName}.entityName.edit"/></span>
                    </c:if>
                    <c:if test="${not empty pageScope.name}">
                        <span class="attributeName"><fmt:message key="${pageScope.entityName}.entityName.edit"/>:</span>
                        <span class="attribute"><c:out value="${pageScope.name}"/></span>
                    </c:if>
                </h1>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <h1>
            <table class="grouping">
                <tr>
                    <c:if test="${not empty pageScope.colorStatus}">
                        <td>
                            <ui:colorStatus displayStatus="${pageScope.colorStatus}" testFlag="${pageScope.testFlag}"/>
                        </td>
                    </c:if>
                    <td>
                        <span class="attributeName"><fmt:message key="${pageScope.entityName}.entityName"/><c:if test="${not empty pageScope.name}">:</c:if></span>
                    </td>
                    <td>
                        <span class="attribute"><c:out value="${pageScope.name}"/></span>
                    </td>
                </tr>
            </table>
        </h1>
    </c:otherwise>
</c:choose>
