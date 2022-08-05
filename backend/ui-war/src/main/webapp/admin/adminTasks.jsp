<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<%@include file="adminPermissions.jsp" %>

<c:if test="${viewChannels1 or viewChannels2 or viewChannels3}">
    <ui:section titleKey="admin.channels">
        <table class="grouping">
            <tr>
                <c:if test="${viewChannels1}">
                    <td class="withGroupOfLinks">
                        <span class="groupOfLinks">
                            <c:if test="${viewAdvertisingChannel}">
                                <a href="/admin/channel/main.action"><fmt:message key="admin.channels"/></a>
                            </c:if>
                            <c:if test="${viewKeywordChannel}">
                                <a href="/admin/KeywordChannel/main.action"><fmt:message key="admin.keywordChannels"/></a>
                            </c:if>
                            <c:if test="${viewCategoryChannel}">
                                <a href="/admin/CategoryChannel/main.action"><fmt:message key="admin.categoryChannels"/></a>
                            </c:if>
                            <c:if test="${viewDeviceChannel}">
                                <a href="/admin/DeviceChannel/main.action"><fmt:message key="admin.deviceChannels"/></a>
                            </c:if>
                        </span>
                    </td>
                </c:if>
                <c:if test="${viewChannels2}">
                    <td class="withGroupOfLinks">
                        <span class="groupOfLinks">
                          <c:if test="${viewDiscoverChannel}">
                              <a href="/admin/DiscoverChannel/main.action"><fmt:message key="admin.discoverChannels"/></a>
                              <a href="/admin/DiscoverChannelList/main.action"><fmt:message key="admin.discoverChannelLists"/></a>
                          </c:if>
                          <c:if test="${viewBehavioralParams}">
                              <a href="/admin/behavioralParameters/main.action"><fmt:message
                                  key="channel.params.list"/></a>
                          </c:if>
                          <c:if test="${viewGeoChannel}">
                              <a href="/admin/GeoChannel/main.action"><fmt:message key="admin.geoChannels"/></a>
                          </c:if>
                        </span>
                    </td>
                </c:if>
                <c:if test="${viewChannels3}">
                    <td class="withGroupOfLinks">
                        <span class="groupOfLinks">
                            <c:if test="${viewTriggerQA}">
                                <a href="/admin/Triggers/main.action"><fmt:message key="admin.triggersApproval"/></a>
                            </c:if>
                            <c:if test="${viewBannedChannel}">
                                <a href="/admin/TriggerListNoTrackChannel/view.action"><fmt:message
                                    key="admin.noTrackingChannel"/></a>
                                <a href="/admin/TriggerListNoAdvChannel/view.action"><fmt:message
                                    key="admin.noAdvertisingChannel"/></a>
                            </c:if>
                            <c:if test="${viewKWMTool}">
                                <a href="/admin/KWMTool/main.action"><fmt:message key="admin.kwmTool"/></a>
                            </c:if>
                        </span>
                    </td>
                </c:if>
            </tr>
        </table>
    </ui:section>
</c:if>

<c:if test="${viewCreativeAndTemplate1 or viewCreativeAndTemplate2}">
    <ui:section titleKey="admin.creativesAndTemplates">
        <table class="grouping">
            <tr>
                <c:if test="${viewCreativeAndTemplate1}">
                    <td class="withGroupOfLinks">
                        <span class="groupOfLinks">
                            <c:if test="${viewCreativeCategory}">
                                <a href="/admin/CreativeCategory/main.action"><fmt:message
                                    key="admin.creativeCategories"/></a>
                            </c:if>
                            <c:if test="${viewCreativeSize}">
                                <a href="/admin/SizeType/main.action"><fmt:message key="SizeType.plural"/></a>
                                <a href="/admin/CreativeSize/main.action"><fmt:message key="admin.creativeSizes"/></a>
                            </c:if>
                        </span>
                    </td>
                </c:if>

                <c:if test="${viewCreativeAndTemplate2}">
                    <td class="withGroupOfLinks">
                        <span class="groupOfLinks">
                            <c:if test="${viewTemplate}">
                                <a href="/admin/CreativeTemplate/main.action"><fmt:message
                                    key="admin.creativeTemplates"/></a>
                                <a href="/admin/DiscoverTemplate/main.action"><fmt:message
                                    key="admin.discoverTemplates"/></a>
                            </c:if>
                            <c:if test="${viewApplicationFormat}">
                                <a href="/admin/ApplicationFormat/main.action"><fmt:message
                                    key="admin.applicationFormats"/></a>
                            </c:if>
                        </span>
                    </td>
                </c:if>
            </tr>
        </table>
    </ui:section>
</c:if>

<c:if test="${viewOther1 or viewOther2 or viewOther3}">
    <ui:section titleKey="admin.other">
        <table class="grouping">
            <tr>
                <c:if test="${viewOther1}">
                    <td class="withGroupOfLinks">
                        <span class="groupOfLinks">
                            <c:if test="${viewAccountType}">
                                <a href="/admin/AccountType/main.action"><fmt:message key="admin.accountTypes"/></a>
                            </c:if>
                            <c:if test="${viewCountry}">
                                <a href="/admin/Country/main.action"><fmt:message key="admin.countries"/></a>
                            </c:if>
                            <c:if test="${viewCurrency}">
                                <a href="/admin/Currency/main.action"><fmt:message key="admin.currencies"/></a>
                            </c:if>
                            <c:if test="${viewCurrencyExchange}">
                                <a href="/admin/CurrencyExchange/main.action"><fmt:message key="admin.currencyExchange"/></a>
                            </c:if>
                            <c:if test="${viewWalledGardens}">
                                <a href="/admin/WalledGarden/main.action"><fmt:message key="admin.walledGardens"/></a>
                            </c:if>
                        </span>
                    </td>
                </c:if>
                <c:if test="${viewOther2}">
                    <td class="withGroupOfLinks">
                        <span class="groupOfLinks">
                            <c:if test="${manageFileManager}">
                                <a href="/admin/fileman/fileManager.action" target="_blank"><fmt:message
                                    key="admin.fileManager"/></a>
                            </c:if>
                            <c:if test="${viewWDRequestMapping}">
                                <a href="/admin/WDRequestMapping/main.action"><fmt:message
                                    key="admin.wdRequestMapping"/></a>
                            </c:if>
                            <c:if test="${viewWDFrequencyCaps}">
                                <a href="/admin/WDFrequencyCaps/view.action"><fmt:message key="admin.WDFrequencyCaps"/></a>
                            </c:if>
                            <c:if test="${viewGlobalParams}">
                                <a href="/admin/GlobalParam/view.action"><fmt:message key="admin.globalParameters"/></a>
                            </c:if>
                            <c:if test="${viewFraudConditions}">
                                <a href="/admin/FraudConditions/main.action"><fmt:message key="fraud.fraudConditions"/></a>
                            </c:if>
                            </span>
                    </td>
                </c:if>
                <c:if test="${viewOther3}">
                    <td class="withGroupOfLinks">
                        <span class="groupOfLinks">
                            <c:if test="${viewInternalAccount}">
                                <a href="/admin/internal/account/list.action"><fmt:message
                                    key="admin.internalAccounts"/></a>
                                <a href="/admin/InternalUser/main.action"><fmt:message key="admin.internalUsers"/></a>
                            </c:if>
                            <c:if test="${viewUserRole}">
                                <a href="/admin/UserRole/main.action"><fmt:message key="admin.userRoles"/></a>
                            </c:if>
                            <c:if test="${viewSearchEngines}">
                                <a href="/admin/SearchEngine/main.action"><fmt:message key="SearchEngine.plural"/></a>
                            </c:if>
                        </span>
                    </td>
                </c:if>
            </tr>
        </table>
    </ui:section>
</c:if>
