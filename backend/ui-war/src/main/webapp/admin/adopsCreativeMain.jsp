<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<ui:pageHeadingByTitle/>

<s:fielderror>
    <s:param value="'search'"/>
</s:fielderror>

<script type="text/javascript">
    $(function() {
        $('#searchParams').pagingAssist({
            action:     '/admin/AdopsDashboard/searchCreativeList.action',
            message:    '${ad:formatMessage("report.loading")}',
            result:     $('#result'),
            autoSubmit: true
        });
    });
</script>
<form id="searchParams">
    <s:hidden name="searchParams.pageSize"/>
    <s:hidden name="searchParams.total"/>
    <input type="hidden" name="PWSToken" value="${sessionScope.PWSToken}"/>
</form>
<div id="result" class="logicalBlock"></div>
