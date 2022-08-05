<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<s:form action="%{#request.moduleName}/saveAdvertisers" id="saveForm">
<s:hidden name="id"/>
<s:hidden name="version"/>
<s:hidden name="account.id"/>

<h2><fmt:message key="user.advertisers"/>:</h2>

<ui:section titleKey="form.main">
    <ui:fieldGroup>
        
        <ui:field>
            <table class="fieldAndAccessories">
                <tr>
                    <td class="withField">
                        <ui:optiontransfer
                            name="selectedAdvertisers"
                            size="9"
                            cssClass="middleLengthText"
                            list="${accountAdvertisers}"
                            selList="${userAdvertisers}"
                            titleKey="account.advertisers.available"
                            selTitleKey="account.advertisers.selected"
                            saveSorting="true"
                            escape="false"
                        />
                    </td>
                </tr>
                <tr>
                    <td class="withError">
                        <s:fielderror><s:param value="'selectedAdvertisers'"/></s:fielderror>
                    </td>
                </tr>
            </table>
        </ui:field>
        
    </ui:fieldGroup>
</ui:section>

<div class="wrapper">
    <ui:button message="form.save" type="submit" />
    <s:if test="isInternal()">
        <ui:button message="form.cancel" onclick="location='advertiserView.action?id=${id}';" type="button" />
    </s:if>
    <s:else>
        <ui:button message="form.cancel" onclick="location='view.action?id=${id}';" type="button" />
    </s:else>
</div>

</s:form>

<script type="text/javascript">
$(function(){
    $('#saveForm').on('submit', function(e){
        $('#selAdvertisers option').prop('selected', true);
    });
});
</select>
