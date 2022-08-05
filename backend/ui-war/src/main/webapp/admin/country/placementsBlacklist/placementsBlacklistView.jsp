<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<script type="text/javascript">
    $(function(){
        $('#placementsBlacklistFormId').pagingAssist({
            action: 'search.action',
            message: '${ad:formatMessage("form.loading.resources")}',
            autoSubmit: ($('#placementsBlacklistFormId_submitSearchNeeded').val() !== 'false'),
            <s:if test="searchParams.page > 1">
            onBeforeSubmit: function(cb){
                window.location.href    = '#${searchParams.page}';
                cb();
            },
            </s:if>
            result: $('#placementsDiv')
        });
    });
</script>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted0('PlacementsBlacklist.update')}">
        <s:url action="bulkUpload" var="url"/>
        <ui:button message="admin.placementsBlacklist.bulkUpload" href="${url}?id=${id}"/>
    </c:if>
    <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=57&id=${country.countryId}"/>
</ui:header>

<s:form id="placementsBlacklistFormId">
    <s:hidden name="id"/>
    <s:hidden name="submitSearchNeeded"/>

    <ui:section titleKey="form.search">

        <ui:fieldGroup>

            <ui:field labelKey="form.url" errors="url">
                <s:textfield name="searchParams.url" id="urlId" cssClass="middleLengthText" maxLength="4096" />
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button message="form.search"/>
            </ui:field>

        </ui:fieldGroup>

    </ui:section>

</s:form>

<div class="logicalBlock" id="placementsDiv"></div>
