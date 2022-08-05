<%-- Set objectType, objectId and refText before using! --%>

<%@page import="com.foros.model.security.ObjectType"%>
<%@page import="com.foros.model.account.Account"%>
<%@page import="com.foros.model.channel.Channel"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="ColocationEnumValue" value='<%=ObjectType.Colocation%>'/>
<c:set var="SiteEnumValue" value='<%=ObjectType.Site%>'/>
<c:set var="UserEnumValue" value='<%=ObjectType.User%>'/>
<c:set var="CampaignEnumValue" value='<%=ObjectType.Campaign%>'/>
<c:set var="OpportunityEnumValue" value='<%=ObjectType.Opportunity%>'/>
<c:set var="CampaignCreativeGroupEnumValue" value='<%=ObjectType.CampaignCreativeGroup%>'/>
<c:set var="CreativeEnumValue" value='<%=ObjectType.Creative%>'/>
<c:set var="CurrencyExchangeEnumValue" value='<%=ObjectType.CurrencyExchange%>'/>
<c:set var="ActionEnumValue" value='<%=ObjectType.Action%>'/>
<c:set var="TagEnumValue" value='<%=ObjectType.Tag%>'/>
<c:set var="WDTagEnumValue" value='<%=ObjectType.WDTag%>'/>
<c:set var="UserRoleEnumValue" value='<%=ObjectType.UserRole%>'/>
<c:set var="CreativeCategoryEnumValue" value='<%=ObjectType.CreativeCategory%>'/>
<c:set var="AccountTypeEnumValue" value='<%=ObjectType.AccountType%>'/>
<c:set var="CreativeTemplateEnumValue" value='<%=ObjectType.CreativeTemplate%>'/>
<c:set var="CreativeSizeEnumValue" value="<%=ObjectType.CreativeSize%>"/>
<c:set var="DiscoverTemplateEnumValue" value='<%=ObjectType.DiscoverTemplate%>'/>
<c:set var="CountryEnumValue" value='<%=ObjectType.Country%>'/>
<c:set var="CTRAlgorithmDataEnumValue" value='<%=ObjectType.CTRAlgorithmData%>'/>
<c:set var="NoTrackingChannelEnumValue" value='<%=ObjectType.NoTrackingChannel%>'/>
<c:set var="NoAdvertisingChannelEnumValue" value='<%=ObjectType.NoAdvertisingChannel%>'/>
<c:set var="PlacementsBlacklistEnumValue" value='<%=ObjectType.PlacementsBlacklist%>'/>
<c:set var="FraudConditionEnumValue" value='<%=ObjectType.FraudCondition%>'/>
<c:set var="WDFrequencyCapsActionEnumValue" value='<%=ObjectType.WDFrequencyCap%>'/>
<c:set var="PredefinedReportEnumValue" value='<%=ObjectType.PredefinedReport%>'/>
<c:set var="BirtReportEnumValue" value='<%=ObjectType.BirtReport%>'/>
<c:set var="SearchEngineEnumValue" value='<%=ObjectType.SearchEngine%>'/>
<c:set var="CampaignCreditEnumValue" value='<%=ObjectType.CampaignCredit%>'/>
<c:set var="WalledGardenEnumValue" value='<%=ObjectType.WalledGarden%>'/>
<c:set var="FileManager" value='<%=ObjectType.FileManager%>'/>
<c:set var="SizeType" value='<%=ObjectType.SizeType%>'/>

<c:set var="isTypeAssignableFromAccount" value='<%=Account.class.isAssignableFrom(((ObjectType)pageContext.getAttribute("objectType")).getObjectClass())%>'/>
<c:set var="isTypeAssignableFromChannel" value='<%=Channel.class.isAssignableFrom(((ObjectType)pageContext.getAttribute("objectType")).getObjectClass())%>'/>

<c:choose>
    <c:when test="${contextName == 'global.menu.advertisers'}">
        <c:set var="accountViewActionName" value="/admin/account/view.action" />
        <c:set var="userViewActionName" value="/admin/account/user/advertiserView.action" />
    </c:when>
    <c:when test="${contextName == 'global.menu.publishers'}">
        <c:set var="accountViewActionName" value="/admin/publisher/account/view.action" />
        <c:set var="userViewActionName" value="/admin/account/user/publisherView.action" />
    </c:when>
    <c:when test="${contextName == 'global.menu.isps'}">
        <c:set var="accountViewActionName" value="/admin/isp/account/view.action" />
        <c:set var="userViewActionName" value="/admin/account/user/ispView.action" />
    </c:when>
    <c:when test="${contextName == 'global.menu.cmps'}">
        <c:set var="accountViewActionName" value="/admin/cmp/account/view.action" />
        <c:set var="userViewActionName" value="/admin/account/user/cmpView.action" />
    </c:when>
    <c:otherwise>
        <c:set var="accountViewActionName" value="/admin/account/view.action" />
        <c:set var="userViewActionName" value="/admin/account/user/view.action" />
        <c:if test="${not empty param.internalUserPage}">
            <c:choose>
                <c:when test="${param.internalUserPage}">
                    <c:set var="userViewActionName" value="/admin/InternalUser/view.action" />
                </c:when>
                <c:otherwise>
                    <c:set var="userViewActionName" value="/admin/internal/account/user/view.action" />
                </c:otherwise>
            </c:choose>
        </c:if>
    </c:otherwise>
</c:choose>

<c:if test="${objectType.name == ColocationEnumValue}">
  <a href="/admin/colocation/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == SiteEnumValue}">
  <a href="${_context}/site/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${isTypeAssignableFromAccount || objectType.name == CampaignCreditEnumValue}">
  <a href="${accountViewActionName}?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == UserEnumValue}">
  <a href="${userViewActionName}?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == CampaignEnumValue}">
  <a href="${_context}/campaign/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == CampaignAllocationEnumValue}">
    <a href="${_context}/campaign/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == OpportunityEnumValue}">
  <a href="/admin/opportunity/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == CampaignCreativeGroupEnumValue}">
   <a href="${_context}/campaign/group/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == CreativeEnumValue}">
   <a href="${_context}/creative/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == CurrencyExchangeEnumValue}">
  <a href="/admin/CurrencyExchange/main.action">${refText}</a>
</c:if>

<c:if test="${isTypeAssignableFromChannel}">
    <a href="${_context}/channel/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == ActionEnumValue}">
    <a href="${_context}/Action/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == TagEnumValue}">
    <a href="${_context}/tag/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == WDTagEnumValue}">
    <a href="${_context}/site/WDTag/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == UserRoleEnumValue}">
    <a href="/admin/UserRole/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == CreativeCategoryEnumValue}">
    <a href="/admin/CreativeCategory/main.action">${refText}</a>
</c:if>

<c:if test="${objectType.name == AccountTypeEnumValue}">
    <a href="/admin/AccountType/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == CreativeTemplateEnumValue}">
    <a href="/admin/CreativeTemplate/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == CreativeSizeEnumValue}">
    <a href="/admin/CreativeSize/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == DiscoverTemplateEnumValue}">
    <a href="/admin/DiscoverTemplate/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == CountryEnumValue}">
    <a href="/admin/Country/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == CTRAlgorithmDataEnumValue}">
    <a href="/admin/Country/CTRAlgorithm/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == NoTrackingChannelEnumValue}">
    <a href="/admin/TriggerListNoTrackChannel/view.action">${refText}</a>
</c:if>

<c:if test="${objectType.name == NoAdvertisingChannelEnumValue}">
    <a href="/admin/TriggerListNoAdvChannel/view.action">${refText}</a>
</c:if>

<c:if test="${objectType.name == PlacementsBlacklistEnumValue}">
    <a href="/admin/Country/PlacementsBlacklist/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == FraudConditionEnumValue}">
    <a href="/admin/FraudConditions/main.action">${refText}</a>
</c:if>

<c:if test="${objectType.name == WDFrequencyCapsActionEnumValue}">
    <a href="/admin/WDFrequencyCaps/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == PredefinedReportEnumValue}">
    ${refText}
</c:if>

<c:if test="${objectType.name == BirtReportEnumValue}">
    <a href="/birt/reports/run/${objectId}/">${refText}</a>
</c:if>

<c:if test="${objectType.name == SearchEngineEnumValue}">
    <a href="/admin/SearchEngine/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == WalledGardenEnumValue}">
    <a href="/admin/WalledGarden/main.action">${refText}</a>
</c:if>

<c:if test="${objectType.name == SizeType}">
    <a href="/admin/SizeType/view.action?id=${objectId}">${refText}</a>
</c:if>

<c:if test="${objectType.name == FileManager}">
    ${refText}
</c:if>

