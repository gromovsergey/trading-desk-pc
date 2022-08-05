<%@ tag description="UI Tab" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ tag import="org.apache.commons.lang.*" %>


<%@ attribute name="selectedIds" required="true" type="java.util.List"%> 
<%@ attribute name="treeId" required="true" type="java.lang.String"%>

<c:set var="JSTreeId"><c:out value="${fn:toUpperCase(fn:substring(treeId, 0 , 1))}${fn:substring(treeId , 1 , fn:length(treeId))}"/></c:set>

<jsp:include page="/WEB-INF/tags/treeFilter/treeFilterJS.jsp">
    <jsp:param value="${JSTreeId}" name="treeId"/>
</jsp:include>



<script type="text/javascript">
    var lastFilterExpandedStatus${JSTreeId} = null;
    function rememberLastExpandedStatus${JSTreeId}(status) {
        lastFilterExpandedStatus${JSTreeId} = status;
    }

    $().ready(function(){
        $('#${treeId}').parents('form:first').submit(function() {
            if ($('#${treeId}').find('input[type="checkbox"]').length > 0
                    && $('#${treeId}').find('input[type="checkbox"][id!="treeRootCheckboxTreeFilter"]:checked').length == 0) {
                showRequiredAlert${JSTreeId}();
                return false;
            }
        });
    });

    

    function getOptions${JSTreeId}(id, type, dataBlockId, root, ajaxCallback) {
        var dataBlock = $('#' + dataBlockId);
        var sendingData = (typeof getSendingData${JSTreeId} == 'function') ? getSendingData${JSTreeId}() : {};
        sendingData.root = root; 
        sendingData.ownerId = id;
        sendingData.selectedIds = [<%=StringUtils.join(((java.util.List)jspContext.getAttribute("selectedIds")).toArray(), ",")%>];
        sendingData.treeId = '${JSTreeId}';
        if (dataBlock.children().length == 0 || sendingData.root) {
            var isPreselected = sendingData != null ? sendingData.selectedIds.length > 0 : false;
            var expandedStatus;
            if (lastFilterExpandedStatus${JSTreeId} === null) {
                expandedStatus = isPreselected;
            } else {
                expandedStatus = lastFilterExpandedStatus${JSTreeId};
            }

            
            $.ajax({
                url : 'treeFilter/' + type + '.action',
                type: 'POST',
                data : sendingData ,
                success: function(data) {
                    dataBlock.html(data);
                    if (ajaxCallback !== undefined && typeof ajaxCallback === 'function') {
                        ajaxCallback();
                    }
                    var checkbox = dataBlock.closest('li,div').find('input[type="checkbox"]:first')[0];
                    switchChildren(checkbox);
                    if ($('#treeFilterCollapsible${JSTreeId}').size() > 0){
                        $('#treeFilterCollapsible${JSTreeId}').collapsible();
                        if (expandedStatus) {
                            $('#treeFilterCollapsible${JSTreeId}').data('collapsible').expand();
                        }
                    }
                    if (sendingData.root && isPreselected) {
                        // deselect all
                        $("#treeRootCheckbox${JSTreeId}").removeAttr("checked");
                        switchAll($("#treeRootCheckbox${JSTreeId}"));
                        // select selected node
                        var levels = (typeof getLevels${JSTreeId} == 'function') ? getLevels${JSTreeId}() : ["advertiserIds", "campaignIds", "ccgIds"];
                        for (var i = sendingData.selectedIds.length - 1; i >= 0; i--) {
                            var selectedId = sendingData.selectedIds[i];
                            if (selectedId) {
                                var chk = $("[name^='" + levels[i] + "'][value='" + selectedId + "']");
                                if (chk.length) {
                                    chk.prop("checked", true);
                                    switchAll(chk.get(0));

                                    // scroll to selection
                                    var scrollParent	= chk.closest(".treeFilterContainer"),
                                            offset				= chk.offset().top - scrollParent.offset().top + scrollParent.scrollTop() - 20;
                                    scrollParent.scrollTop(offset);
                                    if ($.browser.msie){ // refreshing div content in ie
                                        $(window).on('scroll resize', function(){
                                            if (scrollParent.scrollTop()|0 === offset|0) {
                                                scrollParent.scrollTop(offset-1);
                                                scrollParent.scrollTop(offset);
                                            }
                                        });
                                    }
                                    break;
                                }
                            }
                        }
                    }
                },
                waitHolder: $('#filters')
            });
        }
    }
    
</script>
<div id="${treeId}"></div>