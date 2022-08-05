<%@ tag language="java" body-content="empty" description="Renders behavioral parameters edit section" %>

<%@ tag import="com.foros.model.channel.trigger.TriggerType" %>
<%@ tag import="com.foros.model.channel.BehavioralParametersUnits" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<%@ attribute name="bpPropertyName" %>
<%@ attribute name="bpTitleKey" %>
<%@ attribute name="bpTipKey" %>
<%@ attribute name="bpErrorsPath" %>
<%@ attribute name="bpTypes" %>

<c:set var="URL_TRIGGER_TYPE" value="<%=TriggerType.URL.getLetter()%>"/>
<c:set var="SEARCH_KW_TRIGGER_TYPE" value="<%=TriggerType.SEARCH_KEYWORD.getLetter()%>"/>
<c:set var="PAGE_KW_TRIGGER_TYPE" value="<%=TriggerType.PAGE_KEYWORD.getLetter()%>"/>
<c:set var="URL_KW_TRIGGER_TYPE" value="<%=TriggerType.URL_KEYWORD.getLetter()%>"/>

<c:if test="${empty bpPropertyName}">
    <c:set var="bpPropertyName" value="behavioralParameters"/>
</c:if>

<c:if test="${empty bpTitleKey}">
    <c:set var="bpTitleKey" value="channel.params"/>
</c:if>

<c:if test="${empty bpTipKey}">
    <c:set var="bpTipKey" value="channel.params.tooltip"/>
</c:if>

<c:if test="${empty bpErrorsPath}">
    <c:set var="bpErrorsPath" value="${bpPropertyName}"/>
</c:if>

<c:if test="${empty bpTypes}">
    <c:set var="bpTypes" value="${PAGE_KW_TRIGGER_TYPE},${SEARCH_KW_TRIGGER_TYPE},${URL_TRIGGER_TYPE},${URL_KW_TRIGGER_TYPE}"/>
</c:if>

<c:set var="TIMEUNIT_MINUTES_LABEL"><fmt:message key="channel.params.select.minute"/></c:set>
<c:set var="TIMEUNIT_MINUTES_MULTIPLIER" value="<%=BehavioralParametersUnits.MINUTES.getMultiplier()%>"/>
<c:set var="TIMEUNIT_MINUTES_MAXVALUE" value="<%=BehavioralParametersUnits.MINUTES.getMaxValue()%>"/>
<c:set var="TIMEUNIT_HOURS_LABEL"><fmt:message key="channel.params.select.hour"/></c:set>
<c:set var="TIMEUNIT_HOURS_MULTIPLIER" value="<%=BehavioralParametersUnits.HOURS.getMultiplier()%>"/>
<c:set var="TIMEUNIT_HOURS_MAXVALUE" value="<%=BehavioralParametersUnits.HOURS.getMaxValue()%>"/>
<c:set var="TIMEUNIT_DAYS_LABEL"><fmt:message key="channel.params.select.day"/></c:set>
<c:set var="TIMEUNIT_DAYS_MULTIPLIER" value="<%=BehavioralParametersUnits.DAYS.getMultiplier()%>"/>
<c:set var="TIMEUNIT_DAYS_MAXVALUE" value="<%=BehavioralParametersUnits.DAYS.getMaxValue()%>"/>

<script type="text/javascript">

function initTriggers(type, enabled){
    if(enabled){
        $('#bpEnabled\\[' + type + '\\]').prop({checked : enabled})
            .change();
    }
}

$(document).ready(function(){
    var paramsRows = $('.bpRow');
    
    paramsRows.each(function(){
        new ParamsSet($(this));
    });
});
    
/////////////   ParamsSet   //////////////////////////////////
    
    function ParamsSet(jRow){
        this._view = new ParamsSet.View(
            this,
            $('.unitFrom', jRow),
            $('.unitTo', jRow),
            $('.triggerType', jRow),
            $('.units', jRow),
            $('.timing', jRow),
            $('.minimumVisits', jRow), 
            $(':hidden', jRow),  
            $('[name$=\\.timeFrom]', jRow),  
            $('[name$=\\.timeTo]', jRow),  
            $('[name$=\\.minimumVisits]', jRow)  
        );
        var viewData = this._view.getViewData();
        this._model = new ParamsSet.Model(this, viewData.valFrom, viewData.valTo, viewData.trigType);
        
        jRow.data({paramsSet : this});
        this._update();
    }
    ParamsSet.TRG_URLS = 'U';
    ParamsSet.TRG_SRCH_KWRDS = 'S';
    ParamsSet.TRG_PAGE_KWRDS = 'P';
    ParamsSet.TRG_URL_KWRDS = 'R';
    ParamsSet.MIN_MULTIPLIER = ${TIMEUNIT_MINUTES_MULTIPLIER};
    ParamsSet.HR_MULTIPLIER = ${TIMEUNIT_HOURS_MULTIPLIER};
    ParamsSet.DAY_MULTIPLIER = ${TIMEUNIT_DAYS_MULTIPLIER};
    
    ParamsSet.getMultiplier = function(valFrom, valTo){
        if (!+valFrom && !+valTo) return ParamsSet.MIN_MULTIPLIER;
        
        var multiplier = ParamsSet.DAY_MULTIPLIER;
        if (valFrom % multiplier || valTo % multiplier) {
            multiplier = ParamsSet.HR_MULTIPLIER;
            if (valFrom % multiplier || valTo % multiplier) {
                multiplier = ParamsSet.MIN_MULTIPLIER;
            }
        }
        return multiplier;
    };
    
    ParamsSet.prototype = {
        setRange : function(valFrom, valTo){
            this._model.setRange(valFrom, valTo);
            this._view.update(this._model.getModelData());
        }, 
        setTriggerType : function(val){
            this._model.setTrigType(val);
            this._view.update(this._model.getModelData());
        },
        setMinVisits : function(val){
            this._model.setMinVisits(val);
            this._view.update(this._model.getModelData());
        },
        setUnits : function(val, valFrom, valTo) {
            this._model.setUnits(val, valFrom, valTo);
            this._view.update(this._model.getModelData());
        },
        _update : function(){
            var viewData = this._view.getViewData();
            this._model.update(viewData);
            this._view.update(this._model.getModelData());
        }
    };
    
    
    
    
/////////////   ParamsSet.View   //////////////////////////////////
    
    ParamsSet.View = function(controller, jComboFrom, jComboTo, jTriggerType, jUnits, jTiming, jMinVisits, 
                jHiddens, jTimeFromHid, jTimeToHid, jMinVisHid){
        this._controller = controller;
        
        this._jComboFrom = jComboFrom;
        this._jComboTo = jComboTo;
        this._jTriggerType = jTriggerType;
        this._jUnits = jUnits;
        this._jTiming = jTiming;
        this._jMinVisits = jMinVisits;
        this._jHiddens = jHiddens;
        this._jTimeFromHid = jTimeFromHid;
        this._jTimeToHid = jTimeToHid;
        this._jMinVisHid = jMinVisHid;

        for (var i=1; i < arguments.length; i++) {
            if (arguments[i].length === 0) {
                throw('Argument number ' + (i + 1) + ' of function "ParamsSet.View" is not valid');
                return;
            }
        }
        
        this._lastData = {};
        this._setInitVals();
        this._bindHandlers();
        this._setRowDisabled();
    };

    ParamsSet.View._TEXT_NOW = '<fmt:message key="form.now"/>';
    
    ParamsSet.View.prototype = {
        _setInitVals : function(){
            this._jComboFrom[0][0] = new Option('', +this._jTimeFromHid.val(), true, true);
            this._jComboTo[0][0] = new Option('', +this._jTimeToHid.val(), true, true);
            this._jMinVisits.val(this._jMinVisHid.val());
            
            var multiplier = ParamsSet.getMultiplier(+this._jComboFrom.val(), +this._jComboTo.val());
            this._jUnits.val(multiplier);
        },
        _bindHandlers : function(){
            var view = this;
            var controller = this._controller;
            this._jComboFrom
                .add(this._jComboTo)
                    .change(function(){
                        view._onChngRange();
                    });
            this._jUnits.change(function(){
                var viewData = view.getViewData();
                controller.setUnits(this.value, viewData.valFrom, viewData.valTo);
            });
            this._jTriggerType.change(function(){
                view._setRowDisabled();
                controller.setTriggerType(this.value);
            });
            this._jMinVisits.change(function(){
                controller.setMinVisits(this.value);
            });
        },
        _setRowDisabled : function(){
            var toDisable = !$(this._jTriggerType).prop('checked');
            this._jComboFrom
                .add(this._jComboTo)
                .add(this._jUnits)
                .add(this._jMinVisits)
                .add(this._jHiddens)
                    .prop({disabled : toDisable});
        },
        getViewData : function(){
            return {
                trigType : this._jTriggerType.val(),
                units : this._jUnits.val(),
                minVisits : this._jMinVisits.val(),
                valFrom : this._jComboFrom.val(),
                valTo : this._jComboTo.val()
            }
        },
        update : function(modelData){
            this._jTriggerType.val(modelData.trigType);
            this._jUnits.val(modelData.units);
            this._fillCombo('_jComboFrom', modelData.listFrom, this._lastData.listFrom, modelData.valFrom);
            this._fillCombo('_jComboTo', modelData.listTo, this._lastData.listTo, modelData.valTo);
            this._updMinVis(modelData.minVisits, modelData.valFrom, modelData.valTo);
            this._updTiming(modelData.valFrom, modelData.valTo);
            this._lastData = modelData;
            this._jTimeFromHid.val(this._jComboFrom.val());
            this._jTimeToHid.val(this._jComboTo.val());
            this._jMinVisHid.val(this._jMinVisits.val() || 1);
        }, 
        _onChngRange : function(){
            this._controller.setRange(+this._jComboFrom.val(), +this._jComboTo.val());
        },
        _updTiming : function(valFrom, valTo){
            this._jTiming[(!+valTo) ? 'hide' : 'show']();
        },
        _updMinVis : function(minVis, valFrom, valTo){
            if(!this._jTriggerType.prop('checked') || (!valFrom && !valTo) || (valFrom == 0 && valTo == 0)){
                this._jMinVisits.prop({disabled : true});
                this._jMinVisits.val(1);
            }else{
                this._jMinVisits.val(minVis);
                this._jMinVisits.prop({disabled : false});
            }
        },
        _fillCombo : function(comboVarName, list, lastList, valToSelect){
            if (this._isSameList(list, lastList)) return false;
            
            var jCombo = this[comboVarName];
            var emptyCombo = UI.Util.cleanSelect(jCombo, true);
            this[comboVarName] = emptyCombo; 
            var combo = emptyCombo[0];
            
            $.each(list, function(i, el){
                combo[combo.length] = new Option(el.num || ParamsSet.View._TEXT_NOW, el.val, false, el.val == valToSelect);
            });
        }, 
        _isSameList : function(list1, list2){
            if (!list1 || !list2) return false;
            if(list1.length == list2.length && list1[list1.length - 1].val == list2[list2.length - 1].val){
                return true;
            }
            return false;
        }
    };


/////////////   ParamsSet.Model   //////////////////////////////////

    ParamsSet.Model = function(controller, valFrom, valTo, trigType){
        this._valFrom; // Number
        this._valTo; // Number
        this._units; // Number
        this._minVisits; // Number
        this._listAllVals; //Array
        this._listFrom; //Array
        this._listTo; //Array
        this._trigType; // String
        
        this.update({
            valFrom : valFrom, 
            valTo : valTo, 
            trigType : trigType 
        });
    };
    ParamsSet.Model._fillList = function(multiplier, len){
        for(var i=0, list=[]; i<len; i++){
            list.push({num:i, val:i * multiplier});
        }
        return list;
    };
    ParamsSet.Model.LIST_MINUTES = ParamsSet.Model._fillList(ParamsSet.MIN_MULTIPLIER, 60);
    ParamsSet.Model.LIST_HOURS = ParamsSet.Model._fillList(ParamsSet.HR_MULTIPLIER, 24);
    ParamsSet.Model.LIST_DAYS = ParamsSet.Model._fillList(ParamsSet.DAY_MULTIPLIER, 181);
    
    ParamsSet.Model.prototype = {
        getModelData : function(){
            return {
                trigType : this._trigType, 
                units : this._units, 
                minVisits : this._minVisits, 
                valFrom : this._valFrom, 
                valTo : this._valTo, 
                listFrom : this._listFrom, 
                listTo : this._listTo 
            }
        },
        setTrigType : function(val){
            this.update({trigType : val});
        },
        setUnits : function(val, valFrom, valTo){
            this.update({units : val, valFrom : valFrom, valTo : valTo});
        },
        setRange : function(valFrom, valTo){
            this.update({valFrom : valFrom, valTo : valTo});
        },
        setMinVisits : function(val){
            this.update({minVisits : val});
        },
        update : function(params){
            params = $.extend({
                valFrom : this._valFrom, 
                valTo : this._valTo, 
                units : 0, 
                minVisits : this._minVisits, 
                trigType : this._trigType 
            }, params);
            
            this._trigType = params.trigType;
            this._units = params.units || ParamsSet.getMultiplier(params.valFrom, params.valTo);
            this._listAllVals = this._getListAllVals(this._units);
            this._listFrom = this._getListFrom(this._listAllVals);
            this._valFrom = this._getVal(params.valFrom, this._listFrom, this._units);
            this._listTo = this._getListTo(this._listAllVals, this._valFrom, true);
            this._valTo = this._getVal(params.valTo, this._listTo, this._units);
            this._minVisits = this._getMinVisits(params.minVisits, this._valFrom, this._valTo);
        },
        _getMinVisits : function(val, valFrom, valTo){
            return ((!valFrom && !valTo) || !val) ? 1 : val;
        },
        _getVal : function(val, availVals, units){
            var toReset = true;
            if (val > 0) {
                val = (val / ParamsSet.getMultiplier(val, 0)) * units;
            }

            $.each(availVals, function(i, el){
                if(el.val == val){
                    toReset = false;
                    return false;
                }
            });
            return toReset ? availVals[0].val : val;
        },
        _getListFrom : function(allAvailVals){
            return allAvailVals.slice(0, -1);
        },
        _getListTo : function(allAvailVals, valFrom, fromZero){
            if(!fromZero || valFrom > 0){
                var minIdx = 0;
                $.each(allAvailVals, function(i){
                    if(this.val == valFrom){
                        minIdx = i;
                        return false;
                    }
                });
                return allAvailVals.slice(minIdx + 1);
            }else{
                return allAvailVals;
            }
        },
        _getListAllVals : function(unitsVal){
            if(unitsVal == ParamsSet.MIN_MULTIPLIER){
                return ParamsSet.Model.LIST_MINUTES;
            }else if(unitsVal == ParamsSet.HR_MULTIPLIER){
                return ParamsSet.Model.LIST_HOURS;
            }else{
                return ParamsSet.Model.LIST_DAYS;
            }
        }
    }

</script>


<c:forTokens var="bpType" items="${bpTypes}" delims=",">
    <div id="bpRow[${bpType}]" class="bpRow">

        <s:set var="currentParamPath" value="#attr.bpPropertyName + '(' + quote(#attr.bpType) + ')'"/>
        <c:set var="bpLabelText"><fmt:message key="channel.params.matchingCriteria"/></c:set>
        <c:choose>
            <c:when test="${bpType==URL_TRIGGER_TYPE}">
                <c:set var="sectionTitle"><fmt:message key="channel.params.urls"/></c:set>
                <c:set var="part1Text"><fmt:message key="channel.params.part1.url"/></c:set>
                <c:set var="part2Text"><fmt:message key="channel.params.part2.url"/></c:set>
                <c:set var="positiveText"><fmt:message key="channel.urls.positive"/></c:set>
                <c:set var="negativeText"><fmt:message key="channel.urls.negative"/></c:set>
                <c:set var="negativeTipKey"><fmt:message key="channel.urls.negativeTooltip"/></c:set>
                <s:set var="bpEnabled" value="not behavioralParameters.{^ #this.urlTriggerType }.isEmpty"/>
            </c:when>
            <c:when test="${bpType==SEARCH_KW_TRIGGER_TYPE}">
                <c:set var="sectionTitle"><fmt:message key="channel.params.searchKeywords"/></c:set>
                <c:set var="part1Text"><fmt:message key="channel.params.part1.search"/></c:set>
                <c:set var="part2Text"><fmt:message key="channel.params.part2.search"/></c:set>
                <c:set var="positiveText"><fmt:message key="channel.keywords.positive"/></c:set>
                <c:set var="negativeText"><fmt:message key="channel.keywords.negative"/></c:set>
                <c:set var="negativeTipKey"><fmt:message key="channel.searchKeywords.negativeTooltip"/></c:set>
                <s:set var="bpEnabled" value="not behavioralParameters.{^ #this.searchTriggerType }.isEmpty"/>
            </c:when>
            <c:when test="${bpType==PAGE_KW_TRIGGER_TYPE}">
                <c:set var="sectionTitle"><fmt:message key="channel.params.pageKeywords"/></c:set>
                <c:set var="part1Text"><fmt:message key="channel.params.part1.keyword"/></c:set>
                <c:set var="part2Text"><fmt:message key="channel.params.part2.keyword"/></c:set>
                <c:set var="positiveText"><fmt:message key="channel.keywords.positive"/></c:set>
                <c:set var="negativeText"><fmt:message key="channel.keywords.negative"/></c:set>
                <c:set var="negativeTipKey"><fmt:message key="channel.pageKeywords.negativeTooltip"/></c:set>
                <s:set var="bpEnabled" value="not behavioralParameters.{^ #this.pageTriggerType }.isEmpty"/>
            </c:when>
            <c:when test="${bpType==URL_KW_TRIGGER_TYPE}">
                <c:set var="sectionTitle"><fmt:message key="channel.params.urlKeywords"/></c:set>
                <c:set var="part1Text"><fmt:message key="channel.params.part1.keyword"/></c:set>
                <c:set var="part2Text"><fmt:message key="channel.params.part2.keyword"/></c:set>
                <c:set var="positiveText"><fmt:message key="channel.keywords.positive"/></c:set>
                <c:set var="negativeText"><fmt:message key="channel.keywords.negative"/></c:set>
                <c:set var="negativeTipKey"><fmt:message key="channel.urlKeywords.negativeTooltip"/></c:set>
                <s:set var="bpEnabled" value="not behavioralParameters.{^ #this.urlKeywordTriggerType }.isEmpty"/>
            </c:when>
            <c:otherwise>
            </c:otherwise>
        </c:choose>
        <script type="text/javascript">
            $(function() {
                initTriggers("${bpType}", ${bpEnabled});
            });
        </script>

        <s:hidden name="%{#currentParamPath}.triggerType" value="%{#attr.bpType}"/>
        <s:hidden name="%{#currentParamPath}.version"/>
        <s:hidden name="%{#currentParamPath}.timeFrom"/>
        <s:hidden name="%{#currentParamPath}.timeTo"/>
        <s:hidden name="%{#currentParamPath}.weight" value="1"/>
        <s:hidden name="%{#currentParamPath}.minimumVisits"/>

        <ui:section title="${sectionTitle}" titleInputId="bpEnabled[${bpType}]" titleInputValue="${bpType}" errors="behavioralParameters">
            <c:choose>
                <c:when test="${bpType==URL_TRIGGER_TYPE}">
                    <s:set var="triggersProperty" value="'urls'"/>
                </c:when>
                <c:when test="${bpType==SEARCH_KW_TRIGGER_TYPE}">
                    <s:set var="triggersProperty" value="'searchKeywords'"/>
                </c:when>
                <c:when test="${bpType==PAGE_KW_TRIGGER_TYPE}">
                    <s:set var="triggersProperty" value="'pageKeywords'"/>
                </c:when>
                <c:when test="${bpType==URL_KW_TRIGGER_TYPE}">
                    <s:set var="triggersProperty" value="'urlKeywords'"/>
                </c:when>
            </c:choose>

            <s:fielderror><s:param value="%{#triggersProperty}"/></s:fielderror>

            <div class="wrapper">
                <table class="grouping fieldsets">
                    <tr>
                        <td class="singleFieldset">
                            <ui:header styleClass="level2 withTip"><h4>${positiveText}</h4></ui:header>
                            <s:fielderror><s:param value="%{#triggersProperty + '.positive'}"/></s:fielderror>
                            <s:textarea name="%{#triggersProperty}" id="%{#triggersProperty}" wrap="off" cssClass="middleLengthText1"/>
                        </td>
                        <td class="singleFieldset">
                            <ui:header styleClass="level2 withTip">
                                <h4>${negativeText}</h4>
                                <ui:hint>${negativeTipKey}</ui:hint>
                            </ui:header>
                            <s:fielderror><s:param value="%{#triggersProperty + '.negative'}"/></s:fielderror>
                            <s:textarea name="%{#triggersProperty}Negative" id="%{#triggersProperty}Negative" wrap="off" cssClass="middleLengthText1"/>
                        </td>
                    </tr>
                </table>

                <h3></h3>

                <table>
                    <tr>
                        <td>
                            <label class="withInput"><c:out value="${bpLabelText}"/>:</label>
                        </td>
                        <td class="field">
                            <table class="fieldAndAccessories">
                                <tr>
                                    <td class="withField">
                                        <s:textfield maxlength="2" cssClass="minimumVisits smallLengthNumber" id="bpMinimumVisits[%{#attr.bpType}]"/>
                                    </td>
                                    <td class="withField">
                                        <ui:text text="${part1Text}"/>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td class="field">
                            <table class="fieldAndAccessories">
                                <tr>
                                    <td class="withField">
                                        <select id="bpTimeFrom[${bpType}]" class="unitFrom smallLengthText"></select>
                                    </td>
                                    <td class="withField">
                                        <ui:text textKey="channel.params.and"/>
                                    </td>
                                    <td class="withField">
                                        <select id="bpTimeTo[${bpType}]" class="unitTo smallLengthText"></select>
                                    </td>
                                    <td class="withField timing">
                                        <table class="fieldAndAccessories" id="bpTimeUnitTable[${bpType}]">
                                            <tr>
                                                <td class="withField">
                                                    <s:select cssClass="units smallLengthText1"
                                                              list="#{'60':getText('channel.params.select.minute'),'3600':getText('channel.params.select.hour'),'86400':getText('channel.params.select.day')}">
                                                    </s:select>
                                                </td>
                                                <td class="withField">
                                                    <ui:text textKey="channel.params.ago"/>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                    <td class="withField">
                                        <ui:text text="${part2Text}"/>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td>
                            <ui:hint><fmt:message key="channel.params.matchingCriteria.tooltip"/></ui:hint>
                        </td>
                        <td class="withError">
                            <c:set var="fldErrs" value="${currentParamPath},${currentParamPath}.minimumVisits,${currentParamPath}.timeFrom,${currentParamPath}.timeTo" />
                            <c:set var="errList" value="${fn:split(pageScope.fldErrs, ', ')}" />

                            <s:fielderror>
                                <c:forEach items="${errList}" var="err">
                                    <s:param value="%{#attr.err.trim()}"/>
                                </c:forEach>
                            </s:fielderror>
                        </td>
                    </tr>
                </table>

            </div>
        </ui:section>
    </div>
</c:forTokens>
