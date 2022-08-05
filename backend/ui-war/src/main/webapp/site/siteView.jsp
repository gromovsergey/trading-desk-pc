<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ad:requestContext var="publisherContext"/>
<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted('PublisherEntity.update', site)}">
        <ui:button message="form.edit" href="edit.action?id=${id}"  />
    </c:if>
    <c:if test="${ad:isPermitted('Entity.viewLog', site)}">
        <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=1&id=${id}" />
    </c:if>
    <s:if test="allowAdvExclusionApproval">
     <c:if test="${ad:isPermitted('PublisherEntity.viewCreativesApproval', site)}">
        <ui:button message="site.creativesApproval" href="${_context}/site/creativesApproval/main.action?site.id=${id}" />
    </c:if>
    </s:if>
</ui:header>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<ui:section titleKey="form.main">
    <ui:fieldGroup>
        
        <s:if test="%{isInternal()}">
            <ui:simpleField labelKey="site.siteId" value="${id}"/>
        </s:if>
        
        <ui:field labelKey="site.status">
            <ui:statusButtonGroup
                descriptionKey="${displayStatus.description}"
                entity="${site}" restrictionEntity="PublisherEntity"
                deletePage="delete.action" undeletePage="undelete.action"
            >
                <ui:qaStatusButons entity="${site}" restrictionEntity="PublisherEntity"
                    approvePage="approve.action" declinePage="decline.action" />
            </ui:statusButtonGroup>
        </ui:field>
        
        <c:if test="${not empty siteUrl}">
            <c:choose>
              <c:when test="${ad:isUrl(siteUrl)}">
                  <ui:field labelKey="site.url">
                  <a href="<c:out value="${siteUrl}"/>" target="_blank"><c:out value="${siteUrl}"/></a>
                 <c:if test="${ad:isPermitted('Report.ReferrerReport.run', publisherContext.accountId)}">
                      &nbsp;
                      <ui:button message="reports.referrerReport" href="../reporting/referrer/options.action?siteId=${id}"/>
                  </c:if>
              </ui:field>
              </c:when>
              <c:otherwise>
                  <ui:simpleField labelKey="site.url" value="${siteUrl}"/>
              </c:otherwise>
            </c:choose>
        </c:if>

        <c:if test="${not empty siteCategory.id}">
            <ui:simpleField labelKey="site.category" value="${siteCategory.name}"/>
        </c:if>
        
        <c:if test="${allowFreqCaps and not empty noAdsTimeout}">
            <fmt:formatNumber var="noAdsTimeoutString" value="${noAdsTimeout}" groupingUsed="true" maxFractionDigits="0"/>
            <ui:simpleField labelKey="site.noAdsTimeout" value="${noAdsTimeoutString}"/>
        </c:if>
        
        <c:if test="${not empty notes}">
            <ui:simpleField labelKey="site.notes" value="${notes}"/>
        </c:if>
        
        <c:if test="${allowFreqCaps}">
            <ui:frequencyCapView/>
        </c:if>
        
    </ui:fieldGroup>
</ui:section>

<c:if test="${allowAdvExclusions && (not empty visualCategories or not empty contentCategories or not empty tagCategories)}">
    <h2><fmt:message key="site.advertiserExclusions"/></h2>
    <c:if test="${not empty visualCategories}">
        <ui:section titleKey="site.visualCategories">
            <ui:fieldGroup>
                
                <ui:fieldWithShowAll labelKey="site.accept" shortText="${visualCategoriesAcceptShort}" fullText="${visualCategoriesAcceptFull}" style="max-width:1000px;"/>
                
                <ui:fieldWithShowAll labelKey="site.reject" shortText="${visualCategoriesRejectShort}" fullText="${visualCategoriesRejectFull}" style="max-width:1000px;"/>
                
            </ui:fieldGroup>
        </ui:section>
    </c:if>
    <c:if test="${not empty contentCategories}">
        <ui:section titleKey="site.contentCategories">
            <ui:fieldGroup>

                <ui:fieldWithShowAll labelKey="site.accept" shortText="${contentCategoriesAcceptShort}" fullText="${contentCategoriesAcceptFull}" style="max-width:1000px;"/>

                <s:if test="allowAdvExclusionApproval">
                    <ui:fieldWithShowAll labelKey="site.approval" shortText="${contentCategoriesApprovalShort}" fullText="${contentCategoriesApprovalFull}" style="max-width:1000px;"/>
                </s:if>
                
                <ui:fieldWithShowAll labelKey="site.reject" shortText="${contentCategoriesRejectShort}" fullText="${contentCategoriesRejectFull}" style="max-width:1000px;"/>
                
            </ui:fieldGroup>
        </ui:section>
    </c:if>
    <c:if test="${not empty tagCategories}">
        <ui:section titleKey="site.excludedTags">
            <c:set var="textVal">
                <ad:commaWriter items="${tagCategories}" label="creativeCategory.defaultName" escape="false"/>
            </c:set>
            <ui:text text="${pageScope.textVal}"/>
        </ui:section>
    </c:if>
</c:if>

<ui:header styleClass="level2">
    <h2><fmt:message key="site.tags"/></h2>
    <c:if test="${ad:isPermitted('PublisherEntity.create', site)}">
        <ui:button message="form.createNew" href="${_context}/tag/new.action?site.id=${id}" />
    </c:if>
    <s:if test="siteTags.size != 0">
        <ui:button message="site.downloadAllTags" href="${_context}/site/downloadTagsSite.action?id=${id}" target="_blank"/>
    </s:if>
</ui:header>

<display:table name="siteTags" id="tag" class="dataView">
    <display:setProperty name="basic.msg.empty_list">
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>

    </display:setProperty>
    <display:column titleKey="site.tag.name">
        <ui:displayStatus displayStatus="${tag.displayStatus}">
            <a href="${_context}/tag/view.action?id=${tag.id}"><c:out value="${tag.name}"/></a>
        </ui:displayStatus>
    </display:column>
    <display:column titleKey="site.tag.size">
        <ad:commaWriter items="${tag.sizes}" var="size"><c:out value="${ad:localizeName(size.name)}"/></ad:commaWriter>
    </display:column>
    <display:column titleKey="site.tag.pricing">
        <c:if test="${not tag.inventoryEstimationFlag}">
            ${ad:formatTagPricingsForTag(tag)}
        </c:if>
    </display:column>
</display:table>

<c:if test="${allowWDTags}">
    
    <ui:header styleClass="level2">
        <h2><fmt:message key="site.wdtags"/></h2>
        <c:if test="${ad:isPermitted('PublisherEntity.createWDTag', site)}">
            <ui:button message="form.createNew" href="${_context}/site/WDTag/create.action?entity.site.id=${id}" />
        </c:if>
    </ui:header>

    <display:table name="siteWDTags" id="wdtag" class="dataView">
        <display:setProperty name="basic.msg.empty_list">
            <div class="wrapper">
                <fmt:message key="nothing.found.to.display"/>
            </div>
        </display:setProperty>
        <display:column titleKey="site.wdtag.name">
            <ui:displayStatus displayStatus="${wdtag.displayStatus}">
                <a href="${_context}/site/WDTag/view.action?id=${wdtag.id}"><c:out value="${wdtag.name}"/></a>
            </ui:displayStatus>
        </display:column>
        <display:column titleKey="site.wdtag.width">
            <fmt:formatNumber value="${wdtag.width}"/>
        </display:column>
        <display:column titleKey="site.wdtag.height">
            <fmt:formatNumber value="${wdtag.height}"/>
        </display:column>
    </display:table>

</c:if>
