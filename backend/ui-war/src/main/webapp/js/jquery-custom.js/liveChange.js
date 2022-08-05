$.fn.liveChange = function(callback) {

    if (!$.isFunction(callback)) return;

    var isAway  = false,
    handler     = function(jTarget) {
        var prevVal = jTarget.data('prevVal');
        var currVal = jTarget.val();
        if (prevVal != currVal){
            callback(jTarget);
            jTarget.data({prevVal:currVal});
        }
    },
    deferred = function(event) {
        var jTarget = $(event.target);
        clearTimeout(jTarget.data('keyTimeout'));
        jTarget.data({keyTimeout:setTimeout(function() {
            handler(jTarget);
            if (!isAway) {
                jTarget.focus();
            } else {
                isAway  = false;
            }
        }, 300)});
    };

    this.on('change.liveChange drop.liveChange cut.liveChange paste.liveChange input.liveChange', function(e){
        var $this   = $(this);
        if ($this.is('select')){
            handler($this);
        }else if($this.is(':radio')){
            callback($this);
        }else{
            deferred(e);
        }
    }).on('blur.liveChange', function(){
        isAway = true;
    });
    
};
