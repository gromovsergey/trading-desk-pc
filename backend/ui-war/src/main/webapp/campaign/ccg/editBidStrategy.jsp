<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<script type="text/javascript">
    $(function(){
        if ($('#bsMaximiseReach, #bsMinimumCtrGoal').length > 0) updateBidStrategyArea();
        var fStrategyChange = function(){
            if ($('input[name=bidStrategy]:radio:checked').val() == 'MAXIMISE_REACH') {
                $('#minCtrGoalInput').hide(); 
                $('#minCtrGoal').prop('disabled', true);
            } else {
                $('#minCtrGoalInput').show(); 
                $('#minCtrGoal').prop('disabled', false);
            }
        };
        $('input[name=bidStrategy]:radio, input[name=minCtrGoal]:radio').on('change', function(e){
            fStrategyChange();
            $('#bsMinimumCtrGoal').find('.errors').remove();
        });
        fStrategyChange();
    });

    function updateBidStrategyArea() {
        $('#bsMaximiseReach, #bsMinimumCtrGoal').show().find(':input').prop('disabled', false);
    };
</script>

<ui:field id="bsMaximiseReach" labelKey="ccg.bidStrategy" required="true" cssClass="valignFix">
    <table class="fieldAndAccessories">
        <tr>
            <td class="withField">
                <label class="withInput">
                    <s:radio name="bidStrategy" list="'MAXIMISE_REACH'" value="bidStrategy" template="justradio"/>
                    <fmt:message key="ccg.bidStrategy.maximiseReach"/>
                </label>
            </td>
            <td class="withTip">
                <ui:hint>
                    <fmt:message key="ccg.bidStrategy.hint.maximiseReach"/>
                </ui:hint>
            </td>
        </tr>
    </table>
</ui:field>

<ui:field id="bsMinimumCtrGoal" required="true" errors="minCtrGoal" cssClass="valignFix">
    <table class="fieldAndAccessories">
        <tr>
            <td class="withField">
                <label class="withInput">
                    <s:radio name="bidStrategy" list="'MINIMUM_CTR_GOAL'" value="bidStrategy" template="justradio"/>
                    <fmt:message key="ccg.bidStrategy.minimumCtrGoal"/>
                </label>
            </td>
            <td class="withTip">
                <ui:hint>
                    <fmt:message key="ccg.bidStrategy.hint.minimumCtrGoal"/>
                </ui:hint>
            </td>
            <td>
                <table class="fieldAndAccessories" id="minCtrGoalInput">
                    <tr>
                        <td class="withField">
                            <s:textfield name="minCtrGoal" id="minCtrGoal" cssClass="smallLengthText1" maxlength="6"/>
                            %
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</ui:field>
