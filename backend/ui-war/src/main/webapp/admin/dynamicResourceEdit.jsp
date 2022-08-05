<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<c:set var="isArea">
    <c:choose>
        <c:when test="${resourceKey.startsWith('Option-label')}">${true}</c:when>
        <c:otherwise>${false}</c:otherwise>
    </c:choose>
</c:set>
<s:set var="hasValidationErrors" value="%{hasErrors()}"/>

<script type="text/javascript">
    $(function(){
        var hasValidationErrors = ${hasValidationErrors},
            inProgress          = false,
            submitFn            = function(event){
                if (inProgress) return;
                inProgress = true;
                $("#dynamicResourcesForm :button").prop({'disabled': true});
                $.ajax({
                    url: '${actionPath}' + 'save.action',
                    data: $('#dynamicResourcesForm').serialize() ,
                    success: function(data) {
                        if (${isArea}) {
                            $('#localizer_popup_area').html(data);
                        } else {
                            $('#localizer_popup').html(data);
                        }
                        if (!hasValidationErrors) {
                            UI.Dialog.removeAllDialogs();
                        } else {
                            $("#dynamicResourcesForm :button").prop({'disabled': false});
                            inProgress = false;
                        }
                    },
                    type: "POST"
                });
            };
        
        $('#popup_save').on('click', submitFn);
        $('#popup_cancel').on('click', function(e){
            e.preventDefault();
            UI.Dialog.removeAllDialogs();
        });
    
        if (! ${isArea}) {
            $('#dynamicResourcesForm').on('keypress', ':input', function(e){
                if (e.which == 13) {
                    e.preventDefault();
                    submitFn(e);
                }
            });
        }
    });
</script>

<s:form id="dynamicResourcesForm">
    <ui:section titleKey="dynamicResources.main">
        <s:hidden name="resourceKey"/>
        <s:hidden name="entityName"/>
        <ui:fieldGroup>
    
            <s:iterator value="languages" var="lang" status="iStatus">
                <ui:field labelKey="user.language.${lang}" labelForId="value_${lang.isoCode}" required="false" errors="value_${lang.isoCode}">
                    <c:choose>
                        <c:when test="${isArea}">
                            <s:textarea name="values['%{#lang.isoCode}'].value" id="value_%{#lang.isoCode}" cssClass="middleLengthTextList" maxLength="%{maxLength}"/>
                        </c:when>
                        <c:otherwise>
                            <s:textfield name="values['%{#lang.isoCode}'].value" id="value_%{#lang.isoCode}" cssClass="middleLengthText" maxLength="%{maxLength}"/>
                        </c:otherwise>
                    </c:choose>
                    <s:hidden name="values['%{#lang.isoCode}'].lang" value="%{#lang.isoCode}"/>
                    <s:hidden name="values['%{#lang.isoCode}'].key"/>
                </ui:field>
            </s:iterator>
                    
        </ui:fieldGroup>
    </ui:section>
    
    <div class="wrapper">
        <ui:button message="form.save" id="popup_save" type="button" />
        <ui:button message="form.cancel" id="popup_cancel" type="button"/>
    </div>
</s:form>
