<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript">
    $(function() {
        new UI.AjaxLoader().switchOff();
        $('#channelStats_preloader').show();
        $('#channelStatsDiv').load('${_context}/channel/channelStats.action', {id:${id}});
    });
</script>

<div id="channelStatsDiv">
    <ui:header styleClass="level2 withTip">
        <h2><fmt:message key="channel.statistics"/></h2>
    </ui:header>
    <img id="channelStats_preloader" class="hide" src="/images/wait-animation-small.gif">
</div>
