<%@ page import="com.foros.model.channel.Channel" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">
    function approveCreative(creativeId) {
        var dataContainer = $('#creativeApproval_' + creativeId);

        new UI.AjaxLoader().switchOff();
        dataContainer.children('.ajax_loader').show();
        
        $.ajax({
            type : 'POST',
            url : 'approve.action',
            data : $.merge($('#searchForm').serializeArray(), $('#creativeForm_' + creativeId).serializeArray()),
            success : function(data) {
                dataContainer.html(data);
                $(window).resize();
                fixErrorWidth(dataContainer);
            }
        });
    }

    function rejectCreative(creativeId) {
        var dataContainer = $('#creativeApproval_' + creativeId);

        new UI.AjaxLoader().switchOff();
        dataContainer.children('.ajax_loader').show();
        
        $.ajax({
            type : 'POST',
            url : 'reject.action',
            data : $.merge($('#rejectForm').serializeArray(), $.merge($('#searchForm').serializeArray(), $('#creativeForm_' + creativeId).serializeArray())),
            success : function(data, textStatus) {
                if ($(data).filter('#rejectForm').length > 0) {
                    $('#rejectDialog').html(data);
                    $('#rejectFormCId').val(creativeId);
                } else {
                    $('#rejectDialog').dialog('close');
                    dataContainer.html(data);
                    $(window).resize();
                    fixErrorWidth(dataContainer);
                }
            }
        });
    }

    function showRejectDialog(creativeId) {
        var dialogData = $('#rejectDialog');

        $('#rejectFormCId').val(creativeId);
        $('#feedback').val('');
        $('input:radio[id^=rejectReason]', dialogData).prop({"checked":false});
        $('span.errors', dialogData).remove();

        dialogData.dialog("open");
    }
    
    function fixErrorWidth(jqData) {
        var iFW = $('iframe', jqData).eq(0).width(),
        iBW = $('.b-creative__approval-buttons', jqData).eq(0).width(),
        iMW    = (iFW < iBW) ? iBW : iFW;
        $('.wrapper', jqData).width(iMW);
    }

    $(function(){
        $('#rejectDialog').dialog({
            "autoOpen":     false,
            "title":        "<fmt:message key="site.creativesApproval.reject.title"/>",
            "modal":        true,
            "minWidth":     580,
            "minHeight":    500,
            "resizable":    true,
            "buttons": {
                "<fmt:message key="site.creativesApproval.button.reject"/>" : function() {
                    rejectCreative( $('#rejectFormCId').val() );
                },
                "<fmt:message key="form.cancel"/>" : function() {
                    $('#creativeApproval_' + $('#rejectFormCId').val()).children('.ajax_loader').hide();
                    $(this).dialog("close");
                }
            },
            "open": function(){
                $('textarea:visible:first', $(this)).focus();
            }
        });
    });
</script>

<div id="result" class="logicalBlock">
    <s:if test="creativeApprovals.size > 0">
        <p class="withWarningBig">
            <fmt:message key="site.creativesApproval.mixedContentWarning"/>
        </p>

        <ui:pages pageSize="${pageSize}"
                  total="${total}"
                  selectedNumber="${page}"
                  visiblePagesCount="10"
                  handler="goToPage"
                  displayHeader="true"/>

        <s:iterator var="approval" value="creativeApprovals" status="row">
            <c:if test="${currentSizeId == null || approval.creative.size.id != currentSizeId}">
                <c:if test="${!row.first}">
                    </div>
                </c:if>
                <ui:header><h2>Size: <c:out value="${approval.creative.size.name}"/></h2></ui:header>
                <div class="b-creative__approval-list">
                <c:set var="currentSizeId" value="${approval.creative.size.id}"/>
            </c:if>

            <div class="b-creative__approval ajax_loader_container" id="creativeApproval_${approval.creative.id}">
                <%@ include file="creativesApprovalElem.jsp" %>
            </div>

            <c:if test="${row.last}">
                </div>
            </c:if>
        </s:iterator>

        <ui:pages pageSize="${pageSize}"
                  total="${total}"
                  selectedNumber="${page}"
                  visiblePagesCount="10"
                  handler="goToPage"
                  displayHeader="true"/>
    </s:if>
    <s:else>
        <fmt:message key="site.creativesApproval.noCreatives"/>
    </s:else>
</div>

<div id="rejectDialog" class="hide">
    <%@ include file="creativesApprovalRejectDialog.jsp" %>
</div>