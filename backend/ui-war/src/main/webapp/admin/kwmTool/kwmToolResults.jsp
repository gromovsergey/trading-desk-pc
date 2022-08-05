<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<script type="text/javascript">

    $().ready(function() {
        $('#displayAsButton').click(function(){
            $('.kwmToolResults').toggleClass('hide');
            $('#channelMatchResultDiv').toggleClass('hide');
            if($(this).data('html')){
               $(this).val('<fmt:message key="KWMTool.displayAsText"/>');
               $(this).data('html',false);
            }else{
                $(this).val('<fmt:message key="KWMTool.displayAsHTML"/>');
                $(this).data('html',true);
            }
        });
    });

</script>

<ui:header>
    <ui:pageHeadingByTitle/>
    <ui:button id="displayAsButton" message="KWMTool.displayAsText" type="button"/>
</ui:header>

<div class="hide wrapper kwmToolResults" id="textResults">
    <textarea rows="140" cols="14" readonly="true" class="bigLengthText" style="height:400px"><c:out value="${serializedResults}"/></textarea>
</div>

<ui:section id="kwmToolResults" cssClass="kwmToolResults">
    <ui:fieldGroup>
        <s:if test="source == 0">
            <ui:field labelKey="KWMTool.source.url">
                <s:a href="%{sourceUrl}"><s:property value="sourceUrl"/></s:a>
            </ui:field>
            <ui:field labelKey="KWMTool.source.maxSize">
                <c:set var="textVal">
                    <s:property value="maxSize"/> <fmt:message key="KWMTool.source.bytes"/>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
        </s:if>
        <ui:field labelKey="KWMTool.settingsSchema">
            <c:set var="textVal">
                <s:property value="settingsSchema"/>
            </c:set>
            <ui:text text="${pageScope.textVal}"/>
        </ui:field>
        <ui:field labelKey="KWMTool.plainText">
            <s:textarea value="%{result.plainText}" cssClass="bigLengthText" cssStyle="height:200px" readonly="true" cols="70" rows="7"/>
        </ui:field>
        <c:if test="${result.mode != 'FULL_TEXT'}">
            <ui:field labelKey="KWMTool.segmentedText">
                <s:textarea value="%{result.separatedWords}" cssClass="bigLengthText" cssStyle="height:200px" readonly="true" cols="70" rows="7"/>
            </ui:field>
        </c:if>
        <c:choose>
            <c:when test="${result.mode == 'KWM'}">
                <ui:field labelKey="KWMTool.keywordsMiningResults">
                    <display:table name="result.keywords" id="keyword" class="dataView">
                        <display:column titleKey="KWMTool.keyword">
                            <c:out value="${keyword.name}"/>
                        </display:column>
                        <display:column titleKey="KWMTool.weight">
                            ${keyword.value}
                        </display:column>
                    </display:table>
                </ui:field>
            </c:when>
            <c:when test="${result.mode == 'FULL_TEXT'}">
                <ui:field labelKey="KWMTool.fullTextResults">
                    <s:textarea value="%{result.originalFullText}" cssClass="bigLengthText" cssStyle="height:200px"
                                readonly="true" cols="70" rows="7"/>
                </ui:field>
            </c:when>
        </c:choose>
    </ui:fieldGroup>
</ui:section>

<s:if test="adserverDebug">
    <div id="channelMatchResultDiv" class="logicalBlock">
                <h2><fmt:message key="KWMTool.advertisingMatchingResults"/></h2>
                <s:fielderror fieldName="channels"/>
                <display:table name="matchResults.channels" id="channelMatchResult" class="dataView">
                    <display:setProperty name="basic.msg.empty_list"><fmt:message key="nothing.found.to.display"/></display:setProperty>
                    <display:column titleKey="KWMTool.channel">
                        <a href="${_context}/channel/view.action?id=${channelMatchResult.channel.id}">
                <c:out value="${channelMatchResult.channel.name}"/></a>
                    </display:column>
                    <display:column titleKey="KWMTool.matchedTriggers">
                        <ul>
                            <c:if test="${not empty channelMatchResult.searchTriggers}">
                                <li>
                                    <b><fmt:message key="KWMTool.trigger.search"/>:</b>
                                    <ad:commaWriter items="${channelMatchResult.searchTriggers}" var="trigger"><c:out value="${trigger.trigger}"/></ad:commaWriter>
                                </li>
                            </c:if>
                            <c:if test="${not empty channelMatchResult.pageTriggers}">
                                <li>
                                    <b><fmt:message key="KWMTool.trigger.page"/>:</b>
                                    <ad:commaWriter items="${channelMatchResult.pageTriggers}" var="trigger"><c:out value="${trigger.trigger}"/></ad:commaWriter>
                                </li>
                            </c:if>
                            <c:if test="${not empty channelMatchResult.urlTriggers}">
                                <li>
                                    <b><fmt:message key="KWMTool.trigger.url"/>:</b>
                                    <ad:commaWriter items="${channelMatchResult.urlTriggers}" var="trigger"><c:out value="${trigger.trigger}"/></ad:commaWriter>
                                </li>
                            </c:if>
                            <c:if test="${not empty channelMatchResult.urlKeywordTriggers}">
                                <li>
                                    <b><fmt:message key="KWMTool.trigger.urlKeyword"/>:</b>
                                    <ad:commaWriter items="${channelMatchResult.urlKeywordTriggers}" var="trigger"><c:out value="${trigger.trigger}"/></ad:commaWriter>
                                </li>
                            </c:if>
                        </ul>
                    </display:column>
                    <display:column titleKey="KWMTool.matchedCcgs">
                        <ul>
                            <c:forEach items="${channelMatchResult.ccgs}" var="ccg">
                                <li>
                                    <a href="${_context}/campaign/group/view.action?id=${ccg.id}">
                                    <c:out value="${ccg.name}"/></a>
                                </li>
                            </c:forEach>
                        </ul>
                    </display:column>
                </display:table>
                <h2><fmt:message key="KWMTool.webwiseDiscoverMatchingResults"/></h2>
                <s:fielderror fieldName="discoverChannels"/>
                <display:table name="matchResults.discoverChannels" id="channelMatchResult" class="dataView">
                    <display:setProperty name="basic.msg.empty_list"><fmt:message key="nothing.found.to.display"/></display:setProperty>
                    <display:column titleKey="KWMTool.channel">
                        <a href="/admin/DiscoverChannel/view.action?id=${channelMatchResult.channel.id}">
                <c:out value="${channelMatchResult.channel.name}"/></a>
                    </display:column>
                    <display:column titleKey="KWMTool.matchedTriggers">
                        <ul>
                            <c:if test="${not empty channelMatchResult.searchTriggers}">
                                <li>
                                    <b><fmt:message key="KWMTool.trigger.search"/>:</b>
                                    <ad:commaWriter items="${channelMatchResult.searchTriggers}" var="trigger"><c:out value="${trigger.trigger}"/></ad:commaWriter>
                                </li>
                            </c:if>
                            <c:if test="${not empty channelMatchResult.pageTriggers}">
                                <li>
                                    <b><fmt:message key="KWMTool.trigger.page"/>:</b>
                                    <ad:commaWriter items="${channelMatchResult.pageTriggers}" var="trigger"><c:out value="${trigger.trigger}"/></ad:commaWriter>
                                </li>
                            </c:if>
                            <c:if test="${not empty channelMatchResult.urlTriggers}">
                                <li>
                                    <b><fmt:message key="KWMTool.trigger.url"/>:</b>
                                    <ad:commaWriter items="${channelMatchResult.urlTriggers}" var="trigger"><c:out value="${trigger.trigger}"/></ad:commaWriter>
                                </li>
                            </c:if>
                        </ul>
                    </display:column>
                    <display:column titleKey="KWMTool.matchedNews">
                        <ul>
                            <c:forEach items="${channelMatchResult.newsItems}" var="newsItem">
                                <li>
                                    <a href="${newsItem.link}"><c:out value="${newsItem.title}"/></a>
                                </li>
                            </c:forEach>
                        </ul>
                    </display:column>
                </display:table>
    </div>
</s:if>
<s:else>
    <ui:section id="averageTimeResults">
        <ui:fieldGroup>
            <c:set var="fieldValue">
                <s:property value="result.averageTime"/> <fmt:message key="KWMTool.ms"/>
            </c:set>
            <ui:simpleField labelKey="KWMTool.averageTime" value="${fieldValue}"/>
        </ui:fieldGroup>
    </ui:section>
</s:else>
