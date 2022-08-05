<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:section titleKey="form.main">
    <ui:fieldGroup>

        <s:set var="accountRoleString" value="getText('enum.accountRole.'+accountRole)"/>
        <ui:simpleField labelKey="AccountType.accountRole" value="${accountRoleString}"/>

        <c:if test="${accountRole.name == 'ISP'}">
            <ui:field labelKey="AccountType.advancedReports">
                <ui:text textKey="AccountType.advancedReports.${model.advancedReportsFlag}"/>
            </ui:field>
        </c:if>

        <c:if test="${accountRole.name == 'Advertiser' || accountRole.name == 'Publisher' || accountRole.name == 'Agency'}">
            <ui:field labelKey="AccountType.ownershipFlag">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${ownershipFlag}"><s:text name="AccountType.ownership.company"/></c:when>
                        <c:otherwise><s:text name="AccountType.ownership.individual"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
        </c:if>
        
        <c:if test="${accountRole.name == 'Publisher'}">
            <ui:field labelKey="AccountType.freqCaps">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${freqCapsFlag}"><s:text name="form.enabled"/></c:when>
                        <c:otherwise><s:text name="form.disabled"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
            
            <ui:field labelKey="AccountType.inventoryEstimationFlag">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${publisherInventoryEstimationFlag}"><s:text name="form.enabled"/></c:when>
                        <c:otherwise><s:text name="form.disabled"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
            
            <ui:field labelKey="AccountType.advertiserExclusionFlag">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${advExclusionSiteFlag && !advExclusionSiteTagFlag}"><s:text name="AccountType.siteLevel"/></c:when>
                        <c:when test="${advExclusionSiteTagFlag}"><s:text name="AccountType.siteTagLevel"/></c:when>
                        <c:otherwise><s:text name="form.disabled"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>

            <c:if test="${advExclusionSiteFlag || advExclusionSiteTagFlag}">
                <ui:field labelKey="AccountType.advExclusionApprovalFlag">
                    <c:set var="textVal">
                        <c:choose>
                            <c:when test="${advExclusionApprovalFlag}"><s:text name="form.enabled"/></c:when>
                            <c:otherwise><s:text name="form.disabled"/></c:otherwise>
                        </c:choose>
                    </c:set>
                    <ui:text text="${pageScope.textVal}"/>
                </ui:field>
            </c:if>

            <ui:field labelKey="AccountType.wdTagsFlag">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${wdTagsFlag}"><s:text name="form.enabled"/></c:when>
                        <c:otherwise><s:text name="form.disabled"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
            <ui:field labelKey="AccountType.iframeTag">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${showIframeTag}"><s:text name="form.show"/></c:when>
                        <c:otherwise><s:text name="form.hide"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
            <ui:field labelKey="AccountType.browserPassbackTag">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${showBrowserPassbackTag}"><s:text name="form.show"/></c:when>
                        <c:otherwise><s:text name="form.hide"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
            <ui:field labelKey="AccountType.clicksDataForExternalUsers">
                            <c:set var="textVal">
                                <c:choose>
                                    <c:when test="${clicksDataVisibleToExternal}"><s:text name="form.show"/></c:when>
                                    <c:otherwise><s:text name="form.hide"/></c:otherwise>
                                </c:choose>
                            </c:set>
                            <ui:text text="${pageScope.textVal}"/>
            </ui:field>
        </c:if>
        
        <c:if test="${accountRole.name == 'Advertiser' || accountRole.name == 'Agency'}">
            <ui:field labelKey="AccountType.siteTargetingFlag">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${siteTargetingFlag}"><s:text name="form.enabled"/></c:when>
                        <c:otherwise><s:text name="form.disabled"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>

            <c:if test="${accountRole.name == 'Agency'}">
                <ui:field labelKey="AccountType.financialFieldsFlag">
                    <c:set var="textVal">
                        <c:choose>
                            <c:when test="${financialFieldsFlag}"><s:text name="AccountType.financialFields.Advertiser"/></c:when>
                            <c:otherwise><s:text name="AccountType.financialFields.Agency"/></c:otherwise>
                        </c:choose>
                    </c:set>
                    <ui:text text="${pageScope.textVal}"/>
                </ui:field>
            </c:if>
            <ui:field labelKey="AccountType.invoicingFlag">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${invoicingFlag}"><s:text name="AccountType.invoicing.perAdvertiser"/></c:when>
                        <c:otherwise><s:text name="AccountType.invoicing.perCampaign"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>

            <c:if test="${accountRole.name == 'Agency'}">
            <ui:field labelKey="AccountType.inputRatesAndAmountsFlag">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${inputRatesAndAmountsFlag}"><s:text name="AccountType.inputRatesAndAmounts.Gross"/></c:when>
                        <c:otherwise><s:text name="AccountType.inputRatesAndAmounts.Net"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
            <ui:field labelKey="AccountType.auctionRate">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${auctionRate == 'N'}"><s:text name="AccountType.auctionRate.Net"/></c:when>
                        <c:otherwise><s:text name="AccountType.auctionRate.Gross"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
            </c:if>

            <c:if test="${accountRole.name == 'Agency'}">
                <ui:field labelKey="AccountType.commissionFlag">
                    <c:set var="textVal">
                        <c:choose>
                            <c:when test="${invoiceCommissionFlag}"><s:text name="AccountType.commission.include"/></c:when>
                            <c:otherwise><s:text name="AccountType.commission.notInclude"/></c:otherwise>
                        </c:choose>
                    </c:set>
                    <ui:text text="${pageScope.textVal}"/>
                </ui:field>
            </c:if>

            <ui:field labelKey="AccountType.ioManagement">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${ioManagement}"><s:text name="form.enabled"/></c:when>
                        <c:otherwise><s:text name="form.disabled"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>

            <c:if test="${accountRole.name == 'Advertiser' || accountRole.name == 'Agency'}">
                <ui:field labelKey="AccountType.billingModelFlag">
                    <c:set var="textVal">
                        <c:choose>
                            <c:when test="${billingModelFlag}"><s:text name="AccountType.billingModel.publisher"/></c:when>
                            <c:otherwise><s:text name="AccountType.billingModel.advertiser"/></c:otherwise>
                        </c:choose>
                    </c:set>
                    <ui:text text="${pageScope.textVal}"/>
                </ui:field>
            </c:if>
        </c:if>
    </ui:fieldGroup>
</ui:section>

<s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' || accountRole.name == 'CMP' || accountRole.name == 'Internal'">
    <ui:section titleKey="AccountType.checkSettings.channelCheckPeriods">
        <ui:fieldGroup>
        <s:if test="not channelCheck">
            <fmt:message key="form.disabled"/>
        </s:if>
        <s:else>
                <s:iterator value="channelChecks" var="channelCheckEntry">
                    <c:set var="checkNumberLabelKey">AccountType.checkSettings.${channelCheckEntry.key}Check</c:set>
                    <ui:field labelKey="${checkNumberLabelKey}" errors="channel${channelCheckEntry.key}Check" >
                        <c:out value="${channelCheckEntry.value.value}">0</c:out>
                                <fmt:message key="${channelCheckEntry.value.uom.nameKey}"/>
                    </ui:field> 
                </s:iterator>
         </s:else>
        </ui:fieldGroup>
    </ui:section>
</s:if>

<s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser'">
    <ui:section titleKey="AccountType.checkSettings.campaignCheckPeriods">
        <ui:fieldGroup>
        <s:if test="not campaignCheck">
            <fmt:message key="form.disabled"/>
        </s:if>
        <s:else>
                <s:iterator value="campaignChecks" var="campaignCheckEntry">
                    <c:set var="checkNumberLabelKey">AccountType.checkSettings.${campaignCheckEntry.key}Check</c:set>
                    <ui:field labelKey="${checkNumberLabelKey}" errors="channel${campaignCheckEntry.key}Check" >
                        <c:out value="${campaignCheckEntry.value.value}">0</c:out>
                                <fmt:message key="${campaignCheckEntry.value.uom.nameKey}"/>
                    </ui:field> 
                </s:iterator>
         </s:else>
        </ui:fieldGroup>
    </ui:section>
</s:if>


<c:if test="${accountRole.name == 'Advertiser' || accountRole.name == 'Agency'}">
    <h1><s:text name="AccountType.rateTypes"/></h1>
    
    <ui:section titleKey="AccountType.displayCampaigns">
        <ui:section titleKey="AccountType.displayCreativeGroups">
            <ui:fieldGroup>
            
                <ui:field labelKey="AccountType.CPM">
                    <c:set var="textVal">
                        <c:choose>
                            <c:when test="${displayCPMFlag}"><s:text name="yes"/></c:when>
                            <c:otherwise><s:text name="no"/></c:otherwise>
                        </c:choose>
                    </c:set>
                    <ui:text text="${pageScope.textVal}"/>
                </ui:field>
            
                <ui:field labelKey="AccountType.CPC">
                    <c:set var="textVal">
                        <c:choose>
                            <c:when test="${displayCPCFlag}"><s:text name="yes"/></c:when>
                            <c:otherwise><s:text name="no"/></c:otherwise>
                        </c:choose>
                    </c:set>
                    <ui:text text="${pageScope.textVal}"/>
                </ui:field>
            
                <ui:field labelKey="AccountType.CPA">
                    <c:set var="textVal">
                        <c:choose>
                            <c:when test="${displayCPAFlag}"><s:text name="yes"/></c:when>
                            <c:otherwise><s:text name="no"/></c:otherwise>
                        </c:choose>
                    </c:set>
                    <ui:text text="${pageScope.textVal}"/>
                </ui:field>
            
            </ui:fieldGroup>
        </ui:section>
    </ui:section>
    
    <ui:section titleKey="AccountType.textCampaigns">
        <ui:fieldGroup>
            
            <ui:field labelKey="AccountType.keywordTargeted">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${keywordTargetedFlag}"><s:text name="yes"/></c:when>
                        <c:otherwise><s:text name="no"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
        </ui:fieldGroup>
            
        <div class="sectionName">
            <s:text name="AccountType.channelTargeted"/>
        </div>
        
        <ui:fieldGroup>
            <ui:field labelKey="AccountType.CPM">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${textCPMFlag}"><s:text name="yes"/></c:when>
                        <c:otherwise><s:text name="no"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
            
            <ui:field labelKey="AccountType.CPC">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${textCPCFlag}"><s:text name="yes"/></c:when>
                        <c:otherwise><s:text name="no"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
            
            <ui:field labelKey="AccountType.CPA">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${textCPAFlag}"><s:text name="yes"/></c:when>
                        <c:otherwise><s:text name="no"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
        </ui:fieldGroup>
            
    </ui:section>
    
</c:if> 

<s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' ">
    <ui:section titleKey="AccountType.deviceTargeting" id="mobileOptions" errors="mobileOptions">
        <ui:fieldGroup>
            <ui:field>
            <s:iterator value="browsersChannelsIterator" status="index" var="browsersChannel">
                <c:if test="${ad:contains(deviceHelper.selectedChannels, browsersChannel.element.id)}">
                    <c:if test="${not index.first}">
                        <span class='delimiter'>,</span>
                    </c:if>
                    <a href="/admin/DeviceChannel/view.action?id=${browsersChannel.element.id}"><c:out value="${browsersChannel.element.name}"/></a>
                </c:if>
            </s:iterator>
            <br/>
            <s:iterator value="applicationsChannelsIterator" status="index" var="applicationsChannel">
                <c:if test="${ad:contains(deviceHelper.selectedChannels, applicationsChannel.element.id)}">
                    <c:if test="${not index.first}">
                        <span class='delimiter'>,</span>
                    </c:if>
                    <a href="/admin/DeviceChannel/view.action?id=${applicationsChannel.element.id}"><c:out value="${applicationsChannel.element.name}"/></a>
                </c:if>
            </s:iterator>
            </ui:field>
        </ui:fieldGroup>
         
    </ui:section>
</s:if>

<s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' || accountRole.name == 'CMP' ">
    <ui:section titleKey="AccountType.keywordAndUrlLimits">
        <ui:fieldGroup>
            <ui:simpleField labelKey="AccountType.maxKeywordLength" value="${maxKeywordLength}"/>
            <ui:simpleField labelKey="AccountType.maxUrlLength" value="${maxUrlLength}"/>
            <s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' ">
                <ui:simpleField labelKey="AccountType.maxKeywordsPerGroup" value="${maxKeywordsPerGroup}"/>
            </s:if>
            <ui:simpleField labelKey="AccountType.maxKeywordsPerChannel" value="${maxKeywordsPerChannel}"/>
            <ui:simpleField labelKey="AccountType.maxUrlsPerChannel" value="${maxUrlsPerChannel}"/>
        </ui:fieldGroup>
    </ui:section>
</s:if>

<c:if test="${not empty nonTextSizes
              and (accountRole.name == 'Advertiser' or accountRole.name == 'Publisher' or accountRole.name == 'Agency')}">
    
    <ui:section titleKey="CreativeSize.plural">
        <ad:commaWriter var="size" items="${nonTextSizes}"><c:out value="${ad:localizeNameWithStatus(size.name, size.status)}"/></ad:commaWriter>
    </ui:section>
</c:if>
<c:if test="${not empty nonTextTemplates}">
    <c:choose>
        <c:when test="${accountRole.name == 'Advertiser' or accountRole.name == 'Agency'}">
            <ui:section titleKey="CreativeTemplate.plural">
                <ad:commaWriter var="template" items="${nonTextTemplates}"><c:out value="${ad:localizeNameWithStatus(template.name, template.status)}"/></ad:commaWriter>
            </ui:section>
        </c:when>
        <c:when test="${accountRole.name == 'Publisher'}">
            <ui:section titleKey="DiscoverTemplate.plural">
                <ad:commaWriter var="template" items="${nonTextTemplates}"><c:out value="${ad:localizeNameWithStatus(template.name, template.status)}"/></ad:commaWriter>
            </ui:section>
        </c:when>
    </c:choose>
</c:if>
