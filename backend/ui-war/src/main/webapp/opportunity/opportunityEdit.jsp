<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<script type="text/javascript">
    <s:if test="id != null">
    function viewFile(fileName) {
        var url             = "${_context}/opportunity/viewFile.action?fileName=" + fileName + "&id=" + ${id};
        var options         = "resizable=yes,menubar=no,status=no,toolbar=no,scrollbars=no,location=0,dialog=no,height=350,width=350,left=100,top=100,titlebar=no";
        var win             = window.open(url, "printpop", options);
        win.location.href   = url;
        win.focus();
    }
    </s:if>

    function probabilitySelected() {
        <s:if test="id != null">
        switch ($('#probabilityId').val()) {
            case 'IO_SIGNED' :
                $('#poNumberId, #ioNumberId, #attachField, #browseFile, #addLink').prop({readonly : false});
            case 'TARGET' :
            case 'FIRST_CONTACT' :
            case 'BRIEF_RECEIVED' :
            case 'PROPOSAL_SENT':
                $('#amountId, #notesId').prop({readonly : false});
                break;
            case 'LIVE' :
            case 'AWAITING_GO_LIVE' :
                // TODO amount editable depends on campaign Allocations & accountType.ioManagement flag
                <s:if test="model.account.accountType.ioManagement">
                    $('#notesId').prop({readonly : true});
                </s:if>
                <s:else>
                    $('#poNumberId, #ioNumberId, #attachField, #browseFile, #addLink').prop({readonly : true});
                </s:else>
                break;
            case 'LOST' :
                $('#poNumberId, #ioNumberId, #attachField, #browseFile, #addLink').prop({readonly : true});
                $('#amountId, #notesId').prop({readonly : false});
                <s:if test="id != null">
                    $('#attachIOTable').hide();
                </s:if>
                <s:if test="!existingFiles.empty">
                    showIOMembers();
                    hideAddRemoveLinks();
                    return;
                </s:if>
                break;
        }
    </s:if>
        hideAndShowIOMembers();
    }

    function checkProbability() {
        var probability = $('#probabilityId').val();
        if (probability == 'LOST' || probability == 'BRIEF_RECEIVED' || probability == 'PROPOSAL_SENT') {
            if ($('input[name="existingFiles"]').size() > 0) {
                alert('<fmt:message key="opportunity.delete.ioFiles.warning"/>');
                $('#probabilityId').val('IO_SIGNED');
            }
        }
    }

    function hideAndShowIOMembers() {
        var probability = $('#probabilityId').val();
        $('#attachIOTable').show();
        if (probability == 'IO_SIGNED') {
            showIOMembers();
            toggleIOMembersInputON();
            showAddRemoveLinks();
        } else if (probability == 'AWAITING_GO_LIVE' || probability == 'LIVE') {
            showIOMembers();
            toggleIOMembersLabelON();
        <s:if test="id != null">
            $('#attachIOTable').hide();
            hideAddRemoveLinks();
        </s:if>
        <s:else>
            showAddRemoveLinks();
        </s:else>
        } else {
            // if probability == 'TARGET' || probability == 'FIRST_CONTACT' || probability == 'BRIEF_RECEIVED' || probability == 'PROPOSAL_SENT' || probability == ''
            hideIOMembers();
            hideAddRemoveLinks();
        }
    }
    
    function toggleIOMembersInputON() {
        $('label#poNumberId').hide();
        $('label#ioNumberId').hide();
        $('input#poNumberId').show();
        $('input#ioNumberId').show();
    }
    
    function toggleIOMembersLabelON() {
        $('label#poNumberId').show();
        $('label#ioNumberId').show();
        $('input#poNumberId').hide();
        $('input#ioNumberId').hide();
    }

    function showIOMembers() {
        $('#poNumberField, #ioNumberField, #attachField, #browseFile').show();
    }

    function hideIOMembers() {
        $('#poNumberField, #ioNumberField, #attachField, #browseFile').hide();
    }

    function showAddRemoveLinks() {
        $('#addLink, #removeLink').show();
    }

    function hideAddRemoveLinks() {
        $('#addLink, #removeLink').hide();
    }

    $().ready(function() {
        probabilitySelected();
        hideAndShowIOMembers();
    });

</script>
<s:form action="%{model.id != null?'update':'create'}" id="opportunitySave"  enctype="multipart/form-data" method="post">
    <s:hidden name="id"/>
    <s:hidden name="mode"/>
    <s:hidden name="account.id"/>
    <s:hidden name="version"/>
    <ui:pageHeadingByTitle/>

    <div class="wrapper">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:actionerror/>
    </div>

    <ui:section>
        <ui:fieldGroup>
            <ui:field labelKey="opportunity.name" labelForId="nameId" required="true" errors="name">
                <s:textfield name="name" id="nameId" cssClass="middleLengthText" maxlength="100"/>
            </ui:field>

            <c:set var="opportunityAmountLabel">
                <fmt:message key="opportunity.amount"/> (${ad:currencySymbol(existingAccount.currency.currencyCode)})
            </c:set>
            <ui:field label="${opportunityAmountLabel}" labelForId="amountId" required="true" errors="amount">
                <s:textfield name="amount" id="amountId" cssClass="middleLengthText" maxlength="16"/>
            </ui:field>

            <ui:field labelKey="opportunity.probability" labelForId="probabilityId" required="true"
                      errors="probability">
                <s:if test="availableProbabilities.size() > 1">
                    <s:select name="probability" id="probabilityId" value="probability"
                              headerValue="%{getText('form.select.pleaseSelect')}" headerKey=""
                              list="availableProbabilities" cssClass="middleLengthText"
                              listValue="getText('enum.opportunity.probability.'+name())"
                              onchange="checkProbability();probabilitySelected();"/>
                </s:if>
                <s:else>
                    <fmt:message key="enum.opportunity.probability.${model.probability}"/>
                    <s:hidden name="probability" id="probabilityId"/>
                </s:else>
            </ui:field>

            <ui:field labelKey="opportunity.notes" labelForId="notesId" errors="notes">
                <s:textarea name="notes" id="notesId" cssClass="middleLengthText"/>
            </ui:field>

            <ui:field labelKey="opportunity.poNumber" id="poNumberField" errors="poNumber">
                <s:label name="poNumber" id="poNumberId" cssClass="middleLengthText"/>
                <s:textfield name="poNumber" id="poNumberId" cssClass="middleLengthText" maxlength="50"/>
            </ui:field>

            <ui:field labelKey="opportunity.ioNumber" id="ioNumberField" required="true" errors="ioNumber">
                <s:label name="ioNumber" id="ioNumberId" cssClass="middleLengthText"/>
                <s:textfield name="ioNumber" id="ioNumberId" cssClass="middleLengthText" maxlength="50"/>
            </ui:field>

            <ui:field labelKey="opportunity.attachIO" required="true" id="attachField" errors="ioFiles">
                <s:iterator value="existingFiles" var="existingFile">
                    <table id="existingFiles" class="dataView">
                        <tbody>
                        <tr class="existingDynamicRow">
                            <td class="field"><ui:button messageText="${existingFile}" onclick="viewFile('${existingFile}')" id="browseFile"/>
                                <s:hidden name="existingFiles" id="browseFile" value="%{#existingFile}"/></td>
                            <td class="withButton"><ui:button id="removeLink" message="form.delete"
                                                              onclick="UI.Util.Table.delRow($(this).parents('.existingDynamicRow')[0]);"/></td>
                        </tr>
                        </tbody>
                    </table>
                </s:iterator>
                <table id="attachIOTable" class="dataView" style="border:none;">
                    <tbody>
                    <tr class="hide dynamicRow">
                        <td class="field">
                            <s:file name="addedFiles" id="browseFile" size="70"/>
                        </td>
                        <td class="withButton">
                            <ui:button id="removeLink" message="form.delete"
                                       onclick="UI.Util.Table.delRow($(this).parents('.dynamicRow')[0]);"/>
                        </td>
                    </tr>
                    <tr>
                        <s:if test="existingFiles.empty">
                            <td class="field">
                                <s:file name="addedFiles" id="browseFile" size="70"/>
                            </td>
                        </s:if>
                    </tr>
                    </tbody>
                </table>
            </ui:field>

            <ui:field id="addLink">
                <ui:button message="opportunity.add.image" onclick="UI.Util.Table.addRow('attachIOTable');" id="addImageLink"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
    <div class="wrapper">
        <ui:button message="form.save" id="saveButton" type="submit"/>
        <c:choose>
            <c:when test="${empty id}">
                <ui:button message="form.cancel" onclick="location='main.action?advertiserId=${account.id}';" type="button"/>
            </c:when>
            <c:otherwise>
                <ui:button message="form.cancel" onclick="location='view.action?id=${id}';" type="button"/>
            </c:otherwise>
        </c:choose>
    </div>
</s:form>
