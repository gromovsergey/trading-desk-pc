<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:externalLibrary libName="codemirror" />

<ad:requestContext var="advertiserContext"/>

<c:set var="accountId" value="${advertiserContext.accountId}" scope="page"/>

<c:if test="${param['createNew'] == 'true'}">
    <c:if test="${ad:isPermitted('AdvertiserEntity.create', model.account)}">
        <ui:section>
            <fmt:message key="creative.new.message"/>&nbsp;
            <c:choose>
                <c:when test="${not empty paramValues['ccgId']}">
                    <c:set var="createParameter">
                        <ad:commaWriter items="${paramValues['ccgId']}" var="value" separator="&">ccgId=${value}</ad:commaWriter>
                    </c:set>
                </c:when>
                <c:otherwise>
                    <c:set var="createParameter" value="advertiserId=${advertiserContext.advertiserId}"/>
                </c:otherwise>
            </c:choose>
            <a href="new${textCreative ? "Text": "Display"}.action?${createParameter}"><fmt:message key="creative.new.link"/></a>
        </ui:section>
    </c:if>
</c:if>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted('AdvertiserEntity.update', model)}">
        <ui:button message="form.edit" href="edit.action?id=${id}" />
    </c:if>

    <c:if test="${ad:isPermitted('AdvertiserEntity.createCopy', model)}">
        <ui:button message="form.copyAndEdit" href="copy.action?id=${id}" />
    </c:if>
    
    <c:if test="${ad:isPermitted('Entity.viewLog', model)}">
        <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=7&id=${id}" />
    </c:if>
</ui:header>

<s:fielderror><s:param value="'name'"/></s:fielderror>

<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="creative.template">
            <c:choose>
                <c:when test="${ad:isPermitted0('Template.view')}">
                        <ui:displayStatus displayStatus="${template.displayStatus}">
                            <a href="/admin/CreativeTemplate/view.action?id=${template.id}"><c:out value="${ad:localizeName(template.name)}"/></a>
                        </ui:displayStatus>
                </c:when>
                <c:otherwise>
                        <ui:displayStatus displayStatus="${template.displayStatus}">${ad:localizeName(template.name)}</ui:displayStatus>
                </c:otherwise>
            </c:choose>
        </ui:field>
        
        <s:if test="tnsBrand != null">
            <ui:simpleField labelKey="creative.tnsBrandIdName"
            value="${tnsBrand.id} - ${tnsBrand.name}"/>
        </s:if>
        <s:elseif test="showAdvertiserTnsBrand">
            <ui:simpleField labelKey="creative.tnsBrandIdName"
            value="${account.tnsBrand.id} - ${account.tnsBrand.name}"/>
        </s:elseif>


        
        <ui:field labelKey="creative.size">
            <c:choose>
                <c:when test="${ad:isPermitted0('CreativeSize.view')}">
                    <ui:displayStatus displayStatus="${size.displayStatus}">
                        <a href="/admin/CreativeSize/view.action?id=${size.id}"><c:out value="${ad:localizeName(size.name)}"/></a>
                    </ui:displayStatus>
                </c:when>
                <c:otherwise>
                    <ui:displayStatus displayStatus="${size.displayStatus}">${ad:localizeName(size.name)}</ui:displayStatus>
                </c:otherwise>
            </c:choose>
        </ui:field>

        <ui:field labelKey="creative.status">
            <ui:statusButtonGroup
                descriptionKey="${displayStatus.description}"
                entity="${model}" restrictionEntity="AdvertiserEntity"
                activatePage="activate.action" inactivatePage="inactivate.action"
                deletePage="delete.action" undeletePage="undelete.action"
                >
                <ui:qaStatusButons entity="${model}" restrictionEntity="AdvertiserEntity"
                    approvePage="approve.action" declinePage="decline.action"
                    />
            </ui:statusButtonGroup>
        </ui:field>

        <s:if test="isInternal() && showSecureAdServing">
            <ui:field labelKey="creative.secureAdServing">
                <c:set var="textVal">
                    <c:choose>
                        <c:when test="${secureAdServing}"><s:text name="yes"/></c:when>
                        <c:otherwise><s:text name="no"/></c:otherwise>
                    </c:choose>
                </c:set>
                <ui:text text="${pageScope.textVal}" />
            </ui:field>
        </s:if>

        <s:if test="expandable">
            <ui:field labelKey="creative.expandableCreative">
                <c:set var="textVal">creative.expandable.${expandable}
                </c:set>
                <ui:text textKey="${pageScope.textVal}"/>
            </ui:field>

            <ui:field labelKey="creative.expansionDirection">
                <ui:text textKey="enums.CreativeSizeExpansion.${expansion}"/>
            </ui:field>

        </s:if>


    </ui:fieldGroup>
</ui:section>

<ui:section titleKey="creative.preview">
    <ui:creativePreview creativeId="${id}" sizeId="${size.id}" templateId="${template.id}"/>
</ui:section>
<c:if test="${textCreative}">
    <c:forEach var="tagSize" items="${previewTagSizes}">
        <fmt:message var="sectionTitle" key="creative.preview.size">
            <fmt:param>${ad:localizeName(tagSize.name)}</fmt:param>
        </fmt:message>
        <ui:section title="${sectionTitle}">
            <ui:creativePreview creativeId="${id}" sizeId="${tagSize.id}" templateId="${template.id}" style="overflow:hidden; margin-top:5px;"/>
        </ui:section>
    </c:forEach>
</c:if>



<ui:section titleKey="creative.options" present="${optionsDisplayed}">
    <ui:fieldGroup>
        <s:iterator value="options" var="opt">
            <s:set var="optValue" value="optionValues[#opt.id]"/>
            <s:set var="outputValue" value="#optValue.value"/>
            <c:if test="${optValue == null}">
                <s:set var="outputValue" value="#opt.defaultValue"/>
            </c:if>
            <c:if test="${internal || !opt.internalUse}">
                <c:if test="${not empty outputValue}">
                    <ui:field label="${ad:localizeName(opt.name)}" id="selenium_option_id_${opt.id}">
                        <c:choose>
                            <c:when test="${opt.type == 'STRING'}">
                                <ui:text text="${outputValue}"/>
                            </c:when>
                            <c:when test="${opt.type == 'ENUM'}">
                                <s:set var="enumName" value="enumName(#opt, #outputValue)"/>
                                <ui:text text="${enumName}"/>
                            </c:when>
                            <c:when test="${opt.type == 'INTEGER'}">
                                <fmt:formatNumber var="textVal" value="${outputValue}" groupingUsed="true"/>
                                <ui:text text="${textVal}"/>
                            </c:when>
                            <c:when test="${opt.type == 'TEXT'}">
                                <ui:text text="${outputValue}"/>
                            </c:when>
                            <c:when test="${opt.type == 'HTML'}">
                                <textarea data-readonly="true" class="html_highlight">${outputValue}</textarea>
                            </c:when>
                            <c:when test="${opt.type == 'FILE' || opt.type == 'DYNAMIC_FILE' || (opt.type == 'FILE_URL' && optValue != null && optValue.isFile())}">
                                <s:set var="fileUrl" value="fileUrl(#optValue)"/>
                                <a target="_blank" href="<c:url value="${fileUrl}"/>"><c:out value="${optValue.fileStripped}"/></a>
                            </c:when>
                            <c:when test="${opt.type == 'URL' || opt.type == 'URL_WITHOUT_PROTOCOL' || (opt.type == 'FILE_URL' && (optValue == null || optValue.isUrl()))}">
                                <a target="_blank" href="<c:out value="${outputValue}"/>"><c:out value="${outputValue}"/></a>
                            </c:when>
                            <c:when test="${opt.type == 'COLOR'}">
                                <c:set var="textVal">#${outputValue}</c:set>
                                <span class="colorInput">
                                    <ui:text text="${textVal}"/>
                                    <input onfocus="this.blur();"
                                           class="colorBox"
                                           type="text"
                                           readonly="readonly"
                                           style="background-color:#${outputValue};"
                                           tabindex="-1"/>
                                </span>
                            </c:when>
                        </c:choose>
                    </ui:field>
                </c:if>
            </c:if>
        </s:iterator>
    </ui:fieldGroup>
</ui:section>

<ui:section titleKey="creative.categories">
    <ui:fieldGroup>

        <c:if test="${not empty visualCategories}">
            <c:set var="visualCategoriesString">
                <ad:commaWriter items="${visualCategories}" label="name" escape="false"/>
            </c:set>
            <ui:simpleField labelKey="creative.categories.visual" value="${visualCategoriesString}"/>
        </c:if>
        <c:if test="${not empty contentCategories}">
            <c:set var="contentCategoriesString">
                <ad:commaWriter items="${contentCategories}" label="name" escape="false"/>
            </c:set>
            <ui:simpleField labelKey="creative.categories.content" value="${contentCategoriesString}"/>
        </c:if>
        <c:if test="${not empty tags}">
            <c:set var="tagsString">
                <ad:commaWriter items="${tags}" label="name" escape="false" />
            </c:set>
            <ui:simpleField labelKey="creative.categories.tags" value="${tagsString}"/>
        </c:if>
    </ui:fieldGroup>
</ui:section>

<%@ include file="ccgView.jsp"%>

<c:if test="${not empty siteApprovals}">
    <ui:header>
        <h2><fmt:message key="creative.siteApprovals"/></h2>
        <c:if test="${ad:isPermitted('AdvertiserEntity.update', model) && hasRejected}">
            <ui:postButton message="creative.resubmitForApproval" href="reset.action" entityId="${id}"
                           onclick="if (!confirm('${ad:formatMessage('creative.confirmResubmitForApproval')}')) {return false;}"/>
        </c:if>
    </ui:header>
    <display:table name="siteApprovals" class="dataView" id="approval">
        <display:column titleKey="creative.siteApprovals.publisher">
            <ui:displayStatus displayStatus="${approval.publisherDisplayStatus}">
                <c:choose>
                    <c:when test="${ad:isPermitted('Account.view', 'Publisher')}">
                        <a href="${_context}/publisher/account/view.action?id=${approval.publisher.id}">
                            <c:out value="${approval.publisher.name}"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <c:out value="${approval.publisher.name}"/>
                    </c:otherwise>
                </c:choose>
            </ui:displayStatus>
        </display:column>
        <display:column titleKey="creative.siteApprovals.site">
            <ui:displayStatus displayStatus="${approval.siteDisplayStatus}">
                <c:choose>
                    <c:when test="${ad:isPermitted0('PublisherEntity.view')}">
                        <a href="${_context}/site/view.action?id=${approval.site.id}">
                            <c:out value="${approval.site.name}"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <c:out value="${approval.site.name}"/>
                    </c:otherwise>
                </c:choose>
            </ui:displayStatus>
        </display:column>
        <display:column titleKey="creative.siteApprovals.status">
            <c:choose>
                 <c:when test="${approval.approvalStatus == 'APPROVED'}">
                    <fmt:message key="creative.siteApprovals.approvedManually"/>
                </c:when>
                <c:when test="${approval.approvalStatus == 'PENDING'}">
                    <fmt:message key="creative.siteApprovals.pending"/>
                </c:when>
                <c:when test="${approval.approvalStatus == 'REJECTED' and empty approval.feedback}">
                    <fmt:message key="creative.siteApprovals.rejected">
                        <fmt:param><fmt:message key="enums.CreativeRejectReason.${approval.rejectReason}"/></fmt:param>
                    </fmt:message>
                </c:when>
                <c:otherwise>
                    <fmt:message key="creative.siteApprovals.rejectedWithFeedback">
                        <fmt:param><fmt:message key="enums.CreativeRejectReason.${approval.rejectReason}"/></fmt:param>
                        <fmt:param><c:out value="${approval.feedback}"/></fmt:param>
                    </fmt:message>
                </c:otherwise>
            </c:choose>
        </display:column>
    </display:table>
</c:if>

