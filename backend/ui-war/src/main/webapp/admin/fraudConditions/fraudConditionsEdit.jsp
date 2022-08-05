<%@ page import="com.foros.model.admin.FraudCondition" %>
<%@ page import="com.foros.util.StringUtil" %>

<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    $(function() {
        $('.delRow').click(function(){
            var mainTable = $('#conditionsTable');

            deleteRowByButtonInItAndDecrementIndex(this, mainTable[0])
            var mainTableRootRows = $('tbody:eq(0) > tr', mainTable);

            if(mainTableRootRows.length == 1){
                $('#infoAddCondition').show();
            }
            return false;
        });

        $('#addRowBtn').click(function(){
            UI.Util.Table.addRow('conditionsTable');
            $('#infoAddCondition').hide();
            reindexAll();
            return false;
        });
    });
    
    function deleteRowByButtonInItAndDecrementIndex(btn, table) {
        UI.Util.Table.deleteRowByButtonInIt(btn, table);
        var tbody = table.tBodies[0];
        var rowTempl = tbody.rows[0];
        var cnt = UI.Util.Table.getCnt(table, rowTempl);
        $(rowTempl).attr("_cnt", +cnt - 1)
        reindexAll();
    }

    function reindex(property){
        var i = 0;
        $('input[name^=fraudConditions],select[name^=fraudConditions]').each(function(index) {
            if(this.name.match("^fraudConditions\\[\\d\\]."+property)) {
                this.name = 'fraudConditions[' + i + '].' + property;
                i++;
            }
        });
    }

    function reindexAll(){
        var props = ['id','version','limit','type','period','units'];
        for(i in props){
            reindex(props[i]);
        }
    }

</script>

<ui:pageHeadingByTitle/>

<s:form action="admin/FraudConditions/save">

    <div class="wrapper">
        <s:actionerror/>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </div>

<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="fraud.userInactivityTimeout" labelForId="userInactivityTimeout" required="true" errors="userInactivityTimeout">
            <s:textfield name="userInactivityTimeout" id="userInactivityTimeout" cssClass="middleLengthText" maxlength="10"/>
            <s:hidden name="userInactivityTimeoutVersion"/>
            <fmt:message key="fraud.timeoutUnits"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<ui:section titleKey="fraud.fraudConditions">
    <s:if test="fraudConditions.size == 0">
        <span class="infos" id="infoAddCondition"><fmt:message key="fraud.infoAddCondition"/></span>
    </s:if>
    <s:else>
        <span class="infos" id="infoAddCondition" style="display: none"><fmt:message key="fraud.infoAddCondition"/></span>
    </s:else>
    <table class="formFields" id="conditionsTable">
        <tr class="hide" _cnt="${fraudConditions.size()}">
            <td class="field">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withSpan">
                            <input type="hidden" id="admin/FraudConditions/save_fraudConditions(?)_id" value="" name="fraudConditions[?].id" disabled="disabled" maxlength="10">
                            <ui:text textKey="fraudCondition.moreThan"/>
                        </td>
                        <td class="withField">
                            <input type="text" id="admin/FraudConditions/save_fraudConditions(?)_limit" value="" name="fraudConditions[?].limit" disabled="disabled" maxlength="10">
                        </td>
                        <td class="withField">
                            <select id="admin/FraudConditions/save_fraudConditions(?)_type" name="fraudConditions[?].type" disabled="disabled">
                                <option selected="selected" value="CLK"><fmt:message key="fraudCondition.clicks"/></option>
                                <option value="IMP"><fmt:message key="fraudCondition.impressions"/></option>
                            </select>
                        </td>
                        <td class="withSpan">
                            <ui:text textKey="fraudCondition.fromSameUIDDuring"/>
                        </td>
                        <td class="withField">
                            <input type="text" id="admin/FraudConditions/save_fraudConditions(?)_period" value="" name="fraudConditions[?].period" disabled="disabled">
                        </td>
                        <td class="withField">
                            <select id="admin/FraudConditions/save_fraudConditions(?)_units" name="fraudConditions[?].units" disabled="disabled">
                                <option selected="selected" value="second"><fmt:message key="form.select.second"/></option>
                                <option value="minute"><fmt:message key="form.select.minute"/></option>
                                <option value="hour"><fmt:message key="form.select.hour"/></option>
                            </select>
                        </td>
                        <td class="withSpan">
                            <ui:text textKey="fraudCondition.regardlessOfTIDOrCCID"/>
                        </td>
                    </tr>
                </table>
            </td>
            <td class="field">
                <ui:button message="form.delete" subClass="delRow" type="button" />
            </td>
        </tr>
        <s:iterator value="fraudConditions" status="status">
            <tr>
                <td class="field">
                    <table class="fieldAndAccessories">
                        <tr>
                            <td class="withSpan">
                                <s:hidden name="fraudConditions[%{#status.count-1}].id"/>
                                <s:hidden name="fraudConditions[%{#status.count-1}].version"/>
                                <ui:text textKey="fraudCondition.moreThan"/>
                            </td>
                            <td class="withField">
                                <s:textfield name="fraudConditions[%{#status.count-1}].limit" maxlength="10"/>
                            </td>
                            <td class="withField">
                                <s:select name="fraudConditions[%{#status.count-1}].type"
                                      list="fraudConditionsTypes"
                                      listKey="id"
                                      listValue="name"
                                      value="%{type}"
                                      required="true"/>
                            </td>
                            <td class="withSpan">
                                <ui:text textKey="fraudCondition.fromSameUIDDuring"/>
                            </td>
                            <td class="withField">
                                <s:textfield name="fraudConditions[%{#status.count-1}].period" maxlength="10"/>
                            </td>
                            <td class="withField">
                                <s:select name="fraudConditions[%{#status.count-1}].units"
                                      list="fraudConditionsUnits"
                                      listKey="id"
                                      listValue="name"
                                      value="%{units}"
                                      required="true"/>
                            </td>
                            <td class="withSpan">
                                <ui:text textKey="fraudCondition.regardlessOfTIDOrCCID"/>
                            </td>
                        </tr>
                    </table>
                </td>
                <td class="field">
                    <ui:button message="form.delete" subClass="delRow" type="button" />
                </td>
                <td class="withError">
                    <s:fielderror><s:param>fraudConditions[${status.count-1}].limit</s:param></s:fielderror>
                    <s:fielderror><s:param>fraudConditions[${status.count-1}].period</s:param></s:fielderror>
                    <s:fielderror><s:param>fraudConditions[${status.count-1}].type</s:param></s:fielderror>
                    <s:fielderror><s:param>fraudConditions[${status.count-1}].duplicate</s:param></s:fielderror>
                </td>
            </tr>
        </s:iterator>
    </table>
    <div style="margin: 2px;">
        <ui:button message="form.add" id="addRowBtn" type="button" />
    </div>
</ui:section>
<div class="wrapper">
    <ui:button message="form.save"/>
    <ui:button message="form.cancel" onclick="location='main.action'" type="button" />
</div>
</s:form>
