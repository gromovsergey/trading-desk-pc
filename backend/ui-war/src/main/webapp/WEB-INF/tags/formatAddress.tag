<%@tag language="java" body-content="empty" description="Renders address view" %>
<%@tag import="com.foros.web.taglib.AddressFormatter"%>
<%@ tag import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ attribute name="value" required="true" type="com.foros.model.security.AccountAddress"%>
<%@ attribute name="addressFields" required="true" type="java.util.List"%>
<c:if test="${pageScope.value != null && pageScope.addressFields != null}">
    <c:set var="formattedAddress" value="<%=AddressFormatter.format(value, (List)jspContext.getAttribute("addressFields"))%>"/>
    <c:out value="${formattedAddress}"/>
</c:if>
