(function(w, u, $){
    if (w.UI === u) {
        w.UI = {};
    }
    
    w.UI.Environment = {
        'values': {}
    };

    $.environment = function(name, value) {
        if (value !== u){
            return w.UI.Environment.values[name] = value;
        } else {
            return w.UI.Environment.values[name];
        }
    };
})(window, undefined, jQuery);