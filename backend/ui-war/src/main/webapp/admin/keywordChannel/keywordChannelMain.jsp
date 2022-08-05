<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ad" uri="/ad/serverUI"%>

<script type="text/javascript">
    $(function() {
        $('#keywordChannelForm').pagingAssist({
            action:     'search.action',
            message:    '${ad:formatMessage("report.loading")}',
            result:     $('#result')
        });
    });
</script>

<ui:header>
    <ui:pageHeadingByTitle />
    <c:if test="${ad:isPermitted0('GlobalParams.update')}">
        <ui:button href="/admin/KeywordChannel/editDefaultSettings.action" message="channel.manageDefaultSettings" type="link"/>
    </c:if>
    <c:if test="${ad:isPermitted0('KeywordChannel.update')}">
        <ui:button message="form.upload" href="upload.action"/>
    </c:if>
</ui:header>

<s:form id="keywordChannelForm" action="admin/KeywordChannel/search" method="GET">

    <ui:section titleKey="form.search">
        <ui:fieldGroup>

            <ui:field labelKey="channel.search.name">
                <s:textfield cssClass="middleLengthText" name="name" id="searchText" maxlength="100" />
            </ui:field>

            <ui:field labelKey="channel.search.account">
                <s:select name="accountId" cssClass="middleLengthText" list="accounts" emptyOption="--" headerKey=""
                    headerValue="%{getText('form.all')}" listKey="id"
                    listValue="name" />
            </ui:field>

            <ui:field labelKey="channel.search.country">
                <s:select name="countryCode" cssClass="middleLengthText" list="countries" emptyOption="--" headerKey=""
                    headerValue="%{getText('form.all')}" listKey="id"
                    listValue="getText('global.country.' + id + '.name')" />
            </ui:field>

            <ui:field labelKey="channel.search.status">
                <s:select name="status" id="status" list="statuses" listKey="name" listValue="getText(description)"
                    cssClass="middleLengthText" />
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button message="form.search" type="submit" />
            </ui:field>

        </ui:fieldGroup>
    </ui:section>
    
</s:form>

<div id="result" class="logicalBlock"></div>
