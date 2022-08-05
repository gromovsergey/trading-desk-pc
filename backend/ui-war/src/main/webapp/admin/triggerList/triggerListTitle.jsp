<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>
    <c:if test="${empty isViewPage || isViewPage == 'false'}">
        <ui:windowTitle attributeName="${entityName}.edit"/>
    </c:if>
    <c:if test="${not empty isViewPage && isViewPage == 'true'}">
        <ui:windowTitle attributeName="${entityName}"/>
    </c:if>
</title>
