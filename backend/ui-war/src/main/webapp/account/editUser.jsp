<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<s:if test="#attr.contextName == 'global.menu.advertisers'">
    <s:set value="#attr.isCreatePage?'advertiserCreate':'advertiserUpdate'" var="saveActionName"/>
    <s:set var="viewActionName" value="'advertiserView.action'" />
    <s:set var="viewAccountActionName" value="'/admin/advertiser/account/advertiserView.action'" />
</s:if>
<s:elseif test="#attr.contextName == 'global.menu.publishers'">
    <s:set value="#attr.isCreatePage?'publisherCreate':'publisherUpdate'" var="saveActionName"/>
    <s:set var="viewActionName" value="'publisherView.action'" />
    <s:set var="viewAccountActionName" value="'/admin/publisher/account/view.action'" />
</s:elseif>
<s:elseif test="#attr.contextName == 'global.menu.isps'">
    <s:set value="#attr.isCreatePage?'ispCreate':'ispUpdate'" var="saveActionName"/>
    <s:set var="viewActionName" value="'ispView.action'" />
    <s:set var="viewAccountActionName" value="'/admin/isp/account/view.action'" />
</s:elseif>
<s:elseif test="#attr.contextName == 'global.menu.cmps'">
    <s:set value="#attr.isCreatePage?'cmpCreate':'cmpUpdate'" var="saveActionName"/>
    <s:set var="viewActionName" value="'cmpView.action'" />
    <s:set var="viewAccountActionName" value="'/admin/cmp/account/view.action'" />
</s:elseif>
<s:else>
    <s:set value="#attr.isCreatePage?'create':'update'" var="saveActionName"/>
    <s:set var="viewActionName" value="'view.action'" />
    <s:set var="viewAccountActionName" value="'/admin/internal/account/view.action'" />
</s:else>

<s:form action="%{#attr.moduleName}/%{#saveActionName}">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <s:hidden name="account.id"/>
    <s:hidden name="account.name"/>
    <s:hidden name="account.role"/>

<ui:pageHeadingByTitle/>

    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </ui:errorsBlock>

    <ui:section titleKey="form.main">
        <ui:fieldGroup>

            <ui:field labelKey="user.authorization" errors="authType">
                <label class="withInput">
                    <s:radio cssClass="withInput" name="authType" list="'PSWD'" listValue="getText('user.password')"/>
                </label>
                <label class="withInput">
                    <s:radio cssClass="withInput" name="authType" list="'NONE'" listValue="getText('user.noLogin')"/>
                </label>
            </ui:field>

            <ui:field labelKey="user.email" labelForId="email" required="true" errors="email">
                <input type="${ad:isMobileAgent(pageContext.request) ? 'email' : 'text'}" name="email" maxlength="320" value="${email}" id="email" class="middleLengthText">
            </ui:field>

            <ui:field labelKey="user.firstName" labelForId="firstName" required="true" errors="firstName">
                <s:textfield name="firstName" cssClass="middleLengthText" maxLength="50" id="firstName"/>
            </ui:field>

            <ui:field labelKey="user.lastName" labelForId="lastName" required="true" errors="lastName">
                <s:textfield name="lastName" cssClass="middleLengthText" maxLength="50" id="lastName"/>
            </ui:field>

            <ui:field labelKey="user.language" labelForId="language" errors="languageIsoCode">
                <s:select name="language" cssClass="middleLengthText" id="language"
                    list="@com.foros.model.security.Language@values()"
                    listKey="name()" listValue="getText('enums.Language.' + name())"/>
            </ui:field>

            <ui:field labelKey="user.jobTitle" labelForId="jobTitle" errors="jobTitle">
                <s:textfield name="jobTitle" cssClass="middleLengthText" maxLength="30" id="jobTitle"/>
            </ui:field>

            <ui:field id="roleTr" labelKey="user.role" labelForId="roleId" errors="roleId">
                <s:if test="roleChangeAllowed">
                    <s:select name="role.id" cssClass="middleLengthText" id="roleId"
                        list="roles"
                        listKey="id" listValue="name"/>
                </s:if>
                <s:else>
                    <c:out value="${role.name}"/>
                </s:else>
            </ui:field>

            <ui:field labelKey="user.phone" labelForId="phone" required="true" errors="phone">
                <s:textfield name="phone" cssClass="middleLengthText" maxLength="80" id="phone"/>
            </ui:field>

            <s:if test="account.role.name == 'Agency'">
                <ui:field id="advLevelAccessElem" labelKey="user.advertiserLevelAccessControl" labelForId="advLevelAccessFlag">
                    <s:checkbox name="advLevelAccessFlag" id="advLevelAccessFlag"/>
                </ui:field>
            </s:if>

        </ui:fieldGroup>
    </ui:section>

    <s:if test="account.role.name == 'Publisher'">
        <script type="text/javascript">
            $(function() {
                $("#siteLevelAccessFlag").change(function() {
                    $("input, select", "#selectedSites").prop("disabled", !$(this).prop("checked"));
                }).change();
            });
        </script>
        <ui:section titleKey="user.siteLevelAccessControl" titleInputId="siteLevelAccessFlag" titleInputChecked="${siteLevelAccessFlag}">
            <ui:fieldGroup>
                <ui:field id="selectedSites">
                    <table class="fieldAndAccessories">
                        <tr>
                            <td class="withField">
                                <ui:optiontransfer
                                        name="selectedSites"
                                        size="9"
                                        cssClass="middleLengthText"
                                        list="${accountSites}"
                                        selList="${userSites}"
                                        titleKey="user.siteLevelAccessNotAllowed"
                                        selTitleKey="user.siteLevelAccessAllowed"
                                        saveSorting="true"
                                        escape="false"
                                        />
                            </td>
                        </tr>
                        <tr>
                            <td class="withError">
                                <s:fielderror><s:param value="'selectedSites'"/></s:fielderror>
                            </td>
                        </tr>
                    </table>
                </ui:field>
            </ui:fieldGroup>
        </ui:section>
    </s:if>

    <div class="wrapper">
        <ui:button message="form.save" type="submit" novalidate="true" />
        <s:if test="id != null">
            <ui:button message="form.cancel" onclick="location='${viewActionName}?id=${id}';" type="button" />
        </s:if>
        <s:elseif test="%{isInternal()}">
            <ui:button message="form.cancel" onclick="location='${viewAccountActionName}?id=${account.id}';" type="button" />
        </s:elseif>
        <s:else>
            <ui:button message="form.cancel" onclick="location='${_context}/myAccount/myAccountView.action';" type="button" />
        </s:else>
    </div>
</s:form>
