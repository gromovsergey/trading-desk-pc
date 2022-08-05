function sortCompare(a, b){
    var aText = a.text.toLowerCase();
    var bText = b.text.toLowerCase();
    return aText < bText ? -1 : aText > bText ? 1 : 0;
}

function sortSelectByPattern(selId, patternArr){
    var sel = $('#' + selId);
    if (sel.attr('length') < 2) return;
    var arrOpts = $.makeArray(sel[0].options);

    var sortByPattern = function(a, b){
        var idxOfA = $.inArray(a.value, patternArr);
        var idxOfB = $.inArray(b.value, patternArr);
        return idxOfA < idxOfB ? -1 : idxOfA > idxOfB ? 1 : 0;
    };

    arrOpts.sort(patternArr ? sortByPattern : sortCompare);

    //bugfix for Opera - it increases length after options re-assignment
    sel.html('');
    $.each(arrOpts, function(i){
        sel[0].options[i] = new Option(this.text, this.value);
    });
}

function fillSelect(selectId, xmlId, respXML){
    var reportColumns = $(xmlId, respXML);
    if (!reportColumns.length) return;

    var selectContainer = $('#' + selectId);
    selectContainer = UI.Util.cleanSelect(selectContainer, true);

    reportColumns.children().each(function(){
        addItem(selectContainer[0], $(this).attr('value'), $(this).attr('key'));
    });
}

function addItem(sel, text, value){
    if (!value) return;

    // If item already exists then just return
    for (var i = 0; i < sel.length; i++)
        if (sel.options[i].value == value) return;

    sel.options[sel.length] = new Option(text, value, false, false);
}

function reportSubmit(form) {
    if(!form.outputCols.length) {
        alert($.localize("report.invalid.output.columns"));
        return false;
    }

    if(!form.metricCols.length) {
        alert($.localize("report.invalid.metrics.columns"));
        return false;
    }

    $('option', form.outputCols).each(function(){
        this.selected = true;
    });

    $('option', form.metricCols).each(function(){
        this.selected = true;
    });

    $('option', form.exportCols).each(function(){
        this.selected = true;
    });
}
