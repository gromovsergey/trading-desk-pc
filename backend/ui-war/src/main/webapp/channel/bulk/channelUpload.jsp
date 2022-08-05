<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui"%>
<%@ taglib uri="/ad/serverUI" prefix="ad"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
    function submitForm(action, disable) {
        var frm = $('#channelSubmit');
        frm.data({
            preventDoubleSubmit : !!disable
        });
        frm.attr('action', action);
        frm.submit();
    }
    
    function chooseFormat(format) {
        $('#format' + format).prop({
            checked : true
        });
    }
    
    function downloadTemplate(){
        var channelType = $('[name=channelType]:checked').val();
        var format = $('[name=format]:checked').val();
        UI.Util.openLink('template.action?channelTypeHidden=' + channelType + '&format=' + format );
    }
    
</script>

<s:set var="accountId" value="accountId" />

<ui:pageHeadingByTitle />

<ui:section titleKey="channel.upload.title">
    <s:if test="not validationResult">
        <s:actionmessage />
        <s:actionerror escape="false" />
    </s:if>

    <s:form action="upload/validate" method="POST" enctype="multipart/form-data" id="channelUpload">
        <s:hidden name="accountId" />
        <ui:fieldGroup>
            <ui:field labelKey="channel.upload.type" required="true" errors="channelType">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField"><s:radio list="'BEHAVIORAL'" listValue="getText('channel.type.behavioral')" name="channelType" id="channelTypeBEHAVIORAL" /></td>
                        <td>
                            <ui:hint>
                                <fmt:message key="channel.export.behavioral.tip"/>
                            </ui:hint>
                        </td>
                    </tr>
                    <tr>
                        <td class="withField"><s:radio list="'EXPRESSION'" listValue="getText('channel.type.expression')" name="channelType" id="channelTypeEXPRESSION" /></td>
                        <td>
                            <ui:hint>
                                <fmt:message key="channel.export.expression.tip"/>
                            </ui:hint>
                        </td>
                </table>
            </ui:field>
            <ui:field labelKey="channel.upload.format" required="true" errors="format">
                <s:radio list="'CSV'" listValue="getText('form.csv')" name="format" id="formatCSV"/>
                <s:radio list="'TAB'" listValue="getText('form.tab')" name="format" id="formatTAB"/>
                <s:radio list="'XLSX'" listValue="getText('form.xlsx')" name="format" id="formatXLSX"/>
            </ui:field>
            <ui:field>
                <ui:button message="channel.download.template"  type="button" onclick="downloadTemplate()" />
            </ui:field>
            <ui:field labelKey="channel.upload.file" labelForId="bulkFile" required="true" errors="bulkFile">
                <s:file name="bulkFile" id="bulkFile" />
            </ui:field>
            <ui:field cssClass="withButton">
                <ui:button message="channel.upload.button" type="submit" />
                <s:if test="not validationResult">
                    <ui:button message="form.cancel"
                        href="${_context}/channel/contextMain.action${ad:accountParam('?accountId', accountId)}" onclick="" type="button" />
                </s:if>
            </ui:field>
        </ui:fieldGroup>
    </s:form>
</ui:section>

<s:if test="validationResult">
    <p><s:actionmessage />
    <s:actionerror /></p>

    <s:form action="upload/submit" method="POST" id="channelSubmit">
        <ui:section titleKey="channel.validation.title">
            <ui:fieldGroup>
                <ui:field>
                    <s:hidden name="accountId" />
                    <s:hidden name="format" />
                    <s:hidden name="alreadySubmitted" />
                    <s:hidden name="validationResult.id" />
                    <s:hidden name="validationResult.channelType" />
                    <div>
                        <s:hidden name="validationResult.channels.created" />
                        <fmt:message key="channel.validation.newChannel">
                            <fmt:param>
                                <s:property value="validationResult.channels.created" />
                            </fmt:param>
                        </fmt:message>
                    </div>
                    <div>
                        <s:hidden name="validationResult.channels.updated" />
                        <fmt:message key="channel.validation.existingChannel">
                            <fmt:param>
                                <s:property value="validationResult.channels.updated" />
                            </fmt:param>
                        </fmt:message>
                    </div>
                    <s:if test="validationResult.lineWithErrors > 0">
                        <div>
                            <span class="errors"> <s:hidden name="validationResult.lineWithErrors" /> <fmt:message
                                    key="channel.validation.errors">
                                    <fmt:param>
                                        <s:property value="validationResult.lineWithErrors" />
                                    </fmt:param>
                                </fmt:message>
                            </span>
                        </div>
                    </s:if>
                    <div>
                        <fmt:message key="channel.clickExport" />
                    </div>
                </ui:field>
                <ui:field cssClass="withButton">
                    <s:if test="validationResult.lineWithErrors == 0 and not alreadySubmitted and not hasErrors()">
                        <ui:button message="form.submit" onclick="submitForm('submit.action', true)" type="button" />
                    </s:if>
                    <ui:button message="form.export" onclick="submitForm('export.action', false)" type="button" />
                    <s:if test="not alreadySubmitted">
                        <ui:button message="form.cancel"
                            href="${_context}/channel/contextMain.action${ad:accountParam('?accountId', accountId)}" type="button" />
                    </s:if>
                </ui:field>
            </ui:fieldGroup>
        </ui:section>
    </s:form>
</s:if>
