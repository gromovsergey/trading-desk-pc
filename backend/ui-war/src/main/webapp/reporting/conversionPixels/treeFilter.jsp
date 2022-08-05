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
    
    function getLevelsTreeFilter() {
        return ['conversionAdvertiserIds', 'conversionIds'];
    } 
     
    function getSendingDataTreeFilter() {
      return {
          entityFilterMessageKey: "report.conversionPixels.select.Conversion"
      }; 
    } 
    
    function showTreeFilter() {
        var isAdvertiser = ${isAdvertiser};
        if (isAdvertiser) {
            getOptionsTreeFilter(${accountId}, 'conversions', 'treeFilter', true);
        } else {
            getOptionsTreeFilter(${accountId}, 'advertisers', 'treeFilter', true);
        }
		$('#treeFilter').show();
    }
</script>

<ui:treeFilter selectedIds="${selectedIds}" treeId="treeFilter"/>

