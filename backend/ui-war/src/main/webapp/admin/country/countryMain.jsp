<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<display:table name="entities" class="dataView" id="rowEntity">
  <display:setProperty name="basic.msg.empty_list" >
    <div class="wrapper">
        <fmt:message key="nothing.found.to.display"/>
    </div>
  </display:setProperty>
  <display:column titleKey="Country.entityName">
      <s:set var="id" value="%{#attr.rowEntity.id}"/>
      <c:choose>
          <c:when test="${ad:isPermitted0('Country.view')}">
              <s:url var="url" action="%{#attr.moduleName}/%{#attr.entityName}/view">
                  <s:param name="id" value="#id"/>
              </s:url>
              <s:a href="%{url}"><s:text name="global.country.%{#id}.name"/></s:a>
          </c:when>
          <c:otherwise>
              <s:text name="global.country.%{#id}.name"/>
          </c:otherwise>
      </c:choose>
  </display:column>
  <display:column property="id" titleKey="Country.countryCode"/>
  <display:column property="currency.name" titleKey="Country.currency"/>
</display:table>
