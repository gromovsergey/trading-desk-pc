<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<s:bean var="creativeTemplateFileComparator" name="com.foros.util.comparator.CreativeTemplateFileComparator"/>

<c:set var="versionError">
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</c:set>

<s:if test="hasActionErrors() or not empty pageScope.versionError">
    <div class="wrapper">
        ${pageScope.versionError}
        <s:actionerror/>
    </div>
</s:if>

<c:set var="canUpdate" value="${ad:isPermitted('Template.update', entity)}"/>
<c:set var="isNotTextTemplate" value="${entity.defaultName ne 'Text'}"/>

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

        <ui:field labelKey="CreativeTemplate.status">
            <c:set var="descrText"><fmt:message key="enums.Status.${status}"/></c:set>
            <ui:statusButtonGroup
                entity="${entity}"
                restrictionEntity="Template"
                descriptionText="${pageScope.descrText}"
                deletePage="delete.action"
                undeletePage="undelete.action"
            />
        </ui:field>

        <s:if test="status.letter == 'D' && !sortedTemplateOptions.empty">
            <ui:field labelKey="CreativeTemplate.options">
                <c:set var="textVal">
                    <ad:commaWriter var="templateOption" items="${sortedTemplateOptions}"><c:out value="${ad:localizeName(templateOption.option.name)}"/></ad:commaWriter>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
        </s:if>

        <s:if test="!categories.empty">
            <ui:field labelKey="creative.categories.visual">
                <c:set var="textVal">
                    <ad:commaWriter items="${entity.categories}" var="category"><c:out value="${ad:localizeName(category.name)}" escapeXml="false"/></ad:commaWriter>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
        </s:if>

        <ui:field labelKey="CreativeTemplate.expandable.label">
            <s:set var="expandableValue" value="getText('CreativeTemplate.expandable.'+expandable)"></s:set>
            <ui:text text="${pageScope.expandableValue}"/>
        </ui:field>

    </ui:fieldGroup>
</ui:section>

<ui:header styleClass="level2">
  <h2><s:text name="CreativeTemplate.options"/></h2>
  <c:if test="${ad:isPermitted('Template.updateOptions', entity)}">
    <ui:button message="OptionGroup.create" href="/admin/OptionGroup/new.action?templateId=${id}" />
    <c:if test="${not empty optionGroups}">
      <ui:button message="Option.create" href="/admin/Option/new.action?templateId=${id}" />
    </c:if>
  </c:if>
</ui:header>
<c:choose>
  <c:when test="${not empty optionGroups}">
    <table class="dataView">
        <thead>
            <tr>
                <th><fmt:message key="CreativeTemplate.options.group.type"/></th>
                <th><fmt:message key="CreativeTemplate.options.group.name"/></th>
                <th><fmt:message key="CreativeTemplate.options.name"/></th>
                <th><fmt:message key="CreativeTemplate.options.group.token"/></th>
                <th><fmt:message key="CreativeTemplate.options.group.defaultValue"/></th>
            </tr>
        </thead>
        <tbody>
            <c:set var="advertiserLabel"><fmt:message key="CreativeSize.options.group.type.Advertiser"/></c:set>
            <ui:allOptionsView label="${advertiserLabel}" collection="${advertiserOptionGroups}"/>

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

<c:set var="canUpdateFilesMatch" value="${ad:isPermitted('Template.updateFilesMatch', entity)}"/>

<ui:header styleClass="level2">
    <h2><s:text name="CreativeTemplate.filesMatch"/></h2>
    <c:if test="${canUpdateFilesMatch}">
        <s:url var="newFileUrl" action="admin/CreativeTemplateFile/new">
            <s:param name="templateId" value="entity.id"/>
        </s:url>
        <ui:button message="form.addNew" href="${newFileUrl}" />
    </c:if>
</ui:header>

<c:choose>
    <c:when test="${not empty templateFiles}">
            <table class="dataView">
              <thead>
                <tr>
                  <th><s:text name="CreativeTemplate.crSize"/></th>
                  <th><s:text name="CreativeTemplate.appFmt"/></th>
                  <th><s:text name="CreativeTemplate.file"/></th>
                  <th><s:text name="CreativeTemplate.trImpr"/></th>
                  <th><s:text name="CreativeTemplate.type"/></th>
                  <c:if test="${canUpdateFilesMatch}">
                    <th><s:text name="CreativeTemplate.Action"/></th>
                  </c:if>
                </tr>
              </thead>
              <tbody>
              <s:sort comparator="#creativeTemplateFileComparator" source="templateFiles">
                  <s:iterator var="file" status="rowstatus" >
                <s:if test="#rowstatus.odd == true">
                  <tr class="even">
                </s:if>
                <s:else>
                  <tr>
                </s:else>
                <td><a href="/admin/CreativeSize/view.action?id=${file.creativeSize.id}"><c:out value="${ad:localizeName(file.creativeSize.name)}"/></a></td>
                <td><c:out value="${file.applicationFormat.name}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${canUpdateFilesMatch}">
                            <s:url var="editFileUrl" action="admin/CreativeTemplateFile/edit">
                                <s:param name="id" value="id"/>
                                <s:param name="templateId" value="entity.id"/>
                            </s:url>
                            <a href="${editFileUrl}"><c:out value="${file.templateFile}"/></a>
                        </c:when>
                        <c:otherwise>
                            <c:out value="${file.templateFile}"/>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                  <s:if test="#file.impressionsTrackFlag"><fmt:message key="yes"/></s:if>
                  <s:else><fmt:message key="no"/></s:else>
                </td>
                <td><fmt:message key="enums.TemplateFileType.${type}"/></td>
                <c:if test="${canUpdateFilesMatch}">
                    <td>
                        <s:url var="delFileUrl" action="admin/CreativeTemplateFile/delete">
                            <s:param name="id" value="id"/>
                            <s:param name="templateId" value="entity.id"/>
                        </s:url>
                        <ui:postButton message="form.remove"  href="${delFileUrl}" onclick="if (!confirm('${ad:formatMessage('confirmDelete')}')) {return false;}" />
                        <s:fielderror><s:param value="'creativeTemplate.id.' + id"/></s:fielderror>
                    </td>
                </c:if>
                </tr>
              </s:iterator>
              </s:sort>
              </tbody>
            </table>
    </c:when>
    <c:otherwise>
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </c:otherwise>
</c:choose>
