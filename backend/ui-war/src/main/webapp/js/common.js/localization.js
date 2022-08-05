(function(w, u, $){
    if (w.UI === u) {
        w.UI = {};
    }

    w.UI.Localization = {
        '_decimalSeparator':    '.',
        '_groupingSeparator':   ',',
        'setDecimalSeparator':  function(cSeparator) {
            return this._decimalSeparator  = cSeparator.substring(0,1);
        },
        'setGroupingSeparator': function(cSeparator) {
            return this._groupingSeparator  = cSeparator.substring(0,1);
        },
        'MessageProvider': function(messages){
            this.messages   = messages || {};
        },
        'parseFloat':   function(value){
            if (value !== u && value.length) {
                var rxDecimal   = new RegExp('\\'+this._decimalSeparator,'g');        
                var rxGroup     = new RegExp('\\'+this._groupingSeparator,'g');
                
                value = value.replace(rxGroup,'');
                return parseFloat(value.replace(rxDecimal,'.'));
            } else {
                return NaN;
            }
        },
        'parseInt':     function(value){
            if (value !== u && value.length) {
                var rxGroup     = new RegExp('\\'+this._groupingSeparator,'g');
                return parseInt(value.replace(rxGroup,''), 10);
            } else {
                return NaN;
            }
        },
        'showPopup': function(event, options){
            $.ajax({
                url: options.url + 'edit.action',
                type: 'GET',
                data: {resourceKey: options.key, entityName: options.entityName},
                dataType: 'html',
                success: function(data) {
                    var dialog = w.UI.Dialog.createDialog(data, {
                        x: event.pageX, 
                        y: event.pageY 
                    })
                    .attr('id', options.popupName == null ? 'localizer_popup' : options.popupName)
                    .data('options', options)
                    .focus()
                    .on('keyup', function(e){
                        if (e.keyCode == 27) {
                            w.UI.Dialog.removeAllDialogs();
                        }
                    });
                    $('input:text:visible:first', dialog).focus();
                }
            });
        }
    };
    
    w.UI.Localization.MessageProvider.prototype.constructor = w.UI.Localization.MessageProvider;
    
    w.UI.Localization.MessageProvider.prototype.getMessage = function(key) {
        if (this.messages[key] === u) {
            alert('LOCALIZATION: Can\'t find message with key \''+ key + '\'!');
        }
        return this.messages[key];
    };

    w.UI.Localization.MessageProvider.prototype.addMessage = function(key, value) {
        return this.messages[key] = value;
    };
    
    w.UI.Localization.GlobalMessageProvider = new w.UI.Localization.MessageProvider();
    
    $.localize  = function(key, value) {
        if (value !== u) {
            return w.UI.Localization.GlobalMessageProvider.addMessage(key, value);
        } else {
            return w.UI.Localization.GlobalMessageProvider.getMessage(key);
        }
    };
    
})(window, undefined, jQuery);
