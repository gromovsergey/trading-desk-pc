<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript"> 
$(function(){
    $('.parent:visible').on('change', 'input[type="checkbox"]', function(){
        $(this)
            .closest('.parent')
            .next('.children')
            .find('input[type="checkbox"]')
            .prop('checked', $(this).prop('checked'));
    });
    $('.children:visible').on('change', 'input[type="checkbox"]', function(){
        var jqParent    = $(this).closest('.children').prev('.parent').find('input[type="checkbox"]');
        
        if ($(this).prop('checked')) {
            var b = true;
            $(this)
                .closest('.children')
                .find('input[type="checkbox"]')
                .each(function(){
                    b = b && $(this).prop('checked');
                });
            if (b) jqParent.prop('checked', true);
        } else {
            jqParent.prop('checked', false);
        }
    });
});

function creativeSizesChange() {
    var status = $('#sizesId input[data-expandable=true]:checked').length > 0 ? 'Y' : 'N';
    processAllowExpansion(status);
 }

    <s:if test="%{!expandable}">
        $().ready(function() {
    		$("#allowExpandableCreativeField").hide();
        });
	</s:if>
	
	$().ready(function() {
	    $('.parent:visible').change(creativeSizesChange);
	    $('.children:visible').change(creativeSizesChange);
	});
	
</script> 
    <div class="parent"><label for="parent"> <input type="checkbox" value="true"  <c:if test="${allSizesFlag}"> checked="checked" </c:if> name="allSizesFlag" id="parent"><fmt:message key="site.edittag.creativeSizes.all"/></label></div>
    <div id="sizesId" class="children" style="margin-left: 2em;">
        <s:iterator var="size" value="%{sizes}" status="index">
            <s:set var="checked" value="%{#size.id in selectedSizes}"/>
            <div><label for="sizeId_${size.id}"><input type="checkbox" value="${size.id}" <c:if test="${checked}"> checked="checked" </c:if>  data-expandable="${size.expandable}"  name="selectedSizes" id="sizeId_${size.id}">  ${ad:localizeName(size.name)}</label></div>
        </s:iterator>
    </div>

