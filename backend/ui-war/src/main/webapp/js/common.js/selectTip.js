(function(w, u, $){
    if (w.UI === u){
        w.UI = {};
    }
    
    w.UI.SelTiper   = {
        '_selContnrs':      {},
        '_MAX_LENGTH':      100,
        '_assignSelTips':   function(){
            var self    = this;
            this._selContnrs.each(function(){
                self.bindTipToSelect($('select', this));
            })
        },
        'bindTipToSelect':  function(jSelect){
            var curContainer    = jSelect.closest('.grouping.withTip'),
                currTip         = $('.selTip', curContainer);
            
            currTip.html('&nbsp;');

            jSelect.data({'currTip':    currTip}).on('click', function(e){
                w.UI.SelTiper.update(jSelect);
            }).on('keyup', function(e){
                if (e.which == 37 || e.which == 38 || e.which == 39 || e.which == 40) { // arrow keys
                    w.UI.SelTiper.update(jSelect);
                }
            });
        },
        'update':   function(jqSelect){
            var selectedOpts    = $("option:selected", jqSelect),
                currTip         = jqSelect.data('currTip');
            if (!currTip) return;
            var textToShow  = selectedOpts.size() == 1 ? $.trim(selectedOpts.text()) : '',
                editedText  = textToShow.slice(0, this._MAX_LENGTH);

            if (textToShow.length > this._MAX_LENGTH){
                editedText += '…'
            }

            $('.selTip', this._selContnrs).css({'visibility':   'hidden'});
            currTip.css({'visibility': 'visible'}).html(w.UI.Text.escapeHTML(editedText));
        },
        'init': function(){
            this._selContnrs = $('table.grouping.withTip');
            this._assignSelTips();
        }
    };

    $(function(){
        w.UI.SelTiper.init();
    });
    
})(window, undefined, jQuery);