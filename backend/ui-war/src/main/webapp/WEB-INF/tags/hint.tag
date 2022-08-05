<%@ tag description="UI Hint" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<%@ attribute name="inline" required="false" %>

<c:if test="${inline}">
    <c:set var="additionalClass" value=" hintContainerInline"/>
</c:if>

<div class="hintContainer${additionalClass}">
    <div class="hintSign">
        <ui:toolTip>
            <jsp:doBody />
        </ui:toolTip>
    </div>
</div>
