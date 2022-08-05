<%@ tag description="UI Collapsible" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"       prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="labelKey" %>
<%@ attribute name="labelText" %>
<%@ attribute name="onExpand" %>
<%@ attribute name="onCollapse" %>
<%@ attribute name="expanded" type="java.lang.Boolean" %>
<%@ attribute name="cssClass" %>
<%@ attribute name="chBoxName" %>
<%@ attribute name="checked" type="java.lang.Boolean" %>
<%@ attribute name="notCollapsible" type="java.lang.Boolean" %>

<c:set var="labelVal">
    <c:choose>
        <c:when test="${not empty pageScope.labelText}">
            <c:out value="${pageScope.labelText}"/>
        </c:when>
        <c:when test="${not empty pageScope.labelKey}">
            <fmt:message key="${pageScope.labelKey}"/>
        </c:when>
        <c:otherwise></c:otherwise>
    </c:choose>
</c:set>

<div class="collapsible ${pageScope.expanded ? 'expanded expanded_default' : 'collapsed'} ${not empty pageScope.chBoxName ? 'withChBox' : ''} ${pageScope.cssClass}" 
            id="${pageScope.id}" ${not pageScope.notCollapsible ? 'collapsible="collapsible"' : ''}>

    <c:if test="${not empty pageScope.chBoxName}">
        <input type="checkbox" ${not empty pageScope.checked and pageScope.checked ? 'checked="checked"' : ''} class="main collapsible-chbx" >
        <input type="hidden" class="alwaysEnable" name="${pageScope.chBoxName}" value="${not empty pageScope.checked and pageScope.checked ? 'true' : 'false'}"/>
    </c:if>
    <a href="#" class="collapseButt expand" onclick="${pageScope.onExpand};">${pageScope.labelVal}</a>
    <table class="block">
        <tr>
            <td class="mainCell">
                <c:choose>
                    <c:when test="${pageScope.notCollapsible}">
                        <div class="collapseButt">${pageScope.labelVal}</div>
                    </c:when>
                    <c:otherwise>
                        <a href="#" class="collapseButt collapse" onclick="${pageScope.onCollapse};">${pageScope.labelVal}</a>
                        <a href="#" class="collapseButt expand" onclick="${pageScope.onExpand};">${pageScope.labelVal}</a>
                    </c:otherwise>
                </c:choose>
                <div class="content">
                    <jsp:doBody />
                </div>
                <div class="fixing"></div>
            </td>
        </tr>
    </table>
</div>
<script type="text/javascript">
    $(function(){
        $('#${pageScope.id}').collapsible();
    });
</script>
