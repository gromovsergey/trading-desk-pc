<%@ tag description="UI DisplayStatus" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<%@ attribute name="id" %>
<%@ attribute name="cssClass" %>
<%@ attribute name="displayStatus" required="true" type="com.foros.model.DisplayStatus" %>
<%@ attribute name="testFlag" required="false" type="java.lang.Boolean" %>

<div id="${pageScope.id}" class="withDisplayStatus ${pageScope.cssClass}">
    <ui:colorStatus displayStatus="${pageScope.displayStatus}" testFlag="${pageScope.testFlag}"/>
    <jsp:doBody />
</div>
