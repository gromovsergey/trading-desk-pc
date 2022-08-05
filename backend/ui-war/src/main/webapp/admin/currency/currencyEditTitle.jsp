<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<c:set var="paramName"><s:if test="currencyCode != null"><s:text name="global.currency.%{currencyCode}.name"/></s:if></c:set>
<title>
<ui:windowTitleEntity entityName="Currency" name="${paramName}" id="${id}"/>
</title>

