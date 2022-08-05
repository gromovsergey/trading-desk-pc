<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ page import="com.foros.action.WeekScheduleSet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <ui:externalLibrary libName="jquery-css"/>
    <ui:externalLibrary libName="jquery"/>
    <ui:externalLibrary libName="jquery-ui"/>

    <ui:stylesheet fileName="common.css"/>

    <ui:javascript fileName="common.js"/>
    
    <ui:javascript fileName="jquery-custom.js"/>
    
    <title><fmt:message key="deliverySchedule.label" /></title>
</head>
<body>
<s:if test="outsideCampaignSchedule">
    <s:set var="conflictedKey" scope="page">${param.conflictedKey}</s:set>
</s:if>
<c:set var="wholeRangeSet"><%= WeekScheduleSet.WHOLE_RANGE_SET%></c:set>
<c:if test="${campaignScheduleSet != wholeRangeSet}">
    <s:set var="campaignNotRunningKey" scope="page">${param.notRunningKey}</s:set>
</c:if>
<s:set var="scheduleSet"><s:property value="scheduleSet"/></s:set>
<ui:weekRange id="scheduleSet" name="scheduleSet" readonly="true"
              editableRangesName="campaignScheduleSet"
              iconRunningKey="<%=request.getParameter("runningKey")%>"
              iconNotRunningKey="${campaignNotRunningKey}"
              iconAvailableKey="<%=request.getParameter("availableKey")%>"
              iconConflictedKey="${conflictedKey}"/>
</body>
</html>