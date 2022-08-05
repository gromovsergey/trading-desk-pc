<%@ page import="java.util.logging.Level" %>
<%@ page import="java.util.logging.Logger" %>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<% try { %>
<x:parse doc="${logRecord.actionDescription}" var="dom"/>
<x:set var="version" select="string($dom/child::*[position()=1]/@version)"/>

<c:choose>
    <c:when  test="${not empty version}">
        <c:import url="/WEB-INF/classes/com/foros/web/audit/audit-${version}.xsl" var="xslt"/>
    </c:when>
    <c:otherwise>
        <c:import url="/WEB-INF/classes/com/foros/web/audit/audit.xsl" var="xslt"/>
    </c:otherwise>
</c:choose>

<x:transform xml="${dom}" xslt="${xslt}">
    <x:param name="recordId" value="${logRecord.id}"/>
</x:transform>

<% } catch (Exception e) {
Logger logger = Logger.getLogger("/auditLog/logRecordDescription.jsp");
logger.log(Level.SEVERE, e.getMessage()); %>
<fmt:message key="nothing.found.to.display"/>
<% } %>
