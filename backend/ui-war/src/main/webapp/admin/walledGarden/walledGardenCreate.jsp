<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript">

$().ready(function() {
    $('#countryCode').change(function() {
        var selectedValue = $(this).val();
        if(selectedValue != '') {
            fillAccounts(selectedValue);
        }
    }).change();
});

function setSelectedAgencyId(response, selectId) {
    <s:if test="agency != null && agency.id != null">
        $('#' + selectId).val('${agency.id}');
    </s:if>
}

function setSelectedPublisherId(response, selectId) {
    <s:if test="publisher != null && publisher.id != null">
        $('#' + selectId).val('${publisher.id}');
    </s:if>
}

function fillAccounts(countryCode) {
    UI.Data.Options.get('walledGardenFreeAccounts', 'publisherAccountId', {role: ['PUBLISHER'], countryCode: countryCode}, ['form.select.pleaseSelect'], setSelectedPublisherId);
    UI.Data.Options.get('walledGardenFreeAccounts', 'agencyAccountId', {role: ['AGENCY'], countryCode: countryCode}, ['form.select.pleaseSelect'], setSelectedAgencyId);
}

</script>

<ui:pageHeadingByTitle/>

<s:form action="admin/WalledGarden/saveNew">
 
    <ui:section titleKey="WalledGarden.create">
        <s:actionerror/>
        <ui:fieldGroup>
            <ui:field labelKey="WalledGarden.select.country" errors="countryCode" required="true">
                <s:select name="countryCode" id="countryCode" list="countries" headerKey="" headerValue="%{getText('form.select.pleaseSelect')}">
                </s:select>
            </ui:field>

            <ui:field labelKey="WalledGarden.select.publisherAccount" errors="publisher,duplWalledGardernPublisher" required="true">
                    <s:select name="entity.publisher.id" id="publisherAccountId" list="{}"
                listKey="id" listValue="name" headerKey="" headerValue="%{getText('form.select.pleaseSelect')}"/>
            </ui:field>

            <s:set var="marketplace" value="publisherMarketplace"/>
            <s:include value="walledGardenMarketplace.jsp">
                <s:param name="prefix" value="'publisher'" />
            </s:include>

            <ui:field labelKey="WalledGarden.select.agencyAccount" errors="agency,duplWalledGardernAgency" required="true">
                <s:select name="entity.agency.id" id="agencyAccountId" list="{}"
                    listKey="id" listValue="name" headerKey="" headerValue="%{getText('form.select.pleaseSelect')}"/>
            </ui:field>

            <s:set var="marketplace" value="agencyMarketplace"/>
            <s:include value="walledGardenMarketplace.jsp">
                <s:param name="prefix" value="'agency'" />
            </s:include>

        </ui:fieldGroup>
    </ui:section>

<div class="wrapper">
    <ui:button message="form.save" type="submit" />
    <ui:button message="form.cancel" onclick="location='main.action';" type="button" />
</div>
</s:form>
