<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">

function submitForm() {
    $('#saveUserSampleGroupsForm').submit();
}

</script>
<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:actionerror/>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<s:form action="%{#attr.moduleName}/userSampleGroups/update" id="saveUserSampleGroupsForm">
    
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <ui:text textKey="ccg.userSampleGroups.toolTip"/>
    <ui:fieldGroup>
    <ui:field labelKey="ccg.userSampleGroups" errors="userSampleGroupEnd,userSampleGroupStart,userSampleGroups">
        <table><tr>
        <td>
            <ui:text textKey="form.range.from"/>
        </td><td>
            <s:textfield name="userSampleGroupStart" size="3" maxlength="3"></s:textfield>
        </td><td>
            <ui:text textKey="form.range.to"/>
        </td><td>
            <s:textfield name="userSampleGroupEnd" size="3" maxlength="3"></s:textfield>
        </td><td>
            <ui:text textKey="form.range.inclusive"/>
        </td>
        </tr></table>
    </ui:field>
    </ui:fieldGroup>
</s:form>


<div class="wrapper">   
    <ui:button message="form.save" type="submit"  onclick="submitForm();"/>
    <c:if test="${ccgType =='DISPLAY'}">
        <ui:button message="form.cancel" onclick="location='../viewDisplay.action?id=${id}';" type="button"/>
    </c:if>
    <c:if test="${ccgType =='TEXT'}">
        <ui:button message="form.cancel" onclick="location='../viewText.action?id=${id}';" type="button"/>
    </c:if>
</div>
