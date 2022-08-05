<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<ui:pageHeadingByTitle/>

<s:form action="Categories/update" method="POST">
    <s:hidden name="id"/>
    <s:hidden name="version"/>

    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </ui:errorsBlock>

<div class="wrapper">
    <ad:tree items="${categoryChannelTree}" var="node">
        <c:if test="${not empty node.element.id}">
          <label class="withInput">
            <input type="checkbox" name="selectedCategories" value="${node.element.id}" 
                <c:if test="${node.inheritedStatus.letter == 'D'}">disabled="disabled"</c:if>
                <c:if test="${ad:contains(selectedCategories, node.element.id)}">checked="checked"</c:if>>
            <span><c:out value="${ad:localizeName(node.element.localizableName)}"/></span>
            <c:if test="${node.element.status.letter == 'I'}"><span><fmt:message key="suffix.inactive"/></span></c:if>
            <c:if test="${node.element.status.letter == 'D'}"><span><fmt:message key="suffix.deleted"/></span></c:if>
          </label>
        </c:if>
    </ad:tree>
</div>

<script type="text/javascript">
    $(function(){
        $('[type=checkbox]', '#Categories_update').each(function(){
            if ($(this).prop('checked') && $(this).prop('disabled')){
                $(this).prop('disabled', false);
            }
        });
        
        $('#Categories_update').on('click', '.expand', function(e){
            e.preventDefault();
            var jqParent    = $(this).parent();
            if (jqParent.hasClass('treeClosed')) {
                jqParent.removeClass('treeClosed').addClass('treeOpen');
            } else {
                jqParent.removeClass('treeOpen').addClass('treeClosed');
            }
        });
    });
    
</script>

<div class="wrapper">
    <ui:button message="form.save" type="submit"/>
    <ui:button message="form.cancel" action="view?id=${id}" type="button"/>
</div>

</s:form>
