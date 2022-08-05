<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${ad:isPermitted0('AdvertiserEntity.update')}">
    <c:set var="canUpdate" value="true" />
</c:if>

<script type="text/javascript">
    $(function() {
        $('#lookupFormId').unbind('submit');

        $('#campaings_changed').on('click', '.b-campaign__expand', function(e){
            e.preventDefault();
            $(this).closest('tbody').next().toggle().is(':visible') ? $(this).text( $(this).data('hide') ) : $(this).text( $(this).data('show') );
        });
    });
</script>
<ui:pageHeadingByTitle/>

<ui:section>
    <ui:fieldGroup>
    <ui:field labelKey="approvals">
        <c:choose>
            <c:when test="${ad:isPermitted0('AdvertisingChannel.view')}">
                <c:set var="channelsCount" value="${channelsPendingCount}"/>
            </c:when>
            <c:otherwise>
                <c:set var="channelsCount" value="${0}"/>
            </c:otherwise>
        </c:choose>
        <s:if test="creativesPendingCount != 0 || #attr.channelsCount != 0 || discoverChannelsPendingCount != 0">
            <s:if test="creativesPendingCount != 0">
                <c:set var="creativeUrl"><s:url action="admin/AdopsDashboard/creativeList"/></c:set>
                <fmt:message key="aprroval.creative">
                    <fmt:param value="<a href='${creativeUrl}'>${creativesPendingCount}"/>
                    <fmt:param value="</a>"/>
                </fmt:message>
            </s:if>
            <br/>
            <s:if test="#attr.channelsCount != 0">
                <fmt:message key="aprroval.channel">
                    <fmt:param value="<a href='/admin/channel/main.action?status=PENDING_FOROS&testOption=EXCLUDE#auto'>${channelsCount}"/>
                    <fmt:param value="</a>"/>
                </fmt:message>
            </s:if>
            <br/>
            <s:if test="discoverChannelsPendingCount != 0">
                <fmt:message key="aprroval.discoverChannel">
                    <fmt:param value="<a href='/admin/DiscoverChannel/main.action?status=PENDING_FOROS#auto'>${discoverChannelsPendingCount}"/>
                    <fmt:param value="</a>"/>
                </fmt:message>
            </s:if>
        </s:if>
        <s:else>
            <fmt:message key="approval.nothing"/>
        </s:else>
    </ui:field>
    <ui:field labelKey="checks.dueChecks">
        <c:set var="checksUrl"><s:url action="admin/AdopsDashboard/regularReview"/></c:set>
        <a href='${checksUrl}'><fmt:message key="checks.campaignsChannelsDueChecking"/></a>
    </ui:field>
    <ui:field/>
    <ui:field labelKey="admin.dashboard.lookUpbyId" errors="lookupId">
        <s:form action="admin/AdopsDashboard/lookup" id="lookupFormId" method="get">
            <table class="fieldAndAccessories">
            <tr>
                    <td class="withField">
                    <s:textfield name="id" id="lookupId" cssClass="smallLengthText" maxLength="10"/>
                </td>
                    <td class="withField">
                    <s:select name="lookup" id="lookup" 
                              list="#{'account':getText('lookup.option.account'),
                                    'campaign_creative':getText('lookup.option.campaign_creative'),
                                    'campaign':getText('lookup.option.campaign'),
                                    'channel':getText('lookup.option.channel'),
                                    'colocation':getText('lookup.option.colocation'),
                                    'creative':getText('lookup.option.creative'),
                                    'creative_group':getText('lookup.option.creative_group'),
                                    'discover_tag':getText('lookup.option.discover_tag'),
                                    'site':getText('lookup.option.site'),
                                    'tag':getText('lookup.option.tag')}" />
                </td>
                    <td class="withButton">
                    <ui:button message="admin.dashboard.lookup" />
                </td>
            </tr>
        </table>
        </s:form>
    </ui:field>
    </ui:fieldGroup>
</ui:section>

<h2><fmt:message key="admin.changedCampaigns"/></h2>

<s:set var="dashboardData" value="adopsDashboardData"/>

<s:if test="campaigns == null || campaigns.isEmpty">
    <fmt:message key="admin.changedCampaigns.empty"/>
</s:if>
<s:else>
    <s:set var="viewAgencyURL" value="'/admin/advertiser/account/advertiserView.action'"/>
    <s:set var="viewAdvertiserUrl" value="'/admin/advertiser/account/agencyAdvertiserView.action'"/>
    <s:set var="viewCampaignUrl" value="'/admin/campaign/view.action'"/>
    <s:set var="viewCCGUrl" value="'/admin/campaign/group/view.action'"/>
    <s:set var="editDisplayCCGUrl" value="'/admin/campaign/group/editDisplay.action'"/>
    <s:set var="editTextCCGUrl" value="'/admin/campaign/group/editText.action'"/>
    <s:set var="editCampaignUrl" value="'/admin/campaign/edit.action'"/>

    <table class="dataView collapsing" id="campaings_changed">
        <thead>
            <tr>
                <th><fmt:message key="admin.dashboard.advertiser"/></th>
                <th class="withCollapsingButton">&nbsp;</th>
                <th><fmt:message key="admin.dashboard.campaign"/></th>
                <th><fmt:message key="admin.dashboard.date"/></th>
            </tr>
        </thead>
        <s:iterator value="campaigns" var="campaign" status="campaignIterationStatus">
            <s:set var="agency" value="#campaign.account.agency"/>
            <s:set var="adv" value="#campaign.account"/>

            <tbody class="parent">
                <tr>
                    <td>
                        <s:if test="#agency != null">
                            <ui:displayStatus displayStatus="${agency.displayStatus}" testFlag="${agency.testFlag}">
                                <a class="preText" href="${viewAgencyURL}?id=${agency.id}"><c:out value="${agency.name}"/></a>
                                /
                                <a class="preText" href="${viewAdvertiserUrl}?id=${adv.id}"><c:out value="${adv.name}"/></a>
                            </ui:displayStatus>
                        </s:if>
                        <s:else>
                            <ui:displayStatus displayStatus="${adv.displayStatus}" testFlag="${adv.testFlag}">
                                <a class="preText" href="${viewAgencyURL}?id=${adv.id}"><c:out value="${adv.name}"/></a>
                            </ui:displayStatus>
                        </s:else>
                    </td>
                    <td class="withCollapsingButton">
                        <s:if test="#campaign.creativeGroups == null || #campaign.creativeGroups.isEmpty">
                            &nbsp;
                        </s:if>
                        <s:else>
                            <a href="#" class="b-campaign__expand" data-hide="<fmt:message key="admin.dashboard.hide"/>" data-show="<fmt:message key="admin.dashboard.show"/>"><fmt:message key="admin.dashboard.show"/></a>
                        </s:else>
                    </td>
                    <td>
                        <ui:displayStatus displayStatus="${campaign.displayStatus}">
                            <a class="preText" href="${viewCampaignUrl}?id=${campaign.id}"><c:out value="${campaign.name}"/></a>
                            <c:if test="${canUpdate and campaign.inheritedStatus.letter != 'D'}">
                                <ui:button message="form.edit" href="${editCampaignUrl}?id=${campaign.id}"/>
                            </c:if>
                        </ui:displayStatus>
                    </td>
                    <td><fmt:formatDate value="${campaign.version}" type="both" timeStyle="short" dateStyle="short" timeZone="${_userSettings.timeZone}"/></td>
                </tr>
            </tbody>

            <tbody id="data_${campaign.id}" style="display:none" class="child">
                <s:iterator value="#campaign.creativeGroups" var="ccg" status="ccgIterationStatus">
                    <tr>
                        <td></td>
                        <td class="withCollapsingButton"></td>
                        <td>
                            <s:set var="editCCGUrl" value="editDisplayCCGUrl"/>
                            
                            <s:if test="#ccg.ccgType.letter == 'T'">
                                <s:set var="editCCGUrl" value="editTextCCGUrl"/>
                            </s:if>
                            <ui:displayStatus displayStatus="${ccg.displayStatus}">
                                <a class="preText" href="${viewCCGUrl}?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                                <c:if test="${canUpdate and ccg.inheritedStatus.letter != 'D'}">
                                    <a href="${editCCGUrl}?id=${ccg.id}" class="button"><fmt:message key="form.edit"/></a>
                                </c:if>
                            </ui:displayStatus>
                        </td>
                        <td><fmt:formatDate value="${ccg.version}" type="both" timeStyle="short" dateStyle="short" timeZone="${_userSettings.timeZone}"/></td>
                    </tr>
                </s:iterator>
            </tbody>
        </s:iterator>
    </table>
</s:else>
