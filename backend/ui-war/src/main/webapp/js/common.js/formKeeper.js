(function( $ ) {

    var _settings       = {},
    _serializeForm  = function($form) {
        var jsonData    = [], aSelectList = [];

        $form.find('input[type=text], input[type=hidden], textarea, :checked, :selected').not(':disabled').each(function(){
            if ($(this).is('option')) {
                if ($(this).parent('*').is('optgroup')) {
                    var $select = $(this).parent('optgroup').parent('select');
                } else {
                    $select = $(this).parent('select');
                }
                var name    = _settings.useDataNames ? $select.data('name') : $select.attr('name');

                if (name != undefined) {
                    jsonData.push({'name':name, value:$(this).attr('value')});
                }
            } else {
                var name = _settings.useDataNames ? $(this).data('name') : $(this).attr('name');
                if (name != undefined) {
                    jsonData.push({'name':name, value:$(this).val()});
                }
            }
        });
        return jsonData;
    }
    
    var methods = {
        init : function( options ) {

            _settings = $.extend({
                key:                'defaultKey',
                sendChangeEvent:    false,
                useDataNames:       false
            }, options);

            var $this   = $(this);
            if ($this.length > 1) {
                $.error("Can't init formKeeper for several forms");
                return this;
            }

            $this.data('formKeeper', _settings.key);
            return this;
        },
        keep : function() {
            var $this       = $(this),
            key             = $this.data('formKeeper');

            if ($this.length > 1) {
                $.error("Can't keep form data from several forms");
                return this;
            }

            if (key) {
                sessionStorage.setItem(key, JSON.stringify(_serializeForm($this)));
            }
            return this;
        },
        restore : function() {

            var $this       = $(this),
            key             = $this.data('formKeeper');

            if ($this.length > 1) {
                $.error("Can't restore several forms from serialized data");
                return this;
            }

            if (key) {
                var data    = $.parseJSON(sessionStorage.getItem(key));
                if (data != undefined && data.length > 0) {

                    $this.find(':checkbox').not(':disabled').removeAttr('checked');
                    $this.find('select').not(':disabled').children('option').removeAttr('selected');

                    for (var i in data) {
                        $this.find('*['+(_settings.useDataNames ? 'data-':'')+'name="'+data[i].name.replace('.','\\.')+'"]').each(function(){

                            if ($(this).is('select')) {
                                var jqSelect    = $(this);
                                jqSelect.find('option').each(function(){
                                    if ($(this).val() == undefined) return; // nothing to do here
                                    if (data[i].value instanceof Array) {
                                        if (data[i].value.indexOf($(this).val()) != -1) $(this).attr({"selected":"selected"});
                                    } else {
                                        if (data[i].value == $(this).val()) {
                                            $(this).prop({"selected":true});
                                        }
                                    }
                                });
                            } else {
                                switch (this.type) {
                                    case 'radio':
                                    case 'checkbox':
                                        if ($(this).val() == data[i].value) {
                                            $(this).prop({"checked":true});
                                        }
                                    break;
                                    default:
                                        $(this).val(data[i].value);
                                }
                            }
                            if (_settings.sendChangeEvent) {
                                $(this).change();
                            }
                        });
                    }
                }
            }
            return this;
        },
        clear : function(){
            var $this       = $(this),
            key             = $this.data('formKeeper');

            if ($this.length > 1) {
                $.error("Can't clear serialized data from several forms");
                return this;
            }

            if (key) {
                sessionStorage.removeItem(key);
            }
            return this;
        },
        serialize : function(){
            var $this   = $(this);
            if ($this.length > 1) {
                $.error("Can't return serialized data from several forms");
                return this;
            }
            return _serializeForm($this);
        },
        getStored : function(){
            var $this       = $(this),
            key             = $this.data('formKeeper'),
            data            = {};

            if ($this.length > 1) {
                $.error("Can't restore several forms from serialized data");
                return this;
            }

            if (key) {
                var data    = $.parseJSON(sessionStorage.getItem(key));
            }
            return data;
        },
        getKey : function(){
            var $this   = $(this);
            if ($this.length > 1) {
                $.error("Can't return key value from several forms");
                return this;
            }
            return $this.data('formKeeper');
        }
    };

    $.fn.formKeeper = function( method ) {
        if ( methods[method] ) {
            return methods[method].apply( this, Array.prototype.slice.call( arguments, 1 ));
        } else if ( typeof method === 'object' || ! method ) {
            return methods.init.apply( this, arguments ); // default init method
        } else {
            $.error( 'Method ' +  method + ' does not exist on formKeeper' );
        }    
    };

})( jQuery );