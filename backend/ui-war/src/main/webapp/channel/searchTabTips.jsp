<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:set var="impressionTip">
    <div class="textWithHint"><fmt:message key="channel.impressions"/><ui:hint><fmt:message key="channel.impressions.hint"/> </ui:hint></div>
</c:set>
<c:set var="lastUseTip">
    <div class="textWithHint"><fmt:message key="channel.lastUse"/><ui:hint><fmt:message key="channel.lastUse.hint"/> </ui:hint></div>
</c:set>
<c:set var="reuseTip">
    <div class="textWithHint"><fmt:message key="channel.reuse"/><ui:hint><fmt:message key="channel.reuse.hint"/> </ui:hint></div>
</c:set>
<c:set var="populationTip">
    <div class="textWithHint"><fmt:message key="channel.population"/><ui:hint><fmt:message key="channel.population.hint"/> </ui:hint></div>
</c:set>
