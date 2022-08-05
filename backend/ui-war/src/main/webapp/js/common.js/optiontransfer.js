(function(w, u, $){

    if (w.UI === u) {
        w.UI = {};
    }

    w.UI.Optiontransfer   = {
        '_fillSelectObj':   function(sourceObjsWValuesAndTexts, targetSel){
            $.each(sourceObjsWValuesAndTexts, function(){
                targetSel.options[targetSel.options.length] = new Option(this.text, this.value, false, false);
            });
        },
        'sortSelect':   function(target, comparator){
            if (!target.options || target.options.length <= 1) return;
            
            var aryTemp = $.map(target.options, function(n){
                return {'text': n.text, 'value':    n.value};
            });
            
            typeof(comparator) == 'function' ? aryTemp.sort(comparator) : aryTemp.sort(function(a, b){
                return a.value == ''? -1 : a.text > b.text ? 1 : a.text < b.text ? -1 : 0;
            });
            target.length = 0;
            w.UI.Optiontransfer._fillSelectObj(aryTemp, target);
        },
        'selectAllOptions': function(objTargetElement){
            $.each(objTargetElement.options, function(){
                this.selected = !!this.value;
            });
            return false;
        },
        'moveAllOptions':       function(objSourceElement, objTargetElement, allOptionsElement, saveSorting, toSort, onchangeFunction, immovableOptions){
            if (immovableOptions.length) {
                return w.UI.Optiontransfer.moveSelectedOptions(objSourceElement, objTargetElement, allOptionsElement, saveSorting, toSort, onchangeFunction, immovableOptions, true);
            }
            
            if (saveSorting && allOptionsElement){
                objTargetElement.length = 0;
                w.UI.Optiontransfer._fillSelectObj(allOptionsElement.options, objTargetElement)
            }else{
                w.UI.Optiontransfer._fillSelectObj(objSourceElement.options, objTargetElement)
            }
            var wasMoved            = objSourceElement.length != 0;
            objSourceElement.length = 0;
            saveSorting || toSort && w.UI.Optiontransfer.sortSelect(objTargetElement);
            wasMoved && onchangeFunction();
            w.UI.SelTiper.update($(objSourceElement));
        },
        'moveSelectedOptions':  function(objSourceElement, objTargetElement, allOptionsElement, saveSorting, toSort, onchangeFunction, immovableOptions, allOptions){
            var wasMoved = false;

            //looping through source element to find selected options
            $('option', objSourceElement).each(function(){
                if (immovableOptions.length && $.inArray(this.value, immovableOptions) >= 0) {
                    this.selected = false;
                } else if (allOptions) {
                    this.selected = true;
                }
                if (this.selected) {
                    objTargetElement.options[objTargetElement.length] = new Option(this.text, this.value, false, false);
                    $(this).remove();
                    wasMoved = true;
                }
            });
            objSourceElement = w.UI.Util.redrawSelect($(objSourceElement))[0];

            if (!saveSorting){
                toSort && w.UI.Optiontransfer.sortSelect(objTargetElement);
            } else {
                var aryTempValues = $.map(objTargetElement.options, function(n){
                    return n.value;
                });

                objTargetElement.length = 0;

                //sorting target list according to allOptionsElement list
                $.each(allOptionsElement.options, function(){
                    if($.inArray(this.value, aryTempValues) >= 0){
                        objTargetElement.options[objTargetElement.length] = new Option(this.text, this.value, false, false);
                    }
                });
                objTargetElement = w.UI.Util.redrawSelect($(objTargetElement))[0];
            }

            wasMoved && onchangeFunction();
            w.UI.SelTiper.update($(objSourceElement));
        }
    };

    var fSerialize = $.fn.serialize;
    $.fn.serialize = function() {
        var sOut    = '';
        $(this).filter('.optiontransferSelected').each(function(){
            var sName   = $(this).attr('name')+'=';
            $(this).find('option').prop('selected',false).each(function(){
                sOut    += sName + $(this).attr('value') + '&';
            });
        });
        
        var sOld  = fSerialize.apply(this, arguments);
        return sOld === "" ? sOut.slice(0,-1) : sOut+sOld;
    }
    
    var fSerializeArray = $.fn.serializeArray;
    $.fn.serializeArray = function() {
        var aOut    = [];
        $(this).filter('.optiontransferSelected').each(function(){
            var sName   = $(this).attr('name');
            $(this).find('option').prop('selected',false).each(function(){
                aOut.push({
                    "name":     sName,
                    "value":    $(this).attr('value')
                });
            });
        });
        return $.merge(fSerializeArray.apply(this, arguments), aOut);
    }
})(window, undefined, jQuery);