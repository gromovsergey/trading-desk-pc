<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">
    
    function updateDefaultControl(selectControl, clean){
        var selectControl = $(selectControl);
        var val = selectControl.val();
        var strElem = $('#string');
        var txtElem = $('#text');
        var intElem = $('#integer');
        var fileNameElem = $('#optFileName');
        var defaultFieldRow = $('#defaultFieldRow');
        var minValueElem = $('#minValue');
        var maxValueElem = $('#maxValue');
        var minValueFieldRow = $('#minValueFieldRow');
        var maxValueFieldRow = $('#maxValueFieldRow');
        var maxLengthElem = $('#maxLength');
        var maxLengthFieldRow = $('#maxLengthFieldRow');
        var maxLengthFullWidthElem = $('#maxLengthFullWidth');
        var maxLengthFullWidthFieldRow = $('#maxLengthFullWidthFieldRow');
        var colorSpan = $('#colorSpan');
        var colorInput = $('#colorInput');
        var colorBox = $('#colorColor');
        var valuesTable = $('#valuesList');
        var enumValueRadios = $('#valuesTable input[name=defaultEnumValue]:gt(0)');
        var enumValues = $('#valuesTable input[name^=values]:gt(0)');
        var addValueBtn = $('#addValueBtn');
        var requiredFlagRow = $('#requiredFlagRow');
        var requiredChBox = $('#required');
        
        if (clean) {
            var defValueError = $('#defaultFieldRow span:eq(1)');
            var enumValuesErrors = $('#valuesList td .withError');
            defValueError.add(enumValuesErrors).html('');
            strElem
                .add(fileNameElem)
                .add(txtElem)
                .add(intElem)
                .add(minValueElem)
                .add(maxValueElem)
                .add(maxLengthElem)
                .add(maxLengthFullWidthElem)
                .add(colorInput)
                .add(enumValues)
                    .val('');
                    
            colorBox.css('backgroundColor', '#ffffff');
            enumValueRadios.prop({checked : false});
        }
        
        strElem.add(txtElem)
            .add(fileNameElem)
            .add(intElem)
            .add(maxLengthElem)
            .add(maxLengthFullWidthElem)
            .add(colorSpan)
            .add(valuesTable)
            .add(valuesTable)
            .add(requiredFlagRow)
                .hide()
                .prop({disabled : true});
            
        minValueFieldRow
            .add(maxValueFieldRow)
            .add(maxLengthFieldRow)
            .add(maxLengthFullWidthFieldRow)
            .add(defaultFieldRow)
                .hide();
            
        minValueElem
            .add(maxValueElem)
            .add(colorInput)
            .add(enumValues)
            .add(enumValueRadios)
            .add(requiredChBox)
                .prop({disabled : true});
            
        if (val == 'URL') {
            fileNameElem
                .add(maxLengthElem)
                .add(maxLengthFieldRow)
                .add(maxLengthFullWidthElem)
                .add(maxLengthFullWidthFieldRow)
                .show()
                .prop({disabled : false});
        } else if (val == 'URL_WITHOUT_PROTOCOL') {
            fileNameElem
                .add(maxLengthElem)
                .add(maxLengthFieldRow)
                .add(maxLengthFullWidthElem)
                .add(maxLengthFullWidthFieldRow)
                .show()
                .prop({disabled : false});
        } else if (val == 'TEXT') {
            txtElem
                .add(maxLengthElem)
                .add(maxLengthFieldRow)
                .add(maxLengthFullWidthElem)
                .add(maxLengthFullWidthFieldRow)
                .show()
                .prop({disabled : false});
        } else if (val == 'STRING') {
            strElem
                .add(maxLengthElem)
                .add(maxLengthFieldRow)
                .add(maxLengthFullWidthElem)
                .add(maxLengthFullWidthFieldRow)
                .show()
                .prop({disabled : false});
        } else if (val == 'FILE_URL') {
            fileNameElem
                .add(maxLengthElem)
                .add(maxLengthFieldRow)
                .add(maxLengthFullWidthElem)
                .add(maxLengthFullWidthFieldRow)
                .show()
                .prop({disabled : false});
        } else if (val == 'INTEGER') {
            minValueFieldRow
                .add(maxValueFieldRow)
                .add(intElem)
                    .show();
            intElem
                .add(minValueElem)
                .add(maxValueElem)
                    .prop({disabled : false});
        } else if (val == 'COLOR') {
            colorSpan.show()
                .add(colorInput)
                    .prop({disabled : false});
        } else if (val == 'ENUM') {
            valuesTable.add(addValueBtn).show();
            $('#hiddenValuesTR').hide();
            valuesTable
                .add(enumValues)
                .add(enumValueRadios)
                    .prop({disabled : false});
            showHideButtons();
        } else if (val == 'FILE' || val == 'DYNAMIC_FILE') {
            fileNameElem.prop({disabled : false});
        }
        
        if (val != 'ENUM') {
            if(val != 'FILE' && val != 'DYNAMIC_FILE' && val != 'HTML') defaultFieldRow.show();
            requiredFlagRow.show()
                .add(requiredChBox)
                    .prop({disabled : false});
        }

        if (val == 'FILE' || val == 'FILE_URL' || val == 'DYNAMIC_FILE') {
            $('#fileTypeFieldRow').show()
        } else {
            $('#fileTypeFieldRow').hide()
        }
    }
        
    function checkToken() {
        //ToDo: remove FOROSCLICK and FOROSPRECLICK after migration completed
        if (($('#token').val() == 'RANDOM' ||
        $('#token').val() == 'CLICK' ||
        $('#token').val() == 'FOROSCLICK' ||
        $('#token').val() == 'PRECLICK' ||
        $('#token').val() == 'FOROSPRECLICK' ||
        $('#token').val() == 'KEYWORD') &&
        ($('#randomTokenFlag').prop('checked') ||
        $('#clickTokenFlag').prop('checked') ||
        $('#preClickTokenFlag').prop('checked') ||
        $('#keywordTokenFlag').prop('checked'))) {
            alert('${ad:formatMessage("Option.recursiveTokens.forbidden")}');
            UI.Util.enableButtons($('#optionSave'));
            return false;
        }

        return true;
    }
    
    $().ready(function(){
        $('#optionSave').submit(function() {
            var sortOrderPair = $('#sortOrderPair').val().split('_');
            $('#optionGroupId').val(sortOrderPair[0]);
            $('#sortOrder').val(sortOrderPair[1]);
            return (checkToken());
        });

        $('#addValueBtn').click(function() {
            UI.Util.Table.addRow('valuesTable');
            showHideButtons();
            return false;
        });

        $('.delRow').click(function(){
            var mainTable = $('#valuesTable');
            var mainTableRootTRs = $('tbody:eq(0) > tr', mainTable);

            if (mainTableRootTRs.length > 3) {
                UI.Util.Table.deleteRowByButtonInIt(this, mainTable[0]);
            }

            showHideButtons();
            return false;
        });

        showHideButtons();

        var rowsCount = $('#valuesTable').children('tbody:eq(0)').children('tr').length;
        $('#hiddenValuesTR').attr('_cnt', rowsCount);
    });

    function showHideButtons() {
        var btn1 = $('#valuesTable .delRow:eq(1)');
        var btn2 = $('#valuesTable .delRow:eq(2)');
        var addBtn = $('#addValueBtn');
        var valuesCount = $('#valuesTable > tbody > tr:not(:hidden)').length;

        if (valuesCount <= 2) {
            btn1.add(btn2).hide();
            addBtn.show();
        } else {
            btn1.add(btn2).show();
            addBtn[(valuesCount >= 100) ? 'hide' : 'show']();
        }
    }

</script>

<s:form action="admin/Option/%{#attr.isCreatePage?'create':'update'}" enctype="multipart/form-data" id="optionSave">
    <s:hidden name="id"/>
    <s:hidden name="creativeSizeId"/>
    <s:hidden name="templateId"/>
    <s:hidden name="optionGroupId"/>
    <s:hidden name="version"/>
    <s:hidden name="optionGroup.id" id="optionGroupId"/>
    <s:hidden name="sortOrder" id="sortOrder"/>
    <div class="wrapper">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:fielderror><s:param value="'options'"/></s:fielderror>
        <s:actionerror/>
    </div>

    <ui:section>
        <ui:fieldGroup>

            <ui:field labelKey="OptionGroup.position" labelForId="sortOrder" required="true" errors="sortOrder">
                <select id="sortOrderPair" name="sortOrderPair" class="middleLengthText">

                    <c:if test="${advertiserTypeEnabled}">
                        <c:set var="advertiserLabel"><fmt:message key="CreativeSize.options.group.type.Advertiser"/></c:set>
                        <ui:options label="${advertiserLabel}" collection="${advertiserOptionGroups}" sortOrder="${sortOrder}"
                                    currentGroupId="${model.optionGroup.id}" currentOptionId="${id}" optgroupEnabled="${id == null}"/>
                    </c:if>

                    <c:if test="${publisherTypeEnabled}">
                        <c:set var="publisherLabel"><fmt:message key="CreativeSize.options.group.type.Publisher"/></c:set>
                        <ui:options label="${publisherLabel}" collection="${publisherOptionGroups}" sortOrder="${sortOrder}"
                                    currentGroupId="${model.optionGroup.id}" currentOptionId="${id}" optgroupEnabled="${id == null}"/>
                    </c:if>

                    <c:if test="${hiddenTypeEnabled}">
                        <c:set var="hiddenLabel"><fmt:message key="CreativeSize.options.group.type.Hidden"/></c:set>
                        <ui:options label="${hiddenLabel}" collection="${hiddenOptionGroups}" sortOrder="${sortOrder}"
                                    currentGroupId="${model.optionGroup.id}" currentOptionId="${id}" optgroupEnabled="${id == null}"/>
                    </c:if>

                </select>
            </ui:field>

            <ui:field labelKey="defaultName" labelForId="name" required="true" errors="defaultName,name">
                <s:textfield name="defaultName" id="name" cssClass="middleLengthText" maxlength="100"/>
            </ui:field>

            <ui:field labelKey="Option.type" labelForId="type" errors="type">
                <s:select name="type" id="type" list="availableTypes" value="type.name()"
                          listValue="getText('enums.OptionType.'+toString())"
                          cssClass="middleLengthText" onchange="updateDefaultControl(this, true);"/>
            </ui:field>

            <ui:field labelKey="Option.minValue" id="minValueFieldRow" labelForId="minValue" errors="minValue">
                <s:textfield name="minValue" id="minValue" cssClass="smallLengthText1" maxlength="14"/>
            </ui:field>

            <ui:field labelKey="Option.maxValue" id="maxValueFieldRow" labelForId="maxValue" errors="maxValue">
                <s:textfield name="maxValue" id="maxValue" cssClass="smallLengthText1" maxlength="14"/>
            </ui:field>

            <ui:field labelKey="Option.values" id="valuesList">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withError">
                            <s:fielderror>
                                <s:param value="'values'"/>
                            </s:fielderror>
                        </td>
                    </tr>
                    <tr>
                        <td class="withField">
                            <table id="valuesTable" class="dataView">
                                <thead>
                                    <th><fmt:message key="Option.values.name"/></th>
                                    <th><fmt:message key="Option.values.value"/></th>
                                    <th><fmt:message key="Option.values.default"/></th>
                                    <th class="withButton"></th>
                                    <th class="withError"></th>
                                </thead>
                                <tbody>
                                    <tr class="hide" id="hiddenValuesTR">
                                        <td>
                                            <input type="text" name="valuesList[?].name" class="middleLengthText" maxlength="50" disabled="disabled"/>
                                        </td>
                                        <td>
                                            <input type="text" name="valuesList[?].value" class="middleLengthText" maxlength="50" disabled="disabled"/>
                                        </td>
                                        <td class="radio">
                                            <input type="hidden" name="valuesList[?].id"/>
                                            <table class="mandatoryContainer">
                                                <tr>
                                                    <td>
                                                        <input type="radio" name="defaultEnumValue" value="?" disabled="true"/>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                        <td class="withButton">
                                            <ui:button message="Option.values.remove" subClass="delRow" type="button"/>
                                        </td>
                                        <td class="withError"></td>
                                    </tr>
                                    <s:if test="valuesList != null">
                                        <s:iterator value="valuesList" var="value" status="row">
                                            <s:if test="value != null">
                                                <tr>
                                                    <td>
                                                        <s:textfield name="valuesList[%{#row.index}].name"
                                                                     cssClass="middleLengthText" maxlength="50"/>
                                                    </td>
                                                    <td>
                                                        <s:textfield name="valuesList[%{#row.index}].value"
                                                                     cssClass="middleLengthText" maxlength="50"/>
                                                    </td>
                                                    <td class="radio">
                                                        <s:hidden name="valuesList[%{#row.index}].id" />
                                                        <table class="mandatoryContainer">
                                                            <tr>
                                                                <td>
                                                                    <s:radio name="defaultEnumValue" list="%{#row.index}" template="justradio"/>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                    <td class="withButton">
                                                        <ui:button message="Option.values.remove" subClass="delRow"
                                                                   type="button"/>
                                                    </td>
                                                    <td class="withError">
                                                        <s:fielderror>
                                                            <s:param value="'values[' + #row.index + '].name'"/>
                                                            <s:param value="'values[' + #row.index + '].value'"/>
                                                        </s:fielderror>
                                                    </td>
                                                </tr>
                                            </s:if>
                                            <s:else>
                                                <tr class="hide">
                                                </tr>
                                            </s:else>
                                        </s:iterator>
                                    </s:if>
                                    <s:else>
                                        <tr>
                                            <td>
                                                <input type="text" name="valuesList[0].name" class="middleLengthText" maxlength="50"/>
                                            </td>
                                            <td>
                                                <input type="text" name="valuesList[0].value" class="middleLengthText" maxlength="50"/>
                                            </td>
                                            <td class="radio">
                                                <input type="hidden" name="valuesList[0].id"/>
                                                <table class="mandatoryContainer">
                                                    <tr>
                                                        <td>
                                                            <input type="radio" name="defaultEnumValue" value="0"/>
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                            <td class="withButton">
                                                <ui:button message="Option.values.remove" subClass="delRow" type="button"/>
                                            </td>
                                            <td class="withError"></td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <input type="text" name="valuesList[1].name" class="middleLengthText" maxlength="50"/>
                                            </td>
                                            <td>
                                                <input type="text" name="valuesList[1].value" class="middleLengthText" maxlength="50"/>
                                            </td>
                                            <td class="radio">
                                                <input type="hidden" name="valuesList[1].id"/>
                                                <table class="mandatoryContainer">
                                                    <tr>
                                                        <td>
                                                            <input type="radio" name="defaultEnumValue" value="1"/>
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                            <td class="withButton">
                                                <ui:button message="Option.values.remove" subClass="delRow" type="button"/>
                                            </td>
                                            <td class="withError"></td>
                                        </tr>
                                    </s:else>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td class="withButton">
                            <ui:button message="Option.values.add" id="addValueBtn" type="button" />
                        </td>
                    </tr>
                </table>
            </ui:field>

            <ui:field labelKey="Option.required" errors="required" id="requiredFlagRow">
                <s:checkbox name="required" id="required"/>
            </ui:field>

            <ui:field labelKey="Option.internalUse" errors="internalUse">
                <label class="withInput">
                    <s:radio name="internalUse" list="true" template="justradio"/><fmt:message key="yes"/>
                </label>
                <label class="withInput">
                    <s:radio name="internalUse" list="false" template="justradio"/><fmt:message key="no"/>
                </label>
             </ui:field>

            <ui:field labelKey="Option.token" labelForId="token" required="true" errors="token">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <s:textfield name="token" id="token" cssClass="middleLengthText" maxLength="50"/>
                        </td>
                    </tr>
                </table>
            </ui:field>
            
            <ui:field>
                <ui:text textKey="Option.uniqueToken"/>
            </ui:field>

            <ui:field id="fileTypeFieldRow" labelKey="Option.fileTypes" errors="fileTypes">
                <ui:optiontransfer name="selFileTypes.id" list="${allFileTypes}" selList="${selFileTypes}"
                        saveSorting="false" cssClass="smallLengthText1" size="9"
                        titleKey="Option.availableFileTypes" selTitleKey="Option.selectedFileTypes"/>
            </ui:field>

            <ui:field id="defaultFieldRow" labelKey="Option.defValue" errors="defaultValue,integerDefaultValue">
                <s:textfield name="defaultValue" id="optFileName" cssClass="middleLengthText"/>
                <s:textfield name="defaultValue" id="string" cssClass="middleLengthText"/>
                <s:textfield name="integerDefaultValue" id="integer" cssClass="middleLengthText" maxlength="14"/>
                <s:textarea name="defaultValue" id="text" cssClass="middleLengthText" rows="10"/>
                <ui:colorInput id="color" name="defaultValue" value="${defaultValue}"/>
            </ui:field>

            <ui:field labelKey="Option.defaultTooltip" labelForId="label" errors="defaultLabel">
                <s:textarea name="defaultLabel" id="label" cssClass="middleLengthText" maxLength="1000"/>
            </ui:field>

            <ui:field labelKey="Option.maxLength" id="maxLengthFieldRow" labelForId="maxLength" errors="maxLength">
                <s:textfield name="maxLength" id="maxLength" cssClass="smallLengthText1" maxlength="14"/>
            </ui:field>

            <ui:field labelKey="Option.maxLengthFullWidth" id="maxLengthFullWidthFieldRow" labelForId="maxLengthFullWidth" errors="maxLengthFullWidth">
                <s:textfield name="maxLengthFullWidth" id="maxLengthFullWidth" cssClass="smallLengthText1" maxlength="14"/>
            </ui:field>

            <ui:field labelKey="Option.recursiveTokens">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <s:checkbox name="genericTokensFlag" id="genericTokensFlag"/><s:text name="Option.genericTokensFlag"/>
                        </td>
                        <td class="withTip">
                            <ui:hint>
                                <ad:commaWriter items="${genericTokens}" label="name" escape="false"/>
                            </ui:hint>
                        </td>
                    </tr>
                    <tr>
                        <td class="withField">
                            <s:checkbox name="advertiserTokensFlag" id="advertiserTokensFlag"/><s:text name="Option.advertiserTokensFlag"/>
                        </td>
                        <td class="withTip">
                            <ui:hint>
                                <ad:commaWriter items="${advertisersTokens}" label="name" escape="false"/>
                            </ui:hint>
                        </td>
                    </tr>
                    <tr>
                        <td class="withField">
                            <s:checkbox name="publisherTokensFlag" id="publisherTokensFlag"/><s:text name="Option.publisherTokensFlag"/>
                        </td>
                        <td class="withTip">
                            <ui:hint>
                                <ad:commaWriter items="${publisherTokens}" label="name" escape="false"/>
                            </ui:hint>
                        </td>
                    </tr>
                    <tr>
                        <td class="withField">
                            <s:checkbox name="internalTokensFlag" id="internalTokensFlag"/><s:text name="Option.internalTokensFlag"/>
                        </td>
                        <td class="withTip">
                            <ui:hint>
                                <ad:commaWriter items="${internalTokens}"  label="name" escape="false"/>
                            </ui:hint>
                        </td>
                    </tr>
                </table>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>
    
    <script type="text/javascript">
      updateDefaultControl(document.getElementById('type'), false);
    </script>

  <s:if test="id == null">
    <c:choose>
        <c:when test="${optionGroupId != null}">
            <s:url var="createUrl" action="%{#attr.moduleName}/OptionGroup/view.action"><s:param name="id" value="optionGroupId"/></s:url>
        </c:when>
        <c:otherwise>
            <c:if test="${relatedType == 'CREATIVE_TEMPLATE'}">
                <s:url var="createUrl" action="%{#attr.moduleName}/CreativeTemplate/view"><s:param name="id" value="template.id"/></s:url>
            </c:if>
            <c:if test="${relatedType == 'DISCOVER_TEMPLATE'}">
                <s:url var="createUrl" action="%{#attr.moduleName}/DiscoverTemplate/view"><s:param name="id" value="template.id"/></s:url>
            </c:if>
            <c:if test="${relatedType == 'CREATIVE_SIZE'}">
                <s:url var="createUrl" action="%{#attr.moduleName}/CreativeSize/view"><s:param name="id" value="creativeSize.id"/></s:url>
            </c:if>
        </c:otherwise>
    </c:choose>
  </s:if>
  <s:else>
      <s:url var="createUrl" action="%{#attr.moduleName}/Option/view"><s:param name="id" value="id"/></s:url>
  </s:else>

  <s:include value="/templates/formFooter.jsp">
    <s:param name="createUrl">${createUrl}</s:param>
  </s:include>
</s:form>
