<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<s:actionerror/>

<s:if test="channelLists != null">

    <ui:pages pageSize="${pageSize}"
              total="${total}"
              selectedNumber="${page}"
              visiblePagesCount="10"
              handler="goToPage"
              displayHeader="true"/>

    <display:table name="channelLists" class="dataView" id="channel">
        <display:setProperty name="basic.msg.empty_list">
            <div class="wrapper">
                <fmt:message key="nothing.found.to.display"/>
            </div>
        </display:setProperty>

        <display:column titleKey="channel.search.channel">
            <a href="/admin/GeoChannel/view.action?id=${channel.id}"><c:out value="${channel.name}"/></a>
        </display:column>

        <display:column titleKey="channel.search.country" class="fixed">
            <ad:resolveGlobal resource="country" id="${channel.country}"/>
        </display:column>

        <display:column title="${stateLabel}" class="fixed">
            <c:out value="${channel.state}"/>
        </display:column>

        <display:column title="${cityLabel}" class="fixed">
            <c:out value="${channel.city}"/>
        </display:column>

    </display:table>

    <ui:pages pageSize="${pageSize}"
              total="${total}"
              selectedNumber="${page}"
              visiblePagesCount="10"
              handler="goToPage"
              displayHeader="false"/>

</s:if>
