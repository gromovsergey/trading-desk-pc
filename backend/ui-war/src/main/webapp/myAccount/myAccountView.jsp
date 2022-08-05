<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:pageHeadingByTitle/>

<ui:section titleKey="form.general.properties">
    <ui:fieldGroup>
        <s:if test="!companyRegistrationNumber.empty">
	        <s:set var="cnpjKey" value="'account.companyRegistrationNumber'"/>
	        <s:if test="country.countryCode == 'BR'">
	            <s:set var="cnpjKey" value="'account.cnpj'"/>
	        </s:if>
            <ui:simpleField labelKey="${cnpjKey}" value="${companyRegistrationNumber}"/>
        </s:if>
        
        <s:if test="!country.countryCode.empty">
            <ui:simpleField labelKey="account.country" value="${ad:resolveGlobal('country', country.countryCode, false)}"/>
        </s:if>
        
        <s:if test="timezone.key != null">
            <ui:simpleField labelKey="account.timeZone" value="${ad:resolveGlobal('timezone', timezone.key, true)}"/>
        </s:if>

        <s:if test="role.name == 'CMP'">
            <%@ include file="/account/cmpAccountContact.jspf"%>
        </s:if>

        <s:if test="accountManager != null">
            <ui:field labelKey="account.accountManager">
                <c:set var="textVal">
                    <c:out value="${accountManager.firstName}"/> <c:out value="${accountManager.lastName}"/>
                </c:set>
                <ui:text text="${pageScope.textVal}"/><br/>
                <ui:text text="${accountManager.email}"/>
                <c:if test="${not empty accountManager.phone}">
                    <br/><ui:text text="${accountManager.phone}"/>
                </c:if>

            </ui:field>
        </s:if>
        
        <s:if test="(role.name == 'Agency' or role.name == 'Advertiser') and textAdservingMode != null and accountType.allowTextAdvertisingFlag">
            <fmt:message var="textAdvertisingString" key="enums.TextAdservingMode.${textAdservingMode}"/>
            <ui:simpleField labelKey="account.textAdserving" value="${textAdvertisingString}"/>
        </s:if>

        <s:if test="role.name == 'Advertiser'">
            <s:if test="businessArea">
                <ui:simpleField labelKey="account.businessArea" value="${businessArea}"/>
            </s:if>
            <s:if test="specificBusinessArea">
                <ui:simpleField labelKey="account.specificBusinessArea" value="${specificBusinessArea}"/>
            </s:if>
            <s:if test="tnsAdvertiser != null">
                <ui:simpleField labelKey="account.tnsAdvertiserIdName"
                    value="${tnsAdvertiser.id} - ${tnsAdvertiser.name}"/>
            </s:if>
            <s:if test="tnsBrand != null">
                <ui:simpleField labelKey="account.tnsBrandIdName"
                        value="${tnsBrand.id} - ${tnsBrand.name}"/>
            </s:if>
        </s:if>

        <c:if test="${ad:isPermitted('Account.update', model)}">
            <ui:field cssClass="withButton">
                <ui:button message="account.edit" href="myAccountEdit.action" />
            </ui:field>
        </c:if>
        
    </ui:fieldGroup>
</ui:section>

<%@ include file="/account/accountTermsView.jsp" %>

<s:if test="role.name == 'Publisher' || role.name == 'ISP' || role.name == 'CMP'">
    <%@ include file="/account/accountsPayableFinanceTable.jsp" %>
</s:if>

<ui:header styleClass="level2">
    <h2><fmt:message key="account.headers.users"/></h2>
    <c:if test="${ad:isPermitted('User.create', model)}">
        <ui:button message="form.createNew" href="${_context}/myAccount/myUser/new.action"/>
    </c:if>
</ui:header>

<display:table name="accountUsers" class="dataView" id="user">
    <display:setProperty name="basic.msg.empty_list" >
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>
    <display:column titleKey="account.table.title.name">
        <ui:displayStatus displayStatus="${user.displayStatus}">
            <a href="${_context}/myAccount/myUser/view.action?id=${user.id}">${user.fullName}</a>
        </ui:displayStatus>
    </display:column>
    <display:column titleKey="account.table.title.email">
        <c:out value="${user.email}"/>
    </display:column>
    <display:column property="role.name" titleKey="account.table.title.role"/>
    <display:column titleKey="account.table.title.jobTitle"><c:out value="${user.jobTitle}"/></display:column>
    <display:column titleKey="account.table.title.phone"><c:out value="${user.phone}"/></display:column>
</display:table>
