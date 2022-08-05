<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<ad:requestContext var="advertiserContext"/>

<ui:externalLibrary libName="codemirror" />

<script type="text/javascript">
    var currentModuleName =  '<s:property value="%{#request.moduleName}/"/>',
    selectedTemplate;

    $(function(){
        if (!$('#templateId').val() || !$('#sizeId').val()) {
            $('#optionsDiv').hide();
        }

        $('#templateId').on('change', function(){
            checkTemplate();
            handleTnsObjects();
        });

        $('#sizeId').on('change', function(){
            var tokenVals   = $('#sizeOptionsDiv').formKeeper({'key':'sizeOptionsDiv', 'useDataNames':true}).formKeeper('serialize'),
                storedVals  = $.parseJSON(sessionStorage.getItem('sizeOptionsDiv'));

            if (storedVals) {
                storedVals  = $.extend(storedVals, tokenVals);
            } else {
                storedVals  = tokenVals;
            }
            sessionStorage.setItem('sizeOptionsDiv', JSON.stringify(storedVals));
            var sizeIdValue = this.value;

            $('#sizeOptionsDiv')
                .html('<h3 class="level1">${ad:formatMessage("creative.options.loading")}</h3>')
                .load(
                    '${_context}/creative/changeSize.action',
                    $('#saveForm').serializeArray(),
                    function(){
                        $('.collapsible').collapsible();
                        $(this).formKeeper('restore').find('.colorInput').each(function(){
                            $(this).children('.colorBox').css({"background-color":"#"+$(this).children('.smallLengthText').val()});
                        });
                        $(':input', '#sizeOptionsDiv').liveChange(updatePreview);
                        selectedTemplate = $('#templateId').val();
                        if (sizeIdValue) {
                            $('#templateId').empty();
                            UI.Data.get(
                                    'creativesTemplatesForSize', {
                                        "sizeId": sizeIdValue,
                                        "accountTypeId": ${advertiserContext.account.accountType.id}
                                    },
                                    updateTemplate);
                        } else {
                            UI.Data.Options.replaceWith('templateId', ['form.select.pleaseSelect']);
                            $('#optionsDiv').hide();
                            selectVisualCategories();
                            updatePreview();
                        }
                        handleTnsObjects();
                     }
                );
        });

        <s:if test="selectedTemplate != null && selectedTemplate.categories.size > 0">
            $('#visualCategoriesLabel').html('<span class="simpleText"><ad:commaWriter items="${selectedTemplate.categories}" var="category" label="name"><c:out value="${ad:localizeName(category.name)}"/></ad:commaWriter></span>');
            $('#visualCategoriesLabelDiv').show();
            $('#visualCategoriesSelect').hide();
            </s:if>
        <s:else>
            $('#visualCategoriesSelect').show();
            $('#visualCategoriesLabelDiv').hide();
        </s:else>
    });

    function updateTemplate(xml){
        UI.Data.Options._update(xml,'templateId',['form.select.pleaseSelect']);
        if(selectedTemplate){
            $('#templateId option[value='+selectedTemplate+']').prop({"selected": true});
            if ($("#templateId").val() != null){
                checkTemplate();
            }
        }
    }

    var targetElementId = null;
    
    function openFileBrowser(id, fileTypes){
        targetElementId = id;

        var cdStr   = '';
        if (document.getElementById(targetElementId) && document.getElementById(targetElementId).value !== ''){
            var path    = document.getElementById(targetElementId).value;
            path    = path.substring(path[0] !== undefined && path[0] === '/' ? 1 : 0, path.lastIndexOf("/")+1);
            cdStr   = (path !== '' && path !== '/') ? '&currDirStr='+encodeURIComponent(path):'';
        }

        if (id.indexOf(${imageFileId}) > 0) {
            window.open('${_context}/fileman/fileManager.action?mode=textAd&accountId=${advertiserContext.advertiserId}'+cdStr, 'filebrowser','scrollbars=yes,width=800,height=600');
        } else {
            var account = '&accountId=' + '${advertiserContext.advertiserId}';
            window.open('${_context}/fileman/fileManager.action?id=' + id +
                '&mode=creative' + account + '&fileTypes=' + fileTypes+cdStr,'filebrowser','width=820,height=600,resizable=yes,scrollbars=yes');
        }
    }

    function checkTemplate(){
        if (($('#templateId').val() > 0) && $('#sizeId').val()) {
            $('#optionsDiv').show();
            var tokenVals   = $('#optionsDiv').formKeeper({'key': 'optionsDiv', 'useDataNames': true}).formKeeper('serialize'),
                storedVals  = $.parseJSON(sessionStorage.getItem('optionsDiv'));

            if (storedVals) {
                storedVals  = $.extend(storedVals, tokenVals);
            } else {
                storedVals  = tokenVals;
            }
            sessionStorage.setItem('optionsDiv', JSON.stringify(storedVals));

            $('#optionsDiv')
                .html('<h3 class="level1">${ad:formatMessage("creative.options.loading")}</h3>')
                .load(
                    '${_context}/creative/changeTemplate.action',
                    $('#saveForm').serializeArray(), 
                    function(){
                        $('.collapsible').collapsible();
                        $(this).formKeeper('restore').find('.colorInput').each(function(){
                            $(this).children('.colorBox').css({"background-color":"#"+$(this).children('.smallLengthText').val()});
                        });
                        updatePreview();
                        $(':input', '#optionsDiv').liveChange(updatePreview);
                    }
                );
        }else{
            $('#optionsDiv').html('');
            $('#innerDiv').hide();
            selectVisualCategories();
            updatePreview();
        }
    }

    function selectVisualCategories() {
        $('#visualCategoriesSelect').show();
        $('#visualCategoriesLabelDiv').hide();
    }

    $(function() {
        updatePreview();
        $(':input', '#optionsDiv, #sizeOptionsDiv').liveChange(updatePreview);
    });

    function updatePreview() {
        new UI.AjaxLoader().switchOff();
        $('#ajax_loading_img').show();
        $.ajax({
            type: 'POST',
            url: '/liveCreativePreviewGenerator.action',
            dataType: 'text',
            data: prepareData(),
            success : previewSuccess,
            error: previewError,
            waitHolder: $('#creativePreview')
        });
    }

    function prepareData(){
        return $("#saveForm :input").serializeArray();
    }

    function previewError(jqXHR, textStatus, errorThrown) {
        $('#ajax_loading_img').hide();
        $('#previewDivId').html('<fmt:message key="creative.previewIsNotAvailable"/>');

        new UI.AjaxLoader().switchOn();
    }

    function previewSuccess(data, textStatus) {
        $('#ajax_loading_img').hide();
        $('#previewDivId').html(data);

        new UI.AjaxLoader().switchOn();
    }
    
    <c:choose>
        <c:when test="${account.country.countryCode == 'RU' && ad:isInternal()}">
            var showTnsBrand = true; 
        </c:when>
        <c:otherwise>
            var showTnsBrand = false;
        </c:otherwise>
    </c:choose>
    
    var templateIds= [];
    <c:forEach items="${templateIds}" var="id">
    templateIds.push('${id}');
    </c:forEach>

    function handleTnsObjects() {
        if (showTnsBrand){
            if (templateIds.indexOf($('#templateId option:selected').val()) !== -1) {
                $('#tnsBrand').show();
            } else {
                $('#tnsBrand').hide();
                $('#tnsBrandId').extAutocomplete('clear');
            }
        } else {
            $('#tnsBrand').hide();
            $('#tnsBrandId').extAutocomplete('clear');
        }
    }
</script>
<div class="wrapper">
    <s:actionerror/>
</div>
<s:form action="%{#request.moduleName}/%{model.id != null?'update':'create'}" id="saveForm">

    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <s:hidden name="account.id"/>
    <s:hidden name="account.accountType.id"/>
    <s:hidden name="campaignId"/>
    
    <c:if test="${not empty ccgId}">
        <s:iterator value="ccgId" var="curCcgId">
            <s:hidden name="ccgId" value="%{#curCcgId}"/>
        </s:iterator>
    </c:if>

    <ui:pageHeadingByTitle/>
    
    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:fielderror><s:param value="'options'"/></s:fielderror>
    </ui:errorsBlock>

    <ui:section>
        <ui:fieldGroup>
            <ui:field labelKey="creative.name" labelForId="name" required="true" errors="name">
                <s:textfield name="name" cssClass="middleLengthText" maxlength="150"/>
            </ui:field>

            <ui:field labelKey="creative.size" labelForId="sizeId" required="true" errors="size">
            <c:choose>
                <c:when test="${textCreative}">
                    <ui:displayStatus displayStatus="${size.displayStatus}">${ad:localizeName(size.name)}</ui:displayStatus>
                    <s:hidden name="size.id" id="sizeId"/>
                </c:when>
                <c:otherwise>
                <s:select name="size.id" id="sizeId" cssClass="middleLengthText"
                  headerValue="%{getText('form.select.pleaseSelect')}" headerKey=""
                  list="sizes"
                  listKey="id" listValue="name" value="size.id"/>
                </c:otherwise>
            </c:choose>
            </ui:field>
            
            <ui:field labelKey="creative.template" labelForId="templateId" required="true" errors="template">
            <c:choose>
                <c:when test="${textCreative}">
                    <ui:displayStatus displayStatus="${template.displayStatus}">${ad:localizeName(template.name)}</ui:displayStatus>
                    <s:hidden name="template.id" id="templateId"/>
                </c:when>
                <c:otherwise>
                <s:select name="template.id" id="templateId" cssClass="middleLengthText"
                  headerValue="%{getText('form.select.pleaseSelect')}" headerKey=""
                  list="templates"
                  listKey="id" listValue="name" value="template.id"
                  />
                </c:otherwise>
            </c:choose>
            </ui:field>
            
            <ui:field id="tnsBrand" labelKey="creative.tnsBrand">
                <ui:autocomplete id="tnsBrandId" 
                    source="Autocomplete.tnsBrandId.getBrands"
                    defaultValue="${tnsBrand == null ? account.tnsBrand.id : tnsBrand.id}"
                    defaultLabel="${tnsBrand.name== null ? account.tnsBrand.name : tnsBrand.name}"
                    cssClass="middleLengthText" isMultiSelect="false"
                    minLength="1" editable="true" maxLength="100"
                    addInLowercase="false">
                    <script type="text/javascript">
                        Autocomplete.tnsBrandId.getBrands = function(request, response){
                            UI.Data.get('getTnsBrand', {"query": request.term}, function(data){
                                var opts = $.map($('tnsBrand', data), function(el){
                                    return new $.custom.extAutocomplete.Option($('name', el).text(), $('id', el).text());
                                });
                                response(opts);
                            });
                        };
                    </script>
                </ui:autocomplete>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <div class="logicalBlock" id="optionsDiv">
        <%@ include file="/creative/expandableSectionEdit.jsp"%>

        <s:hidden name="template.version"/>
        <s:set var="groups" value="template.advertiserOptionGroups"/>
        <c:set var="optionTitleKey" value="CreativeSize.options"/>
        <%@ include file="/admin/option/optionValuesEdit.jsp"%>
    </div>

    <div class="logicalBlock" id="sizeOptionsDiv">
        <s:hidden name="size.version"/>
        <s:set var="groups" value="size.advertiserOptionGroups"/>
        <c:set var="optionTitleKey" value="CreativeSize.size.options"/>
        <%@ include file="/admin/option/optionValuesEdit.jsp"%>
    </div>

    <ui:section id="creativePreview" titleKey="creative.preview">
        <div class="ajax_loader_container">
            <div class="ajax_loader hide" id="ajax_loading_img"></div>
            <div id="previewDivId"/>
        </div>
    </ui:section>

    <c:choose>
    <c:when test="${textCreative}">
        <jsp:include page="categoriesEdit.jsp"/>
    </c:when>
    <c:otherwise>
        <ui:section titleKey="creative.categories" cssStyle="min-width:100%;">
            <jsp:include page="categoriesEdit.jsp"/>
        </ui:section>
    </c:otherwise>
    </c:choose>

    <c:if test="${textCreative}">
        <ui:section titleKey="textAd.tagSizes" id="tagSizes">
            <div>
                <fmt:message key="textAd.tagSizes.selectionText"/>
            </div>
        
            <ui:fieldGroup>
            <ui:field>
            <label for="enableAllAvailableSizes" class="parent">
                <s:checkbox name="enableAllAvailableSizes" id="enableAllAvailableSizes"/>
                <fmt:message key="textAd.tagSizes.allAvailableSizes"/>
            </label>
        
            <div class="children" style="margin-left: 2em;">
        
                <c:forEach var="sizeType" items="${sizeTypes}">
                    <c:set var="showChildren" value="${sizeType.advertiserSizeSelection == 'TYPE_AND_SIZE_LEVEL'}"/>
                    <s:set var="checked" value="%{model.sizeTypes.{^ #this.id == #attr.sizeType.id}}"/>
                    <label class="parent  ${showChildren ? '' : 'leaf'}">
                        <input type="checkbox"
                               value="${sizeType.id}"
                               ${not empty checked ? "checked=\"checked22\"" : ""}
                               class="creativeSizeTypes"
                               name="sizeTypes(${sizeType.id}l).id">
                        <c:out value="${ad:localizeName(sizeType.name)}"/>
                    </label>
        
                    <div class="children" style="margin-left: 2em;">
                        <c:if test="${sizeType.advertiserSizeSelection == 'TYPE_AND_SIZE_LEVEL'}">
                            <c:forEach var="crSize" items="${sizeType.sizes}">
                                <div>
                                    <label class="leaf">
                                        <s:set var="checked" value="%{model.tagSizes.{^ #this.id == #attr.crSize.id}}"/>
                                        <input type="checkbox"
                                               value="${crSize.id}"
                                               ${not empty checked ? "checked=\"checked\"" : ""}
                                               class="creativeSizes"
                                               name="tagSizes(${crSize.id}l).id">
                                        <c:out value="${ad:localizeName(crSize.name)}"/>
                                    </label>
                                </div>
                            </c:forEach>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
            </ui:field>
            </ui:fieldGroup>
        
            <c:forEach var="sizeType" items="${sizeTypes}">
                <c:forEach var="crSize" items="${sizeType.sizes}">
                    <s:set var="groups" value="#attr.crSize.advertiserOptionGroups"/>
                    <fmt:message var="optionTitle" key="textAd.tagSizes.options">
                        <fmt:param>${ad:localizeName(sizeType.name)}</fmt:param>
                        <fmt:param>${ad:localizeName(crSize.name)}</fmt:param>
                    </fmt:message>
                    <div class="optionsHolder" ui-sizeType="${sizeType.id}" ui-size="${crSize.id}" style="display: none">
                        <%@ include file="/admin/option/optionValuesEdit.jsp"%>
                    </div>
                </c:forEach>
            </c:forEach>
        
        </ui:section>
        
        <script type="text/javascript">
        $(function(){

            $('.parent').on('change', ':checkbox', function(e){
                $(this).closest('.parent')
                    .next('.children')
                    .find(':checkbox')
                    .prop('checked', $(this).prop('checked'));
            });
            $('.children').on('change', ':checkbox', function(e){
                e.stopPropagation();

                function checkUp(jqParent, func){
                    var up  = jqParent.closest('.children').prev('.parent').find(':checkbox');
                    if (up.length) {
                        up.prop('checked', func(jqParent));
                        checkUp(up, func);
                    }
                }

                if ($(this).prop('checked')) {
                    checkUp($(this), function(a){
                        var b = true;
                        a.closest('.children')
                                .find(':checkbox')
                                .each(function(){
                                    b = b && $(this).prop('checked');
                                });
                        return b;
                    });
                } else {
                    checkUp($(this), function(){return false;});
                }
            });

            var initCheckboxes = function(checkboxes, toggleFunction) {
                checkboxes.on('change', toggleFunction);
                checkboxes.filter(':checked').each(toggleFunction);
            };

            initCheckboxes($('#enableAllAvailableSizes'), function() {
                $('.optionsHolder').toggle($(this).prop('checked'));
            });
            initCheckboxes($('#tagSizes').find('input.creativeSizeTypes'), function() {
                $('.optionsHolder[ui-sizeType=' + $(this).val() + ']').toggle($(this).prop('checked'));
            });
            initCheckboxes($('#tagSizes').find('input.creativeSizes'), function() {
                $('.optionsHolder[ui-size=' + $(this).val() + ']').toggle($(this).prop('checked'));
            });

            $('#tagSizes').find('input:checked').each(function() {
                $(this).closest('.parent').next('.children').find('input[type="checkbox"]').prop('checked', true);
            });

            $('#saveForm').submit(function() {
                $('.optionsHolder:not(:visible)').remove();
            });
        });
        </script>

    </c:if>

<c:if test="${not empty ccgId}">
    <s:if test="%{canUpdateWeight()}">
        <ui:section>
            <ui:fieldGroup>
                <ui:field labelKey="weight" labelForId="weight" tipKey="creative.weight.note" errors="weight" required="true">
                    <s:textfield name="campaignCreative.weight" cssClass="smallLengthText" maxlength="10"/>
                </ui:field>
            </ui:fieldGroup>
        </ui:section>
    </s:if>
    <ui:frequencyCapEdit fcPropertyName="campaignCreative.frequencyCap" />
</c:if>

    <div class="wrapper">
        <ui:button message="form.save" type="submit" />
        <c:choose>
            <c:when test="${not empty campaignId}">
                <ui:button message="form.cancel" onclick="location='${_context}/campaign/view.action?id=${campaignId}';" type="button"/>
            </c:when>
            <c:when test="${not empty ccgId}">
                <ui:button message="form.cancel" onclick="location='${_context}/campaign/group/view.action?id=${ccgId[0]}';" type="button"/>
            </c:when>
            <c:when test="${empty id}">
                <ui:button message="form.cancel" onclick="location='main.action?advertiserId=${account.id}';" type="button"/>
            </c:when>
            <c:otherwise>
                <ui:button message="form.cancel" onclick="location='view.action?id=${id}';" type="button"/>
            </c:otherwise>
        </c:choose>
    </div>
</s:form>
<script type="text/javascript">
    $(function() {
        handleTnsObjects();
    });
</script> 
