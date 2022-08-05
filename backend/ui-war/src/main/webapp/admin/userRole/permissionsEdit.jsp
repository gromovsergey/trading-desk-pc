<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<script type="text/javascript">

function handleCheckChange(objectType, action) {
    var jqParent = $('.objectType-'+objectType+'-'+action);
    var jqChildren = $('.parameter-'+objectType+'-'+action);
    
    if(jqParent.is(':checked')){
        jqChildren.attr({
            checked:'checked',
            disabled:'disabled'
        });
    }else{
        jqChildren.prop({disabled : false})
            .prop({checked : false});
    }
}

$().ready(function(){
    $('.basePermission').each(function() {
        if($(this).is(':checked')) {
            this.onclick();
        }
    });
});

</script>

<s:form action="admin/UserRole/Permissions/save">
    <s:hidden name="userRole.id"/>
    <s:hidden name="userRole.version"/>
    <s:fielderror>
        <s:param value="'version'"/>
    </s:fielderror>
    <s:if test="hasActionErrors()">
        <div style="margin-top: 5px; margin-bottom: 5px">
            <s:actionerror/>
        </div>
    </s:if>

    <s:set var="permissionCounter" value="0"/>

    <%@include file="policyEdit.jsp"%>

    <s:if test="not predefinedReportsPolicy.isEmpty()">
        <ui:header styleClass="level2">
            <h2><s:text name="UserRole.predefinedReportPermissions" /></h2>
        </ui:header>

        <s:set var="currentPolicy" value="predefinedReportsPolicy"/>
        <%@include file="policyTableEdit.jsp"%>
    </s:if>

    <s:if test="not birtReportsPolicy.isEmpty()">
        <ui:header styleClass="level2">
            <h2><s:text name="UserRole.birtReportPermissions" /></h2>
        </ui:header>

        <s:set var="currentPolicy" value="birtReportsPolicy"/>
        <%@include file="policyTableEdit.jsp"%>
    </s:if>

    <s:if test="not agentReportPolicy.isEmpty()">
        <ui:header styleClass="level2">
            <h2><s:text name="UserRole.agentReportPermissions" /></h2>
        </ui:header>

        <s:set var="currentPolicy" value="agentReportPolicy"/>
        <%@include file="policyTableEdit.jsp"%>
    </s:if>

    <s:if test="not audienceResearchPolicy.isEmpty()">
        <ui:header styleClass="level2">
            <h2><s:text name="UserRole.audienceResearchPermissions" /></h2>
        </ui:header>

        <s:set var="currentPolicy" value="audienceResearchPolicy"/>
        <%@include file="policyTableEdit.jsp"%>
    </s:if>

    <div class="wrapper">
        <ui:button message="form.save" type="submit" />
        <ui:button message="form.cancel" onclick="location='../view.action?id=${userRole.id}';" type="button" />
    </div>

</s:form>
