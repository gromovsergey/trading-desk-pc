<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
    function showRequiredAlertTreeFilter() {
    	alert('${ad:formatMessage("errors.advertiserReport.filters.required")}');
	}

    function showTreeFilter() {
        var accountId = getAccountId();
        if (accountId) {
            var level = getAgencyId() ? 'advertisers' : 'campaigns';
            getOptionsTreeFilter(accountId, level, 'treeFilter', true);
            $('#treeFilter').show();
        } else {
            $('#treeFilter').hide();
        }
    }

    function getSendingDataTreeFilter() {
        return {
            display: ${isDisplay == 'true' ? 'true' : 'false'},
            detailLevel: $('[name=reportType]:checked').val()
    	};
    }
</script>


<ui:treeFilter selectedIds="${selectedIds}" treeId="treeFilter"/>

