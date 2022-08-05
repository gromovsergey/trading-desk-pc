<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
                    <ui:button message="creative.moveToSet" onclick="showDialog();" />
                    <div id="moveToSet" title="<fmt:message key="creative.moveToSet"/>" style="display:none;">
                        <table class="formFields">
                            <tr>
                                <td class="field">
                                    <fmt:message key="creative.moveToSet.desc"/>:
                                </td>
                            </tr>
                            <tr>
                                <td class="field nomargin">
                                    <label class="withInput" style="display:inline">
                                        <s:radio id="moveToSetRadioEnd" name="moveToSet" value="true" list="true" template="justradio"/>
                                        <fmt:message key="creative.moveToSet.newAndInsertAtTheEnd"/>
                                    </label>
                                </td>
                            </tr>
                            <tr>
                                <td class="field nomargin">
                                    <label class="withInput" style="display:inline">
                                        <s:radio id="moveToSetRadioBefore" name="moveToSet"  list="false" template="justradio"/>
                                        <fmt:message key="creative.moveToSet.newAndInsertBefore"/>
                                    </label>
                                    &nbsp;
                                    <s:select disabled="true" id="idSetNumberBefore" name="setNumberBefore" list="creativeSetCount" listValue="%{getText('creative.set', '', toString())}"/>
                                </td>
                            </tr>
                            <tr>
                                <td class="field nomargin">
                                    <label class="withInput" style="display:inline">
                                        <s:radio id="moveToSetRadioAppend" name="moveToSet" list="false" template="justradio"/>
                                        <fmt:message key="creative.moveToSet.append"/>
                                    </label>
                                    &nbsp;
                                    <s:select disabled="true" id="idSetNumberAppend" name="setNumberAppend" list="creativeSetCount" listValue="%{getText('creative.set', '', toString())}"/>
                                </td>
                            </tr>
                        </table>
                        <div class="wrapper">
                            <ui:button message="form.save" type="button" onclick="moveCreativesToSet();"/>
                            <ui:button message="form.cancel" onclick="$('#moveToSet').dialog('close');" type="button"/>
                        </div>
                    </div>
                    <script type="text/javascript">
                        $(function(){
                            $('#moveToSet').dialog({
                                autoOpen: false,
                                width: 400
                            });
                        });
                        
                        function showDialog() {
                            if ( $('[name=textAdIds]').is(':checked') || $('[name=creativesIds]').is(':checked') ) {
                                $('#moveToSet').dialog('open');
                            }
                        }
                        
                        function moveCreativesToSet() {

                            var url = "";
                            var action = $('[name=moveToSet]:checked').prop('id');
                            if (action.match('^moveToSetRadioEnd')) {
                                url = '${_context}/campaign/group/creative/bulk/insertLastSet.action';
                            } else if (action.match('^moveToSetRadioBefore')) {
                                $('#setNumber').val($('[name=setNumberBefore]').val());
                                url = '${_context}/campaign/group/creative/bulk/insertSet.action';
                            } else if (action.match('^moveToSetRadioAppend')) {
                                $('#setNumber').val($('[name=setNumberAppend]').val());
                                url = '${_context}/campaign/group/creative/bulk/moveToSet.action';
                            }

                            if (url && $('#setNumber')) {
                                $('#creativesForm').attr('action', url).submit();
                            }
                        }
                        
                        function moveToSetRadioHandler() {
                            if ($('#moveToSetRadioEndtrue').prop('checked')){
                                $('#idSetNumberAppend').attr("disabled", "disabled");
                                $('#idSetNumberBefore').attr("disabled", "disabled");
                                
                            }
                            
                            if ($('#moveToSetRadioBeforefalse').prop('checked')){
                                $('#idSetNumberAppend').attr("disabled", "disabled");
                                $('#idSetNumberBefore').removeAttr("disabled");
                                
                            }
                            
                            if ($('#moveToSetRadioAppendfalse').prop('checked')){
                                $('#idSetNumberAppend').removeAttr("disabled");
                                $('#idSetNumberBefore').attr("disabled", "disabled");
                                
                            }
                        }
                        
                        $(document).ready(function () {
                            $('input[name=moveToSet]').on('change keyup',moveToSetRadioHandler);
                        });
                    </script>