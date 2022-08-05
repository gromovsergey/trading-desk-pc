<%@ tag description="UI Field" %>

<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="id" %>
<%@ attribute name="cssClass" %>
<%@ attribute name="labelKey" %>
<%@ attribute name="label" %>
<%@ attribute name="labelForId" %>
<%@ attribute name="required" type="java.lang.Boolean" %>
<%@ attribute name="errors" %>
<%@ attribute name="value" rtexprvalue="true" type="java.lang.String" required="true" %>
<%@ attribute name="resourceKey" rtexprvalue="true" required="true"%>
<%@ attribute name="resourceUrl" required="true"%>
<%@ attribute name="entityName" required="false"%>
<%@ attribute name="isArea" type="java.lang.Boolean" required="false"%>
<%@ attribute name="escapeXml" type="java.lang.Boolean" required="false"%>

<c:if test="${isArea == null}"><c:set var="isArea" value="${false}" /></c:if>
<c:if test="${escapeXml == null}"><c:set var="escapeXml" value="${true}" /></c:if>

<ui:field id="${pageScope.id}" cssClass="${pageScope.cssClass}" labelKey="${pageScope.labelKey}" label="${pageScope.label}"
          labelForId="${pageScope.labelForId}" required="${pageScope.required}" errors="${pageScope.errors}" escapeXml="${escapeXml}">
    <div class="markerIconContainer">
        <ui:text text="${pageScope.value}" escapeXml="${escapeXml}"/>
       	<div class="markerIcon localize_${pageScope.id}" title="Localize" />
    </div>
</ui:field>

<script type="text/javascript">
    $().ready(function(){
        $('.localize_${pageScope.id}').attr('title', $.localize('dynamicResources.localize'));
        $('.localize_${pageScope.id}').click(function(event){
            var popupName = ${isArea} ? 'localizer_popup_area' : 'localizer_popup'
            UI.Localization.showPopup(event, {url : '${pageScope.resourceUrl}', key : '${pageScope.resourceKey}', entityName : '${pageScope.entityName}', popupName : popupName});
            return false;
        });
    });
</script>