<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<ui:pageHeadingByTitle/>
<script type="text/javascript">
    $(function(){
        $(document).off('submit.preventDoubleSubmit');
    });

    function doSubmit(action, multiSubmitFlag) {
        $('#finalUploadForm').data({"preventDoubleSubmit": !!multiSubmitFlag}).attr('action', action).submit();
    }
</script>

<jsp:useBean id="requestContexts" type="com.foros.util.context.RequestContexts" scope="request"/>
<s:form styleId="uploadForm" action="%{#request.moduleName}/validateUpload" method="post" enctype="multipart/form-data">

    <s:hidden name="publisherId"/>
    
    <ui:section titleKey="form.upload.csv" errors="error">
        <s:actionmessage/>
        <ui:fieldGroup>
            <c:set var="textForTip">
                <fmt:message key="site.upload.fileTip">
                    <fmt:param value="${maxFileUploadSizeInMb}"/>
                    <fmt:param value="${maxRowCount}"/>
                </fmt:message>
            </c:set>
            <ui:field labelKey="form.file" labelForId="fileId" required="true" tipText="${textForTip}" errors="fileToUpload">
                
                <s:file name="fileToUpload" id="fileToUpload" cssClass="middleLengthText"/>
            </ui:field>
            
            <ui:field cssClass="withButton">
                <ui:button message="site.upload" type="submit" />
                <c:choose>
                    <c:when test="${not requestContexts.set}">
                        <ui:button message="form.cancel" action="admin/publisher/account/main" type="button"/>
                    </c:when>
                    <c:otherwise>
                        <ad:requestContext var="publisherContext"/>
                        <ui:button message="form.cancel" href="main.action${ad:accountParam('?accountId', publisherContext.accountId)}" type="button"/>
                    </c:otherwise>
                </c:choose>
            </ui:field>
            
        </ui:fieldGroup>
    </ui:section>
</s:form>

<c:if test="${validationResult.valid or validationResult.errorsExist}">
    <span class="infos"><s:actionmessage/></span>
    <s:actionerror/>
    <s:form action="%{#request.moduleName}/submitUpload" method="post" id="finalUploadForm">
        <s:hidden name="validationResult.id"/>
        <s:hidden name="publisherId"/>

        <ui:section titleKey="site.upload.validation.title">
            <div>
                <s:hidden name="validationResult.sites.created"/>
                <fmt:message key="site.upload.validation.site.created">
                    <fmt:param><c:out value="${validationResult.sites.created}"/></fmt:param>
                </fmt:message>
            </div>
            <div>
                <s:hidden name="validationResult.sites.updated"/>
                <fmt:message key="site.upload.validation.site.updated">
                    <fmt:param><c:out value="${validationResult.sites.updated}"/></fmt:param>
                </fmt:message>
            </div>
            <div>
                <s:hidden name="validationResult.tags.created"/>
                <fmt:message key="site.upload.validation.tag.created">
                    <fmt:param><c:out value="${validationResult.tags.created}"/></fmt:param>
                </fmt:message>
            </div>
            <div>
                <s:hidden name="validationResult.tags.updated"/>
                <fmt:message key="site.upload.validation.tag.updated">
                    <fmt:param><c:out value="${validationResult.tags.updated}"/></fmt:param>
                </fmt:message>
            </div>

            <c:if test="${validationResult.errorsExist}">
                <div>
                    <span class="errors">
                        <s:hidden name="validationResult.lineWithErrors"/>
                        <fmt:message key="site.upload.validation.tag.errors">
                            <fmt:param><c:out value="${validationResult.lineWithErrors}"/></fmt:param>
                        </fmt:message>
                    </span>
                </div>
            </c:if>

            <ui:fieldGroup>

                <ui:field><fmt:message key="site.upload.validation.clickExport"/></ui:field>

                <ui:field cssClass="withButton">
                    <c:if test="${not validationResult.errorsExist and not uploadSubmitted}">
                        <ui:button message="form.submit" onclick="doSubmit('submitUpload.action', true);" type="button"/>
                    </c:if>

                    <ui:button message="form.export" onclick="doSubmit('exportResult.action', false);" type="button"/>

                    <c:if test="${not uploadSubmitted}">
                        <c:choose>
                            <c:when test="${not requestContexts.set}">
                                <ui:button message="form.cancel" action="admin/publisher/account/main" type="button"/>
                            </c:when>
                            <c:otherwise>
                                <ui:button message="form.cancel" href="main.action${ad:accountParam('?accountId', param['publisherId'])}" type="button"/>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </ui:field>
            </ui:fieldGroup>
        </ui:section>
    </s:form>
</c:if>
