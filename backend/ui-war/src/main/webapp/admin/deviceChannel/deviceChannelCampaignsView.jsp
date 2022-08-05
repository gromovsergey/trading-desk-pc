<%@ page import="com.foros.model.channel.Channel" %>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<s:set var="viewCampaignUrl" value="'/admin/campaign/view.action'"/>
<s:set var="viewCCGUrl" value="'/admin/campaign/group/view.action'"/>

<ui:pages pageSize="${searchForm.pageSize}"
          total="${searchForm.total}"
          selectedNumber="${searchForm.page}"
          visiblePagesCount="10"
          handler="goToPage"
          displayHeader="true"/>

<table class="dataViewSection">
        <tr class="bodyZone">
            <td>
                <display:table name="associatedCampaignCreativeGroups" class="dataView" id="ccg">
                    <display:setProperty name="basic.msg.empty_list" >
                      <div class="wrapper">
                          <fmt:message key="nothing.found.to.display"/>
                      </div>
                    </display:setProperty>
                
                    <display:column titleKey="channel.search.account">
                        <ui:accountLink id="${ccg.account.internalAccount.id}" name="${ccg.account.internalAccount.name}"
                                        displayStatus="${ccg.account.internalAccount.displayStatus}"/>
                    </display:column>
                
                    <display:column titleKey="campaign.advertiser">
                        <ui:accountLink id="${ccg.account.id}" name="${ccg.account.name}"
                                        displayStatus="${ccg.account.displayStatus}"/>
                    </display:column>

                    <display:column titleKey="campaign">
                        <ui:displayStatus displayStatus="${ccg.campaign.displayStatus}">
                            <a href="${viewCampaignUrl}?id=${ccg.campaign.id}"><c:out value="${ccg.campaign.name}"/></a>
                        </ui:displayStatus>
                    </display:column>

                    <display:column titleKey="creative.campaignCreative.creativeGroup">
                        <ui:displayStatus displayStatus="${ccg.displayStatus}">
                            <a href="${viewCCGUrl}?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                        </ui:displayStatus>
                    </display:column>
                </display:table>
            </td>
        </tr>
</table>

<ui:pages pageSize="${searchForm.pageSize}"
          total="${searchForm.total}"
          selectedNumber="${searchForm.page}"
          visiblePagesCount="10"
          handler="goToPage"
          displayHeader="false"/>

