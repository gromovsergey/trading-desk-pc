<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<display:table name="entities" class="dataView" id="size">
    <display:setProperty name="basic.msg.empty_list" >
          <div class="wrapper">
              <fmt:message key="nothing.found.to.display"/>
          </div>
      </display:setProperty>
    <display:column titleKey="CreativeSize.name" style="width:30%;" sortProperty="name">
        <ui:displayStatus displayStatus="${size.displayStatus}">
            <a href="<s:url action="%{#attr.moduleName}/%{#attr.entityName}/view"/>?id=${size.id}"><c:out value="${ad:localizeName(size.localizableName)}"/></a>
        </ui:displayStatus>
    </display:column>
    <display:column property="protocolName"  titleKey="CreativeSize.protocolName"/>
    <display:column titleKey="CreativeSize.width" class="number">
        <fmt:formatNumber value="${size.width}" groupingUsed="true"/>
    </display:column>
    <display:column titleKey="CreativeSize.height" class="number">
        <fmt:formatNumber value="${size.height}" groupingUsed="true"/>
    </display:column>
</display:table>
