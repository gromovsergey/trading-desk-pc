(function(w, u, $){
    if (w.UI === u) {
        w.UI = {};
    }

    if(w.UI.Util === u) {
        w.UI.Util = {};
    }
    
    w.UI.Util.ErrorStorage = {
        'ERRORS_CONTAINER_SIZE':    100, 
        'SPY_ENABLED':      true, 
        'NOTIFY_ENABLED':   true, 
        'ERRORS':   [], 
        'Error':    function(message, url, line){
            this.message    = message;
            this.url    = url;
            this.line   = line;
        },
        'start':    function(){
            w.onerror   = this.errorHandler;
            for(var i=0; i < w.frames.length; i++){
                w.frames[i].onerror = this.errorHandler;
            }
        },
        'stop': function(){
            w.onerror = null;
            for(var i=0; i < w.frames.length; i++){
                w.frames[i].onerror = null;
            }
        },
        'errorHandler': function(message, url, line){
            var errors  = w.UI.Util.ErrorStorage.ERRORS;
            
            while(errors.length > w.UI.Util.ErrorStorage.ERRORS_CONTAINER_SIZE) {
                errors.shift();
            }
        
            var error = new w.UI.Util.ErrorStorage.Error(message, url, line);
            
            errors.push(error);
            w.UI.Util.ErrorStorage.NOTIFY_ENABLED && w.UI.Util.ErrorStorage.notify(error);
            return false;
        },
        'notify':  function(error){
            if (document.body) {  // if DOM loaded (OUI-9812)
                var curr    = this,
                    block   = $('#error-storage-notify-block').length ? $('#error-storage-notify-block') : this._createNotifyBlock();
                $('#error-storage-notify-block-error-message').html(error.toString());
                block.fadeIn("fast");
                setTimeout(function(){
                    block.fadeOut("fast");
                }, 3000);
            } else {
                $(function(){
                    w.UI.Util.ErrorStorage.notify(error);
                });
            }
        },
        '_createNotifyBlock':   function(){
            return $('<div />').attr({'id':   'error-storage-notify-block'})
            .css({
                'display':      'none',
                'position':     'fixed',
                'right':        0,
                'top':          0,
                'height':       '150px',
                'width':        '280px',
                'background':   '#fff',
                'border':       '2px solid #ccc',
                'z-index':      2,
                'padding':      '12px',
                'font-size':    '13px'
            })
            .append( $('<h3 />').text('Error Spy') )
            .append( $('<div />').attr({'id':   'error-storage-notify-block-error-message'}).css({'text-align': 'center'}) )
            .append( $('<a />').attr('href', '#').on('click', function(e){
                e.preventDefault();
                $(this.parentNode).fadeOut("fast");
            }).text('close') ).appendTo('body');
        },
        'init': function(){
            if (this.SPY_ENABLED) {
                this.start();
                var errorStorage = this;
                $(document).keydown(function(e) {
                    var event = e.originalEvent;
                    if(event.shiftKey && event.ctrlKey && event.altKey && event.keyCode == 90) { // Alt+Ctrl+Shift+Z
                        var sErrors = '';
                        $.each(errorStorage.ERRORS, function() {
                            sErrors += '<li style="margin-bottom: 2em">' + this.toString() + '</li>';
                        });
                        
                        $('<ul>').attr('id', 'error-storage-exception-panel-list').css('margin-left', '1.5em').html(sErrors).dialog({
                            'title': 'Exceptions panel',
                            'buttons': [{
                                'text':     'Close',
                                'click':    function() {
                                    $(this).dialog('close');
                                }
                            }],
                            'open': function(){
                                var $this   = $(this);
                                setTimeout(function(){
                                    $this.dialog('close');
                                }, 15000);
                            },
                            'width': 400
                        });
                    }
                });
            }
        }
    };
    
    w.UI.Util.ErrorStorage.Error.prototype = {
        'constructor':  w.UI.Util.ErrorStorage.Error, 
        'toString':     function(){
            return 'Location: ' + this.getLocation() + '<br />\n Message: <strong>' + this.getData() + '</strong>';
        },
        'getLocation':  function(){
            if (this.url){
                return this.url + (this.line?':'+this.line:'');
            } else {
                return 'unknown source';
            }
        },
        'getData':  function(){
            return this.message ? this.message : 'no data';
        }
    };
    
    w.UI.Util.ErrorStorage.init();
    
})(window, undefined, jQuery);