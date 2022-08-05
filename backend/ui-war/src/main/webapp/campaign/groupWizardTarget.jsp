<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<script language="JavaScript">
    $(function(){ // document ready
        $('#namingConventionId0').on('click focus', function(e){
            e.stopPropagation();
            e.preventDefault();
            $('[name=namingConventionRadio]:last').prop('checked',true);
        });
        
        $('#groupWizardTargetForm').on('submit', function(e){
            $('[name=namingConventionRadio]').each(function () {
                if (this.checked) {
                    var currentVal = $('#namingConventionId' + this.value).text();
                    if (currentVal == '') {
                        currentVal = $('#namingConventionId' + this.value).val();
                    }
                    $('#namingConventionId').val(currentVal);
                }
            });
        });
    });
</script>

<ui:header>
    <ui:pageHeadingByTitle/>
</ui:header>

<h2><fmt:message key="campaign.creative.groupsWizard.step1"/></h2>
<p><fmt:message key="campaign.creative.groupsWizard.hint1"/></p>
<p>&nbsp;</p>

<s:form action="%{#attr.moduleName}/new%{#attr.entityName}Settings" id="groupWizardTargetForm">

    <s:hidden name="navigateBack" value="false"/>
    <s:hidden name="campaignId"/>
    <s:hidden name="namingConvention" id="namingConventionId"/>

    <ui:section>
        <ui:fieldGroup>
            <s:if test="campaign.account.international">
                <ui:field id="countryElem" labelKey="channel.country" labelForId="countryCode" errors="countryCode">
                    <s:select name="countryCode" id="countryCode" cssClass="middleLengthText"
                              list="countries" value="countryCode"
                              listKey="id" listValue="getText('global.country.' + id + '.name')"/>
                </ui:field>
            </s:if>
            <ui:field labelKey="campaign.channelTargetsList" tipKey="campaign.creative.groupsWizard.target.hint" required="true" errors="channelTargetsList">
                <s:textarea cssClass="middleLengthText"
                            name="channelTargetsList"/>
            </ui:field>
            <ui:field labelKey="campaign.newGroupNamingConvention" required="true" errors="namingConvention">
                <c:forEach var="namingConvention" items="${predefinedNamingConventions}" varStatus="loopStatus">
                    <label style="white-space:pre;" class="withInput"><input type="radio" name="namingConventionRadio" ${namingConvention == defaultNamingConvention ? 'checked="true"' : ''} value="${loopStatus.count}"> &nbsp;<span id="namingConventionId${loopStatus.count}"><c:out value="${namingConvention}" escapeXml="true"/></span></label>
                </c:forEach>
                <label style="margin:5px 0;white-space:pre;" class="withInput"><input type="radio" name="namingConventionRadio" ${customizableNamingConvention == defaultNamingConvention ? 'checked="true"' : ''} value="0"><s:textfield cssClass="middleLengthText" id="namingConventionId0" name="customizableNamingConvention"/></label>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <div class="wrapper" style="clear:both;">
        <ui:button message="form.next" type="submit"/>
        <ui:button onclick="location.href='${_context}/campaign/view.action?id=${campaignId}'" message="form.cancel" type="button" />
    </div>

</s:form>
