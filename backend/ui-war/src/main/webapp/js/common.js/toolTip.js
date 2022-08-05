(function(w, u, $){
    if (w.UI === u){
        w.UI = {};
    }

    w.UI.Hint = function(jqCaller){
        var self        = this;
        this._jCaller   = jqCaller;
        this._jToolTip  = this._jCaller.children('.toolTip');
        
        this._jToolTip.appendTo('body'); // for floating over all caller's parent containers
        this._jCaller
            .add(this._jToolTip).on('mouseover', function(e){
                self.showHint(e.pageX + 15, e.pageY + 15);
            }).on('mouseout', function(){
                self.hideHint();
            });
    }

    w.UI.Hint.prototype = {
        '_HINT_DELAY':  500,  // delay before toolTip hides
        'showHint':     function(mouseX, mouseY){ // shows toolTip, and places it near it's caller (div with "hint" sigh)
            var callerOffset    = this._jCaller.offset();
            this._hintTimeout && clearTimeout(this._hintTimeout);
            this._jToolTip.show();
            this._updateHintPos(mouseX, mouseY);
        },
        '_updateHintPos':   function(currX, currY){ // updates position of toolTip relatively it's caller (div with "hint" sigh)
            var docOffsetLeft   = $('html').offset().left,
                windowWidth     = $(window).width(),
                tipWidth        = this._jToolTip.width(),
                tipRight        = currX + tipWidth,
                tipWindowRight  = tipRight - docOffsetLeft;
            
            this._jToolTip.css({'top':  currY});
            if (tipWindowRight > windowWidth){
                this._jToolTip.css({'left': currX - tipWidth});
            } else {
                this._jToolTip.css({'left': currX});
            }
        },
        '_setHintTimeout':  function(callback){
            this._hintTimeout && clearTimeout(this._hintTimeout);
            this._hintTimeout   = setTimeout(callback, this._HINT_DELAY);
        },
        'hideHint': function(){ // hides current toolTip
            var toolTip = this._jToolTip;
            this._setHintTimeout(function(){
                toolTip.hide()
            });
        }
    };

    w.UI.Hint.initHints = function(){
        $('.toolTip').each(function(){
            var self    = $(this);
            
            if (self.data('hint')) return true;
            
            var hint = new w.UI.Hint($(this).parent());
            self.data({'hint': hint});
        })
    };

})(window, undefined, jQuery);