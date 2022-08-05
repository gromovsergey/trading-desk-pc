<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<script type="text/javascript">
    function toggleAllRows(header) {
        $('[name=setNumberIds]').prop({checked : header.checked});
    }

    function getSelectedRows() {
        var rows = [];
        $('[name=setNumberIds]').each(function() {
            if (this.checked) {
                rows.push({id: this.value});
            }
        });
        return rows;
    }

    function proceedBulkOperationForChecked() {
        var rows = getSelectedRows();
        if (rows.length == 0) {
            return;
        }
        var ids = $.map(rows, function(row){
            return row.id;
        });

        if(confirm('${ad:formatMessage('confirmBulkChange')}')) {
            var frm = $('#updateFormId');
            frm.submit();
        }
    }

    $(function(){
        $('#bulk_btn').menubutton();
    });

</script>

<s:if test="placementsBlacklist != null && placementsBlacklist.size > 0">

    <ui:pages pageSize="${searchParams.pageSize}"
              total="${searchParams.total}"
              selectedNumber="${searchParams.page}"
              handler="goToPage"
              visiblePagesCount="10"
              displayHeader="true"/>

    <form id="updateFormId" action="drop.action" method="POST">
        <s:hidden name="searchParams.url"/>
        <s:hidden name="searchParams.page"/>
        <s:hidden name="id"/>
        <input type="hidden" name="PWSToken" value="${sessionScope.PWSToken}"/>

        <table class="dataViewSection">
            <tr class="controlsZone">
                <td>
                    <table class="grouping">
                        <tr>
                            <td class="withButtons">
                                <table class="fieldAndAccessories">
                                    <tr>
                                        <td class="withButton">
                                            <a class="button" id="bulk_btn" href="#"><fmt:message key="ccg.bulk.menu"/></a>
                                            <ul id="bulk_menu" class="hide b-menu__bulk">
                                                <c:if test="${ad:isPermitted0('PlacementsBlacklist.update')}">
                                                    <li>
                                                        <ui:button message="form.delete" onclick="proceedBulkOperationForChecked();" />
                                                    </li>
                                                </c:if>
                                            </ul>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr class="bodyZone">
                <td>
                    <table class="dataView" id="creativeList">
                        <thead>
                        <tr><th width="1">
                            <input type="checkbox" onclick="toggleAllRows(this)"/>
                        </th><th>
                            <s:text name="admin.placementsBlacklist.url"/>
                        </th><th>
                            <s:text name="admin.placementsBlacklist.adSize"/>
                        </th><th>
                            <s:text name="admin.placementsBlacklist.reason"/>
                        </th><th>
                            <s:text name="admin.placementsBlacklist.dateAdded"/>
                        </th><th>
                            <s:text name="admin.placementsBlacklist.addedBy"/>
                        </th>
                        </tr>
                        </thead>
                        <tbody>
                        <s:iterator value="placementsBlacklist" var="blacklist" status="row">
                            <tr class="creativeRow"><td>
                                <input type="checkbox" name="setNumberIds" value="${blacklist.id}"/>
                            </td><td>
                                <a href="${blacklist.url}"><c:out value="${blacklist.url}"/></a>
                            </td><td>
                                <c:out value="${blacklist.sizeName}"/>
                            </td><td>
                                <c:out value="${blacklist.reasonAsString}"/>
                            </td><td>
                                <c:out value="${blacklist.dateAdded}"/>
                            </td><td>
                                <a href="${_context}/InternalUser/view.action?id=${blacklist.user.id}"><c:out value="${blacklist.user.firstName} ${blacklist.user.lastName}"/></a>
                            </td>
                            </tr>
                        </s:iterator>
                        </tbody>
                    </table>
                </td>
            </tr>
        </table>
    </form>

    <ui:pages pageSize="${searchParams.pageSize}"
              total="${searchParams.total}"
              selectedNumber="${searchParams.page}"
              handler="goToPage"
              visiblePagesCount="10"
              displayHeader="true"/>

</s:if>
<s:else>
    <div class="wrapper">
        <fmt:message key="nothing.found.to.display"/>
    </div>
</s:else>
