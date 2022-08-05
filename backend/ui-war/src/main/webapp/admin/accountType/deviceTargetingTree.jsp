<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="/ad/serverUI" prefix="ad"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<tiles:useAttribute name="tree" ignore="true"/>
<tiles:useAttribute name="rootChannels" ignore="true"/>
<tiles:useAttribute name="prefix"/>

<c:if test="${tree != null}">
    <table class="nomargin" id="${prefix}DeviceTargetingTable">
        <tr>
            <td class="withField"><label class="withInput">
                    <input type="checkbox"
                    name="deviceHelper.selectedChannels"
                    id="${prefix}SelectedChannels"
                    <c:if test="${ad:contains(deviceHelper.selectedChannels, tree.element.id)}">checked="checked"</c:if>
                    <c:if test="${ad:contains(deviceHelper.disabledChannels, tree.element.id)}">disabled</c:if>
                    data-children="<c:forEach items="${tree.children}" var="child"><c:out value="${child.element.id};"/></c:forEach>"
                    value="${tree.element.id}"
                    class="withInput"> <fmt:message
                        key="ccg.devices.${prefix}" />
            </label></td>
        </tr>
    </table>
    <table id="${prefix}DeviceChannelsTable" style="margin-bottom:1em;">
        <tr>
            <c:forEach items="${tree.children}"
                var="channelTree">
                <td style="vertical-align: top;">
                    <ad:tree
                        items="${channelTree}" var="node"
                        openNodes="${deviceHelper.openNodes}">
                        <c:set var="currentHintKey" value=""/>
                        <c:forEach items="${rootChannels}" var="idName">
                            <c:if test="${idName.id.equals(node.element.id)}">
                                <c:set var="currentHintKey" value="ccg.devices.${idName.name}.tip"/>
                            </c:if>
                        </c:forEach>

                        <label class="withInput"> <input
                            type="checkbox"
                            id="${prefix}SelectedChannels_${node.element.id}"
                            name="deviceHelper.selectedChannels"
                            value="${node.element.id}"
                            <c:if test="${ad:contains(deviceHelper.selectedChannels, node.element.id)}">checked="checked"</c:if>
                            <c:if test="${ad:contains(deviceHelper.disabledChannels, node.element.id)}">disabled</c:if>
                            data-parent="${node.level == 1? tree.element.id : node.parent.element.id}"
                            data-children="<c:forEach items="${node.children}" var="child"><c:out value="${child.element.id};"/></c:forEach>" />
                            <span><c:out
                                    value="${ad:localizeName(node.element.localizableName)}" /></span>
                            <c:if
                                test="${node.element.status.letter == 'I'}">
                                <span><fmt:message
                                        key="suffix.inactive" /></span>
                            </c:if> <c:if
                                test="${node.element.status.letter == 'D'}">
                                <span><fmt:message
                                        key="suffix.deleted" /></span>
                            </c:if>
                            <c:if test="${currentHintKey != ''}">
                                <ui:hint inline="true">
                                    <fmt:message key="${currentHintKey}" />
                                </ui:hint>
                            </c:if>
                        </label>
                    </ad:tree>
                </td>
            </c:forEach>
        </tr>
    </table>
    
    <script type="text/javascript">
        if (UI.initTrees === undefined) {
            UI.initTrees    = true;
            $(function(){
                $(document).on('click', '.expand', function(e){
                    e.preventDefault();
                    var jqParent    = $(this).parent();
                    if (jqParent.hasClass('treeClosed')) {
                        jqParent.removeClass('treeClosed').addClass('treeOpen');
                    } else {
                        jqParent.removeClass('treeOpen').addClass('treeClosed');
                    }
                });
            });
        }
    </script>
</c:if>
