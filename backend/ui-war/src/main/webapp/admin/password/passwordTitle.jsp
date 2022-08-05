<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:importAttribute name="titleProperty" scope="page"></tiles:importAttribute>
<title><ui:windowTitle attributeName="${titleProperty}"/></title>
