<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ page import="com.foros.model.VersionHelper" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="com.foros.security.currentuser.CurrentUserSettingsHolder" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DateFormat" %>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<tiles:importAttribute scope="request" name="moduleName"/>
<tiles:importAttribute ignore="true" scope="request" name="entityName"/>
<tiles:importAttribute ignore="true" scope="request" name="simpleTitle"/>
<tiles:importAttribute ignore="true" scope="request" name="isAuditLogPage"/>
<tiles:importAttribute ignore="true" scope="request" name="isViewPage"/>
<tiles:importAttribute ignore="true" scope="request" name="isCreatePage"/>
<tiles:importAttribute ignore="true" scope="request" name="titleProperty"/>
<tiles:importAttribute ignore="true" scope="request" name="systemTitle"/>
<tiles:importAttribute ignore="true" scope="request" name="createEntityRestriction"/>
<tiles:importAttribute ignore="true" scope="request" name="updateEntityRestriction"/>
<tiles:importAttribute ignore="true" scope="request" name="onEntityRestriction"/>
<tiles:importAttribute ignore="true" scope="request" name="taskTitle"/>
<tiles:importAttribute ignore="true" scope="request" name="hasAuditLog"/>
<tiles:importAttribute ignore="true" scope="request" name="isReport"/>
<tiles:importAttribute ignore="true" scope="request" name="isTagPreview"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head profile="http://www.w3.org/2005/10/profile">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <tiles:insertAttribute name="title">
        <tiles:putAttribute name="systemTitle" type="string" value="${systemTitle}" />
        <tiles:putAttribute name="isViewPage" type="string" value="${isViewPage}" />
        <c:if test="${not empty entityName}">
            <tiles:putAttribute name="entityName" type="string" value="${entityName}" />
        </c:if>
        <c:if test="${not empty titleProperty}">
            <tiles:putAttribute name="titleProperty" type="string" value="${titleProperty}" />
        </c:if>
    </tiles:insertAttribute>
    <link rel="icon" href="/images/logo.png" />
    <link rel="shortcut icon" href="/images/logo.png" />

    <ui:stylesheet fileName="common.css"/>
    <ui:externalLibrary libName="jquery-css"/>
    <c:if test="${ad:isActiveLocale('ja') || ad:isActiveLocale('zh')}">
        <ui:stylesheet fileName="hieroglyph.css"/>
    </c:if>
    <ui:externalLibrary libName="jquery"/>
    <ui:externalLibrary libName="jquery-ui"/>
    
    <ui:browserDetect type="safari">
        <ui:stylesheet fileName="safari.css"/>
    </ui:browserDetect>
    <ui:browserDetect type="ie">
        <ui:stylesheet fileName="ie.css"/>
    </ui:browserDetect>
    <ui:browserDetect type="firefox">
        <ui:stylesheet fileName="ff.css"/>
    </ui:browserDetect>
    
    <ui:javascript fileName="jquery-custom.js"/>
    <ui:javascript fileName="common.js"/>
    
    <% 
        Locale locale = CurrentUserSettingsHolder.getLocale();
        SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
    
    java.text.DecimalFormat decimalCountryLang = (java.text.DecimalFormat) java.text.NumberFormat.getNumberInstance(locale);
    java.text.DecimalFormatSymbols decimalFormatSymbols = decimalCountryLang.getDecimalFormatSymbols();
    
    %>
        
    <c:set var="folder"><%=application.getAttribute(VersionHelper.TIMESTAMP_PROPERTY)%></c:set>
    <c:set var="ulanguage" value="<%=locale.getLanguage()%>" />
    <script type="text/javascript" src="/scripts/${folder}/localization/ui.datepicker-${ulanguage}.js"></script>
    
    <script type="text/javascript">
        UI.Localization.setDecimalSeparator("<%=decimalFormatSymbols.getDecimalSeparator()%>");
        UI.Localization.setGroupingSeparator("<%=decimalFormatSymbols.getGroupingSeparator()%>");
    
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
        $.localize('confirmCopy', '<fmt:message key="confirmCopy"/>');
        $.environment('ui.user.account.id', '${_principal.accountId}');
        $.environment('dateFormat', '<%= StringEscapeUtils.escapeJavaScript(sdf.toPattern())%>');
        $.environment('datepickerDateFormat', '<%= StringEscapeUtils.escapeJavaScript(sdf.toPattern())%>'
            .replace(/(^|[^y])yy([^y]|$)/g, '$1y$2') // replaces 'yy' to 'y'
            .replace(/(^|[^y])y{4}([^y]|$)/g, '$1yy$2') // replaces 'yyyy' to 'yy'
            .replace(/(^|[^M])M([^M]|$)/g, '$1m$2') // replaces 'M' to 'm'
            .replace(/(^|[^M])MM([^M]|$)/g, '$1mm$2') // replaces 'MM' to 'm'
        );

        $.ajaxSetup({ traditional: true });

        $(function(){
            $.datepicker.setDefaults({
                dateFormat: $.environment('datepickerDateFormat'),
                changeMonth: true,
                changeYear: true,
                showOn: 'both',
                numberOfMonths: 2,
                buttonImageOnly: true,
                yearRange:'c-5:c+1',
                shortYearCutoff: '+20',
                buttonImage: '/images/calendar.gif'
            });
            
            UI.Hint.initHints();

            <ui:browserDetect type="safari">
            $(document).on('submit', 'form', function(){
                var action = $(this).attr('action');
                
                if(action){
                    var newVal = new Date().getTime();
                    
                    if(action.indexOf('rand') == -1){
                        action += (action.indexOf('?') == -1 ? '?' : '&');
                        action += 'rand=' + newVal;
                    }else{
                        action = action.replace(/(rand=)\d+/, '$1' + newVal);
                    }
                    $(this).attr({action : action});
                }
            });
            UI.Util.preventSafariDblClk();
            </ui:browserDetect>
            <ui:browserDetect type="ie">
            $('.dataView th:last-child').css({borderRightColor : '#666'});
            $('.filterZone label:last-child').css({marginBottom : 0});
            $('table.filestable th:last-child').css({width:1, paddingRight:7});
            $('table.grouping.separated td > *:last-child').css({marginRight:0});
            $('table.grouping.separated td:last-child').css({padding:0});
            </ui:browserDetect>
            
            $(document).ajaxStop(function(){
                UI.Hint.initHints();
            });
        });
    </script>
    
    <tiles:insertAttribute name="head" ignore="true"/>
  </head>
  <body>

  <s:if test="isInternal()">
    <dl id="quick_search_data" class="b-qsearchresults">
    </dl>
  </s:if>

  <iframe id="historyFrame" src="/blank.htm" style="display:none"></iframe>
  <c:set var="LoggedUserName"><ad:userName userId="${_principal.userId}" escapeHTML="false" /></c:set>
    <table id="root">
    <c:choose>
    <c:when test="${(empty isReport or isReport == 'false')
                    and (empty isTagPreview or isTagPreview == 'false')
                    and (param.standalone != 'true')}">
        <tr id="header">
            <td class="rootCell">
                <div id="headContainer">
                    <div id="applicationLogo">Target RTB</div>
                    <div id="loginZone">
                        <span id="userLogin"><ui:text text="${LoggedUserName}" subClass="entityName"/></span>

                        <span class="delimiter">|</span>
                        <span id="accountName"><%@ include file="switchAccountUI.jspf"%></span>
                        <span class="delimiter">|</span>
                        <c:if test="${not _principal.anonymous}">
                            <a id="logout" target="_top" href="<s:url value="/login/j_spring_security_logout"/>"><fmt:message key="form.logout"/></a>
                            <span class="delimiter">|</span>
                        </c:if>

                        <s:if test="isInternal()">
                            <form method="post" id="quick_search_form"><input type="search" id="quick_search" class="gray" placeholder="<fmt:message key="quicksearch.search"/>" autocomplete="off" onclick="$(this).removeClass('gray').val('');" onblur="$(this).removeClass('gray').val('');" onfocus="$(this).removeClass('gray').val('');" /><img src="/images/wait-animation-small.gif" id="quick_search_preloader" /></form>
                        </s:if>
                        <c:if test="${not empty _user.userCredential.lastLoginDate}">
                            <div align="right">
                                <table><tr><td>
                                    <fmt:message key="lastLogin"/>&nbsp;<fmt:formatDate value="${_user.userCredential.lastLoginDate}" type="both" timeStyle="short" dateStyle="short" timeZone="${_userSettings.timeZone}"/>&nbsp;<c:out value="${ad:resolveGlobal('timezone', _userSettings.timeZone.ID, true)}"/>&nbsp;<fmt:message key="lastLogin.from"/>&nbsp;${_user.userCredential.lastLoginIP}
                                </td></tr></table>
                            </div>
                        </c:if>
                    </div>
                    <tiles:importAttribute scope="request" name="activeMenuItemKey" ignore="true"/>
                    <tiles:importAttribute scope="request" name="contextName" ignore="true"/>
                    <tiles:insertAttribute name="menu" ignore="true"/>
                    <c:if test="${activeMenuItemKey != 'global.menu.reports'}">
                        <script type="text/javascript">
                            $(function() {
                                $(document).on('submit.preventDoubleSubmit', 'form', function(e){
                                    if ( $(this).data('preventDoubleSubmit') !== false ) {
                                        if( $(this).data('disabledButts') ) {
                                            e.preventDefault();
                                        } else {
                                            UI.Util.disableButtons( $(this) );
                                        }
                                    }
                                });
                            });
                        </script>
                    </c:if>
                </div>
            </td>
        </tr>
        <tiles:insertAttribute name="accountContextMenu" ignore="true"/>

        <tr id="breadCrumbs">
            <td class="rootCell">
                <div class="contentBody">
                    <tiles:insertAttribute name="path" ignore="true">
                        <tiles:putAttribute name="isViewPage" type="string" value="${isViewPage}"  />
                    </tiles:insertAttribute>
                </div>
            </td>
        </tr>
    
        <tr id="content">
            <td class="rootCell">
                <div class="contentBody">
                    <tiles:insertAttribute name="bodyHeader" ignore="true">
                        <tiles:putAttribute name="createEntityRestriction" type="string" value="${createEntityRestriction}" />
                        <tiles:putAttribute name="updateEntityRestriction" type="string" value="${updateEntityRestriction}" />
                        <tiles:putAttribute name="hasAuditLog" type="string" value="${hasAuditLog}"/>
                    </tiles:insertAttribute>
                    <tiles:insertAttribute name="body"/>
                    <tiles:insertAttribute name="bodyFooter" ignore="true"/>
                </div>
            </td>
        </tr>
    </c:when>
    <c:otherwise>
        <tr id="header">
            <td class="rootCell">
                <div id="headContainer">
                    <div id="applicationLogo">Target RTB</div>
                </div>
            </td>
        </tr>
        <tr id="content">
            <td class="rootCell">
                <div class="contentBody">
                    <tiles:insertAttribute name="body"/>
                </div>
            </td>
        </tr>
    </c:otherwise>
    </c:choose>
    <tr id="footer">
        <td>
            <ui:constraintViolations/>
            <div id="appVersion">
                <s:if test="isInternal()">
                    <c:set var="versionName"><fmt:message key="form.version"/></c:set>
                    <c:set var="versionValue"><%=application.getAttribute(VersionHelper.VERSION_PROPERTY)%></c:set>
                    <c:out value="${versionName} ${versionValue}"/>
                </s:if>
                <s:else>
                    <a href="${_context}/TermsOfUse.action"><s:text name="TermsOfUse.termsOfUse"/></a>
                          <a href="${_context}/Contacts.action"><s:text name="contacts.contactUs"/></a>
                </s:else>
            </div>
            <div id="copyright">
                <fmt:message key="form.copyright"/>
            </div>
        </td>
    </tr>
    </table>
  </body>
</html>
