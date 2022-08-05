<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="paramDate">
    <fmt:formatDate value="${exchange.effectiveDate}" type="both" timeStyle="short" dateStyle="short" timeZone="${_userSettings.timeZone}"/>
</c:set>
<c:set var="paramName">
    <s:text name="CurrencyExchange.h1OnDate" />
</c:set>
<title>
    <ui:windowTitle attributeName="${paramName} ${paramDate}" isSimpleText="true"/>
</title>
