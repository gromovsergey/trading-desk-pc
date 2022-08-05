<%@ tag description="Local css file link" %>

<%@ tag import="com.foros.model.VersionHelper" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="fileName" required="true" %>

<c:set var="folder"><%=application.getAttribute(VersionHelper.TIMESTAMP_PROPERTY)%></c:set>
<link rel="stylesheet" href="/styles/${folder}/${pageScope.fileName}" />