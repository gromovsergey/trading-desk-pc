<c:set var="accountName"><ad:accountName accountId="${accountId}" escapeHTML="false" appendStatusSuffix="true"/></c:set>
<c:if test="${not empty accountId or not empty accountName}">
<input type="hidden" id="accountPair" name="accountPair" value="${accountId}_${accountName}"/>
</c:if>
