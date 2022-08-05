<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:choose>
        <c:when test="${moduleName == 'admin/advertiser/account'}">
            <c:if test="${ad:isPermitted('Account.create', 'Advertiser')}">
                <c:set var="buttText">
                    <s:text name="advertiserAccount.createNew"/>
                </c:set>
                <ui:button messageText="${buttText}" href="/${moduleName}/advertiserNew.action" />
            </c:if>
            <c:if test="${ad:isPermitted('Account.create', 'Agency')}">
                <c:set var="buttText">
                    <s:text name="agencyAccount.createNew"/>
                </c:set>
                <ui:button messageText="${buttText}" href="/${moduleName}/agencyNew.action" />
            </c:if>
        </c:when>
        <c:when test="${moduleName == 'admin/publisher/account'}">
            <c:if test="${ad:isPermitted('Account.create', 'Publisher')}">
	            <c:set var="buttText">
	                <s:text name="%{#attr.entityName}.createNew"/>
	            </c:set>
	            <ui:button messageText="${buttText}" href="/${moduleName}/new.action" />
            </c:if>
            <c:if test="${ad:isPermitted('PublisherEntity.upload', null)}">
                 <c:set var="buttTextUpload">
                    <s:text name="form.upload.csv"/>
                </c:set>
                <ui:button  messageText="${buttTextUpload}" href="/admin/site/selectForUpload.action" />
            </c:if>
        </c:when>
        <c:when test="${moduleName == 'admin/isp/account'}">
            <c:if test="${ad:isPermitted('Account.create', 'ISP')}">
	            <c:set var="buttText">
	                <s:text name="%{#attr.entityName}.createNew"/>
	            </c:set>
	            <ui:button messageText="${buttText}" href="/${moduleName}/new.action" />
            </c:if>
        </c:when>
        <c:when test="${moduleName == 'admin/cmp/account'}">
            <c:if test="${ad:isPermitted('Account.create', 'CMP')}">
	            <c:set var="buttText">
	                <s:text name="%{#attr.entityName}.createNew"/>
	            </c:set>
	            <ui:button messageText="${buttText}" href="/${moduleName}/new.action" />
            </c:if>
        </c:when>
    </c:choose>
</ui:header>
