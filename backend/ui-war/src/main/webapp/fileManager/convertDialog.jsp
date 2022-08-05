<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<form id="convertForm">
    <s:hidden name="sourceFileName" id="sourceFileName"/>
    <s:hidden name="clickMacro" id="clickMacro"/>
    <ui:fieldGroup>
        <ui:field>
            <label class="withInput">
                <s:checkbox name="withoutClickUrlMacro" id="withoutClickUrlMacro" />
                <s:text name="fileman.convertSWFDialog.addWithoutClickUrlMacro"/>
            </label>
        </ui:field>

        <ui:field labelKey="fileman.convertSWFDialog.filename" cssClass="withoutMacro">
            <s:textfield name="targetFileName" id="targetFileName" size="30"/>
            <s:fielderror fieldName="targetFileName"/>
        </ui:field>

        <ui:field>
            <label class="withInput">
                <s:checkbox name="withClickUrlMacro" id="withClickUrlMacro" />
                <s:text name="fileman.convertSWFDialog.addWithClickUrlMacro"/>
            </label>
        </ui:field>

        <ui:field labelKey="fileman.convertSWFDialog.filename" cssClass="withMacro">
            <s:textfield name="targetFileNameWithMacro" id="targetFileNameWithMacro" size="30"/>
            <s:fielderror fieldName="targetFileNameWithMacro"/>
        </ui:field>

        <ui:field labelKey="fileman.convertSWFDialog.clickTagSpelling" id="clickTagSpelling" cssClass="withMacro">
            <label class="withInput">
                <input type="radio" name="clickTagSpelling" value="clickTag" id="clickTag"/>
                <s:text name="fileman.convertSWFDialog.clickTagSpellingClickTag"/>
            </label>
            <label class="withInput">
                <input type="radio" name="clickTagSpelling" value="link1" id="link1" checked/>
                <s:text name="fileman.convertSWFDialog.clickTagSpellingLink1"/>
            </label>
            <div class="rowWithButton">
                <label class="withInput">
                    <input type="radio" name="clickTagSpelling" id="clickTagSpellingOther"/>
                    <s:text name="fileman.convertSWFDialog.clickTagSpellingOther"/>
                    <input type="text" id="clickTagSpellingCustom"/>
                </label>
            </div>
            <s:fielderror fieldName="clickTagSpelling"/>
        </ui:field>
    </ui:fieldGroup>
</form>

<div id="convertResult">
    <s:fielderror fieldName="sourceFileName"/>
    <s:fielderror fieldName="swiffy"/>
</div>

<script>
    function prepareConversion() {
        if ($("#withClickUrlMacro").prop("checked")) {
            if ($("#clickTagSpellingOther").prop("checked")) {
                $("#clickMacro").val($("#clickTagSpellingCustom").val());
            } else {
                $("#clickMacro").val($("input[name=clickTagSpelling]:radio:checked").val());
            }
        }
    }

    function changeWithoutMacroFields() {
        if ($("#withoutClickUrlMacro").prop("checked")) {
            $(".withoutMacro").show();
        } else {
            $(".withoutMacro").hide();
        }
    }
    function changeWithMacroFields() {
        if ($("#withClickUrlMacro").prop("checked")) {
            $(".withMacro").show();
        } else {
            $(".withMacro").hide();
        }
    }

    $(function() {
        $("#withoutClickUrlMacro").on("change", function() {
            changeWithoutMacroFields();
        });
        $("#withClickUrlMacro").on("change", function() {
            changeWithMacroFields();
        });
        changeWithMacroFields();
        changeWithoutMacroFields();

        var clickTagMacro = $("#clickMacro").val();
        if (clickTagMacro == '' || clickTagMacro == 'link1') {
            $("#link1").prop("checked", true);
        } else if (clickTagMacro == 'clickTag') {
            $("#clickTag").prop("checked", true);
        } else {
            $("#clickTagSpellingOther").prop("checked", true);
            $("#clickTagSpellingCustom").val(clickTagMacro);

        }
    })
</script>
