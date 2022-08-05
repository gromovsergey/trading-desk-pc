<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/struts-tags" prefix="s"%>

<ad:requestContext var="publisherContext"/>
<c:set var="reporting" value="${_context}/reporting"/>

<ui:header>
    <ui:pageHeadingByTitle />
    <ui:externalLibrary libName="codemirror" />
    
    <c:if test="${ad:isPermitted('PublisherEntity.update', tag)}">
        <ui:button message="form.edit" href="edit.action?id=${id}" />
    </c:if>
    <c:if test="${!inventoryEstimationFlag}">
        <ui:button message="site.edittag.preview" href="preview.action?id=${id}"
                   target="_blank" id="_previewButton"/>
    </c:if>
    
    <c:if test="${ad:isPermitted('PublisherEntity.updateOptions', tag) && availableCreativeCustomization}">
        <ui:button message="site.edittag.editCreativeCustomization" href="editCreativeCustomization.action?id=${id}"/>
    </c:if>

    <c:if test="${ad:isPermitted('Report.ReferrerReport.run', publisherContext.accountId)}">
        <ui:button message="reports.referrerReport" href="${reporting}/referrer/options.action?tagId=${id}"  />
    </c:if>

    <c:set var="entityBean" value="${tag}"/>
    <%@ include file="../auditLog/viewLogButton.jspf" %>
</ui:header>

    <ui:section titleKey="form.main">
        <ui:fieldGroup>

        <ui:field labelKey="site.tag.status">
            <c:set var="statusDescriptionKey">enums.Status.${status}</c:set>
            <ui:statusButtonGroup
                descriptionKey="${statusDescriptionKey}"
                entity="${tag}" restrictionEntity="PublisherEntity"
                deletePage="delete.action" undeletePage="undelete.action" />
        </ui:field>

        <ui:field labelKey="site.edittag.sizeType">
            <c:choose>
                <c:when test="${ad:isPermitted0('CreativeSize.view')}">
                    <a href="/admin/SizeType/view.action?id=${sizeType.id}"><c:out value="${ad:localizeName(sizeType.name)}"/></a>
                </c:when>
                <c:otherwise>
                    <c:out value="${ad:localizeName(sizeType.name)}"/>
                </c:otherwise>
            </c:choose>
        </ui:field>

        <ui:field labelKey="${tag.sizeType.multiSize ? 'site.edittag.creativeSizes' : 'site.edittag.creativeSize'}">
            <c:if test="${tag.allSizesFlag}">
                <fmt:message key="site.edittag.creativeSizes.all"/>
            </c:if>
            <s:iterator var="size" value="%{sortedSizes}" status="iter">
                <ui:displayStatus displayStatus="${size.displayStatus}">
                    <c:choose>
                        <c:when test="${ad:isPermitted0('CreativeSize.view')}">
                            <a href="/admin/CreativeSize/view.action?id=${size.id}"><c:out value="${ad:localizeName(size.name)}"/></a><s:if test="!#iter.last">,</s:if>
                        </c:when>
                        <c:otherwise>
                            ${ad:localizeName(size.name)}<s:if test="!#iter.last">,</s:if>
                        </c:otherwise>
                    </c:choose>
                </ui:displayStatus>
            </s:iterator>
        </ui:field>

         <ui:field labelKey="tags.allowExpandableCreative">
             <c:set var="allowExpandableCreativeKey">tags.allowExpandableCreative.${allowExpandable}</c:set>
             <ui:text textKey="${pageScope.allowExpandableCreativeKey}" />
         </ui:field>

            <c:if test="${!inventoryEstimationFlag}">
                <ui:field labelKey="site.edittag.tagPricings">
                    <table class="dataView" id="address">
                        <thead>
                            <tr>
                                <th><fmt:message key="site.edittag.tagPricings.country"/></th>
                                <th><fmt:message key="creative.type"/></th>
                                <th><fmt:message key="ccg.rate.type"/></th>
                                <th><fmt:message key="site.edittag.tagPricings.rate"/></th>
                            </tr>
                        </thead>
                        <tbody>
                        <s:iterator value="pricings" var="tPricing" status="tPricingStatus">
                            <tr>
                                <c:choose>
                                    <c:when test="${tPricingStatus.first}">
                                        <td colspan="3">
                                            <fmt:message key="site.edittag.tagPricings.country.defaultWorldwide"/>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>
                                            <c:choose>
                                                <c:when test="${tPricing.country != null }">
                                                    <ad:resolveGlobal resource="country" id="${tPricing.country.countryCode}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <fmt:message key="site.edittag.tagPricings.country.defaultWorldwide"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${tPricing.ccgType == null}">
                                                    <fmt:message key="ccg.type.All"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <fmt:message key="ccg.type.${tPricing.ccgType.pageExtension}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${tPricing.ccgRateType == null}">
                                                    <fmt:message key="enum.RateType.All"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <fmt:message key="enum.RateType.${tPricing.ccgRateType.name}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </c:otherwise>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${tPricing.siteRate.rateType=='CPM'}">
                                        <td class="currency">
                                                ${ad:formatCurrency(tPricing.siteRate.rate, account.currency.currencyCode)}
                                            <fmt:message key="enums.SiteRateType.CPM"/>
                                        </td>
                                    </c:when>
                                    <c:when test="${tPricing.siteRate.rateType=='RS'}">
                                        <td class="currency">
                                                <fmt:formatNumber value="${tPricing.siteRate.ratePercent}" minFractionDigits="2"/>%
                                            <fmt:message key="enums.SiteRateType.RS"/>
                                        </td>
                                    </c:when>
                                </c:choose>
                            </tr>
                        </s:iterator>
                        </tbody>
                    </table>
                </ui:field>
            </c:if>

            <c:if test="${ad:isPermitted0('AuctionSettings.view')}">
                <ui:field labelKey="AuctionSettings.title">
                    <table>
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${empty tagAuctionSettings}">
                                        <fmt:message key="AuctionSettings.defaultValues">
                                            <fmt:param>
                                                <fmt:formatNumber value="${defaultAuctionSettings.maxEcpmShare}"/>
                                            </fmt:param>
                                            <fmt:param>
                                                <fmt:formatNumber value="${defaultAuctionSettings.propProbabilityShare}"/>
                                            </fmt:param>
                                            <fmt:param>
                                                <fmt:formatNumber value="${defaultAuctionSettings.randomShare}"/>
                                            </fmt:param>
                                        </fmt:message>
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:message key="AuctionSettings.allocationsValues">
                                            <fmt:param>
                                                <fmt:formatNumber value="${tagAuctionSettings.maxEcpmShare}"/>
                                            </fmt:param>
                                            <fmt:param>
                                                <fmt:formatNumber value="${tagAuctionSettings.propProbabilityShare}"/>
                                            </fmt:param>
                                            <fmt:param>
                                                <fmt:formatNumber value="${tagAuctionSettings.randomShare}"/>
                                            </fmt:param>
                                        </fmt:message>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <ui:hint><fmt:message key="AuctionSettings.allocationsValues.hint"/></ui:hint>
                            </td>
                            <td>
                                <c:if test="${ad:isPermitted('AuctionSettings.update', tag)}">
                                    <ui:button message="form.edit" href="editAuctionSettings.action?id=${id}"/>
                                </c:if>
                            </td>
                        </tr>
                    </table>
                </ui:field>
            </c:if>

            <c:if test="${!inventoryEstimationFlag}">
                <c:if test="${not empty passback}">
                    <ui:field labelKey="site.edittag.passback" labelForId="name" errors="file.passback">
                        <s:if test="passbackHtml != null">
                            <c:if test="${not empty passbackHtml}">
                                <textarea id="passbackHtml" data-readonly="true" class="bigLengthText html_highlight"><c:out value="${passbackHtml}"/></textarea>
                            </c:if>
                        </s:if>
                        <s:elseif test="isUrl(passback)">
                            <a href="<c:out value="${passback}"/>" target="_blank"><c:out value="${passback}"/></a>
                        </s:elseif>
                    </ui:field>
                </c:if>

                <c:if test="${not empty tagView}">
                    <ui:field labelKey="site.edittag.tagHtmlCode" labelForId="name">
                        <textarea name="tagView" id="tagView" data-readonly="true" class="html_highlight"><c:out value="${tagView}"/></textarea>
                    </ui:field>
                </c:if>

                <c:if test="${showIframeTag and not empty iframeTagView}">
                    <ui:field labelKey="site.tag.iframeTag" labelForId="name">
                        <textarea name="iframeTagView" id="iframeTagView" data-readonly="true" class="html_highlight"><c:out value="${iframeTagView}"/></textarea>
                    </ui:field>
                </c:if>

                <c:if test="${showBrowserPassbackTag and not empty browserPassbackTagView}">
                    <ui:field labelKey="site.tag.browserPassbackTag" labelForId="name">
                        <textarea name="browserPassbackTagView" id="browserPassbackTagView" data-readonly="true" class="html_highlight"><c:out value="${browserPassbackTagView}"/></textarea>
                    </ui:field>
                    <ui:field>
                        <span style="font-style: italic;"><fmt:message key="site.tag.browserPassbackTag.tooltip"/></span>
                    </ui:field>
                </c:if>

            </c:if>

            <c:if test="${inventoryEstimationFlag and not empty inventoryEstimationTagView}">
                <ui:field labelKey="site.edittag.inventoryEstimationTagHtmlCode" id="_inventoryEstimationTagHtmlCode">
                    <table class="fieldAndAccessories">
                        <tr>
                            <td class="withField">
                                <textarea name="inventoryEstimationTagView" id="inventoryEstimationTagView" class="html_highlight" data-readonly="true" cols="70" rows="7" style="height:200px"><c:out value="${inventoryEstimationTagView}"/></textarea>
                            </td>
                        </tr>
                        <tr>
                            <td class="withButton">
                                <c:if test="${ad:isPermitted('Report.run', 'inventoryEstimation')}">
                                    <ui:button message="reports.inventoryEstimationReport" href="${_context}/reporting/inventoryEstimation/options.action?tagId=${id}" />
                                </c:if>
                            </td>
                        </tr>
                    </table>
                </ui:field>
            </c:if>

            <c:set var="marketplaceType" value="${tag.marketplaceType}"/>
            <c:if test="${marketplaceType != 'NOT_SET'}">
            <ui:field labelKey="site.tag.wgSettings">
                <c:set var="textVal">
                    <fmt:message key="WalledGarden.publisher.marketplace.${marketplaceType}"/>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
            </c:if>
            
            <c:if test="${tag.contentCategories.size() > 0}">
                <c:set var="contentCategoriesString">
                    <ad:commaWriter items="${contentCategories}" label="name" escape="false"/>
                </c:set>
                <ui:simpleField labelKey="site.tag.category" value="${contentCategoriesString}"/>
            </c:if>

            <c:if test="${exclusionFlagAccountLevel}">
                <ui:field labelKey="site.edittag.tagExclusions" >
                        <c:choose>
                    <c:when test="${tagLevelExclusionFlag}">
                        <ui:text textKey="form.enabled"/>
                    </c:when>
                    <c:otherwise>
                        <ui:text textKey="form.disabled"/>
                    </c:otherwise>
                        </c:choose>
                </ui:field>
            </c:if>

            <c:if test="${tagLevelExclusionFlag}">
                <ui:field labelKey="site.visualCategories" cssClass="subsectionRow" />

                <ui:fieldWithShowAll labelKey="site.tag.accept" shortText="${visualCategoriesAcceptShort}" fullText="${visualCategoriesAcceptFull}"/>

                <ui:fieldWithShowAll labelKey="site.tag.reject" shortText="${visualCategoriesRejectShort}" fullText="${visualCategoriesRejectFull}"/>
            </c:if>
        </ui:fieldGroup>
    </ui:section>
