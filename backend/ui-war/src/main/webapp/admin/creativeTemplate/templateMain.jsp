<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<display:table name="creativeTemplates" class="dataView" id="template">
  <display:setProperty name="basic.msg.empty_list" >
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>
  <display:column titleKey="CreativeTemplate.name">
      <ui:displayStatus displayStatus="${template.displayStatus}">
        <a href="<s:url action="%{#attr.moduleName}/%{#attr.entityName}/view"/>?id=${template.id}"><c:out value="${template.name}"/></a>
      </ui:displayStatus>
  </display:column>
</display:table>
