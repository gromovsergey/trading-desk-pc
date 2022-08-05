<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>
<c:set var="countryName"><s:text name="global.country.%{entity.countryCode}.name"/></c:set>
<c:set var="countryCode" value="entity.countryCode"/>
<ui:windowTitleEntity entityName="Country" name="${countryName}" id="${countryCode}" isViewPage="${isViewPage}"/>
</title>
