<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted0('Country.update')}">
        <ui:button message="form.edit" href="edit.action?id=${id}" />
    </c:if>
    <s:url var="logUrl" value="/admin/auditLog/view.action">
            <s:param name="type">${ad:getObjectType(model)}</s:param>
            <s:param name="id">${country.countryId}</s:param>
    </s:url>
    <ui:button message="form.viewLog" href="${logUrl}"/>
</ui:header>

<ui:section titleKey="ctrAlgorithmData.historyAdjustments">
    <ui:fieldGroup>
        <ui:field>
            <s:text name="ctrAlgorithmData.historyClicks"/>&nbsp;=
            (<s:text name="ctrAlgorithmData.clicksLast"/>
            <s:property value="clicksInterval1Days"/>
            <s:text name="ctrAlgorithmData.days"/>)&nbsp;*
            <s:property value="clicksInterval1Weight"/>&nbsp;+
            (<s:text name="ctrAlgorithmData.clicks"/>&nbsp;<s:property value="clicksInterval1Days + 1"/>&nbsp;-
            <s:property value="clicksInterval2Days"/>
            <s:text name="ctrAlgorithmData.daysAgo"/>)&nbsp;*
            <s:property value="clicksInterval2Weight"/>&nbsp;+
            (<s:text name="ctrAlgorithmData.clicks"/>&nbsp;&gt;&nbsp;<s:property value="clicksInterval2Days"/>
            <s:text name="ctrAlgorithmData.daysAgo"/>)&nbsp;*
            <s:property value="clicksInterval3Weight"/>
        </ui:field>
        <ui:field>
            <s:text name="ctrAlgorithmData.historyImpressions"/>&nbsp;=
            (<s:text name="ctrAlgorithmData.impsLast"/>
            <s:property value="impsInterval1Days"/>
            <s:text name="ctrAlgorithmData.days"/>)&nbsp;*
            <s:property value="impsInterval1Weight"/>&nbsp;+
            (<s:text name="ctrAlgorithmData.imps"/>&nbsp;<s:property value="impsInterval1Days + 1"/>&nbsp;-
            <s:property value="impsInterval2Days"/>
            <s:text name="ctrAlgorithmData.daysAgo"/>)&nbsp;*
            <s:property value="impsInterval2Weight"/>&nbsp;+
            (<s:text name="ctrAlgorithmData.imps"/>&nbsp;&gt;&nbsp;<s:property value="impsInterval2Days"/>
            <s:text name="ctrAlgorithmData.daysAgo"/>)&nbsp;*
            <s:property value="impsInterval3Weight"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>
<ui:section titleKey="ctrAlgorithmData.publisherAdjustments">
    <ui:fieldGroup>
        <fmt:formatNumber value="${pubCTRDefaultPercent}" var="pubCTRDefault_f"/>
        <ui:simpleField labelKey="ctrAlgorithmData.pubCTRDefault" value="${pubCTRDefault_f}%"/>
        <ui:field labelKey="ctrAlgorithmData.impressionsLevels" cssClass="subsectionRow"/>
        <ui:simpleField labelKey="ctrAlgorithmData.sysCTRLevel" value="${sysCTRLevel}"/>
        <ui:simpleField labelKey="ctrAlgorithmData.pubCTRLevel" value="${pubCTRLevel}"/>
        <ui:simpleField labelKey="ctrAlgorithmData.siteCTRLevel" value="${siteCTRLevel}"/>
        <ui:simpleField labelKey="ctrAlgorithmData.tagCTRLevel" value="${tagCTRLevel}"/>
    </ui:fieldGroup>
</ui:section>
<ui:section titleKey="ctrAlgorithmData.KWTTG">
    <ui:fieldGroup>
        <fmt:formatNumber value="${kwtgCTRDefaultPercent}" var="kwtgCTRDefault_f"/>
        <ui:simpleField labelKey="ctrAlgorithmData.kwtgCTRDefault" value="${kwtgCTRDefault_f}%"/>
        <ui:field labelKey="ctrAlgorithmData.impressionsLevels" cssClass="subsectionRow"/>
        <ui:simpleField labelKey="ctrAlgorithmData.sysKwtgCTRLevel" value="${sysKwtgCTRLevel}"/>
        <ui:simpleField labelKey="ctrAlgorithmData.keywordCTRLevel" value="${keywordCTRLevel}"/>
        <ui:field labelKey="ctrAlgorithmData.KWinTGCTR">
            <s:text name="ctrAlgorithmData.forKWCTR"/>
            <s:property value="ccgkeywordKwCTRLevel"/>
            <s:text name="ctrAlgorithmData.forTGCTR"/>
            <s:property value="ccgkeywordTgCTRLevel"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>
<ui:section titleKey="ctrAlgorithmData.timeOfWeek">
    <ui:fieldGroup>
        <fmt:formatNumber value="${towRawPercent}" var="towRaw_f"/>
        <ui:simpleField labelKey="ctrAlgorithmData.towRaw" value="${towRaw_f}%"/>
        <ui:field labelKey="ctrAlgorithmData.impressionsLevels" cssClass="subsectionRow"/>
        <ui:simpleField labelKey="ctrAlgorithmData.sysTOWLevel" value="${sysTOWLevel}"/>
        <ui:simpleField labelKey="ctrAlgorithmData.campaignTOWLevel" value="${campaignTOWLevel}"/>
        <ui:simpleField labelKey="ctrAlgorithmData.tgTOWLevel" value="${tgTOWLevel}"/>
        <ui:simpleField labelKey="ctrAlgorithmData.keywordTOWLevel" value="${keywordTOWLevel}"/>
        <ui:field labelKey="ctrAlgorithmData.KWinTGTOW">
            <s:text name="ctrAlgorithmData.forKWTOW"/>
            <s:property value="ccgkeywordKwTOWLevel"/>
            <s:text name="ctrAlgorithmData.forTGTOW"/>
            <s:property value="ccgkeywordTgTOWLevel"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>
<ui:section titleKey="ctrAlgorithmData.randomAdserving">
    <ui:fieldGroup>
        <ui:simpleField labelKey="ctrAlgorithmData.cpcRandomImps" value="${cpcRandomImps}"/>
    </ui:fieldGroup>
    <ui:fieldGroup>
        <ui:simpleField labelKey="ctrAlgorithmData.cpaRandomImps" value="${cpaRandomImps}"/>
    </ui:fieldGroup>
</ui:section>

<s:if test="!advertiserExclusions.isEmpty() || !campaignExclusions.isEmpty()">
    <ui:section titleKey="ctrAlgorithmData.exclusions">
        <ui:fieldGroup>
            <s:if test="!advertiserExclusions.isEmpty()">
                <ui:field labelKey="ctrAlgorithmData.byAdvertiser">
                    <s:iterator value="namedAdvertiserExclusions" var="exclusion" status="iStatus">
                        <s:property value="#exclusion.name"/>
                        <s:if test="!#iStatus.last">,</s:if>
                    </s:iterator>
                </ui:field>
            </s:if>
            <ui:simpleField labelKey="ctrAlgorithmData.byCampaign" value="${campaignExclusionsText}"/>
        </ui:fieldGroup>
    </ui:section>
</s:if>
