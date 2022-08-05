<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:pageHeadingByTitle/>
<s:property value="value" escape="false"/>
<input type="hidden"/>