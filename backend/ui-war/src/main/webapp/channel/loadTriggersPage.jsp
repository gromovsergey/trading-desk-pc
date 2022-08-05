<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<c:choose>
    <c:when test="${triggersFilter.triggerType == 'PAGE_KEYWORD'}">
        <c:set var="triggerColumnName" value="report.output.field.keyword"/>
        <c:set var="sectionId" value="reportDataPageKeywords"/>
    </c:when>
    <c:when test="${triggersFilter.triggerType == 'SEARCH_KEYWORD'}">
        <c:set var="triggerColumnName" value="report.output.field.keyword"/>
        <c:set var="sectionId" value="reportDataSearchKeywords"/>
    </c:when>
    <c:when test="${triggersFilter.triggerType == 'URL_KEYWORD'}">
        <c:set var="triggerColumnName" value="report.output.field.keyword"/>
        <c:set var="sectionId" value="reportDataUrlKeywords"/>
    </c:when>
    <c:otherwise>
        <c:set var="triggerColumnName" value="report.output.field.URL"/>
        <c:set var="sectionId" value="reportDataUrls"/>
    </c:otherwise>
</c:choose>

<div id="${sectionId}" class="logicalBlock ajax_loader_container">
    <c:choose>
    <c:when test="${not empty triggers}">
    <script type="text/javascript">
        $(function() {
            var repDiv      = $('#${sectionId}').siblings('.ajax_loader').hide().end(),
            form            = repDiv.closest('form'),
            qaStatus        = form.find('input[name="triggersFilter.qaStatus"]:checked').val();

            var reloadDiv = function(page) {
                form.find('input[name="' + qaStatus + '_page"]').val(page);
                form.find('input[name="triggersFilter.page"]').val(page);

                repDiv.siblings('.ajax_loader').show().end().ajaxPanel({
                    url : 'loadTriggersPage.action',
                    form : form
                }).replace();
            };

            $('.paginationButtons', repDiv).on('click', 'a.pagingButton', function(e){
                e.preventDefault();
                var page = $(this).data('page');
                reloadDiv(page);
            });

            $("#${sectionId}_table").ajaxTableSorter({
                sortList: [
                    {sortKey: 'original_trigger', defaultOrder: 'ASC'},
                    {sortKey: 'hits', defaultOrder: 'DESC'},
                    {sortKey: 'impressions', defaultOrder: 'DESC'},
                    {sortKey: 'clicks', defaultOrder: 'DESC'},
                    {sortKey: 'ctr', defaultOrder: 'DESC'}
                ],
                sortOrder: function(col) {
                    return col.sortKey === '${triggersFilter.sortKey}' ? '${triggersFilter.sortOrder}' : null;
                },
                sortCallback: function(sortKey, sortOrder) {
                    form.find('input[name="' + qaStatus + '_sortKey"]').val(sortKey);
                    form.find('input[name="triggersFilter.sortKey"]').val(sortKey);

                    form.find('input[name="' + qaStatus + '_sortOrder"]').val(sortOrder);
                    form.find('input[name="triggersFilter.sortOrder"]').val(sortOrder);

                    reloadDiv(1);
                }
            });
        });
    </script>
    <div class="b-triggercontainer">
        <c:choose>
            <c:when test="${triggersFilter.qaStatus == 'A'}">
                <table class="dataView" id="${sectionId}_table">
                    <thead>
                    <th><fmt:message key="${triggerColumnName}"/></th>
                    <th><fmt:message key="report.output.field.hits"/></th>
                    <th><fmt:message key="report.output.field.impressions"/></th>
                    <th><fmt:message key="report.output.field.clicks"/></th>
                    <th><fmt:message key="report.output.field.CTR"/></th>
                    </thead>
                    <tbody>
                    <c:forEach items="${triggers}" var="row">
                        <tr>
                            <td>
                                <c:out value="${row.originalTrigger}"/>
                            </td>
                            <td class="number">
                                <fmt:formatNumber value="${row.hits}" groupingUsed="true"/>
                            </td>
                            <td class="number">
                                <fmt:formatNumber value="${row.impressions}" groupingUsed="true"/>
                            </td>
                            <td class="number">
                                <fmt:formatNumber value="${row.clicks}" groupingUsed="true"/>
                            </td>
                            <td class="number">
                                <fmt:formatNumber value="${row.ctr}" maxFractionDigits="2"/>%
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <table class="dataView" id="${sectionId}_table">
                    <thead>
                    <th><fmt:message key="${triggerColumnName}"/></th>
                    </thead>
                    <tbody>
                    <c:forEach items="${triggers}" var="row">
                        <tr>
                            <td>
                                <c:out value="${row.originalTrigger}"/>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
        <ui:pages pageSize="${triggersFilter.pageSize}"
                  total="${triggersTotal}" selectedNumber="${triggersFilter.page}"
                  visiblePagesCount="10" handler="goToPage" displayHeader="true" />
    </div>
        </c:when>
        <c:otherwise>
            <div class="wrapper">
                <fmt:message key="nothing.found.to.display"/>
            </div>
            <script type="text/javascript">
                $(function() {
                    $('#${sectionId}').siblings('.ajax_loader').hide();
                });
            </script>
        </c:otherwise>
        </c:choose>
</div>