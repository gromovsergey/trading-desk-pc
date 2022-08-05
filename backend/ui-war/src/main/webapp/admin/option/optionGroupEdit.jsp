<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">
$().ready(function(){
    $('#optionGroupSave').submit(function() {
        var sortOrderPair = $('#sortOrderPair').val().split('_');
        $('#type').val(sortOrderPair[0]);
        $('#sortOrder').val(sortOrderPair[1]);
        return true;
    });
})
</script>

<s:form action="admin/OptionGroup/%{#attr.isCreatePage?'create':'update'}" method="post" id="optionGroupSave">
    <s:hidden name="id"/>
    <s:hidden name="creativeSizeId"/>
    <s:hidden name="templateId"/>
    <s:hidden name="version"/>
    <s:hidden name="type" id="type"/>
    <s:hidden name="sortOrder" id="sortOrder"/>

    <div class="wrapper">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:actionerror/>
    </div>

    <ui:section>
        <ui:fieldGroup>

            <ui:field labelKey="OptionGroup.position" labelForId="sortOrder" required="true" errors="sortOrder">
                <select name="sortOrderPair" id="sortOrderPair" class="middleLengthText">
                    <c:if test="${advertiserTypeEnabled}">
                        <c:set var="advertiserLabel"><fmt:message key="CreativeSize.options.group.type.Advertiser"/></c:set>
                        <ui:optionGroups groupLabel="${advertiserLabel}" collection="${advertiserOptionGroups}"
                                         currentGroupId="${id}" sortOrder="${type == 'Advertiser' ? sortOrder : ''}"
                                         optgroupEnabled="${id == null}" type="Advertiser" />
                    </c:if>
                    
                    <c:if test="${publisherTypeEnabled}">
                        <c:set var="publisherLabel"><fmt:message key="CreativeSize.options.group.type.Publisher"/></c:set>
                        <ui:optionGroups groupLabel="${publisherLabel}" collection="${publisherOptionGroups}"
                                         currentGroupId="${id}" sortOrder="${type == 'Publisher' ? sortOrder : ''}"
                                         optgroupEnabled="${id == null && relatedType != 'DISCOVER_TEMPLATE'}" type="Publisher" />
                    </c:if>

                    <c:if test="${hiddenTypeEnabled}">
                        <c:set var="hiddenLabel"><fmt:message key="CreativeSize.options.group.type.Hidden"/></c:set>
                        <ui:optionGroups groupLabel="${hiddenLabel}" collection="${hiddenOptionGroups}"
                                         currentGroupId="${id}" sortOrder="${type == 'Hidden' ? sortOrder : ''}"
                                         optgroupEnabled="${id == null}" type="Hidden" />
                    </c:if>
                </select>
            </ui:field>

            <ui:field labelKey="defaultName" labelForId="name" required="true" errors="defaultName,name">
                <s:textfield name="defaultName" id="name" cssClass="middleLengthText" maxlength="100"/>
            </ui:field>

            <ui:field labelKey="Option.defaultTooltip" labelForId="label" errors="defaultLabel">
                <s:textfield name="defaultLabel" id="label" cssClass="middleLengthText" maxLength="50"/>
            </ui:field>

            <ui:field labelKey="OptionGroup.availability" errors="availability">
                <s:radio cssClass="withInput" name="availability" list="availabilities" listKey="key"
                         listValue="value" value="availability"/>
            </ui:field>
            <ui:field labelKey="OptionGroup.collapsibility" errors="colapsability">
                <s:radio cssClass="withInput" name="collapsability" list="collapsabilities" listKey="key"
                         listValue="value" value="collapsability"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

  <s:if test="id == null">
    <c:if test="${relatedType == 'CREATIVE_TEMPLATE'}">
        <s:url var="createUrl" action="%{#attr.moduleName}/CreativeTemplate/view"><s:param name="id" value="template.id"/></s:url>
    </c:if>
    <c:if test="${relatedType == 'DISCOVER_TEMPLATE'}">
        <s:url var="createUrl" action="%{#attr.moduleName}/DiscoverTemplate/view"><s:param name="id" value="template.id"/></s:url>
    </c:if>
    <c:if test="${relatedType == 'CREATIVE_SIZE'}">
        <s:url var="createUrl" action="%{#attr.moduleName}/CreativeSize/view"><s:param name="id" value="creativeSize.id"/></s:url>
    </c:if>
  </s:if>
  <s:else>
      <s:url var="createUrl" action="%{#attr.moduleName}/OptionGroup/view"><s:param name="id" value="id"/></s:url>
  </s:else>

  <s:include value="/templates/formFooter.jsp">
    <s:param name="createUrl">${createUrl}</s:param>
  </s:include>
</s:form>
