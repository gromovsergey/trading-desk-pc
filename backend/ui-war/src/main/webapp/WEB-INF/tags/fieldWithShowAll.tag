<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<%@ attribute name="labelKey" required="true" %>
<%@ attribute name="shortText" required="true" %>
<%@ attribute name="fullText" required="true" %>
<%@ attribute name="style" required="false" type="java.lang.String"%>

<c:if test="${not empty pageScope.shortText}">
    <ui:field labelKey="${pageScope.labelKey}">
        <div class="showAllText" <c:if test="${not empty pageScope.style}">style="${pageScope.style}"</c:if>>
        <ui:text text="${pageScope.shortText}"/>
        <c:if test="${pageScope.shortText != pageScope.fullText}">
            <ui:button message="form.showAll" onclick="$(this).parent('.showAllText').hide().next('.showAllText').show();" type="link"/>
            </div>
            <div class="showAllText hide" <c:if test="${not empty pageScope.style}">style="${pageScope.style}"</c:if>>
                <ui:text text="${pageScope.fullText}"/>
                <ui:button message="form.hide" onclick="$(this).parent('.showAllText').hide().prev('.showAllText').show();" type="link"/>
        </c:if>
        </div>
    </ui:field>
</c:if>