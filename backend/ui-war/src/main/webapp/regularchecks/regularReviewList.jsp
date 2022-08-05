<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:pages pageSize="${searchParams.pageSize}"
    total="${searchParams.total}" selectedNumber="${searchParams.page}"
    visiblePagesCount="10" handler="goToPage" displayHeader="true" />
<form id="searchParams">
    <s:hidden name="searchParams.pageSize"/>
    <s:hidden name="searchParams.total"/>
    <input type="hidden" name="PWSToken" value="${sessionScope.PWSToken}"/>
</form>
<display:table name="reviewEntities" class="dataView" id="entity">
  <display:setProperty name="basic.msg.empty_list" >
      <div class="wrapper">
          <fmt:message key="nothing.found.to.display"/>
      </div>
  </display:setProperty>

  <display:column titleKey="Account.entityName">
      <c:out value="${entity.accountName}"/>
  </display:column>
 
<s:if test="entityType == 'campaigns'">
  <display:column titleKey="Advertiser.entityName">
      <c:out value="${entity.advertiserName}"/>
  </display:column>

  <display:column titleKey="enums.ObjectType.Campaign">
      <c:out value="${entity.campaignName}"/>
  </display:column>

  <display:column titleKey="enums.ObjectType.CampaignCreativeGroup">
      <a href="${_context}/campaign/group/view.action?id=${entity.entityId}"><c:out value="${entity.entityName}"/></a>
  </display:column>

  <display:column titleKey="checks.checkStatus.due">
      ${entity.dueCaption}
  </display:column>
</s:if>
<s:else>
  <display:column titleKey="enums.ObjectType.Channel">
      <a href="${_context}/channel/view.action?id=${entity.entityId}"><c:out value="${entity.entityName}"/></a>
  </display:column>

  <display:column titleKey="checks.checkStatus.due">
      ${entity.dueCaption}
  </display:column>
</s:else>
</display:table>
<ui:pages pageSize="${searchParams.pageSize}"
          total="${searchParams.total}"
          selectedNumber="${searchParams.page}"
          visiblePagesCount="10"
          handler="goToPage"
          displayHeader="true"/>
