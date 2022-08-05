<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<tiles:useAttribute name="data" ignore="true"/>

<fmt:message var="notAvailable" key="notAvailable"/>
<td class="number">${empty data.dailyUsers ? notAvailable : ad:formatNumber(data.dailyUsers)}</td>
<td class="number">${empty data.monthlyUsers ? notAvailable : ad:formatNumber(data.monthlyUsers)}</td>
