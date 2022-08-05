$.widget('custom.extAutocomplete', $.ui.autocomplete, {
    options : $.extend({}, $.ui.autocomplete.prototype.options, {
        minLength : 0,
        noResultsText : 'no results',
        addNewText : 'add new',
        source : [],
        pick : function(event, data){}, 
        select: function(event, data) {
            var self = $(this).data('extAutocomplete');
            return self._onSelect.apply(self, arguments);
        },
        addInLowercase : false,
        defaultLabel : '',
        defaultValue : ''
    }),
    _create : function(){ // redefined method
        $.ui.autocomplete.prototype._create.apply(this);
        
        var self = this;
        this._jValKeeper = this._createValKeeper();
        
        this.element.attr({name : this.element.attr('name') + '_text'});
        
        $('<div>')
            .addClass('autocompleteContainer')
            .insertBefore(this.element)
            .css({width : this.element.width()})
            .append(this._jValKeeper)
            .append(this.element);
        var oSelected   = this._getSelectedFromSource();
        if (oSelected.selected !== true) {
            oSelected.label = this.options.defaultLabel;
            oSelected.value = this.options.defaultValue;
        }
        this._setVal(oSelected);
        this._setEventHandlers();
        this._addComboButton();
        
        this.menu.element.menu('option', 'focus', function(event, ui){ // redefined assignment of 'focus' event to menu
            var item = ui.item.data('item.autocomplete');
            if (item.isService) return;
            if (false !== self._trigger('focus', event, {item: item})) {
                // use value to match what will end up in the input, if it was a key event
                if (/^key/.test(event.originalEvent.type)) {
                    self.element.val(item.label);
                }
            }
        });
    },
    _setEventHandlers : function(){
        var self = this; 
        
        this.element.on('paste.autocomplete', function(e){
            if (self.element.val() !== '') {
                self.element.trigger('keydown.autocomplete');
            }
        });
        
        this.element.on('blur.extAutocomplete', function(e){ // removes data from hidden select if input has no value
            var sVal    = self.element.val();
            if (self.element.val() !== self._pickedOpt.label){
                self.clear();
                self.element.val(sVal);
            }
        });
        
        this.element.on('click.extAutocomplete', function(e){
            if (self.element.val() !== '') {
                self._toggleMenu.call(self, e);
                self.element.focus();
            }
        });
    },
    _search: function(value) { // redefined method. Preventing error, when entering too long text
        if(value.length < 1000){
            $.ui.autocomplete.prototype._search.apply(this, arguments);
        }
    },
    _createValKeeper : function(){
        return $('<input type="hidden" name="'+this.element.attr('id')+'">');
    },
    _onSelect : function(event, data){
        if(data.item.isService){
            if(data.item.isAdding){
                var newOpt = new $.custom.extAutocomplete.Option(this.element.val(), this.element.val());
                
                this._setVal(newOpt);
                data.item = newOpt;
                this._trigger('pick', event, data);
            }else{
                return false;
            }
        }else{
            this._setVal(data.item);
            this._trigger('pick', event, data);
        }
        
        return false;
    },
    _getSelectedFromSource : function(){
        var resOpt = new $.custom.extAutocomplete.Option();
        
        if($.isArray(this.options.source)){
            $.each(this.options.source, function(i, opt){
                if(opt.selected){
                    resOpt = opt;
                    return false;
                }
            })
        }
        return resOpt;
    },
    _toggleMenu : function(event){
        if(this.menu.element.is(':visible')){
            this.close(event);
        }else{
            this.search(this.element.val());
        }
    },
    _addComboButton : function(){
        var self = this;
        var comboButton = $('<div>')
            .addClass('comboButton')
            .insertAfter(this.element)
            .click(function(event){
                self._toggleMenu.call(self, event);
                self.element.focus();
            });
            
        var buttWidth = parseFloat(comboButton.css('width'));
        this.element.width(this.element.width() - buttWidth);
    },
    __response : function(opts){ // redefined method
        opts = $.makeArray(opts);
        var optToAdd = null;
        if(!opts.length){
            optToAdd = new $.custom.extAutocomplete.Option(this.options.noResultsText, '', false, false, true);
        }
        
        optToAdd && opts.unshift(optToAdd);
        $.ui.autocomplete.prototype.__response.call(this, opts);
    },
    _renderItem: function(ul, item) { // redefined method
        var searchingStr    = UI.Util.escapeRegexp(this.element.val()),
        editedLabel         = item.label;

        if (!item.isService && searchingStr !== ''){
            editedLabel = editedLabel.replace(new RegExp('('+searchingStr+')',"gi"),'<strong>$1</strong>').replace(/&/g,'&amp;');
        }

        var itemContent = $("<a />")
            .html(editedLabel);
            
        return $("<li />")
            .data("item.autocomplete", item)
            .append(itemContent)
            .addClass(item.isAdding ? 'creator' : '')
            .appendTo(ul);
    },
    getVal : function(){
        return this._jValKeeper.val();
    },
    _setVal : function(opt){
        this._pickedOpt = opt;
        
        this.element.val(opt.label);
        this._jValKeeper.val(opt.value);
    },
    clear : function(){
        this._setVal(new $.custom.extAutocomplete.Option());
    }
});

$.custom.extAutocomplete.Option = function(label, value, selected, isAdding, isService){
    this.label = label || '';
    this.value = value || '';
    this.selected = !!selected;
    this.isAdding = !!isAdding;
    this.isService = !!this.isAdding || !!isService;
}




$.widget('custom.tokenizer', $.custom.extAutocomplete, {
    options : $.extend({}, $.custom.extAutocomplete.prototype.options, {
        pickedOpts : [],
        editable : false,
        preventDoubling : true, 
        paramValueName : 'value',  
        paramTextName : 'text',  
        keepText : false, 
        optionClose : function(event, data){}, 
        select: function(event, data) {
            var self = $(this).data('tokenizer');
            return self._onSelect.apply(self, arguments);
        }
    }), 
    _create : function(){
        $.custom.extAutocomplete.prototype._create.apply(this);
        var self = this;
        
        this.element
            .unbind('blur.extAutocomplete')
            .bind('keydown.tokenizer', function(event){
                if (self.options.disabled || self.element.prop('readonly')) {
                    return;
                }
                if(event.keyCode == $.ui.keyCode.ENTER){
                    if($.trim(self.element.val())){
                        if(self._possibleToAdd && !self.selectedItem){
                            var opt = new $.custom.extAutocomplete.Option(self.element.val(), self.element.val());
                            self._addPicked(event, opt);
                            self.close(event);
                        }
                        event.preventDefault();
                    }
                }
            });
        
        var jTokenKeeper = $('<div>')
            .insertBefore(this.element)
            .tokenKeeper({
                name : this.element.attr('id'), 
                paramValueName : this.options.paramValueName,  
                paramTextName : this.options.paramTextName, 
                keepText : this.options.keepText, 
                optionAdd : function(e, data){
                    self._possibleToAdd = false;
                    self.element.val('');
                    self.element.trigger('keydown.autocomplete');
                }, 
                optionRemove : function(event, data){
                    self._delPicked(data.option)
                    event.which && self._trigger('optionClose', event, data);
                }
            });
            
        this._dataKeeper = jTokenKeeper.data('tokenKeeper');
        var pickedOpts = this.options.pickedOpts.concat(this._getSelectedFromSource());
        this._setOption('pickedOpts', pickedOpts);
    },
    _onSelect : function(event, data){
        var opt = data.item;
        
        if(data.item.isService){
            if(!data.item.isAdding) return false;
            opt = new $.custom.extAutocomplete.Option(this.element.val(), this.element.val());
        }
        this._addPicked(event, opt);
        return false;
    }, 
    _getSelectedFromSource : function(){
        if(!$.isArray(this.options.source)) return [];
        
        return $.map(this.options.source, function(el){
            return el.selected ? el : null;
        })
    },
    _createValKeeper : function(){
        return $();
    },
    _addComboButton : function(){
    },
    __response : function(opts){ // redefined method
        opts = $.makeArray(opts);
        var textIsEntered = $.trim(this.element.val());
        var optToAdd = null;
        var pickedVal   = this.element.val();
        if (this.options.addInLowercase) {
            pickedVal   = pickedVal.toLowerCase();
        }
        var textIsPicked = this._isSameTextInPicked(pickedVal);
        this._possibleToAdd = false;
        
        if(this.options.editable){
            if (textIsEntered){
                if (opts.length && !textIsPicked){
                    if(!this._isSameTextInFound(opts)){
                        this._possibleToAdd = true;
                        optToAdd = new $.custom.extAutocomplete.Option(this.options.addNewText, '', false, true);
                    }
                } else {
                    if (!textIsPicked) {
                        this._possibleToAdd = true;
                        optToAdd = new $.custom.extAutocomplete.Option(this.options.noResultsText + ' (' + this.options.addNewText + ')', '', false, true);
                    } else if (!this._getFilteredOpts(opts).length) {
                        optToAdd = new $.custom.extAutocomplete.Option(this.options.noResultsText, '', false, false, true);
                    }
                }
            }
        } else {
            var aOptsFiltered   = this._getFilteredOpts(opts);
            if(!aOptsFiltered.length){
                optToAdd = new $.custom.extAutocomplete.Option(this.options.noResultsText, '', false, false, true);
            }
        }

        optToAdd && opts.unshift(optToAdd);
        $.custom.extAutocomplete.prototype.__response.call(this, opts);
    },
    _addPicked : function(event, opt){
        if (event && this._trigger('pick', event, {item : opt}) === false) return;
        if (opt.label === '' && opt.value === '') return;
        
        if (this.options.addInLowercase) {
            opt.label   = opt.label.toLowerCase();
            opt.value   = opt.value.toLowerCase();
        }
        
        this.options.pickedOpts.push(opt);
        this._dataKeeper.addOption(opt);
    },
    _delPicked : function(pickedOpt){
        this.options.pickedOpts = $.grep(this.options.pickedOpts, function(opt, i){
            return (opt !== pickedOpt);
        });
    },
    _replacePickedOpts : function(pickedOpts){
        var self = this;
        
        this.clear();
        $.each(pickedOpts, function(i, el){
            self._addPicked(null, el);
        })
    },
    _renderMenu: function(ul, items) { // redefined method
        var self = this;
        
        if(this.options.preventDoubling){
            items = this._getFilteredOpts(items);
        }
        
        $.each(items, function(index, item) {
            self._renderItem(ul, item);
        });
    },
    _getFilteredOpts : function(availOpts){ // removes already selected options from array
        var pickedOpts = this.options.pickedOpts;
        
        return $.grep(availOpts, function(availOpt, i){
            var res = true;
            $.each(pickedOpts, function(idx, picked){
                if(picked.value == availOpt.value){
                    res = false;
                    return false;
                }
            });
            return res;
        });
    },
    _setOption: function(key, value) {
        if(key === 'pickedOpts'){
            this._replacePickedOpts(value);
        }else{
            $.ui.autocomplete.prototype._setOption.apply(this, arguments);
        }
    },
    _isSameTextInFound : function(opts){ // if found absolutely identical text
        var searchingText = this.element.val();
        var res = false;
        
        $.each(opts, function(idx, opt){
            if(opt.label == searchingText){
                res = true;
                return false;
            }
        })
        return res;
    },
    _isSameTextInPicked : function(text){
        var res = false;
        
        $.each(this.options.pickedOpts, function(i, opt){
            if(opt.label == text){
                res = true;
                return false;
            }
        })
        return res;
    },
    getVal : function(){
        return this._dataKeeper.getData();
    },
    clear : function(){
        this.element.val('');
        this._dataKeeper.clear();
    }
});


$.widget('tokenizer.tokenKeeper', {
    options : {
        name : '', 
        paramValueName : 'value',  
        paramTextName : 'text',  
        keepText : false, 
        optionRemove : function(event, option){}, 
        optionAdd : function(event, option){}
    }, 
    _create : function(){
        this._chosenOptions = [];
        
        this.element.addClass('tokenKeeper');
    }, 
    addOption : function(option){
        var newItem = $('<div>');
        var self = this;
        
        this.element.append(newItem);
        
        newItem.chosenOption({
            name : this.options.name, 
            index : this._chosenOptions.length, 
            paramValueName : this.options.paramValueName,  
            paramTextName : this.options.paramTextName, 
            keepText : this.options.keepText, 
            label : option.label, 
            value : option.value,  
            close : function(event){
                self._removeOption($(this).data('chosenOption'));
                self._trigger('optionRemove', event, {option:option});
            }
        });
        this._chosenOptions.push(newItem.data('chosenOption'));
        
        this._trigger('optionAdd', null, {option:option});
    },
    _removeOption : function(item){
        this._chosenOptions = $.map(this._chosenOptions, function(el, i){
            if(el != item) return el;
        });
        
        $.each(this._chosenOptions, function(i, el){
            el.setIndex(i);
        });
    },
    getData : function(){
        return $.map(this._chosenOptions, function(el, i){
            return el.getData();
        })
    },
    clear : function(){
        $.each(this._chosenOptions, function(i, el){
            el.destroy();
        });
    }
});



$.widget('tokenizer.chosenOption', {
    options : {
        name : '', 
        index : 0, 
        label : '', 
        value : '',  
        paramValueName : 'value',  
        paramTextName : 'text',  
        keepText : false, 
        close : function(event){}
    }, 
    _create : function(){
        if(!this.options.label) return;
        
        var self = this;
        var jDataKeepers = this._createDataKeepers();
        var maxWidth = $('input:text', this.element.closest('.autocompleteContainer')).width() - 30;
        var textContainer = $('<div>').addClass('textContainer')
            .text(this.options.label);
        var threeDots = $('<div>').addClass('threeDots')
            .text('\u2026')
            .hide();
        
        this.element.addClass('chosenOption')
            .css({maxWidth : maxWidth})
            .append(textContainer)
            .append(threeDots)
            .append('<div class="close">')
            .append(jDataKeepers);
            
        if(this.element[0].scrollWidth - this.element.width() > 20){
            threeDots.show();
            this.element.attr({title : this.options.label});
        }
        
        $('.close', this.element).click(function(event){
            self.destroy(event);
        });
    },
    _createDataKeepers : function(){
        this._jTextKeeper = (this.options.keepText
                ? $('<input type="hidden">').attr({name : this.options.name + '[' + this.options.index + '].' + this.options.paramTextName})
                : $())
            .val(this.options.label);
            
        this._jValKeeper = $('<input type="hidden">')
            .attr({name : this.options.keepText 
                ? this.options.name + '[' + this.options.index + '].' + this.options.paramValueName
                : this.options.name
            })
            .val(this.options.value);
            
        return this._jValKeeper.add(this._jTextKeeper);
    },
    setIndex : function(idx){
        if(!this.options.keepText) return;
        
        var textKeeperName = this._jTextKeeper.attr('name');
        var valKeeperName = this._jValKeeper.attr('name');
        
        var startOfIdx = this.options.name.length + 1;
        var endOfIdx = startOfIdx + (this.options.index + '').length;
        var beforeIdx = valKeeperName.substring(0, startOfIdx);
        
        this._jTextKeeper.attr({name : beforeIdx + idx + textKeeperName.substring(endOfIdx)})
        this._jValKeeper.attr({name : beforeIdx + idx + valKeeperName.substring(endOfIdx)})
        
        this._setOption('index', idx);
    },
    getData : function(){
        return {
            text : this.options.label, 
            value : this.options.value
        }
    },
    destroy : function(event){
        if(this._trigger('close', event) === false) return;
        
        this._jTextKeeper.add(this._jValKeeper).remove();
        $.Widget.prototype.destroy.call(this);
        this.element.remove();
    }
});
