<script type="text/javascript">
	$(function(){

		// Dialog			
		$('#channelTriggerQAHTML').dialog({
			autoOpen: false
		});
		
		// Dialog Link
		$('#dialog_link').click(function(){
			$('#channelTriggerQAHTML').dialog('open');
			return false;
		});
		
	});
</script>

<c:choose>
    <c:when test="${not empty reason.triggers}">
        <fmt:message var="reasonText" key="channel.channelTriggerQA.brief">
            <fmt:param>
                <ui:button message="channel.channelTriggerQA.triggers"
                        onclick="$('#channelTriggerQAHTML').dialog('open');"/>
            </fmt:param>
        </fmt:message>
        <c:set var="extra">
            <div id="channelTriggerQAHTML" style="display:none;">
                <fmt:message key="channel.channelTriggerQA.description"/>:
                <ul>
                    <c:forEach var="triggers" items="${reason.triggers}">
                        <li><c:out value="${triggers}"/></li>
                    </c:forEach>
                </ul>
            </div>
        </c:set>
    </c:when>
    <c:otherwise>
        <c:set var="reasonText">
            <fmt:message key="channel.channelTriggerQA.approved"/>
        </c:set>
    </c:otherwise>
</c:choose>
