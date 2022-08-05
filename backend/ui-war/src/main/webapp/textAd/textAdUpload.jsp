<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<s:set var="advertiserId" value="advertiserId"/>

<script type="text/javascript">
    function submitForm(action, disable) {
        var frm = $('#textAdSubmit');
        frm.data({preventDoubleSubmit : !!disable});
        frm.attr('action', action);
        frm.submit();
    }
    function downloadTemplate(){
        var tgtType = $('[name=tgtType]:checked').val();
        var format = $('[name=format]:checked').val();
        var accountId = "${ad:accountParam('advertiserId', advertiserId)}";
        UI.Util.openLink('template.action?' + (accountId ? accountId +"&"  : "")  +'tgtType=' + tgtType + '&format=' + format);
    }
</script>

<ui:pageHeadingByTitle/>

<ui:section titleKey="TextAd.upload.title">
    <s:if test="not validationResult">
        <s:actionmessage/>
        <s:actionerror escape="false" />
    </s:if>
    
    <s:form action="%{#request.moduleName}/validate" method="POST" enctype="multipart/form-data" id="textAdUpload">
        <s:hidden name="advertiserId"/>
        <s:if test="campaignTypes.size()==1">
            <hidden name="tgtType" value="${campaignTypes[0]}"/>
        </s:if>

        <ui:fieldGroup>
            <s:if test="campaignTypes.size()!=1">
                <ui:field labelKey="TextAd.upload.campaignType" required="true" errors="tgtType">
                    <s:radio name="tgtType"
                             list="campaignTypes"
                             listKey="name()" listValue="getText('TextAd.upload.campaignType.' + name())"/>
                </ui:field>
            </s:if>
            <ui:field labelKey="TextAd.upload.format" required="true" errors="format">
                <s:radio list="'CSV'" listValue="getText('form.csv')" name="format"/>
                <s:radio list="'TAB'" listValue="getText('form.tab')" name="format"/>
                <s:radio list="'XLSX'" listValue="getText('form.xlsx')" name="format"/>
            </ui:field>
            <ui:field>
                <ui:button message="channel.download.template"  type="button" onclick="downloadTemplate()" />
            </ui:field>
            <ui:field labelKey="TextAd.upload.file" labelForId="uploadFile" required="true" errors="bulkFile">
                <s:file name="bulkFile" id="bulkFile"/>
            </ui:field>
            <ui:field cssClass="withButton">
                <ui:button message="TextAd.upload.button" type="submit"/>
                <s:if test="not validationResult">
                    <ui:button message="form.cancel" href="${_context}/campaign/campaigns.action${ad:accountParam('?advertiserId', advertiserId)}" type="button"/>
                </s:if>
            </ui:field>
        </ui:fieldGroup>
    </s:form>
</ui:section>
    
<s:if test="validationResult">
    <p><s:actionmessage/>
    <s:actionerror/></p>

    <s:form action="%{#request.moduleName}/submit" method="POST" id="textAdSubmit">
        <ui:section titleKey="TextAd.validation.title">
            <ui:fieldGroup>
                <ui:field>
                    <s:hidden name="advertiserId"/>
                    <s:hidden name="format"/>
                    <s:hidden name="alreadySubmitted"/>
                    <s:hidden name="validationResult.id"/>
                    <s:hidden name="tgtType"/>
                    <div>
                        <s:hidden name="validationResult.campaigns.created"/>
                        <fmt:message key="TextAd.validation.newCampaign"><fmt:param>
                            <s:property value="validationResult.campaigns.created"/>
                        </fmt:param></fmt:message>
                    </div>
                    <div>
                        <s:hidden name="validationResult.groups.created"/>
                        <fmt:message key="TextAd.validation.newGroup"><fmt:param>
                            <s:property value="validationResult.groups.created"/>
                        </fmt:param></fmt:message>
                    </div>
                    <div>
                        <s:hidden name="validationResult.ads.created"/>
                        <fmt:message key="TextAd.validation.newAds"><fmt:param>
                            <s:property value="validationResult.ads.created"/>
                        </fmt:param></fmt:message>
                    </div>
                    <div>
                        <s:hidden name="validationResult.keywords.created"/>
                        <fmt:message key="TextAd.validation.newKeywords"><fmt:param>
                            <s:property value="validationResult.keywords.created"/>
                        </fmt:param></fmt:message>
                    </div>
                    <div>
                        <s:hidden name="validationResult.creatives.created"/>
                        <fmt:message key="TextAd.validation.newCreatives"><fmt:param>
                            <s:property value="validationResult.creatives.created"/>
                        </fmt:param></fmt:message>
                    </div>

                    <div>
                        <s:hidden name="validationResult.campaigns.updated"/>
                        <fmt:message key="TextAd.validation.existingCampaign"><fmt:param>
                            <s:property value="validationResult.campaigns.updated"/>
                        </fmt:param></fmt:message>
                    </div>
                    <div>
                        <s:hidden name="validationResult.groups.updated"/>
                        <fmt:message key="TextAd.validation.existingGroup"><fmt:param>
                            <s:property value="validationResult.groups.updated"/>
                        </fmt:param></fmt:message>
                    </div>
                    <div>
                        <s:hidden name="validationResult.ads.updated"/>
                        <fmt:message key="TextAd.validation.existingAds"><fmt:param>
                            <s:property value="validationResult.ads.updated"/>
                        </fmt:param></fmt:message>
                    </div>
                    <div>
                        <s:hidden name="validationResult.keywords.updated"/>
                        <fmt:message key="TextAd.validation.existingKeywords"><fmt:param>
                            <s:property value="validationResult.keywords.updated"/>
                        </fmt:param></fmt:message>
                    </div>
                    <div>
                        <s:hidden name="validationResult.creatives.updated"/>
                        <fmt:message key="TextAd.validation.existingCreatives"><fmt:param>
                            <s:property value="validationResult.creatives.updated"/>
                        </fmt:param></fmt:message>
                    </div>
                    
                    <s:if test="validationResult.lineWithErrors > 0">
                        <div>
                            <span class="errors">
                                <s:hidden name="validationResult.lineWithErrors"/>
                                <fmt:message key="TextAd.validation.errors"><fmt:param>
                                    <s:property value="validationResult.lineWithErrors"/>
                                </fmt:param></fmt:message>
                            </span>
                        </div>
                    </s:if>

                    <div>
                        <fmt:message key="TextAd.clickExport"/>
                    </div>
                </ui:field>
                <ui:field cssClass="withButton">
                    <s:if test="validationResult.lineWithErrors == 0 and not alreadySubmitted and not hasErrors()">
                        <ui:button message="form.submit" onclick="submitForm('submit.action', true)" type="button"/>
                    </s:if>
                    <ui:button message="form.export" onclick="submitForm('export.action', false)" type="button"/>
                    <s:if test="not alreadySubmitted">
                        <ui:button message="form.cancel" href="${_context}/campaign/campaigns.action${ad:accountParam('?advertiserId', advertiserId)}" type="button"/>
                    </s:if>
                </ui:field>
            </ui:fieldGroup>
        </ui:section>
    </s:form>
</s:if>
