<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:form action="%{#request.moduleName}/updateAuctionSettings" id="actionSave">
    <s:hidden name="id"/>
    <s:hidden name="version"/>

    <ui:pageHeadingByTitle/>

    <ui:errorsBlock>
        <s:fielderror>
            <s:param value="'version'"/>
            <s:param value="'allocations'"/>
        </s:fielderror>
    </ui:errorsBlock>

    <ui:section titleKey="AuctionSettings.allocationsByType">
        <ui:fieldGroup>
            <ui:field labelKey="AuctionSettings.maximumECPM" labelForId="maxEcpmShare" errors="maxEcpmShare" required="true">
                <s:textfield name="maxEcpmShare" id="maxEcpmShare" cssClass="middleLengthNumber" maxLength="6"/>%
            </ui:field>
            <ui:field labelKey="AuctionSettings.proportionalProbability" labelForId="propProbabilityShare" errors="propProbabilityShare" required="true">
                <s:textfield name="propProbabilityShare" id="propProbabilityShare" cssClass="middleLengthNumber" maxLength="6"/>%
            </ui:field>
            <ui:field labelKey="AuctionSettings.random" labelForId="randomShare" errors="randomShare" required="true">
                <s:textfield name="randomShare" id="randomShare" cssClass="middleLengthNumber" maxLength="6"/>%
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <fmt:message var="maxRandomCpmLabel" key="AuctionSettings.maximumECPM"/>
    <ui:section titleKey="AuctionSettings.randomAuctionSettings">
        <ui:fieldGroup>
            <ui:field label="${maxRandomCpmLabel} (${ad:currencySymbol(internalAccount.currency.currencyCode)})" labelForId="maxRandomCpm" errors="maxRandomCpm" required="true">
                <s:textfield name="maxRandomCpm" id="maxRandomCpm" cssClass="middleLengthNumber" maxLength="12"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <div class="wrapper">
        <ui:button message="form.save" type="submit"/>
        <s:if test="id == null">
            <ui:button message="form.cancel" type="button" onclick="location='list.action';"/>
        </s:if>
        <s:else>
            <ui:button message="form.cancel" type="button" onclick="location='viewAuctionSettings.action?id=${id}';"/>
        </s:else>
    </div>
</s:form>
