/**
 * required libs: jquery, UI.Localization
 */
(function(w, u, $){

    if (w.UI === u) {
        w.UI = {};
    }

    w.UI.AjaxLoader = new function() {
        var instance,
        _id         = 'ajax_loader_singleton',
        _jqThis     = $('#'+_id),
        _semaphor   = 0,
        _isOff      = false,
        _container  = {},
        _setSize    = function(){
            if (_container === 'body') {
                _jqThis.width($(w).width()).height($(w).height());
            } else {
                _jqThis.removeAttr('style');
            }
        };
        
        function AjaxLoader(callback) {
            if ( !instance ) {
                instance = this;
            } else {
                return instance;
            }
            
            if (!_jqThis.length) {
                _container  = 'body';
                _jqThis = $('<div />').attr({
                    'id': _id,
                    'class': 'ajax_loader'
                }).appendTo(_container);
                _setSize();
                
                $(document).on('keydown.ajaxloader', function(e){
                    switch (e.keyCode) {
                        case 27:
                            if (instance.isShown()) {
                                instance.interrupt(callback);
                            }
                        break;
                    }
                });
                _jqThis.focus();
                
                $(w).on('resize.ajaxloader', _setSize);
            }
        }

        AjaxLoader.prototype.show = function() {
            if (_isOff) return;
            if (_semaphor == 0) {
                _jqThis.show();
            }
            _semaphor++;
            return this;
        };
        AjaxLoader.prototype.hide = function() {
            _semaphor   = (_semaphor > 0) ? _semaphor-1 : 0;
            if (_semaphor == 0) {
                _jqThis.hide();
            }
            return this;
        };
        AjaxLoader.prototype.interrupt = function(callback) {
            _semaphor   = 0;
            _jqThis.hide();
            if (callback !== u) {
                callback();
            }
            return this;
        };
        AjaxLoader.prototype.isShown = function() {
            return (_semaphor > 0);
        };
        AjaxLoader.prototype.switchOff = function() {
            _isOff  = true;
            return this;
        };
        AjaxLoader.prototype.switchOn = function() {
            _isOff  = false;
            return this;
        };
        AjaxLoader.prototype.setContainer = function(selector) {
            var jqContainer = $(selector);
            if (jqContainer !== u && jqContainer.length) {
                _container  = selector;
                jqContainer.addClass('ajax_loader_container');
            } else {
                _container  = 'body';
            }
            instance.interrupt();
            _jqThis.appendTo(_container);
            _setSize();
            return this;
        };
        AjaxLoader.prototype.setContainerBody   = function(){
            return instance.setContainer();
        }
        return AjaxLoader;
    }


    if (w.UI.Data === u) {
        w.UI.Data = {};
    }

    w.UI.Data.sRequestUrl = '/xml/';

    /**
     * Send request and call callback if it's successed
     *
     * @param type type of request
     * @param params params of request
     * @param callback handler 'function (data, textStatus) {...}'
     */
    w.UI.Data.get = function(type, params, callback, dataType, ajaxSettings) {
        return w.UI.Data.getUrl(w.UI.Data.sRequestUrl + type + '.action', dataType, params, callback, ajaxSettings);
    };

    w.UI.Data.post = function(type, params, callback, dataType, ajaxSettings) {
        return w.UI.Data.sendUrl(w.UI.Data.sRequestUrl + type + '.action', 'POST', dataType, params, callback, ajaxSettings);
    }

    /**
     * Send request and call callback if it's successed
     *
     * @param url url of request
     * @param params params of request
     * @param callback handler 'function (data, textStatus) {...}'
     * @param ajaxSettings is other settings for $.ajax method'
     */
    w.UI.Data.sendUrl = function(url, methodType, dataType, params, callback, ajaxSettings) {
        var settings = $.extend({
                url: url,
                data: $.param(params, true),
                success: function(data){
                    w.UI.Data._defaultHandler(data, callback);
                },
                error: w.UI.Data._defaultErrorHandler, 
                type: methodType || 'GET',
                dataType: dataType || 'xml',
                xhr: function(){
                    return w.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject('Microsoft.XMLHTTP')
                }
            }, 
            ajaxSettings
        );
        return $.ajax(settings);
    };

    w.UI.Data.getUrl = function(url, dataType, params, callback, ajaxSettings) {
        return w.UI.Data.sendUrl(url, 'GET', dataType, params, callback, ajaxSettings)
    };

    w.UI.Data.serializeForm = function(form) {
        var res = new Array();
        $(':input', $(form)).each(function(index, input){
            input = $(input);
            var name = input.attr('name');
            
            if (name == null || name == "") return;
            if (input.is(':checkbox') && !input.is(':checked')) return;
            if (input.is(':radio') && !input.is(':checked')) return;
            if (input.is(':disabled')) return;
            
            res.push({
                name: name,
                value: input.val()
            });
        });
        return res;
    };

    w.UI.Data._defaultHandler = function(data, callback) {
        var errorLength;
        try {
            errorLength = $('error', data).length
        } catch(e) {
            // ignore error absence
        }

        if (errorLength) {
            w.UI.Data._defaultErrorHandler();
        } else {
            callback(data);
        }
    };

    w.UI.Data._defaultErrorHandler = function() {
        w.UI.Util.redirectToErrorPage();
    };

    w.UI.Data._prepareSettings = function(settings) {
        // use default error handler
        var errorHandler = settings.error || w.UI.Data._defaultErrorHandler;

        settings.error = function(xhr, textStatus, errorThrown) {
        
            if (xhr.status == 0) {
                // does anybody know what is it?
                return;
            }

            // handle non authenticated
            if (xhr.status == 401) {
                // reload page to allow server to remember last requested url and show the login page
                w.location.href = w.location.pathname + w.location.search;
                return;
            }

            if(xhr.status == 403){
                w.UI.Util.redirectToErrorPage(403);
                return;
            }

            if(xhr.status == 404){
                w.UI.Util.redirectToErrorPage(404);
                return;
            }

            // handle other errors
            errorHandler(xhr, textStatus, errorThrown);
        };

        var completeHandler = settings.complete;

        settings.complete = function(jqXHR, textStatus) {
            new w.UI.AjaxLoader().hide();
            completeHandler && completeHandler.apply(this, arguments);
        };

        // See OUI-8435
        settings.xhr = function() {
            return w.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject('Microsoft.XMLHTTP')
        };
    };

    // Override $.ajax method
    var originalAjaxMethod = $.ajax;

    $.ajax = function(){
        w.UI.Data._prepareSettings(arguments[0]);
        new w.UI.AjaxLoader().show();
        return originalAjaxMethod.apply(this, arguments);
    }

    /**
     * w.UI.Data.Options package
     */
    w.UI.Data.Options = {};

    /**
     * Send reqest to server
     *
     * @param type type of request
     * @param selectId if of 'select' element
     * @param callback callback function(data, selectId, advancedOptions)
     * @param params additional parameters
     * @param additionalOptionMessages additioanal option messages
     */
    w.UI.Data.Options.fill = function(type, selectId, callback, params, additionalOptionMessages, ajaxSettings) {
        w.UI.Data.Options.replaceWith(selectId, ['form.select.wait']);

        var specialCallback = function(data, textStatus){
            var names = selectId.split(/\,\s*/);
            $.each(names, function(i, name) {
                callback(data, name, additionalOptionMessages);
            });
        }
        return w.UI.Data.post(type, params, specialCallback, null, ajaxSettings);
    };

    w.UI.Data.Options.get = function(type, selectedId, params, additionalOptionMessages, callback, ajaxSettings) {
        var resultCallback = w.UI.Data.Options._update;
        if(callback) {        
            var resultCallback = function(response, selectId, additionalOptionMessages) {    
                w.UI.Data.Options._update(response, selectId, additionalOptionMessages);
                callback(response, selectId, additionalOptionMessages);
            }
        }
        return w.UI.Data.Options.fill(type, selectedId, resultCallback, params, additionalOptionMessages, ajaxSettings);
    };

    w.UI.Data.Options._update = function(response, selectId, additionalOptionMessages) {
        var options = $('options', response);
        if (!options.length) return;

        w.UI.Data.Options.replaceWith(selectId, additionalOptionMessages, response);
        var selectContainer = $('#' + selectId);
        if (!selectContainer.length) return;
        var selectContainerOptions = selectContainer[0].options;
        if (!selectContainerOptions) return;
        
        var jqSelect    = $('<select />');
        $('option', options).each(function(i) {
            var jqOption    = $('<option />').attr('value',$(this).attr('id')).text($(this).text()).appendTo(jqSelect);
        });
        selectContainer.append(jqSelect.children('option'));
        selectContainer[0].selectedIndex    = 0;
    };

    /**
     * Replace content of select by localized options
     *
     * @param selectId id or element
     * @param messageKeys message keys of options
     * @param response optional parameter wtih response data for filtering options
     */
    w.UI.Data.Options.replaceWith = function(selectId, messageKeys, response) {
        var names = selectId.split(/\,\s*/);
        $.each(names, function(i, name) {
            var currentSelect = $('#' + name);
            
            if (!currentSelect.length) return true;
            var currentSelectOptions = currentSelect[0].options;
            if (!currentSelectOptions) return true;  // checking if it is SELECT element
            
            currentSelect.empty();
            var localizedOpts = w.UI.Data.Options.createLocalizedOptions(messageKeys, response);
            $.each(localizedOpts, function(i){
                currentSelectOptions[i] = this;
            });
        });
    };

    /**
     * Create array of Option object, contains localized values.
     * Use messageKey.condition for filtering options.
     *
     * @param messages array of message keys or arrays [messagekey, value]
     * @param data optional parameter with data for conditioning
     */
    w.UI.Data.Options.createLocalizedOptions = function(messages, data) {
        var resultArr = [];

        if (messages != null) {
            $.each(messages, function(i, messageKey) {
                if ($.isArray(messageKey)) {
                    var message = messageKey[0];
                    var value = messageKey[1];
                    resultArr.push(UI.Data.Options._createLocalizedOption(message, value));
                } else if(typeof messageKey == 'object') {
                    var condition = messageKey.condition ?
                                    messageKey.condition : w.UI.Data.Options.Conditions.alwaysTrueCondition;

                    if(condition(data)) {
                        resultArr.push(UI.Data.Options._createLocalizedOption(messageKey.key, messageKey.value));
                    }
                } else {
                    resultArr.push(UI.Data.Options._createLocalizedOption(messageKey));
                }
            });
        }

        return resultArr;
    };

    w.UI.Data.Options._createLocalizedOption = function(messageKey, value) {
        var message = $.localize(messageKey);
        return new Option(message, value ? value : '');
    };

    /**
     * w.UI.Data.Options.Conditions package
     */
    w.UI.Data.Options.Conditions = {};

    /**
     * Condition wihtch always true
     */
    w.UI.Data.Options.Conditions.alwaysTrueCondition = function(data) { return true; };

    /**
     * Condition check options count in data and retrun true if them more than one
     */
    w.UI.Data.Options.Conditions.ifNotUniqueCondition = function(data) {
        return $("options option", data).length != 1;
    };

    $.fn.ajaxPanel = function(settings) {
        var self = this;
        var panel = self.data('ajaxPanel') || {
            url: settings.url,
            form: settings.form == null || settings.form == '' ? $(null) : $(settings.form),
            load: function() {
                var params = this.form ? w.UI.Data.serializeForm(this.form) : {};
                $.ajax({
                    type: 'POST',
                    url: this.url,
                    data: params,
                    success: function(data) {
                        self.html(data);
                        if(typeof(settings.onload) == 'function'){
                            settings.onload(self);
                        }
                    },
                    waitHolder: self
                });
            },
            loadOnShow: function() {
                var panel = this;
                w.UI.Util.setIntervalUntil(function() {
                    var isVisible = self.is(':visible');
                    isVisible && panel.load();
                    return !isVisible;
                }, 100);
            },
            replace: function() {
                var params = this.form ? w.UI.Data.serializeForm(this.form) : {};
                $.ajax({
                    type: 'POST',
                    url: this.url,
                    data: params,
                    success: function(data) {
                        self.replaceWith(data);
                        if(typeof(settings.onload) == 'function'){
                            settings.onload(self);
                        }
                    },
                    waitHolder: self
                });
            }
        };
        self.data('ajaxPanel', panel);

        return panel;
    };
})(window, undefined, jQuery);