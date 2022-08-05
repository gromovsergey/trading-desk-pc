<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    function showHideOptInStatusTargeting() {
        if ($('#optInStatusTargetingFlag').is(":checked")) {
            $("#optInStatusTargetingOptions")
                    .show()
                    .find(":input").removeAttr('disabled');
        } else {
            $("#optInStatusTargetingOptions")
                    .hide()
                    .find(":input")
                        .attr('disabled', 'disabled')
                        .prop({checked:false});
            $('#minUidAge')[0].value = '';
        }
    }

    function emptyMinUidAge() {
        if (!$('#minUidAgeFlag').checked) {
            $('#minUidAge')[0].value = '';
        }
    }

    $().ready(function(){
        $('#minUidAgeFlag').change(function(){
            if (!this.checked) {
                $('#minUidAge')[0].value = '';
            }
        });

        $('#minUidAge').click(function(e){
            e.stopPropagation();
            $('#minUidAgeFlag')[0].checked = true;
            $('#optedInUsers')[0].checked = true;
            return false;
        });

        $('#optedInUsers').click(function(){
            if (!this.checked) {
                $('#minUidAgeFlag')[0].checked = false;
                $('#minUidAge')[0].value = '';
            }
        });

        $('#minUidAgeFlag').click(function(){
            if (this.checked) {
                $('#optedInUsers')[0].checked = true;
            }
        });
    });

    $(showHideOptInStatusTargeting);
</script>

<ui:section id="ccgOptInStatusTargeting">
    <ui:fieldGroup>
        <ui:field>
            <ul class="chBoxesTree">
                <li>
                    <label class="withInput">
                        <s:checkbox id="optInStatusTargetingFlag" name="optInStatusTargetingFlag"
                                    onchange="showHideOptInStatusTargeting();"/>
                        <s:text name="ccg.optInStatusTargeting"/>
                        <s:fielderror><s:param value="'optInStatusTargeting'"/></s:fielderror>
                    </label>
                </li>
                <li>
                    <div id="optInStatusTargetingOptions">
                        <ul>
                            <li>
                                <label class="withInput">
                                    <s:checkbox id="optedInUsers" name="optInStatusTargeting.optedInUsers"/><s:text
                                        name="ccg.optInStatusTargeting.optedIn"/>
                                </label>
                                <ul>
                                    <li>
                                        <label class="withInput">
                                            <s:checkbox id="minUidAgeFlag" name="minUidAgeFlag"/>
                                            <fmt:message key="ccg.optInStatusTargeting.optedIn.minUidAge">
                                                <fmt:param>
                                                    <s:textfield name="minUidAge" id="minUidAge" size="4" maxLength="5"/>
                                                </fmt:param>
                                            </fmt:message>
                                            <s:fielderror><s:param value="'minUidAge'"/></s:fielderror>
                                        </label>
                                    </li>
                                </ul>
                            </li>
                            <li>
                                <label class="withInput">
                                    <s:checkbox id="optedOutUsers" name="optInStatusTargeting.optedOutUsers"/><s:text
                                        name="ccg.optInStatusTargeting.optedOut"/>
                                </label>
                            </li>
                            <li>
                                <label class="withInput">
                                    <s:checkbox id="unknownUsers" name="optInStatusTargeting.unknownUsers"/><s:text
                                        name="ccg.optInStatusTargeting.unknown"/>
                                </label>
                            </li>
                        </ul>
                    </div>
                </li>
            </ul>
        </ui:field>
    </ui:fieldGroup>
</ui:section>