<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
    $(function(){
        $('#searchForm').pagingAssist({
            action:     '/admin/AdopsDashboard/regularReviewSearch.action',
            autoSubmit: true,
            message:    '${ad:formatMessage("report.loading")}',
            result:     $('#result')
        });
        $('#countryCode').change(function() {
            $('#searchForm').submit();
        });
        $('#entity_switch input').on('change', function(e){
            e.stopPropagation();
            $('#searchForm').submit();
        });
    });
</script>

<ui:pageHeadingByTitle/>

<s:form id="searchForm">
<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="account.country" labelForId="country" errors="countryCode">
            <s:select name="countryCode" id="countryCode" list="countries"
                cssClass="middleLengthText" listKey="id"
                listValue="getText('global.country.' + id + '.name')"/>
        </ui:field>
        <ui:field labelKey="checks.entityTypes" required="true" id="entity_switch" cssClass="valignFix">
            <label for="switch_campaigns" style="display: inline; margin-right: 0;"><input id="switch_campaigns" type="radio" name="entityType" value="campaigns" <c:if test="${entityType=='campaigns'}">checked="checked"</c:if>/><fmt:message key="checks.entityType.campaigns"/></label> &nbsp;
            <label for="switch_channels" style="display: inline; margin-right: 0;"><input id="switch_channels" type="radio" name="entityType" value="channels" <c:if test="${entityType=='channels'}">checked="checked"</c:if>/><fmt:message key="checks.entityType.channels"/></label>
        </ui:field>
    </ui:fieldGroup>
</ui:section>
</s:form>

<div id="result" class="logicalBlock">
</div>
