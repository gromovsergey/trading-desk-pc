<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript">
function creativeSizeChange() {
    
    var status = $('#sizeId option:selected').data('expandable') ? 'Y' : 'N';
    processAllowExpansion(status);
 }

$().ready(function() {
	$('#sizeId').change(creativeSizeChange);
});
</script>

<s:set var="sizeId" value="%{getTag().getSizes().iterator().next().getId()}"/>
<select name="selectedSizes" id="sizeId" class="middleLengthText">
    <option value=""><fmt:message key="form.select.pleaseSelect"/></option>
    <s:iterator var="size" value="%{sizes}">
        <s:if test="%{#size.id == #sizeId}">
            <option value="${size.id}" data-expandable="${size.expandable}" selected="selected">${ad:localizeName(size.name)}</option>
        </s:if>
        <s:else>
            <option value="${size.id}" data-expandable="${size.expandable}">${ad:localizeName(size.name)}</option>
        </s:else>
    </s:iterator>
</select>
