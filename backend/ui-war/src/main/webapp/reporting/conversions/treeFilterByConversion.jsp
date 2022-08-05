<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">

    $().ready(function() {
        showTreeFilterByConversion();
    });

     function showRequiredAlertTreeFilterByConversion() {
        alert('${ad:formatMessage("errors.advertiserReport.filters.required")}');
    }
    
    function getLevelsTreeFilterByConversion() {
        return ['conversionAdvertiserIds', 'conversionIds'];
    } 
    
    
    function getSendingDataTreeFilterByConversion() {
        return {
            entityFilterMessageKey : "report.conversionPixels.select.byConversion"
        }; 
      } 
     
    function showTreeFilterByConversion() {
        var isAdvertiser = ${isAdvertiser};
        if (isAdvertiser) {
            getOptionsTreeFilterByConversion(${accountId}, 'conversions', 'treeFilterByConversion', true);
        } else {
            getOptionsTreeFilterByConversion(${accountId}, 'advertisersByConversions', 'treeFilterByConversion', true);
        }
        $('#treeFilterByConversion').show();
    }

</script>

<ui:treeFilter selectedIds="${byConversionSelectedIds}" treeId="treeFilterByConversion"/>

