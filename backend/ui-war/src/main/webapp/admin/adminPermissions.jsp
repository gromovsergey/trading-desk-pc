<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<c:set var="viewAdvertisingChannel" value="#{ad:isPermitted0('AdvertisingChannel.view')}"/>
<c:set var="viewKeywordChannel" value="#{ad:isPermitted0('KeywordChannel.view')}"/>
<c:set var="viewCategoryChannel" value="#{ad:isPermitted0('CategoryChannel.view')}"/>
<c:set var="viewDeviceChannel" value="#{ad:isPermitted0('DeviceChannel.view')}"/>

<c:set var="viewDiscoverChannel" value="#{ad:isPermitted0('DiscoverChannel.view')}"/>
<c:set var="viewBehavioralParams" value="#{ad:isPermitted0('BehavioralParams.view')}"/>
<c:set var="viewGeoChannel" value="#{ad:isPermitted0('GeoChannel.view')}"/>

<c:set var="viewTriggerQA" value="#{ad:isPermitted0('TriggerQA.view')}"/>
<c:set var="viewBannedChannel" value="#{ad:isPermitted0('BannedChannel.view')}"/>
<c:set var="viewKWMTool" value="#{ad:isPermitted0('KWMTool.view')}"/>

<c:set var="viewChannels1" value="#{viewAdvertisingChannel or viewCategoryChannel or viewDeviceChannel or viewKeywordChannel}"/>
<c:set var="viewChannels2" value="#{viewDiscoverChannel or viewBehavioralParams or viewGeoChannel}"/>
<c:set var="viewChannels3" value="#{viewTriggerQA or viewBannedChannel or viewKWMTool}"/>

<c:set var="viewCreativeCategory" value="#{ad:isPermitted0('CreativeCategory.view')}"/>
<c:set var="viewCreativeSize" value="#{ad:isPermitted0('CreativeSize.view')}"/>

<c:set var="viewTemplate" value="#{ad:isPermitted0('Template.view')}"/>
<c:set var="viewApplicationFormat" value="#{ad:isPermitted0('ApplicationFormat.view')}"/>

<c:set var="viewCreativeAndTemplate1" value="#{viewCreativeCategory or viewCreativeSize}"/>
<c:set var="viewCreativeAndTemplate2" value="#{viewTemplate or viewApplicationFormat}"/>

<c:set var="viewAccountType" value="#{ad:isPermitted0('AccountType.view')}"/>
<c:set var="viewCountry" value="#{ad:isPermitted0('Country.view')}"/>
<c:set var="viewCurrency" value="#{ad:isPermitted0('Currency.view')}"/>
<c:set var="viewCurrencyExchange" value="#{ad:isPermitted0('CurrencyExchange.view')}"/>


<c:set var="manageFileManager" value="#{ad:isPermitted0('FileManager.manage')}"/>
<c:set var="viewWDRequestMapping" value="#{ad:isPermitted0('WDRequestMapping.view')}"/>
<c:set var="viewWDFrequencyCaps" value="#{ad:isPermitted0('WDFrequencyCaps.view')}"/>
<c:set var="viewGlobalParams" value="#{ad:isPermitted0('GlobalParams.view')}"/>
<c:set var="viewFraudConditions" value="#{ad:isPermitted0('FraudConditions.view')}"/>
<c:set var="viewSearchEngines" value="#{ad:isPermitted0('SearchEngine.view')}"/>

<c:set var="viewInternalAccount" value="#{ad:isPermitted('Account.view', 'Internal')}"/>
<c:set var="viewUserRole" value="#{ad:isPermitted0('UserRole.view')}"/>

<c:set var="viewWalledGardens" value="#{ad:isPermitted0('WalledGarden.view')}"/>

<c:set var="viewOther1" value="#{viewAccountType or viewCountry or viewCurrency or viewCurrencyExchange or viewWalledGardens}"/>
<c:set var="viewOther2" value="#{manageFileManager or viewWDRequestMapping or viewWDFrequencyCaps or viewGlobalParams or viewFraudConditions}"/>
<c:set var="viewOther3" value="#{viewInternalAccount or viewUserRole}"/>
