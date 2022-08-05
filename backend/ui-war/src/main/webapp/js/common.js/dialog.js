(function(w, u, $){
    if (w.UI === u){
        w.UI = {};
    }
    
    w.UI.Dialog   = {
        '_allowedOnlyOne':  true, 
        '_dialogs':         [], 
        'createDialog':     function(jqData, options){
            var options = options || {};
            
            this._allowedOnlyOne && this.removeAllDialogs();
            
            var popupDiv = $('<div class="dialog" />').appendTo('body').html(jqData);
                
            this._posDialog(popupDiv, options)
            this._dialogs.push(popupDiv);
            
            return popupDiv;
        }, 
        '_posDialog':   function(jDialog, options){
            var xDial   = 0,
                yDial   = 0;
            if (options.x && options.y){
                xDial   = options.x|0;
                yDial   = options.y|0;
            } else {
                xDial   = Math.max(($(window).width() - jDialog.width()) / 2 + $(window).scrollLeft(), 0);
                yDial   = Math.max(($(window).height() - jDialog.height()) / 2 + $(window).scrollTop(), 0);
            }
            jDialog.css({'left': xDial, 'top': yDial});
        },
        'removeAllDialogs': function(){
            $.each(this._dialogs, function(){
                $(this).remove();
            });
            this._dialogs = [];
        },
        'init': function(options){
            if (options.allowedOnlyOne === false){
                this._allowedOnlyOne = false;
            }
        }
    };
})(window, undefined, jQuery);