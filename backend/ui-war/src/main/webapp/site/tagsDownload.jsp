<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:pageHeadingByTitle/>
<s:if test="%{!sites.empty}">
    <s:set var="showDeletedTagsHeader" value="true"/>
    <s:set var="showDeletedTags" value="false" />
    <s:set var="showInventoryTags" value="false" />
    <s:set var="inventoryMessageShown" value="false"/>


    <%--Common part--%>
    <s:iterator value="sites" var="site">
        <%@ include file="/site/tagsDownloadSection.jspf" %>
    </s:iterator>

     <%--Inventory estimation part--%>
    <s:set var="showInventoryTags" value="true"/>
    <s:iterator value="sites" var="site">
        <%@ include file="/site/tagsDownloadSection.jspf" %>
    </s:iterator>

    <%--Deleted sites and tags part--%>
    <s:if test="isInternal()">
        <s:set var="isInternal" value="true"/>
        <s:set var="showDeletedTagsHeader" value="false"/>
        <s:set var="showDeletedTags" value="true" />
        <s:set var="inventoryMessageShown" value="true"/>

        <s:iterator value="sites" var="site">
            <%@ include file="/site/tagsDownloadSection.jspf" %>
        </s:iterator>

    </s:if>
</s:if>
<s:else>
        <h2><fmt:message key="tags.noActiveTags"/></h2>
</s:else>
