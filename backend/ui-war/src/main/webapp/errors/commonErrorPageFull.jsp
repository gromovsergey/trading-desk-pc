<%@page import="com.foros.model.VersionHelper"%>
<%@ page import="com.foros.util.ExceptionUtil" %>
<%@ page contentType="text/html" %>
<%@ page isErrorPage="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="java.net.URLEncoder"%>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head profile="http://www.w3.org/2005/10/profile">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Error page - <fmt:message key="systemTitle"/></title>
    <link rel="icon" href="/images/logo.png" />
    <link rel="shortcut icon" href="/images/logo.png" />
    <ui:stylesheet fileName="common.css" />
    <c:if test="${ad:isActiveLocale('ja') || ad:isActiveLocale('zh')}">
        <ui:stylesheet fileName="hieroglyph.css"/>
    </c:if>
    <ui:externalLibrary libName="jquery-css"/>
    <ui:externalLibrary libName="jquery"/>
    <ui:externalLibrary libName="jquery-ui"/>
    
    <ui:javascript fileName="jquery-custom.js"/>
    <ui:javascript fileName="common.js"/>
    
    <script type="text/javascript">
        $.localize('form.all', '<fmt:message key="form.all"/>');
        $.localize('form.select.wait', '<fmt:message key="form.select.wait"/>');
        $.localize('form.select.none', '<fmt:message key="form.select.none"/>');
        $.localize('form.select.pleaseSelect', '<fmt:message key="form.select.pleaseSelect"/>');
        $.localize('error.ajax', '<fmt:message key="error.ajax"/>');
        $.localize('error.ajax.unauthorized', '<fmt:message key="error.ajax.unauthorized"/>');
        $.localize('form.select.notSpecified', '<fmt:message key="form.select.notSpecified"/>');
        $.localize('report.invalid.output.columns', '<fmt:message key="report.invalid.output.columns"/>');
        $.localize('report.invalid.metrics.columns', '<fmt:message key="report.invalid.metrics.columns"/>');
        $.localize('report.account.required', '<fmt:message key="report.account.required"/>');
        $.localize('selectTip.selected', '<fmt:message key="selectTip.selected"/>');
        $.localize('report.output.field.agency', '<fmt:message key="report.output.field.agency"/>');
        $.localize('report.output.field.adv', '<fmt:message key="report.output.field.adv"/>');
        $.localize('dynamicResources.localize', '<fmt:message key="dynamicResources.localize"/>');
        $.localize('channel.visibility.PUB', '<fmt:message key="channel.visibility.PUB"/>');
        $.localize('channel.visibility.PRI', '<fmt:message key="channel.visibility.PRI"/>');
        $.localize('channel.visibility.CMP', '<fmt:message key="channel.visibility.CMP"/>');
        $.environment('ui.user.account.id', '${_principal.accountId}');
    </script>
</head>

<body>
<c:set var="LoggedUserName"><ad:userName userId="${_principal.userId}" escapeHTML="false" /></c:set>
<table id="root">
<c:choose>
<c:when test="${empty isReport or isReport == 'false'}">
    <tr id="header">
        <td class="rootCell">
            <div id="headContainer">
                <div id="applicationLogo">Target RTB</div>
                <div id="loginZone">
                    <span id="userLogin"><ui:text text="${LoggedUserName}" subClass="entityName"/></span>
                    <span class="delimiter">|</span>
                    <span id="accountName"><%@ include file="../templates/switchAccountUI.jspf"%></span>
                    <span class="delimiter">|</span>
                    <c:if test="${not _principal.anonymous}">
                        <a id="logout" target="_top" href="<s:url value="/login/j_spring_security_logout"/>"><fmt:message key="form.logout"/></a>
                        <span class="delimiter">|</span>
                    </c:if>
                    <s:if test="isInternal()">
                        <form method="post" id="quick_search_form"><input type="search" id="quick_search" class="gray" placeholder="<fmt:message key="quicksearch.search"/>" autocomplete="off" onclick="$(this).removeClass('gray').val('');" onblur="$(this).removeClass('gray').val('');" onfocus="$(this).removeClass('gray').val('');" /><img src="/images/wait-animation-small.gif" id="quick_search_preloader" /></form>
                        <span class="delimiter">|</span>
                    </s:if>
                </div>
        
                <c:set var="activeMenuItemKey" value=""/>
    
                <c:if test="${ad:isAdvertiser() || ad:isAgency()}">
                    <%@ include file="/menu/advertiserMenu.jsp"%>
                </c:if>
                <c:if test="${ad:isPublisher()}">
                    <%@ include file="/menu/publisherMenu.jsp"%>
                </c:if>
                <c:if test="${ad:isIsp()}">
                    <%@ include file="/menu/ispMenu.jsp"%>
                </c:if>
                <c:if test="${ad:isCmp()}">
                    <%@ include file="/menu/cmpMenu.jsp"%>
                </c:if>
                <c:if test="${ad:isInternal()}">
                    <%@ include file="/menu/internalMenu.jsp"%>
                </c:if>
            </div>
        </td>
    </tr>
    <c:set var="errOnPage">
        <fmt:message key="page.title.errorPage"/>
    </c:set>
    <c:if test="${not empty pageScope.errOnPage}">
        <tr id="breadCrumbs">
            <td class="rootCell">
                <div class="contentBody">
                    ${pageScope.errOnPage}
                </div>
            </td>
        </tr>
    </c:if>
    <tr id="content">
        <td class="rootCell">
            <div class="contentBody">
                <%@ include file="commonErrorPage.jsp"%>
            </div>
        </td>
    </tr>
    <c:if test="${not _principal.anonymous}">
        <c:if test="${!ad:isInternal() and _method == 'GET'}">
            <tr>
                <td>
                    <div align="left">
                        <span>
                            <c:if test="<%=ExceptionUtil.getCause(pageContext.getException(), SecurityException.class) != null%>">
                                <c:import url="${_context}/switchContextError.action">
                                    <c:param name="requestedURL" value="${_url}"/>
                                </c:import>
                            </c:if>
                        </span>
                    </div>
                </td>
            </tr>
        </c:if>
    </c:if>
    <tr id="footer">
        <td>
        <div id="appVersion">
            <c:if test="${ad:isInternal()}">
                <c:set var="versionName"><fmt:message key="form.version"/></c:set>
                <c:set var="versionValue"><%=application.getAttribute(VersionHelper.VERSION_PROPERTY)%></c:set>
                <c:out value="${versionName} ${versionValue}"/>
            </c:if>
            <c:if test="${ad:isAdvertiser() ||
                        ad:isPublisher() ||
                        ad:isIsp() ||
                        ad:isAgency() ||
                        ad:isCmp()}">
                            <a href="${_context}/TermsOfUse.action"><fmt:message key="TermsOfUse.termsOfUse"/></a>
                            <a href="${_context}/Contacts.action"><fmt:message key="contacts.contactUs"/></a>
            </c:if>
        </div>
        <div id="copyright">
            <fmt:message key="form.copyright"/>
        </div>
        </td>
    </tr>
</c:when>
<c:otherwise>
    <div id="page">
        <h1>
            Error page
        </h1>
        <%@ include file="commonErrorPage.jsp"%>
    </div>
</c:otherwise>
</c:choose>
</table>
</body>
</html>
