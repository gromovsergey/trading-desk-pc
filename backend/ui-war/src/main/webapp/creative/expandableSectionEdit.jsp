<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${model.template.expandable && not empty size.expansions}" >
    <ui:section titleKey="creative.expandableTemplate">
        <ui:fieldGroup>
            <ui:field labelKey="creative.expandableCreative" required="true" cssClass="valignFix">
				<s:radio id="expandableRadio" list="#{true, false}" name="model.expandable" value="model.expandable" listValue="getText('creative.expandable.'+key)" onchange="$('#expansionsSelect').prop({disabled : $(this).val() == 'false'}).attr({value:''});"/>
				<s:set var="pixelsValue" value="getText('creative.pixels')"/>
				<table style="margin-top:.75em;">
					<ui:simpleField labelKey="creative.maxExpandedSize" value="${size.maxWidth} X ${size.maxHeight} ${pageScope.pixelsValue}"/>
				</table>
            </ui:field>

            <ui:field labelKey="creative.expansionDirection" required="true" errors="expansion,expandableSize">
              <jsp:useBean id="expansionsComparator" class="org.springframework.util.comparator.ComparableComparator" scope="page"/>
              <s:sort comparator="#attr.expansionsComparator" source="size.expansions" var="sortedExpansions"/>
              <s:select id="expansionsSelect" name="expansion" disabled="%{!expandable}" list="#attr.sortedExpansions" listKey="name()" listValue="getText('enums.CreativeSizeExpansion.' + name())" cssClass="middleLengthText"
                          headerValue="%{getText('form.select.pleaseSelect')}" headerKey="" value="expansion"></s:select>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</c:if>