<script type="text/javascript">
	$(function(){

		// Dialog			
		$('#channelMaxUrlTriggerShareHTML').dialog({
			autoOpen: false, 
			width: 600
		});
		
	});
</script>

<fmt:message var="reasonText" key="channel.channelMaxUrlTriggerShare.brief">
    <fmt:param>
        <ui:button message="channel.channelMaxUrlTriggerShare"
                   onclick="$('#channelMaxUrlTriggerShareHTML').dialog('open');"/>
    </fmt:param>
</fmt:message>
<c:set var="extra">
    <div id="channelMaxUrlTriggerShareHTML" style="display:none;">
        <c:if test="${not empty reason.groups}">
            <fmt:message key="channel.channelMaxUrlTriggerShare.description1">
                <fmt:param>
                    <fmt:formatNumber type="percent" maxFractionDigits="2" value="${reason.threshold}"/>
                </fmt:param>
            </fmt:message><br/>
            <ul>
                <c:forEach var="group" items="${reason.groups}">
                    <li><c:out value="${group}"/></li>
                </c:forEach>
            </ul>
        </c:if>
        <c:if test="${not empty reason.triggers}">
            <fmt:message key="channel.channelMaxUrlTriggerShare.description2">
                <fmt:param>
                    <fmt:formatNumber type="percent" maxFractionDigits="2" value="${reason.threshold}"/>
                </fmt:param>
            </fmt:message><br/>
            <ul>
                <c:forEach var="trigger" items="${reason.triggers}">
                    <li><c:out value="${trigger}"/></li>
                </c:forEach>
            </ul>
        </c:if>
        <fmt:message key="channel.channelMaxUrlTriggerShare.description3"/><br/><br/>
        <b><fmt:message key="channel.channelMaxUrlTriggerShare"/></b>:
        <fmt:message key="channel.channelMaxUrlTriggerShare.definition"/><br/>
        <b><fmt:message key="channel.uniqueURL"/></b>:
        <fmt:message key="channel.uniqueURL.definition"/><br/>
    </div>
</c:set>
