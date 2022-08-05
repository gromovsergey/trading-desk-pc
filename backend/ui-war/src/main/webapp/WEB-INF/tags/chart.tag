<%@ tag description="Flash Chart" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core"      prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"       prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags"                       prefix="ui" %>
<%@ taglib uri="/ad/serverUI"                           prefix="ad" %>

<%@ attribute name="total"      required="true" type="java.lang.Integer" %>
<%@ attribute name="type"       required="true" %>
<%@ attribute name="id"         required="true" %>
<%@ attribute name="msgKey"     required="true" %>
<%@ attribute name="reportLink" required="false" %>
<%@ attribute name="textReportLink" required="false" %>
<%@ attribute name="selected" %><!-- Option values in format: selected="x,y1,y2" -->

<ui:section titleKey="chart">
    <c:choose>
        <c:when test="${pageScope.total > 0}">
            <script type="text/javascript">
            
                var myPlot;
            
                function getChart() {
                    <c:choose>
                        <c:when test="${pageScope.type == 'campaignChart'}">
                            var params = {campaignId:${pageScope.id}, x:$('#x-select').val(), y1:$('#y1-select').val(), y2:$('#y2-select').val()};
                        </c:when>
                        <c:otherwise>
                            var params = {ccgId:${pageScope.id}, x:$('#x-select').val(), y1:$('#y1-select').val(), y2:$('#y2-select').val()};
                        </c:otherwise>
                    </c:choose>
                    UI.Data.get('${pageScope.type}', params, applyChart, 'text');
                }
                
                function applyChart(data) {
                    var oData   = $.parseXML(data),
                        aLines      = [],
                        aTooltips   = [],
                        aCategory   = [],
                        aColors     = [],
                        aMin        = [],
                        aMax        = [0,0],
                        f_align     = function(min, max) {
                            if (max === min) min   = 0;
                            if (min === 0 && max === 0) return {max: 1, min: 0};
                            var dx  = max-min;
                            var p   = Math.floor(Math.log(dx)/Math.log(10));
                            dx      = Math.ceil(dx/Math.pow(10, p));
                            var add = Math.round(dx*20)*Math.pow(10, p-2);
                            return {max: max+add, min: (min-add < 0 ? 0 : min-add)};
                        };
                    
                    if ($('dataset:eq(0)', oData).find('set').length === 0 && $('dataset:eq(1)', oData).find('set').length === 0) {
                        $('#chart .content').html('<fmt:message key="${pageScope.msgKey}"/>');
                        return;
                    }
                    
                    for (var i=0; i<$('category', oData).length;i++) {
                        var d   = new Date();
                        d.setTime( $('category', oData).eq(i).attr('label') );
                        aCategory.push( d.toDateString() );
                    }
                    
                    for (i=0; i<$('dataset', oData).length;i++) {
                        var aTVals  = [],
                        aTText      = [],
                        jqDataset   = $('dataset', oData).eq(i);
                        aColors.push('#'+jqDataset.attr('color'));
                        
                        for (var j=0; j<$('set', jqDataset).length;j++) {
                            var mVal    = $('set', jqDataset).eq(j).attr('value'),
                                aText   = $('set', jqDataset).eq(j).attr('tooltext').split('{br}');
                                
                            if (aText.length === 3) {
                                aText[1]    = '<span style="color:#'+$('dataset', oData).eq(0).attr('color')+'">'+aText[1]+'</span>';
                                aText[2]    = '<span style="color:#'+$('dataset', oData).eq(1).attr('color')+'">'+aText[2]+'</span>';
                                aTText.push(aText.join('<br />'));
                            } else {
                                aTText.push($('set', jqDataset).eq(j).attr('tooltext').replace(/\{br\}/g, '<br />'));
                            }    

                            aTVals.push([aCategory[j], mVal]);
                            if (aMax[i] < mVal) {
                                aMax[i] = parseFloat(mVal);
                            }
                            if (aMin[i] === undefined) {
                                aMin[i] = parseFloat(mVal);
                            } else if (mVal < aMin[i]) {
                                aMin[i] = parseFloat(mVal);
                            }
                        }
                        aLines.push(aTVals);
                        aTooltips.push(aTText);
                    }
                    
                    
                    for (i=0; i<$('dataset', oData).length;i++) {
                        var res = f_align(aMin[i], aMax[i]);
                        aMax[i] = res.max;
                        aMin[i] = res.min;
                    }
                    
                    if (myPlot){
                        myPlot.destroy();
                    }
                    
                    $('#jqplot-div').width(getChartWidth());
                    
                    myPlot   = $.jqplot('jqplot-div', aLines, {
                        axes:{
                            xaxis:{
                                renderer:$.jqplot.DateAxisRenderer,
                                tickOptions:{
                                    formatString:'%d/%m/%y'
                                }
                            },
                            yaxis:{
                                label: $('dataset', oData).eq(0).attr('seriesName'),
                                labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
                                min: aMin[0],
                                max: aMax[0]
                            },
                            y2axis:{
                                label: $('dataset', oData).eq(1).attr('seriesName'),
                                labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
                                min: aMin[1],
                                max: aMax[1]
                            }
                        },
                        axesDefaults:{
                            useSeriesColor: true
                        }, 
                        highlighter: {
                            tooltipContentEditor: function (str, seriesIndex, pointIndex) {
                                return aTooltips[seriesIndex][pointIndex] != undefined ? aTooltips[seriesIndex][pointIndex] : '';
                            },
                            show: true,
                            sizeAdjust: 7.5,
                            tooltipLocation: 'n'
                        },
                        cursor: {
                            show:true,
                            zoom:true,
                            showTooltip:false
                        },
                        series:[
                            {
                                color: aColors[0]
                            },
                            {
                                color: aColors[1],
                                yaxis: 'y2axis'
                            }
                        ]
                    });
                }
                
                function setSelected(xVal, y1Val, y2Val) {
                    $('#x-select > option[value="' + (xVal || '30days') + '"]')
                        .add('#y1-select > option[value="' + (y1Val || 'imps') + '"]')
                        .add('#y2-select > option[value="' + (y2Val || 'clicks') + '"]')
                        .prop({selected : true});
                }
                
                function getChartWidth() {
                    var width = (window.innerWidth || document.documentElement.clientWidth) - 150;
                    return width>700 ? width : 700;
                }
            </script>
            <div style="padding-bottom:10px;">
                
                <link rel="stylesheet" type="text/css" href="/thirdparty/jqplot/1.0.8/jquery.jqplot.min.css" />
                <!--[if lt IE 9]><script type="text/javascript" src="/thirdparty/jqplot/1.0.8/excanvas.min.js"></script><![endif]-->
                <script type="text/javascript" src="/thirdparty/jqplot/1.0.8/jquery.jqplot.min.js"></script>
                <script type="text/javascript" src="/thirdparty/jqplot/1.0.8/plugins/jqplot.dateAxisRenderer.min.js"></script>
                <script type="text/javascript" src="/thirdparty/jqplot/1.0.8/plugins/jqplot.cursor.min.js"></script>
                <script type="text/javascript" src="/thirdparty/jqplot/1.0.8/plugins/jqplot.highlighter.min.js"></script>
                <script type="text/javascript" src="/thirdparty/jqplot/1.0.8/plugins/jqplot.canvasTextRenderer.min.js"></script>
                <script type="text/javascript" src="/thirdparty/jqplot/1.0.8/plugins/jqplot.canvasAxisLabelRenderer.min.js"></script>

                <div id="jqplot-div" style="margin:0 0 2em 20px; height:300px;"></div>

                <table id="chart-control-bar" style="width:100%">
                    <tr>
                        <td style="float:left;">
                            <label for="y1-select"><fmt:message key="chart.axis.leftY"/></label>
                            <select id="y1-select" name="y1Select" onchange="getChart();">
                                <option value="imps"><fmt:message key="chart.impressions.daily"/></option>
                                <option value="imps_rt"><fmt:message key="chart.impressions.runningTotal"/></option>
                                <option value="clicks"><fmt:message key="chart.clicks.daily"/></option>
                                <option value="clicks_rt"><fmt:message key="chart.clicks.runningTotal"/></option>
                                <option value="ctr"><fmt:message key="chart.CTR.daily"/></option>
                                <option value="ctr_rt"><fmt:message key="chart.CTR.runningTotal"/></option>
                                <option value="uniq"><fmt:message key="chart.uniqueUsers.daily"/></option>
                                <option value="uniq_rt"><fmt:message key="chart.uniqueUsers.runningTotal"/></option>
                                <c:if test="${ad:isInternal()}">
                                    <option value="total_cost"><fmt:message key="chart.total_cost.daily"/></option>
                                    <option value="total_cost_rt"><fmt:message key="chart.total_cost.runningTotal"/></option>
                                </c:if>
                            </select>
                        </td>

                        <td style="text-align:center;">
                            <label for="x-select"><fmt:message key="chart.axis.X"/></label>
                            <select id="x-select" name="xSelect" onchange="getChart();">
                                <option value="all" selected><fmt:message key="chart.date"/></option>
                                <option value="30days"><fmt:message key="chart.date.last30Days"/></option>
                            </select>
                        </td>

                        <td style="float:right">
                            <label for="y2-select"><fmt:message key="chart.axis.rightY"/></label>
                            <select id="y2-select" name="y2Select" onchange="getChart();">
                                <option value="imps"><fmt:message key="chart.impressions.daily"/></option>
                                <option value="imps_rt"><fmt:message key="chart.impressions.runningTotal"/></option>
                                <option value="clicks"><fmt:message key="chart.clicks.daily"/></option>
                                <option value="clicks_rt"><fmt:message key="chart.clicks.runningTotal"/></option>
                                <option value="ctr"><fmt:message key="chart.CTR.daily"/></option>
                                <option value="ctr_rt"><fmt:message key="chart.CTR.runningTotal"/></option>
                                <option value="uniq"><fmt:message key="chart.uniqueUsers.daily"/></option>
                                <option value="uniq_rt"><fmt:message key="chart.uniqueUsers.runningTotal"/></option>
                                <c:if test="${ad:isInternal()}">
                                    <option value="total_cost"><fmt:message key="chart.total_cost.daily"/></option>
                                    <option value="total_cost_rt"><fmt:message key="chart.total_cost.runningTotal"/></option>
                                </c:if>
                            </select>
                        </td>
                    </tr>
                </table>
            </div>
            <script type="text/javascript">
                $(function(){
                    <c:set var="selList" value="${fn:split(pageScope.selected, ',')}" />
                    setSelected('${selList[0]}', '${selList[1]}', '${selList[2]}');     // set axis subjects
                    getChart();     // render initial chart

                    $(document).ajaxStart(function() {
                        $('#chart-control-bar').find('select').attr({'disabled':'disabled'});
                    });

                    $(document).ajaxStop(function() {
                        $('#chart-control-bar').find('select').removeAttr('disabled');
                    });
                });
            </script>
            <c:set var="canRunAdvertiserReport" value="${ad:isPermitted('Report.run', 'advertiser')}"/>
            <c:set var="canRunTextAdvertisingReport" value="${ad:isPermitted('Report.run', 'textAdvertising')}"/>
            <c:if test="${(not empty pageScope.reportLink and canRunAdvertiserReport)
                           or (not empty pageScope.textReportLink and canRunTextAdvertisingReport)}">
                <fmt:message key="chart.message.seeAlso"/>
                <c:if test="${not empty pageScope.reportLink and canRunAdvertiserReport}">
                    <a href="${pageScope.reportLink}">
                        <fmt:message key="chart.displayAdvertisingReports"/>
                    </a>
                </c:if>
                <c:if test="${not empty pageScope.reportLink and not empty pageScope.textReportLink
                              and canRunAdvertiserReport and canRunTextAdvertisingReport}">
                    ,
                </c:if>
                <c:if test="${not empty pageScope.textReportLink and canRunTextAdvertisingReport}">
                    <a href="${pageScope.textReportLink}">
                        <fmt:message key="chart.textAdvertisingReports"/>
                    </a>
                </c:if>
            </c:if>
        </c:when>
        <c:otherwise>
            <fmt:message key="${pageScope.msgKey}"/>
        </c:otherwise>
    </c:choose>
</ui:section>
