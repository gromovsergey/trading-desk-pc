<%@ tag description="UI Field" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<%@ attribute name="styleId" %>
<%@ attribute name="action" required="true" %>
<%@ attribute name="method" %>
<%@ attribute name="styleClass" %>

<c:if test="${not empty pageScope.styleId}">
  <c:set var="id_attr" value="id=\"${styleId}\""/>
</c:if>
<c:if test="${empty pageScope.styleId}">
  <c:set var="id_attr" value=""/>
</c:if>

<c:if test="${empty pageScope.method}">
  <c:set var="method" value="post"/>
</c:if>

<c:if test="${empty pageScope.styleClass}">
  <c:set var="styleClass" value=""/>
</c:if>

<form ${id_attr} action="${action}" method="${method}" class="${styleClass}">
    <input type="hidden" name="PWSToken" value="${sessionScope.PWSToken}"/>
    <jsp:doBody/>
</form>
