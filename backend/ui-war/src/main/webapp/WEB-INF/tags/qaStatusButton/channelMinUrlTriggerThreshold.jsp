<script type="text/javascript">
	$(function(){

		// Dialog			
		$('#channelMinUrlTriggerThresholdHTML').dialog({
			autoOpen: false, 
			width: 600
		});
		
	});
</script>

<fmt:message var="reasonText" key="channel.channelMinUrlTriggerThreshold.brief">
    <fmt:param>
        <ui:button message="channel.channelMinUrlTriggerThreshold"
                   onclick="$('#channelMinUrlTriggerThresholdHTML').dialog('open');"/>
    </fmt:param>
</fmt:message>

<c:set var="extra">
    <div id="channelMinUrlTriggerThresholdHTML" style="display:none;">
        <fmt:message key="channel.channelMinUrlTriggerThreshold.description1">
            <fmt:param value="${reason.threshold}"/>
        </fmt:message><br/>
        <fmt:message key="channel.channelMinUrlTriggerThreshold.description2">
            <fmt:param value="${reason.threshold-reason.value}"/>
        </fmt:message><br/><br/>
        <b><fmt:message key="channel.channelMinUrlTriggerThreshold"/></b>:
        <fmt:message key="channel.channelMinUrlTriggerThreshold.definition"/><br/>
        <b><fmt:message key="channel.uniqueURL"/></b>:
        <fmt:message key="channel.uniqueURL.definition"/><br/>
    </div>
</c:set>
