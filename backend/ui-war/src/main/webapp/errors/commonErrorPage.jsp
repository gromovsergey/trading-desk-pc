<%@ page import="com.foros.util.ErrorPageUtil" %>
<%@ page contentType="text/html"%>
<%@ page isErrorPage="true" %>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<span class="errors">
    <c:out value="<%=ErrorPageUtil.getUserFriendlyMessage(pageContext.getException())%>" escapeXml="true"/>
</span>
