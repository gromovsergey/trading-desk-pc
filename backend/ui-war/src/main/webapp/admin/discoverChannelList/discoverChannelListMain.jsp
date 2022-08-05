<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted0('DiscoverChannel.create')}">
        <a class="button" href="/admin/DiscoverChannelList/create.action"><fmt:message key="form.createNew"/></a>
    </c:if>
</ui:header>

<%@ include file="discoverSearchForm.jsp" %>
