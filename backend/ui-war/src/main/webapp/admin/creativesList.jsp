<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:pages pageSize="${searchParams.pageSize}"
          total="${searchParams.total}"
          selectedNumber="${searchParams.page}"
          visiblePagesCount="10"
          handler="goToPage"
          displayHeader="true"/>
<display:table name="creatives" class="dataView" id="creative">
  <display:setProperty name="basic.msg.empty_list" >
      <div class="wrapper">
          <fmt:message key="nothing.found.to.display"/>
      </div>
  </display:setProperty>

  <display:column titleKey="creative.search.lastModified" style="white-space:nowrap;">
       <fmt:formatDate value="${creative.version}" type="both" timeStyle="short" dateStyle="short"
                      timeZone="${_userSettings.timeZone}"/>
  </display:column>

  <display:column titleKey="creative.search.account">
      <a href="/admin/account/view.action?id=${creative.accountId}"><c:out value="${creative.accountName}"/></a>
  </display:column>

  <display:column titleKey="creative.search.creative">
      <a href="/admin/creative/view.action?id=${creative.id}"><c:out value="${creative.name}"/></a>
  </display:column>

  <display:column titleKey="creative.search.size">
    <ui:displayStatus displayStatus="${creative.sizeDisplayStatus}">
      <c:out value="${ad:localizeName(creative.sizeName)}"/>
    </ui:displayStatus>
  </display:column>

  <display:column titleKey="creative.search.template">
    <ui:displayStatus displayStatus="${creative.templateDisplayStatus}">
        <c:out value="${ad:localizeName(creative.templateName)}"/>
    </ui:displayStatus>
  </display:column>
</display:table>
<ui:pages pageSize="${searchParams.pageSize}"
          total="${searchParams.total}"
          selectedNumber="${searchParams.page}"
          visiblePagesCount="10"
          handler="goToPage"
          displayHeader="true"/>
