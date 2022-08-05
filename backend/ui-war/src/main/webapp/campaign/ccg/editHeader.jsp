<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<c:set var="pageExt" scope="request" value="${existingGroup.ccgType.pageExtension}"/>

<script type="text/javascript">
    var CPM_FACTOR = 1000;

    function recalculateGoal(){
        var rateType    = getSelectedRateType(),
            rate        = UI.Localization.parseFloat($('#'+rateType+'RateInput').val()),
            budget      = UI.Localization.parseFloat($('#budgetInput').val());

        if(!isNaN(budget) && !isNaN(rate) && rate > 0 && budget > 0 && !/[eE]/.test($('#'+rateType+'RateInput').val()+$('#budgetInput').val())){
            var res = (rateType == 'CPM') ? (CPM_FACTOR * budget) / rate : budget / rate;
            // Compensate FP accuracy errors such as 17 / 0.17 = 99.99999...
            res = Math.round(res * Math.pow(10,10));
            res = Math.floor(res / Math.pow(10,10));
            $('#goalValue').html(res);
        }else{
            $('#goalValue').html('');
        }
    }

    function translateRateType(result){
        <c:forEach items="${rateTypes}" var="rateType">
            if (result == <s:property value="#attr.rateType.ordinal()"/>) return '${rateType.name}';
        </c:forEach>
        return '';
    }

    function getSelectedRateType() {
        return translateRateType($('#rateTypeOrdinal').val());
    }

    function initRatesAndGoals() {
        updateRateArea();
        recalculateGoal();
        updateBidStrategyArea()
    }

    function updateRateArea(){
        var rateType = getSelectedRateType();

        $('#CPCRateInput, #CPMRateInput, #CPARateInput').hide();
        $('#' + rateType + 'RateInput').show();

        $('#budgetInput').prop({disabled : !isRateAllowed(rateType)});

        $('#goalMeasure').html(rateType == 'CPC' ? '<fmt:message key="ccg.clicks"/>' : rateType == 'CPA' ? '<fmt:message key="ccg.actions"/>' : '<fmt:message key="ccg.impressions"/>');

        if(rateType == 'CPA'){
            $('#actionTrackingChB').prop({disabled : false});
            $('#actionTrackingTr').show();
        }else{
            $('#actionTrackingChB').prop({disabled : true});
            $('#actionTrackingTr').hide();
        }
    }

    function cleanOtherInputRateFields(arrInputs, input, value) {
        $.each(arrInputs, function(){
            if(this == input) {
               $('#' + this + 'RateInput').val(value);
               return true;
            }
            $('#' + this + 'RateInput').val('0');
        })
    }

    function getPreviousInput(arrInputs) {
        var previousInput;
        $.each(arrInputs, function(){
            if ($('#' + this + 'RateInput').css('display') != 'none') previousInput = this.toString();
        })
        return previousInput;
    }

    function rateSelected(){
        var rateType = getSelectedRateType();
        var previousRateType = getPreviousInput(['CPA', 'CPC', 'CPM']);
        var value = $('#' + previousRateType + 'RateInput').val()
        cleanOtherInputRateFields(['CPA', 'CPC', 'CPM'], rateType, value);

        updateRateArea();
        recalculateGoal();
        updateBidStrategyArea();
        <s:if test="%{!isInternal()}">
        checkCCGOptimization();
        </s:if>
    }

    function checkCCGOptimization() {
        var rateType = getSelectedRateType();
        if (rateType != 'CPM') {
            $('#CCGOptimization').hide();
            $('#optimisationTooltip').hide();
            $('#tooltip').show();
        }  else {
            $('#CCGOptimization').show();
            $('#tooltip').hide();
            $('#optimisationTooltip').show();

        }
    }
    
    function rateTypesCount() {
        return <s:property value="rateTypes.size()"/> == 1;
    }

    function isRateAllowed(rate){
        <c:forEach var="allowedRate" items="${rateTypes}">
            if('${allowedRate}' == rate) return true;
        </c:forEach>
        return false;
    }
    
    function generateAllowedCodes(){
        var OS = detectOS();
        return getAllowedCodes();

        function detectOS(){
            if (navigator.appVersion.indexOf("Win") >= 0) return 'Windows';
            if (navigator.appVersion.indexOf("Mac") >= 0) return 'MacOS';
            if (navigator.appVersion.indexOf("X11") >= 0) return 'UNIX';
            if (navigator.appVersion.indexOf("Linux") >= 0) return 'Linux';
            return 'Unknown OS'
        }

        function getAllowedCodes(){
            var arrCodes = [
                8,     // backspace
                13   // enter
            ];
            if(!$.browser.msie){
                arrCodes.push(
                    9,    // tab
                    27    // escape
                );
            }
            if(!$.browser.msie && !$.browser.safari){
                arrCodes.push(
                    35,   // end
                    36,   // home
                    46    // delete
                );
            }
            if(!$.browser.safari && !($.browser.mozilla && (OS == 'MacOS'))){
                arrCodes.push(
                    16,   // shift
                    17,   // ctrl
                    18    // alt
                );
            }
            if(!$.browser.safari && !((OS == 'MacOS') && ($.browser.mozilla || $.browser.opera))){
                arrCodes.push(
                    20,   // caps lock
                    91    // windows (start)
                );
            }
            if($.browser.mozilla || $.browser.opera){
                arrCodes.push(
                    37,   // left arrow
                    38,   // up arrow
                    39,   // right arrow
                    40    // down arrow
                );
            }
            if(!$.browser.msie && !$.browser.safari && !($.browser.opera && (OS == 'MacOS'))){
                arrCodes.push(
                    112,  // f1
                    113,  // f2
                    114,  // f3
                    115,  // f4
                    116,  // f5
                    117,  // f6
                    118,  // f7
                    119,  // f8
                    120,  // f9
                    121,  // f10
                    122,  // f11
                    123   // f12
                );
            }
            return arrCodes;
        }
    }

    var ALLOWEDKEYCODES = generateAllowedCodes();

    $.datepicker.setDefaults({
        showOn: 'both',
        numberOfMonths: 2,
        buttonImageOnly: true,
        buttonImage: '/images/calendar.gif'
    });

    $().ready(function() {
        initRatesAndGoals();

        $("#dateStartDisplay, #dateEndDisplay")
            .datepicker()
            /*.keypress(function(e){
                if(e.ctrlKey) return true;
                if($.inArray(e.keyCode, ALLOWEDKEYCODES) >= 0) return true;

                var txt = String.fromCharCode(e.charCode ? e.charCode : e.keyCode);
                /[\d \/:]/.test(txt) && UI.Text.insertAtCaret(this, txt);
                return false;
            });*/

        $('#rateTypeOrdinal').change(function() {
            rateSelected();
            $('#ccgRate .errors').hide();
        });

        $('#rateTypeOrdinal').focus(function(){
            $(this).data('focused', 'true');
        })

        $('#rateTypeOrdinal').blur(function(){
            $(this).removeData('focused');
        })

        $(document).keyup(function(e){
            var rateTypeOrd = $('#rateTypeOrdinal');
            if(rateTypeOrd.data('focused')){
                if(e.keyCode == 38 || e.keyCode == 40){
                    rateTypeOrd.change();
                }
            }
        })

        $('#CPARateInput, #CPCRateInput, #CPMRateInput, #budgetInput')
            .change(recalculateGoal);
        
        <s:if test="%{!isInternal()}">
        checkCCGOptimization();
        </s:if>
    });

</script>

