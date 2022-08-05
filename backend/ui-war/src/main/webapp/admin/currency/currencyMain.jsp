<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<display:table name="currencies" class="dataView" sort="external" defaultsort="1" id="currency">
  <display:setProperty name="basic.msg.empty_list" >
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>
  <display:column titleKey="Currency.name">
      <s:set name="currencyName" value="getCurrencyName(#attr.currency.currencyCode)"/>
      <c:choose>
          <c:when test="${ad:isPermitted0('Currency.update')}">
              <a href="<s:url action="%{#attr.moduleName}/%{#attr.entityName}/edit"/>?id=${currency.id}"><c:out value="${currencyName}"/></a>
          </c:when>
          <c:otherwise>
              <c:out value="${currencyName}"/>
          </c:otherwise>
      </c:choose>
  </display:column>
  <display:column titleKey="Currency.symbol">
      <s:property value="getCurrencySymbol(#attr.currency.currencyCode)"/>
  </display:column>
  <display:column property="currencyCode" titleKey="Currency.currencyCode"/>
  <display:column property="fractionDigits" titleKey="Currency.fractionDigits" class="number"/>
  <display:column titleKey="Currency.rate" class="number">
    <fmt:formatNumber value="${currency.rate}" maxFractionDigits="5" groupingUsed="true"/>
  </display:column>
  <display:column titleKey="Currency.source">
    <fmt:message key="${currency.source.resourceKey}"/>
  </display:column>
  <display:column property="lastUpdated" titleKey="Currency.lastUpdated"/>
</display:table>
