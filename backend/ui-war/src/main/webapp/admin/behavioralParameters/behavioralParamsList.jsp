<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted0('BehavioralParams.create')}">
        <c:set var="buttHref">
            <s:url action="admin/behavioralParameters/new"/>
        </c:set>
        <ui:button message="form.createNew" href="${buttHref}" />
    </c:if>
</ui:header>

<display:table name="behavioralParams" class="dataView" id="behavioralParam">
    <display:setProperty name="basic.msg.empty_list">
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>

    <display:column titleKey="channel.search.name">
        <a href="<s:url action="admin/behavioralParameters/view">
                   <s:param name="id" value="%{#attr.behavioralParam.id}"/>
                 </s:url>"><c:out value="${behavioralParam.name}"/></a>
    </display:column>
</display:table>
