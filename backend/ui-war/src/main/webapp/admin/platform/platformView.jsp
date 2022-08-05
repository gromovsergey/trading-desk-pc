<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>


<ui:header>
    <ui:pageHeadingByTitle />
</ui:header>

<ui:errorsBlock>
    <s:actionerror/>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<ui:section>
    <ui:fieldGroup>
      <ui:field labelKey="platform.type">
          ${model.type}
      </ui:field>
    </ui:fieldGroup>
</ui:section>

<ui:header styleClass="level2">
    <h2><fmt:message key="platform.detectors"/></h2>
</ui:header>

<table id="row" class="dataView">
    <thead>
    <tr>
        <th><fmt:message key="platform.priority"/> </th>
        <th><fmt:message key="platform.matchMarker"/> </th>
        <th><fmt:message key="platform.matchRegexp"/> </th>
        <th><fmt:message key="platform.outputRegexp"/> </th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${platformDetectors}" var="detector">
        <tr>
            <td>${detector.priority}</td>
            <td>${detector.matchMarker}</td>
            <td>${detector.matchRegexp}</td>
            <td>${detector.outputRegexp}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

