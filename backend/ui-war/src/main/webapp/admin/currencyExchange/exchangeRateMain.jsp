<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>


<ui:header>    
    <ui:pageHeadingByTitle /> 
    <c:if test="${ad:isPermitted0('CurrencyExchange.update')}">
    <s:url action="%{#attr.moduleName}/%{#attr.entityName}/edit" var="url"/>
    <ui:button message="form.edit" href="${url}" />
    </c:if>
    <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=6" />
</ui:header>

<table class="dataView">
  <thead>
    <tr>
      <th><s:text name="CurrencyExchange.currency"/></th>
      <th><s:text name="CurrencyExchange.currencyRate"/></th>
    </tr>
  </thead>
  <tbody>
    <s:if test="!currencyExchangeRates.empty">
      <s:iterator value="currencyExchangeRates" var="exchRate">
        <tr>
          <td>
            <s:property value="name" escape="true"/> (<s:property value="symbol" escape="true"/>)
          </td>
          <td class="number">
            <fmt:formatNumber value="${exchRate.rate}" groupingUsed="true" maxFractionDigits="5"/>
          </td>
        </tr>
      </s:iterator>
    </s:if>
  </tbody>
</table>
