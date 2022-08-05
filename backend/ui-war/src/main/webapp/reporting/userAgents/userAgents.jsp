<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    $().ready(function() {
        $.datepicker.setDefaults({
            showOn: 'both',
            mandatory: true,
            numberOfMonths: 2,
            showCurrentAtPos: 1,
            maxDate: "+1d",
            buttonImageOnly: true,
            buttonImage: '<s:url value="/images/calendar.gif"/>'
        });

        $('#dateDisplay').datepicker();

        $('#dateDisplay').change(function() {
            $('#dateHidden').val($('#dateDisplay').val());
        });
        
        $('#dateDisplay').change();
    })
</script>

<ui:pageHeadingByTitle/>

<s:form id="userAgentsForm" action="run" method="post" target="_blank">
    <%@include file="../enableDoubleSubmit.jsp"%>

    <ui:section titleKey="form.filter">
        <ui:fieldGroup>

            <ui:field cssClass="valignFix" labelKey="report.input.field.date" labelForId="fastChangeId">
                <s:hidden name="date" id="dateHidden"/>
                <s:textfield size="11" id="dateDisplay" name="dateDisplay" readonly="true"/>
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button id="submitButton" message="report.button.runReport"/>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>
</s:form>
