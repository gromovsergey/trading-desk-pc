<%@ page import="com.foros.model.channel.BehavioralParametersUnits" %>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:set var="TIMEUNIT_MINUTES_MULTIPLIER" value="<%=BehavioralParametersUnits.MINUTES.getMultiplier()%>"/>
<c:set var="TIMEUNIT_HOURS_MULTIPLIER" value="<%=BehavioralParametersUnits.HOURS.getMultiplier()%>"/>
<c:set var="TIMEUNIT_DAYS_MULTIPLIER" value="<%=BehavioralParametersUnits.DAYS.getMultiplier()%>"/>

<script type="text/javascript">
    $().ready(function() {
        
        var paramsRows = $('tr[id^=paramsRow]:not([id=paramsRow\\?])');
        
        paramsRows.each(function(){
            new ParamsSet($(this));
        });

        UI.Util.Table.initCnt('paramsTable');

        $('.delRow').click(function(){
            var jMainTable = $('#paramsTable');

            UI.Util.Table.deleteRowByButtonInIt(this, jMainTable[0]);
            var jMainTableRootRows = $('tbody:eq(0) > tr', jMainTable);
            
            if(jMainTableRootRows.length == 1){
                $('#infoAddParameter').show();
            }
            return false;
        });
        
        $('#addRowBtn').click(function(){
            var newParamsSet = new ParamsSet(UI.Util.Table.addRow('paramsTable'));
            
            newParamsSet.setRange(0, 0);
            $('#infoAddParameter').hide();
            return false;
        });

        $('#behaviouralParamForm').submit(function(){
            $('input.minimumVisits').prop({disabled : false});
            return true;
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
            $('.minimumVisits', jRow)
        );
        var viewData = this._view.getViewData();
        this._model = new ParamsSet.Model(this, viewData.valFrom, viewData.valTo, viewData.trigType);
        
        jRow.data({paramsSet : this});
        this._update();
    }

    ParamsSet.TRG_URLS = 'U';
    ParamsSet.TRG_SRCH_KWRDS = 'S';
    ParamsSet.TRG_PAGE_KWRDS = 'P';
    
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
        }
    };


/////////////   ParamsSet.View   //////////////////////////////////
    
    ParamsSet.View = function(controller, jComboFrom, jComboTo, jTriggerType, jUnits, jTiming, jMinVisits){
        this._controller = controller;
        
        this._jComboFrom = jComboFrom;
        this._jComboTo = jComboTo;
        this._jTriggerType = jTriggerType;
        this._jUnits = jUnits;
        this._jTiming = jTiming;
        this._jMinVisits = jMinVisits;
        
        for (var i=1; i < arguments.length; i++) {
            if (arguments[i].length === 0) {
                throw('Argument number ' + (i + 1) + ' of function "ParamsSet.View" is not valid');
            }
        }
        
        this._lastData = {};
        this._bindHandlers();
    };

    ParamsSet.View._TEXT_NOW = '<fmt:message key="form.now"/>';
    
    ParamsSet.View.prototype = {
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
                controller.setTriggerType(this.value);
            });
            this._jMinVisits.change(function(){
                controller.setMinVisits(this.value);
            });
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
        }, 
        _onChngRange : function(){
            this._controller.setRange(+this._jComboFrom.val(), +this._jComboTo.val());
        },
        _updTiming : function(valFrom, valTo){
            this._jTiming[(!valTo) ? 'hide' : 'show']();
        },
        _updMinVis : function(minVis, valFrom, valTo){
            if(!valFrom && !valTo){
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
    }
    ParamsSet.Model._fillList = function(multiplier, len){
        for(var i=0, list=[]; i<len; i++){
            list.push({num:i, val:i * multiplier});
        }
        return list;
    }
    ParamsSet.Model.MIN_MULTIPLIER = ${TIMEUNIT_MINUTES_MULTIPLIER};
    ParamsSet.Model.HR_MULTIPLIER = ${TIMEUNIT_HOURS_MULTIPLIER};
    ParamsSet.Model.DAY_MULTIPLIER = ${TIMEUNIT_DAYS_MULTIPLIER};
    ParamsSet.Model.LIST_MINUTES = ParamsSet.Model._fillList(ParamsSet.Model.MIN_MULTIPLIER, 60);
    ParamsSet.Model.LIST_HOURS = ParamsSet.Model._fillList(ParamsSet.Model.HR_MULTIPLIER, 24);
    ParamsSet.Model.LIST_DAYS = ParamsSet.Model._fillList(ParamsSet.Model.DAY_MULTIPLIER, 181);
    
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
            this._units = params.units || this._getUnits(params.valFrom, params.valTo);
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
                val = (val / this._getUnits(val, 0)) * units;
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
            if(unitsVal == ParamsSet.Model.MIN_MULTIPLIER){
                return ParamsSet.Model.LIST_MINUTES;
            }else if(unitsVal == ParamsSet.Model.HR_MULTIPLIER){
                return ParamsSet.Model.LIST_HOURS;
            }else{
                return ParamsSet.Model.LIST_DAYS;
            }
        },
        _getUnits : function(valFrom, valTo){
            if (!valFrom && !valTo) return ParamsSet.Model.MIN_MULTIPLIER;
            
            var multiplier = ParamsSet.Model.DAY_MULTIPLIER;
            if (valFrom % multiplier || valTo % multiplier) {
                multiplier = ParamsSet.Model.HR_MULTIPLIER;
                if (valFrom % multiplier || valTo % multiplier) {
                    multiplier = ParamsSet.Model.MIN_MULTIPLIER;
                }
            }
            return multiplier;
        }
    }
    
</script>
<s:form action="admin/behavioralParameters/%{#attr.isCreatePage?'create':'update'}" id="behaviouralParamForm">
<s:hidden name="id"/>
<s:hidden name="version"/>

<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<ui:section>
    <ui:fieldGroup>

        <ui:field labelKey="channel.name" labelForId="name" required="true" errors="name">
            <s:textfield name="name" cssClass="middleLengthText" styleId="name" maxlength="100"/>
        </ui:field>

        <ui:field labelKey="channel.params.threshold" labelForId="threshold" required="true" errors="threshold">
          <s:textfield name="threshold" styleId="threshold" cssClass="smallLengthText1" maxlength="18"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>


<ui:section titleKey="channel.params" tipKey="channel.params.tooltip">
    <s:if test="behavioralParameters.size == 0">
        <span class="infos" id="infoAddParameter"><fmt:message key="channel.params.noParams"/></span>
    </s:if>
    <s:else>
        <span class="infos" id="infoAddParameter" style="display: none"><fmt:message key="channel.params.noParams"/></span>
    </s:else>
        <table class="formFields" id="paramsTable">
            <tr class="hide" id="paramsRow?">
                <td class="field">
                    <table class="fieldAndAccessories">
                        <tr>
                            <td class="withField">
                                <input type="hidden" name="behavioralParameters[?].id" disabled="disabled"/>
                                <input type="hidden" name="behavioralParameters[?].version" disabled="disabled"/>
                                <input type="text" name="behavioralParameters[?].minimumVisits" maxlength="2" class="minimumVisits smallLengthNumber" disabled="disabled"/>
                            </td>
                            <td class="withField">
                                <select name="behavioralParameters[?].triggerType" class="triggerType smallLengthText1" disabled="disabled">
                                    <option value="U"><fmt:message key="channel.params.urls"/></option>
                                    <option value="S"><fmt:message key="channel.params.searchKeywords"/></option>
                                    <option value="P"><fmt:message key="channel.params.pageKeywords"/></option>
                                </select>
                            </td>
                            <td id="range?">
                                <table class="fieldAndAccessories">
                                    <tr>
                                        <td class="withField">
                                            <ui:text textKey="channel.params.between"/>
                                        </td>
                                        <td class="withField">
                                            <select name="behavioralParameters[?].timeFrom" class="unitFrom smallLengthText" disabled="disabled">
                                                <option value="0"><fmt:message key="form.now"/></option>
                                            </select>
                                        </td>
                                        <td class="withField">
                                            <ui:text textKey="channel.params.and"/>
                                        </td>
                                        <td class="withField">
                                            <select name="behavioralParameters[?].timeTo" class="unitTo smallLengthText" disabled="disabled">
                                                <option value="0"><fmt:message key="form.now"/></option>
                                            </select>
                                        </td>
                                        <td class="withField timing">
                                            <table class="fieldAndAccessories">
                                                <tr>
                                                    <td class="withField">
                                                        <select class="units smallLengthText1" name="behavioralParameters[?].triggerType" disabled="disabled">
                                                            <option value="${TIMEUNIT_MINUTES_MULTIPLIER}"><fmt:message key="channel.params.select.minute"/></option>
                                                            <option value="${TIMEUNIT_HOURS_MULTIPLIER}"><fmt:message key="channel.params.select.hour"/></option>
                                                            <option value="${TIMEUNIT_DAYS_MULTIPLIER}"><fmt:message key="channel.params.select.day"/></option>
                                                        </select>
                                                    </td>
                                                    <td class="withField">
                                                        <ui:text textKey="channel.params.ago"/>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                        <c:set var="part2Url">
                                            <fmt:message key="channel.params.part2.url"/>
                                        </c:set>
                                        <c:if test="${not empty part2Url}">
                                            <td class="withField">
                                                <ui:text text="${part2Url}"/>
                                            </td>
                                        </c:if>
                                    </tr>
                                </table>
                            </td>
                            <td>
                                <table class="fieldAndAccessories">
                                    <tr>
                                        <td class="withField">
                                            <ui:text textKey="channel.withWeight"/>
                                        </td>
                                        <td class="withField">
                                            <input type="text" name="behavioralParameters[?].weight" id="weight?" maxlength="3" class="smallLengthNumber" value="1" disabled="disabled"/>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
                <td class="field">
                    <ui:button message="form.delete" subClass="delRow" type="button" />
                </td>
            </tr>
            <s:iterator value="%{behavioralParameters}" var="behavioralParameter" status="row">
                <s:if test="#behavioralParameter != null">
                <s:set var="rowIdx" value="#row.index"/>
                <tr id="paramsRow${rowIdx}">
                    <td class="field">
                        <table class="fieldAndAccessories">
                            <tr>
                                <td class="withField">
                                    <s:hidden name="behavioralParameters[%{#row.index}].id"/>
                                    <s:hidden name="behavioralParameters[%{#row.index}].version"/>
                                    <s:hidden name="behavioralParameters[%{#row.index}].paramsList.id"/>
                                    <s:textfield name="behavioralParameters[%{#row.index}].minimumVisits" maxlength="2" cssClass="minimumVisits smallLengthNumber"/>
                                </td>
                                <c:set var="errorMinimumVisits">
                                    <s:fielderror><s:param value="'behavioralParameters[' + #row.index + '].minimumVisits'"/></s:fielderror>
                                </c:set>
                                <c:if test="${not empty errorMinimumVisits}">
                                    <td class="withError">
                                        ${errorMinimumVisits}
                                    </td>
                                </c:if>
                                <td class="withField">
                                    <s:select name="behavioralParameters[%{#row.index}].triggerType" cssClass="triggerType smallLengthText1"
                                            list="#{'U':getText('channel.params.urls'),'S':getText('channel.params.searchKeywords'),'P':getText('channel.params.pageKeywords')}">
                                    </s:select>
                                </td>
                                <td id="range${row.index}">
                                    <table class="fieldAndAccessories">
                                        <tr>
                                            <td class="withField">
                                                <ui:text textKey="channel.params.between"/>
                                            </td>
                                            <td class="withField">
                                                <s:select name="behavioralParameters[%{#row.index}].timeFrom" cssClass="unitFrom smallLengthText"
                                                        list="#{'0':getText('form.now')}">
                                                </s:select>
                                            </td>
                                            <c:set var="errorTimeFrom">
                                                <s:fielderror><s:param value="'behavioralParameters[' + #row.index + '].timeFrom'"/></s:fielderror>
                                            </c:set>
                                            <c:if test="${not empty errorTimeFrom}">
                                                <td class="withError">
                                                    ${errorTimeFrom}
                                                </td>
                                            </c:if>
                                            <td class="withField">
                                                <ui:text textKey="channel.params.and"/>
                                            </td>
                                            <td class="withField">
                                                <s:select name="behavioralParameters[%{#row.index}].timeTo" cssClass="unitTo smallLengthText"
                                                        list="#{'0':getText('form.now')}">
                                                </s:select>
                                            </td>
                                            <c:set var="errorTimeTo">
                                                <s:fielderror><s:param value="'behavioralParameters[' + #row.index + '].timeTo'"/></s:fielderror>
                                            </c:set>
                                            <c:if test="${not empty errorTimeTo}">
                                                <td class="withError">
                                                    ${errorTimeTo}
                                                </td>
                                            </c:if>
                                            <td class="timing">
                                                <table class="fieldAndAccessories">
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
                                            <c:set var="part2Url">
                                                <fmt:message key="channel.params.part2.url"/>
                                            </c:set>
                                            <c:if test="${not empty part2Url}">
                                                <td class="withField">
                                                    <ui:text text="${part2Url}"/>
                                                </td>
                                            </c:if>
                                        </tr>
                                    </table>
                                </td>
                                <td class="withField">
                                    <table class="fieldAndAccessories">
                                        <tr>
                                            <td class="withField">
                                                <ui:text textKey="channel.withWeight"/>
                                            </td>
                                            <td class="withField">
                                                <s:textfield name="behavioralParameters[%{#row.index}].weight" id="weight%{#row.index}" maxlength="3"
                                                    cssClass="smallLengthNumber"/>
                                            </td>
                                            <c:set var="errorWeight">
                                                <s:fielderror><s:param value="'behavioralParameters[' + #row.index + '].weight'"/></s:fielderror>
                                            </c:set>
                                            <c:if test="${not empty errorWeight}">
                                                <td class="withError">
                                                    ${errorWeight}
                                                </td>
                                            </c:if>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                    <td class="field">
                        <ui:button message="form.delete" subClass="delRow" type="button" />
                    </td>
                    <c:set var="errorTimeInterval">
                        <s:fielderror><s:param value="'behavioralParameters[' + #row.index + ']'"/></s:fielderror>
                    </c:set>
                    <c:if test="${not empty errorTimeInterval}">
                        <td class="withError">
                            ${errorTimeInterval}
                        </td>
                    </c:if>
                </tr>
                </s:if>
            </s:iterator>
        </table>
        <div style="margin: 2px;">
            <ui:button message="form.add" id="addRowBtn" type="button" />
            <s:fielderror><s:param value="'params'"/></s:fielderror>
        </div>
</ui:section>

<script type="text/javascript">
    $().ready(function(){
        <s:iterator value="behavioralParameters" var="bParam" status="row">
            <s:if test="#bParam != null">
                var paramsSet = $('#paramsRow' + ${row.index}).data('paramsSet');
                paramsSet.setRange(
                    <s:if test="#bParam.timeFrom == null ">0</s:if>
                    <s:else>${bParam.timeFrom}</s:else>, 
                    <s:if test="#bParam.timeTo == null">0</s:if>
                    <s:else>${bParam.timeTo}</s:else>
                );
            </s:if>
        </s:iterator>
    });
</script>
<s:include value="/templates/formFooter.jsp"/>
</s:form>
