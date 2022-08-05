<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="/ad/serverUI" prefix="ad"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui"%>

<script type="text/javascript">
    function submitForm() {
        $('#saveForm').submit();
    }
</script>
<ui:pageHeadingByTitle />

<ui:errorsBlock>
    <s:actionerror />
    <s:fielderror>
        <s:param value="'version'" />
    </s:fielderror>
</ui:errorsBlock>

<s:form action="%{#request.moduleName}/deviceTargeting/update" id="saveForm">
    <s:hidden name="id" />
    <s:hidden name="name" />
    <s:hidden name="version" />
    
     <jsp:include
        page="/admin/accountType/deviceTargetingEdit.jsp">
        <jsp:param name="isDeviceTargetingEditPage" value="true" />
     </jsp:include>
</s:form>

<div class="wrapper">
    <ui:button message="form.save" onclick="submitForm();" type="submit" />
    <c:if test="${ccgType =='DISPLAY'}">
        <ui:button message="form.cancel"
            onclick="location='../viewDisplay.action?id=${id}';"
            type="button" />
    </c:if>
    <c:if test="${ccgType =='TEXT'}">
        <ui:button message="form.cancel"
            onclick="location='../viewText.action?id=${id}';"
            type="button" />
    </c:if>
</div>
