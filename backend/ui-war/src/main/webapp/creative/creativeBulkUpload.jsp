<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui"%>
<%@ taglib uri="/ad/serverUI" prefix="ad"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
    function submitForm(action, disable) {
        var frm = $('#creativesUploadSubmit');
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

    function downloadTemplate() {
        var format = $('[name=format]:checked').val();
        UI.Util.openLink('template.action?format=' + format );
    }

</script>

<ui:pageHeadingByTitle />

<ui:section titleKey="creative.upload.breadcrumbs">
    <s:if test="not validationResult">
        <s:actionmessage />
        <s:actionerror escape="false" />
    </s:if>

    <s:form action="%{#request.moduleName}/upload/validate" method="POST" enctype="multipart/form-data" id="creativesUploadId">
        <s:hidden name="id"/>
        <s:hidden name="advertiserId"/>

        <ui:fieldGroup>
            <ui:field labelKey="channel.upload.format" required="true" errors="format">
                <s:radio list="'CSV'" listValue="getText('form.csv')" name="format" id="formatCSV"/>
                <s:radio list="'TAB'" listValue="getText('form.tab')" name="format"/>
                <s:radio list="'XLSX'" listValue="getText('form.xlsx')" name="format"/>
            </ui:field>
            <ui:field>
                <ui:button message="creative.upload.downloadTemplate"  type="button" onclick="downloadTemplate()" />
            </ui:field>
            <ui:field labelKey="creative.upload.file" labelForId="bulkFile" required="true" errors="bulkFile">
                <s:file name="bulkFile" id="bulkFile" />
            </ui:field>
            <ui:field cssClass="withButton">
                <ui:button message="creative.upload.button" type="submit" />
                <s:if test="not validationResult">
                    <ui:button message="form.cancel" href="${_context}/creative/main.action?advertiserId=${advertiserId}" onclick="" type="button" />
                </s:if>
            </ui:field>
        </ui:fieldGroup>
    </s:form>
</ui:section>

<s:if test="validationResult">
    <p><s:actionmessage />
        <s:actionerror /></p>

    <s:form action="%{#request.moduleName}/upload/submit" method="POST" id="creativesUploadSubmit">
        <s:hidden name="id"/>
        <s:hidden name="advertiserId"/>

        <ui:section titleKey="creative.upload.resultTitle">
            <ui:fieldGroup>
                <ui:field>
                    <s:hidden name="format" />
                    <s:hidden name="alreadySubmitted" />
                    <s:hidden name="validationResult.id" />
                    <div>
                        <s:hidden name="validationResult.creatives.created" />
                        <fmt:message key="creative.upload.validation.new">
                            <fmt:param>
                                <s:property value="validationResult.creatives.created" />
                            </fmt:param>
                        </fmt:message>
                    </div>
                    <div>
                        <s:hidden name="validationResult.creatives.updated" />
                        <fmt:message key="creative.upload.validation.updated">
                            <fmt:param>
                                <s:property value="validationResult.creatives.updated" />
                            </fmt:param>
                        </fmt:message>
                    </div>
                    <s:if test="validationResult.lineWithErrors > 0">
                        <div>
                            <span class="errors">
                                <s:hidden name="validationResult.lineWithErrors" />
                                <fmt:message key="creative.upload.validation.errors">
                                    <fmt:param>
                                        <s:property value="validationResult.lineWithErrors" />
                                    </fmt:param>
                                </fmt:message>
                            </span>
                        </div>
                    </s:if>
                    <div>
                        <fmt:message key="creative.upload.validation.clickExport" />
                    </div>
                </ui:field>
                <ui:field cssClass="withButton">
                    <s:if test="validationResult.lineWithErrors == 0 and not alreadySubmitted and not hasErrors()">
                        <ui:button message="form.submit" onclick="submitForm('submit.action', true)" type="button" />
                    </s:if>
                    <ui:button message="form.export" onclick="submitForm('export.action', false)" type="button" />
                    <s:if test="not alreadySubmitted">
                        <ui:button message="form.cancel" href="${_context}/creative/upload/main.action?advertiserId=${advertiserId}" type="button" />
                    </s:if>
                </ui:field>
            </ui:fieldGroup>
        </ui:section>
    </s:form>
</s:if>
