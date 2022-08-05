<%@ tag description="UI Tab" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<%@attribute name="id" required="true" rtexprvalue="true" type="java.lang.String"%>
<%@attribute name="data" required="true" rtexprvalue="true" type="com.foros.reporting.serializer.SimpleReportData" %>
<%@attribute name="sort" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@attribute name="sortUrl" required="false" rtexprvalue="true" type="java.lang.String"%>
<%@attribute name="sortProperty" required="false" rtexprvalue="true" type="java.lang.String"%>
<%@attribute name="sortId" required="false" rtexprvalue="true" type="java.lang.String"%>
<%@attribute name="noDataMessageKey" required="false" rtexprvalue="true" type="java.lang.String"%>

<c:set var="meta" value="${pageScope.data.metaData}"/>
<c:set var="columns" value="${meta.columns}"/>
<c:set var="sortColumns" value="${meta.sortColumns}"/>
<c:if test="${empty pageScope.sortProperty}">
    <c:set var="sortProperty" value="sortColumn"/>
</c:if>
<c:if test="${empty pageScope.sortId}">
    <c:set var="sortId" value="${pageScope.id}"/>
</c:if>

<div id="${pageScope.id}" class="logicalBlock">
    <c:set var="sortColumnName" value="${sortProperty}.column"/>
    <c:set var="sortOrderName" value="${sortProperty}.order"/>

    <c:choose>
        <c:when test="${not empty pageScope.data and not empty pageScope.data.rows}">
            <c:if test="${pageScope.sort}">
                <script type="text/javascript">
                    $(function() {
                        var repTable = $('#${pageScope.id}_table');
                        var repDiv = $('#${sortId}');
                        var sortUrl = '${pageScope.sortUrl}';
        
                        var sortColumnInput = getHidden(repDiv, '${sortColumnName}');
                        var sortOrderInput = getHidden(repDiv, '${sortOrderName}');

                        sortColumnInput.val('${sortColumns[0].column.nameKey}');
                        sortOrderInput.val('${sortColumns[0].order}');

                        repTable.ajaxTableSorter({
                            sortList: [
                                <ad:commaWriter var="headers" varStatus="headerStatus" items="${pageScope.data.headers}">
                                    <c:set var="col" value="${columns[headerStatus.index]}"/>
                                    {sortKey: '${col.nameKey}', defaultOrder: '${col.defaultOrder}'}
                                </ad:commaWriter>
                            ],
                            sortOrder: function(col) {
                                return col.sortKey === sortColumnInput.val() ? sortOrderInput.val() : null;
                            },
                            sortCallback: function(sortKey, sortOrder) {
                                sortColumnInput.val(sortKey);
                                sortOrderInput.val(sortOrder);
                                
                                repDiv.ajaxPanel({
                                    url : sortUrl,
                                    form : repDiv.closest('form')
                                }).replace();
                            }
                        });

                        function getHidden(repDiv, name) {
                            var input = $('input[name="' + name + '"]', repDiv);
                            if (input.length == 0) {
                                input = $('<input type="hidden"/>')
                                        .attr("name", name)
                                        .appendTo(repDiv);
                            }
                            return input;
                        }
                    });
                </script>
            </c:if>
            <table id="${pageScope.id}_table" class="dataView">
                <thead>
                    <c:forEach items="${pageScope.data.headers}" var="header">
                        <th>${pageScope.header.html}</th>
                    </c:forEach>
                </thead>
                <tbody>
                    <c:forEach items="${pageScope.data.rows}" var="row">
                        <tr
                            ><c:forEach items="${row.values}" var="cell" varStatus="cellStatus"
                                ><td class="${cell.cssClasses}">${cell.html}</td
                            ></c:forEach
                        ></tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div class="wrapper">
                <fmt:message key="${pageScope.noDataMessageKey != null? pageScope.noDataMessageKey: 'report.no.data.available'}"/>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<div class="fixing"></div>
