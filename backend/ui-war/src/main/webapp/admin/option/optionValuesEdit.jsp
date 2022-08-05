<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="isOptionsEmpty" value="true"/>
<c:set var="isOnlyInternalOptions" value="true"/>
<s:iterator value="groups" var="optionGroup">
    <c:if test="${optionGroup.options.size() > 0}">
        <c:set var="isOptionsEmpty" value="false"/>
    </c:if>
    
    
    
    <s:iterator value="#optionGroup.options" var="option">
        <c:if test="${isOnlyInternalOptions && !option.internalUse}">
            <c:set var="isOnlyInternalOptions" value="false"/>
        </c:if>
    </s:iterator>
</s:iterator>

<c:if test="${!internal && isOnlyInternalOptions}">
    <c:set var="isOptionsEmpty" value="true"/>
</c:if>

<c:if test="${not isOptionsEmpty}">
<ui:section titleKey="${optionTitleKey}" title="${optionTitle}">
    <s:iterator value="groups" var="optionGroup">
        <c:set var="groupEnabled" value="${empty groupStateValues[optionGroup.id] ? (optionGroup.availability.name == 'D' ? false : true) : groupStateValues[optionGroup.id].enabled}"/>
        <c:set var="groupCollapsed" value="${empty groupStateValues[optionGroup.id] ? (optionGroup.collapsability.name == 'C' ? true : false) : groupStateValues[optionGroup.id].collapsed}"/>
        <c:set var="labelText" value="${ad:localizeName(optionGroup.name)}"/>
        <s:set var="groupState" value="groupStateValues['%{#optionGroup.id}']"/>
        <c:set var="groupStateName" value="groupStateValues['${optionGroup.id}'].enabled"/>

        <s:if test='#attr.groupCollapsed && !fieldErrors.empty'>
            <s:iterator value="#optionGroup.options" var="option">
                <s:if test="fieldErrors.containsKey('optionValues['+#option.id+'].value')">
                    <c:set var="groupCollapsed" value="${false}"/>
                </s:if>
            </s:iterator>
        </s:if>

        <ui:collapsible id="collabsible_section_${optionGroup.id}" cssClass="optionGroup"
                        labelText="${pageScope.labelText}"
                        expanded="${!groupCollapsed}"
                        chBoxName="${optionGroup.availability.name != 'A' ? groupStateName : ''}"
                        checked="${optionGroup.availability.name != 'A' ? groupEnabled : ''}"
                        notCollapsible="${optionGroup.collapsability.name == 'N' ? 'true': 'false'}">

            <s:set var="isNew" value="%{model.id == null}" scope="page"/>
                
            <ui:fieldGroup>
                <ui:field cssClass="hide">
                    <c:choose>
                        <c:when test="${entityType =='Creative' && !isNew}">
                            <s:hidden name="groupStateValues['%{#optionGroup.id}'].id.creativeId" value="%{model.id}" cssClass="alwaysEnable"/>
                        </c:when>
                        <c:when test="${entityType =='Tag' && !isNew}">
                            <s:hidden name="groupStateValues['%{#optionGroup.id}'].id.tagId" value="%{model.id}" cssClass="alwaysEnable"/>
                        </c:when>
                        <c:when test="${entityType =='WDTag' && !isNew}">
                            <s:hidden name="groupStateValues['%{#optionGroup.id}'].id.wdTagId" value="%{model.id}" cssClass="alwaysEnable"/>
                        </c:when>
                        <c:when test="${entityType =='TextAd' && !isNew}">
                            <s:hidden name="groupStateValues['%{#optionGroup.id}'].id.creativeId" value="%{model.creative.id}" cssClass="alwaysEnable"/>
                        </c:when>
                    </c:choose>
                    <s:hidden name="groupStateValues['%{#optionGroup.id}'].id.optionGroupId" value="%{#optionGroup.id}" cssClass="alwaysEnable"/>
                    <s:hidden name="groupStateValues['%{#optionGroup.id}'].collapsed" value="%{#attr.groupCollapsed}" cssClass="alwaysEnable"/>
                    <s:hidden name="groupStateValues['%{#optionGroup.id}'].version" cssClass="alwaysEnable"/>
                </ui:field>
                <s:iterator value="#optionGroup.options" var="option" status="row">
                    <c:if test="${internal || !option.internalUse}">
                        <s:include value="/admin/option/optionControl.jsp"/>
                    </c:if>
                </s:iterator>
            </ui:fieldGroup>
        </ui:collapsible>

    </s:iterator>
</ui:section>
<script type="text/javascript">
    $(function(){
        $('.collapsible').each(function(){
            var $this    = $(this);
            $this.find('.collapseButt').on('click.collapsible', function(){
                $this.find('.content input:hidden[id$="__collapsed"]').val($this.hasClass('collapsed'));
            });
        });
    });
</script>
</c:if>
