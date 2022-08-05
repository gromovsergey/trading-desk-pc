<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">
    $(function() {
        $('#searchForm').pagingAssist({
            action:     '/admin/GeoChannel/search.action',
            message:    '${ad:formatMessage("channel.search.loading")}',
            result:     $('#result')
        });
    });
</script>

<s:form id="searchForm">
    <s:actionerror/>

    <ui:section titleKey="form.search">
        <ui:fieldGroup>

            <ui:field labelKey="channel.search.name" labelForId="name" errors="name">
                <s:textfield name="name" id="name" cssClass="middleLengthText" maxlength="100"/>
            </ui:field>

            <ui:field labelKey="channel.search.country" labelForId="countryCode" errors="countryCode">
                <s:select list="countries"
                             name="countryCode" id="countryCode" cssClass="middleLengthText"
                             headerKey="" headerValue="%{getText('form.all')}"
                             listKey="id" listValue="getText('global.country.' + id + '.name')"/>
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button message="form.search" onclick="$('#geoChannels').val('true');" type="submit"/>
            </ui:field>

    </ui:fieldGroup>
    </ui:section>
</s:form>

<div id="result" class="logicalBlock">
</div>
