<%@ page import="java.util.Collections" %>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    function onCountriesChange() {
        var roles = ['ISP'];
        UI.Data.Options.fill(
                'idNameAccounts', 'available_accountIds_id, alloptions_accountIds_id',
                updateAccounts, {
                    role: roles,
                    countryCodes: getCountryCodes(),
                    concatResultForValue: false
                }, []);
    }

    function onAccountsChange() {
        UI.Data.Options.fill(
                'colocations', 'available_colocationIds_id, alloptions_colocationIds_id',
                updateColocations, {
                    ispAccountIds: getIspAccountIds(),
                    concatResultForValue: false
                }, []);
    }

    function updateColocations(response, selectId, additionalOptionMessages) {
        UI.Data.Options._update(response, selectId, additionalOptionMessages);
        if (selectId == 'available_colocationIds_id') {
            updateObjects('selected_colocationIds_id', 'available_colocationIds_id');
        }
    }

    function getIspAccountIds() {
        var res = '';
        var selectobject = document.getElementById('selected_accountIds_id');
        for (var i = 0; i < selectobject.length; i++) {
            if (i != 0) {
                res = res + ',';
            }
            res = res + selectobject.options[i].value;
        }
        return res;
    }

    function updateAccounts(response, selectId, additionalOptionMessages) {
        UI.Data.Options._update(response, selectId, additionalOptionMessages);
        if (selectId == 'available_accountIds_id') {
            updateObjects('selected_accountIds_id', 'available_accountIds_id');
            onAccountsChange();
        }
    }

    function getCountryCodes() {
        var res = [''];
        var selectobject = document.getElementById('selected_countryCodes_id');
        for (var i = 0; i < selectobject.length; i++) {
            res.push(selectobject.options[i].value);
        }
        return res;
    }

    function updateObjects(selectedId, availableId) {
        var selectobject = document.getElementById(selectedId);
        var availableobject = document.getElementById(availableId);
        for (var i = selectobject.length-1; i >= 0; --i) {
            var option = selectobject.options[i].value;
            var found = false;
            for (var j = availableobject.length-1; j >= 0; --j) {
                if (availableobject.options[j].value == option) {
                    found = true;
                    availableobject.remove(j);
                }
            }
            if (!found) {
                selectobject.remove(i);
            }
        }
    }
</script>

<ui:pageHeadingByTitle/>

<s:set var="canSelectAccount" value="isInternal() && accountId == null"/>

<s:form id="channelUsageForm" action="run" method="post" target="_blank">
    <ui:section titleKey="form.filter">
        <ui:fieldGroup>
            <ui:field labelKey="report.input.field.dateRange">
                <ui:daterange
                        fromDateFieldName="dateRange.begin"
                        toDateFieldName="dateRange.end"
                        options="Y T WTD MTD LW LM R QTD LQ LY YTD"
                        fastChangeId="MTD"
                        currentPos="1"
                        maxDate="+1d"
                        validateRange="true"
                        timeZoneAccountId="${_principal.accountId}"/>
            </ui:field>
            <s:if test="canSelectAccount">
                <ui:field labelKey="Country.entityName" required="true">
                    <ui:optiontransfer
                            name="countryCodes"
                            size="9"
                            cssClass="middleLengthText"
                            listKey="id"
                            listValue="name"
                            list="${countries}"
                            selList="<%=Collections.emptyList()%>"
                            selListKey="id"
                            selListValue="name"
                            titleKey="ccg.country.select.available"
                            selTitleKey="ccg.country.select.selected"
                            saveSorting="true"
                            onchange="onCountriesChange()"
                            />
                </ui:field>
                <ui:field labelKey="report.input.field.ispAccount" required="true">
                    <ui:optiontransfer
                            name="accountIds"
                            size="9"
                            cssClass="middleLengthText"
                            listKey="id"
                            listValue="name"
                            list="<%= Collections.emptyList() %>"
                            selList="<%= Collections.emptyList() %>"
                            selListKey="id"
                            selListValue="name"
                            titleKey="report.accounts.available"
                            selTitleKey="report.accounts.selected"
                            saveSorting="true"
                            onchange="onAccountsChange()"
                            />
                </ui:field>
            </s:if>
            <s:else>
                <input type="hidden" name="accountIds" value="${accountId}"/>
            </s:else>
            <ui:field labelKey="colocation" required="true">
                <ui:optiontransfer
                        name="colocationIds"
                        size="9"
                        escape="true"
                        cssClass="middleLengthText"
                        listKey="id"
                        listValue="name"
                        list="${colocations}"
                        selList="<%= Collections.emptyList() %>"
                        selListKey="id"
                        selListValue="name"
                        titleKey="report.colocations.available"
                        selTitleKey="report.colocations.selected"
                        saveSorting="true"
                        />
            </ui:field>
            <ui:field cssClass="withButton">
                <ui:button id="submitButton" message="report.button.runReport"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</s:form>