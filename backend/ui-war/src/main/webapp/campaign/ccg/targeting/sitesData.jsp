<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<c:set var="canViewSite" value="${ad:isPermitted0('PublisherEntity.view')}"/>
<c:forEach var="site" items="${groupSites}" varStatus="row">
    <tr>
        
        <c:if test="${row.first}"><td style="border-top:none;" rowspan="${groupSites.size()}"></td></c:if>
        <td class="ccg_target">
        <span class="simpleText">
            <c:if test="${canViewSite}"><a href="/admin/site/view.action?id=${site.id}"></c:if>
            <c:out value="${site.name}"/>
            <c:if test="${canViewSite}"></a></c:if>
        </span>
        </td>
        <c:set var="stats" value="${targetingStats.sites[site.id]}"/>
        <tiles:insertTemplate template="/campaign/ccg/targeting/statsData.jsp">
            <tiles:putAttribute name="data" value="${stats}"/>
        </tiles:insertTemplate>
    </tr>
</c:forEach>
