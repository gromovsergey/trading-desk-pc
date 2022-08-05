<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:form action="%{#request.moduleName}/updateAuctionSettings" id="saveForm">
    <s:hidden name="id"/>
    <s:hidden name="version"/>

    <ui:pageHeadingByTitle/>

    <ui:header>
        <p><em><fmt:message key="AuctionSettings.tagFormTip"/></em></p>
    </ui:header>

    <ui:errorsBlock>
        <s:fielderror>
            <s:param value="'version'"/>
            <s:param value="'allocations'"/>
        </s:fielderror>
    </ui:errorsBlock>

    <ui:section titleKey="AuctionSettings.allocationsByType">
        <ui:fieldGroup>
            <ui:field labelKey="AuctionSettings.maximumECPM" labelForId="maxEcpmShare" errors="maxEcpmShare">
                <s:textfield name="maxEcpmShare" id="maxEcpmShare" cssClass="middleLengthNumber" maxLength="6"/>%
            </ui:field>
            <ui:field labelKey="AuctionSettings.proportionalProbability" labelForId="propProbabilityShare" errors="propProbabilityShare">
                <s:textfield name="propProbabilityShare" id="propProbabilityShare" cssClass="middleLengthNumber" maxLength="6"/>%
            </ui:field>
            <ui:field labelKey="AuctionSettings.random" labelForId="randomShare" errors="randomShare">
                <s:textfield name="randomShare" id="randomShare" cssClass="middleLengthNumber" maxLength="6"/>%
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <div class="wrapper">
        <ui:button message="form.save" type="submit"/>
        <s:if test="id == null">
            <ui:button message="form.cancel" type="button" onclick="location='list.action';"/>
        </s:if>
        <s:else>
            <ui:button message="form.cancel" type="button" onclick="location='view.action?id=${id}';"/>
        </s:else>
    </div>
</s:form>
