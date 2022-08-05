<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script type="text/javascript">
    function moveOption(optionId, type) {
        $('#optionMoveId').val(optionId);
        $('#optionMoveType').val(type);
        $('#optionMoveForm').submit();
    }
</script>

<c:set var="canUpdate" value="${ad:isPermitted('Template.update', entity)}"/>

<s:bean var="discoverTemplateFileComparator" name="com.foros.util.comparator.DiscoverTemplateFileComparator"/>

<c:set var="versionError">
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</c:set>

<s:if test="hasActionErrors() or not empty pageScope.versionError">
    <div class="wrapper">
        ${versionError}
        <s:actionerror/>
    </div>
</s:if>

<ui:section titleKey="form.main" >
    <ui:fieldGroup>
        <c:if test="${canUpdate}">
            <ui:localizedField id="name" labelKey="defaultName" value="${defaultName}"
                resourceKey="Template.${id}"
                 resourceUrl="/admin/resource/htmlName/"
                 entityName="Template"/>
        </c:if>
        <c:if test="${not canUpdate}">
            <ui:simpleField labelKey="defaultName" value="${defaultName}"/>
        </c:if>

        <ui:field labelKey="DiscoverTemplate.status">
            <c:set var="statusDescriptionKey">enums.Status.${status}</c:set>
            <ui:statusButtonGroup
                descriptionKey="${statusDescriptionKey}"
                restrictionEntity="Template" entity="${entity}"
                deletePage="delete.action"
                undeletePage="undelete.action"/>
        </ui:field>

        <s:if test="status.letter == 'D' && !templateOptions.empty">
            <ui:field labelKey="DiscoverTemplate.options">
                <c:set var="textVal">
                    <ad:commaWriter var="templateOption" items="${sortedTemplateOptions}"><c:out value="${ad:localizeName(templateOption.option.name)}"/></ad:commaWriter>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
        </s:if>
    </ui:fieldGroup>
</ui:section>

<ui:header styleClass="level2">
  <h2><s:text name="DiscoverTemplate.options"/></h2>
  <c:if test="${canUpdate}">
    <ui:button message="OptionGroup.create" href="/admin/OptionGroup/new.action?templateId=${id}" />
    <c:if test="${not empty optionGroups}">
      <ui:button message="Option.create" href="/admin/Option/new.action?templateId=${id}" />
      <!--  ui:button message="OptionGroup.editOrder" href="/admin/OptionGroup/editOrder.action?templateId=${id}" /-->
    </c:if>
  </c:if>
</ui:header>
<c:choose>
  <c:when test="${not empty optionGroups}">
    <table class="dataView">
        <thead>
        <tr>
            <th><fmt:message key="DiscoverTemplate.options.group.type"/></th>
            <th><fmt:message key="DiscoverTemplate.options.group.name"/></th>
            <th><fmt:message key="DiscoverTemplate.options.name"/></th>
            <th><fmt:message key="DiscoverTemplate.options.group.token"/></th>
            <th><fmt:message key="DiscoverTemplate.options.group.defaultValue"/></th>
        </tr>
        </thead>
        <tbody>
            <c:set var="publisherLabel"><fmt:message key="CreativeSize.options.group.type.Publisher"/></c:set>
            <ui:allOptionsView label="${publisherLabel}" collection="${publisherOptionGroups}"/>

            <c:set var="hiddenLabel"><fmt:message key="CreativeSize.options.group.type.Hidden"/></c:set>
            <ui:allOptionsView label="${hiddenLabel}" collection="${hiddenOptionGroups}"/>
        </tbody>
    </table>
  </c:when>
  <c:otherwise>
      <div class="wrapper">
          <fmt:message key="nothing.found.to.display"/>
      </div>
  </c:otherwise>
</c:choose>

<ui:header styleClass="level2">
    <h2><s:text name="DiscoverTemplate.files"/></h2>
</ui:header>
<table class="dataView">
    <thead>
      <tr>
        <th><s:text name="DiscoverTemplate.appFmt"/></th>
        <th><s:text name="DiscoverTemplate.file"/></th>
        <th><s:text name="DiscoverTemplate.type"/></th>
      </tr>
    </thead>
    <tbody>
        <s:sort comparator="#discoverTemplateFileComparator" source="templateFiles">
            <s:iterator var="file">
                <tr>
                    <td><c:out value="${file.applicationFormat.name}"/></td>
                    <td><c:out value="${file.templateFile}"/></td>
                    <td><fmt:message key="enums.TemplateFileType.${type}"/></td>
                </tr>
            </s:iterator>
        </s:sort>
    </tbody>
</table>
