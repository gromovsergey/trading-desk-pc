<%@ tag description="Groupping statusButtons tag and corresponding content" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%@ attribute name="entity" required="true" type="com.foros.model.StatusEntityBase" %>
<%@ attribute name="restrictionEntity" required="true" rtexprvalue="true" %>
<%@ attribute name="activatePage" rtexprvalue="true" %>
<%@ attribute name="inactivatePage" rtexprvalue="true" %>
<%@ attribute name="deletePage" rtexprvalue="true" %>
<%@ attribute name="undeletePage" rtexprvalue="true" %>
<%@ attribute name="descriptionKey" %>
<%@ attribute name="descriptionText" %>
<%@ attribute name="customClass" %>
<%@ attribute name="tipKey" %>
<%@ attribute name="tipText" %>

<c:set var="currentBody">
    <jsp:doBody />
</c:set>

<table class="fieldAndAccessories ${pageScope.customClass}">
    <tr>
        <c:if test="${not empty pageScope.descriptionKey or not empty pageScope.descriptionText}">
            <c:set var="descr">
                <c:if test="${not empty pageScope.descriptionKey}">
                    <fmt:message key="${pageScope.descriptionKey}"/>
                </c:if>
                <c:if test="${not empty pageScope.descriptionText}">${pageScope.descriptionText}</c:if>
            </c:set>
            <td class="withField">
                <ui:text text="${descr}"/>
            </td>
        </c:if>

        <c:if test="${not empty pageScope.tipText or not empty pageScope.tipKey}">
            <td class="withTip">
                <c:choose>
                    <c:when test="${not empty pageScope.tipText}">
                        <ui:hint>
                            <c:out value="${pageScope.tipText}" />
                        </ui:hint>
                    </c:when>
                    <c:otherwise>
                        <ui:hint>
                            <fmt:message key="${pageScope.tipKey}"/>
                        </ui:hint>
                    </c:otherwise>
                </c:choose>
            </td>
        </c:if>
        
        <td class="withButton">
            <ui:statusButtons
                entity="${pageScope.entity}"
                restrictionEntity="${pageScope.restrictionEntity}"
                activatePage="${pageScope.activatePage}"
                inactivatePage="${pageScope.inactivatePage}"
                deletePage="${pageScope.deletePage}"
                undeletePage="${pageScope.undeletePage}"
            />
        </td>
        <c:if test="${not empty pageScope.currentBody}">
            <td class="withButton">
                <jsp:doBody />
            </td>
        </c:if>
    </tr>
</table>