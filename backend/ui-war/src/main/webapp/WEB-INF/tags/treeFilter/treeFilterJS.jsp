<script type="text/javascript">
function clickButton${param.treeId}(btn, id, type) {
    btn.parentNode.className = (btn.parentNode.className=='treeOpen') ? "treeClosed" : "treeOpen";

    if (btn.parentNode.className=='treeOpen') {
        getOptions${param.treeId}(id, type, 'data_' + type + id, null);
    }
}

// Checkbox functions
function switchAll(elem) {
    if (elem.checked) {
        enable(elem);
    } else {
        disable(elem);
    }
    switchParents(elem);
    switchChildren(elem);
}

function switchChildren(elem) {
    var $elem   = $(elem);
    $elem.closest('li, div.treeFilterContainer.withRoot').find('ul:first > li :checkbox').each(function(){
        $(this).prop({'checked': $elem.prop('checked')});
        if (elem.checked) {
            enable(this);
        } else {
            disable(this);
        }
    });
}

function switchParents(elem) {
    var jqElem       = $(elem),
    container       = jqElem.closest('ul.treeClickable'),
    parentContainer = container.closest('li[class^="tree"], div.treeFilterContainer.withRoot').eq(0),
    parentCheckbox  = {};
    
    if (container.length === 0) return;

    if (jqElem.prop('checked')) {
        var allSiblingsChecked = true,
        jqFirstChild    = container.children('li').find(':checkbox:first').each(function() {
            allSiblingsChecked = allSiblingsChecked && this.checked;
        });
        if (allSiblingsChecked) {
            jqParent = parentContainer.find(':checkbox:first');
            if (jqParent.length && !jqParent.prop('checked')) {
                jqParent.prop({'checked':true});
                switchParents(jqParent[0]);
                enable(jqElem[0]);
                jqFirstChild.each(function(){
                    enable(this);
                });
            }
        }
    } else {
        jqParent = parentContainer.find(':checkbox:first');
        if (jqParent.length && jqParent.prop('checked')) {
            jqParent.prop({'checked':false});
            switchParents(jqParent[0]);
            disable(jqElem[0]);
            container.siblings('li').find(':checkbox:first').each(function(){
                disable(this);
            });
        }
    }
}

function disable(elem) {
    var name = $(elem).attr('name');
    if (name === undefined) return;
    if (name.indexOf('_disabled') < 0) {
        $(elem).attr({name:name + '_disabled'});
    }
}

function enable(elem) {
    var name = $(elem).attr('name');
    if (name === undefined) return;
    if (name.indexOf('_disabled') >= 0) {
        var pos = name.indexOf("_disabled");
        $(elem).attr({name:name.substr(0, pos)});
    }
}
</script>