<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui"%>
<%@ taglib uri="/ad/serverUI" prefix="ad"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
    function submitForm(action, disable) {
        var frm = $('#placementsBlacklistSubmit');
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
        var format = $('[name=format]:checked').val();
        UI.Util.openLink('/admin/Country/PlacementsBlacklist/bulkUpload/template.action?format=' + format );
    }

</script>

<ui:pageHeadingByTitle />

<ui:section titleKey="admin.placementsBlacklist.bulkUpload.breadcrumbs">
    <s:if test="not validationResult">
        <s:actionmessage />
        <s:actionerror escape="false" />
    </s:if>

    <s:form action="bulkUpload/validate" method="POST" enctype="multipart/form-data" id="placementsBlacklistUpload">
        <s:hidden name="id"/>

        <ui:fieldGroup>
            <ui:field labelKey="admin.placementsBlacklist.bulkUpload.columns">
                <fmt:message key="admin.placementsBlacklist.bulkUpload.columnsDescription"/>
            </ui:field>
            <ui:field labelKey="channel.upload.format" required="true" errors="format">
                <s:radio list="'CSV'" listValue="getText('form.csv')" name="format" id="formatCSV"/>
                <s:radio list="'XLSX'" listValue="getText('form.xlsx')" name="format"/>
            </ui:field>
            <ui:field>
                <ui:button message="admin.placementsBlacklist.download.template"  type="button" onclick="downloadTemplate()" />
            </ui:field>
            <ui:field labelKey="admin.placementsBlacklist.bulkUpload.file" labelForId="bulkFile" required="true" errors="bulkFile">
                <s:file name="bulkFile" id="bulkFile" />
            </ui:field>
            <ui:field cssClass="withButton">
                <ui:button message="admin.placementsBlacklist.bulkUpload.button" type="submit" />
                <s:if test="not validationResult">
                    <ui:button message="form.cancel" href="${_context}/Country/PlacementsBlacklist/view.action?id=${id}" onclick="" type="button" />
                </s:if>
            </ui:field>
        </ui:fieldGroup>
    </s:form>
</ui:section>

<s:if test="validationResult">
    <p><s:actionmessage />
        <s:actionerror /></p>

    <s:form action="bulkUpload/submit" method="POST" id="placementsBlacklistSubmit">
        <s:hidden name="id"/>

        <ui:section titleKey="admin.placementsBlacklist.bulkUpload.resultTitle">
            <ui:fieldGroup>
                <ui:field>
                    <s:hidden name="format" />
                    <s:hidden name="alreadySubmitted" />
                    <s:hidden name="validationResult.id" />
                    <div>
                        <s:hidden name="validationResult.placementsBlacklist.created" />
                        <fmt:message key="admin.placementsBlacklist.bulkUpload.validation.new">
                            <fmt:param>
                                <s:property value="validationResult.placementsBlacklist.created" />
                            </fmt:param>
                        </fmt:message>
                    </div>
                    <div>
                        <s:hidden name="validationResult.placementsBlacklist.updated" />
                        <fmt:message key="admin.placementsBlacklist.bulkUpload.validation.dropped">
                            <fmt:param>
                                <s:property value="validationResult.placementsBlacklist.updated" />
                            </fmt:param>
                        </fmt:message>
                    </div>
                    <s:if test="validationResult.lineWithErrors > 0">
                        <div>
                            <span class="errors">
                                <s:hidden name="validationResult.lineWithErrors" />
                                <fmt:message key="admin.placementsBlacklist.bulkUpload.validation.errors">
                                    <fmt:param>
                                        <s:property value="validationResult.lineWithErrors" />
                                    </fmt:param>
                                </fmt:message>
                            </span>
                        </div>
                    </s:if>
                    <div>
                        <fmt:message key="admin.placementsBlacklist.bulkUpload.validation.clickExport" />
                    </div>
                </ui:field>
                <ui:field cssClass="withButton">
                    <s:if test="validationResult.lineWithErrors == 0 and not alreadySubmitted and not hasErrors()">
                        <ui:button message="form.submit" onclick="submitForm('submit.action', true)" type="button" />
                    </s:if>
                    <ui:button message="form.export" onclick="submitForm('export.action', false)" type="button" />
                    <s:if test="not alreadySubmitted">
                        <ui:button message="form.cancel" href="${_context}/Country/PlacementsBlacklist/bulkUpload.action?id=${id}" type="button" />
                    </s:if>
                </ui:field>
            </ui:fieldGroup>
        </ui:section>
    </s:form>
</s:if>
