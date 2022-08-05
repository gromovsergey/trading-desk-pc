<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<%@ attribute name="styleClass" required="false" %>
<%@ attribute name="tipKey" %>
<%@ attribute name="tipText" %>

<div class="pageHeading ${styleClass}">
    <jsp:doBody />
</div>
<div class="fixing"></div>
