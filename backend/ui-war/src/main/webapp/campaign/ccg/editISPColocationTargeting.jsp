<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    function colocationsControl(toHide){
        $('#colocationTable')[toHide ? 'hide' : 'show']();
    }
</script>

<c:if test="${ad:isInternal()}">
    <ui:section titleKey="ccg.colocation.targeting" id="ccgColocationTargeting">
        <ui:fieldGroup>
            <ui:field>
                <table class="grouping">
                    <tr>
                        <td>
                            <label class="withInput">
                                <s:radio name="ispColocationTargetingFlag" id="colocationsAllId" list="false" template="justradio" onclick="colocationsControl(true);"
                                        /><fmt:message key="ccg.colocation.all"/>
                            </label>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label class="withInput">
                                <s:radio name="ispColocationTargetingFlag" id="colocationSpecificColocationsId" list="true" template="justradio" onclick="colocationsControl(false);"
                                        /><fmt:message key="ccg.colocation.specific"/>
                            </label>
                        </td>
                    </tr>
                </table>
            </ui:field>

            <ui:field id="colocationTable">
                <table class="grouping">
                    <tr>
                        <s:fielderror><s:param value="'colocations'"/></s:fielderror>
                    </tr>
                    <tr>
                        <td>
                            <ui:autocomplete
                                    id="selectedColocationIds"
                                    source="getColocations"
                                    selectedItems="${groupColocations}"
                                    selectedNameKey="fullName"
                                    selectedValueKey="id"
                                    cssClass="bigLengthText"
                                    isMultiSelect="true"
                                    minLength="1"
                                    >
                                    
                            <s:if test="id == null">
                                <s:set var="ispCountryCode" value="account.country.countryCode"/>
                            </s:if>
                            <s:else>
                                <s:set var="ispCountryCode" value="country.countryCode"/>
                            </s:else>
                            <script type="text/javascript">
                                function getColocations(request, response){
                                    UI.Data.get('ispColocations', {name:request.term, countryCode:'<s:property value="ispCountryCode"/>',
                                        testAccount:'<s:property value="account.testFlag"/>'}, function(data) {
                                        var opts = $('option', data).map(function() {
                                            var curr = $(this);
                                            return {label:curr.text(), value:curr.attr('id')};
                                        });
                                        response(opts);
                                    });
                                };
                            </script>
                            </ui:autocomplete>
                        </td>
                    </tr>
                </table>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>

    <s:if test="!ispColocationTargetingFlag">
        <script type="text/javascript">
            $().ready(function(){
                colocationsControl(true);
            })
        </script>
    </s:if>
</c:if>