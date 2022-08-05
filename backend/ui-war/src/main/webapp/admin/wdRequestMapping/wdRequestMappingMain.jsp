<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="wdRequestMappingBean" scope="request" class="com.foros.model.admin.WDRequestMapping"/>
<c:set var="permissionEdit" value="${ad:isPermitted0('WDRequestMapping.update')}"/>
<display:table name="entities" class="dataView" id="wdRequestMapping">
  <display:setProperty name="basic.msg.empty_list" >
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>
  <display:column titleKey="WDRequestMapping.name" style="width:30%;">
    <c:if test="${permissionEdit}">
      <a href="<s:url action="%{#attr.moduleName}/%{#attr.entityName}/edit"/>?id=${wdRequestMapping.id}"><c:out value="${wdRequestMapping.name}"/></a>
    </c:if>
    <c:if test="${!permissionEdit}">
      <c:out value="${wdRequestMapping.name}"/>
    </c:if>
  </display:column>
  <display:column titleKey="WDRequestMapping.description"><c:out value="${wdRequestMapping.description}"/></display:column>
  <display:column titleKey="WDRequestMapping.actions">
    <c:if test="${permissionEdit}">
    <jsp:setProperty name="wdRequestMappingBean" property="id" value="${wdRequestMapping.id}"/>
    <ui:postButton message="form.delete" href="delete.action" entityId="${wdRequestMapping.id}"
        onclick="if (!confirm('${ad:formatMessage('confirmDelete')}')) {return false;}"/>
    </c:if>
  </display:column>
</display:table>
