<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<s:hidden name="size.version"/>
<s:set var="groups" value="size.advertiserOptionGroups"/>
<c:set var="optionTitleKey" value="CreativeSize.size.options"/>
<%@ include file="/admin/option/optionValuesEdit.jsp"%>

<!-- Hide Visual Categories selection if template has categories -->
<script type="text/javascript">
    $().ready(function() {
        <s:if test="selectedTemplate != null && selectedTemplate.categories.size > 0">
            $('#visualCategoriesLabel').html('<span class="simpleText"><ad:commaWriter items="${selectedTemplate.categories}" var="category" label="name"><c:out value="${ad:localizeName(category.name)}"/></ad:commaWriter></span>');
            $('#visualCategoriesLabelDiv').show();
            $('#visualCategoriesSelect').hide();
        </s:if>
        <s:else>
            $('#visualCategoriesSelect').show();
            $('#visualCategoriesLabelDiv').hide();
        </s:else>
    });
</script>
