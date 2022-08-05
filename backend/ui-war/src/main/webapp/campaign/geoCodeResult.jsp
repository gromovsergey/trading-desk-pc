<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:if test="${fn:length(addresses) > 0}">
<table id="addresses" class="dataView" width="100%">
    <thead>
    <tr>
        <td><fmt:message key='channel.address.locations'/></td>
        <td><fmt:message key='channel.address.category'/></td>
        <td><fmt:message key='channel.address.latlon'/></td>
        <td><fmt:message key='channel.address.actions'/></td>
    </tr>
    </th>
    </thead>
    <c:forEach items="${addresses}" var="address" varStatus="status">
        <tr id="addressRow__${status.index}" class="geoChannelsRow">
            <td id="address__${status.index}"><c:out value="${address.address}"/></td>
            <td>
                Address
            </td>
            <td id="latlon__${status.index}">
                <fmt:formatNumber value="${address.latitude}" minFractionDigits="4" maxFractionDigits="4"/>&nbsp;&nbsp;<fmt:formatNumber value="${address.longitude}" minFractionDigits="4" maxFractionDigits="4"/>&nbsp;
                <a href="#" onclick="openMap(${address.latitude}, ${address.longitude})"><fmt:message key="channel.address.map"/></a>
            </td>
            <td>
                <ui:button message="form.add" href="#" onclick="return addAddress('${status.index}');" />
            </td>
        </tr>
    </c:forEach>
</table>
</c:if>
<c:if test="${fn:length(addresses) == 0}">
    <span><fmt:message key='channel.address.notFound'/></span>
</c:if>