(function(w, u, $){
    if (w.UI === u){
        w.UI = {};
    }
    
    w.UI.Password = {
        '_MIN_LENGTH':  8,
        '_rule': [{
            'func': function(pwd){ // check upper case
                return pwd.toLowerCase() != pwd; 
            },
            'weight':   1
        },
        {
            'func': function(pwd){ // check lower case 
                return pwd.toUpperCase() != pwd;
            },
            'weight':   1
        },
        {
            'func': function(pwd){ // check digits
                return /\d/.test(pwd);
            },
            'weight':   1
        },
        {
            'func': function(pwd){ // check non-alphanumeric characters
                for (var i=0; i<pwd.length; i++){
                    var sym = pwd.charAt(i);
                    if (/\d/.test(sym)) continue;
                    if (sym.toLowerCase() != sym || sym.toUpperCase() != sym) continue; // test for letters
                    return true;
                }
            },
            'weight':1
        }],
        '_status':      [], 
        '_getScore':    function (pwd){
            var score = 0;
            if (pwd.length < this._MIN_LENGTH) return score;
            for (var i=0; i<this._rule.length; i++){
                this._rule[i].func(pwd) && score++;
            }
            return score;
        },
        'setStatusMessages':    function(aMessages) {
            this._status    = aMessages;
            return this;
        },
        'getStatus': function(pwd){
            var score = this._getScore(pwd);
            return this._status[Math.min(score, this._status.length-1)];
        }
    };
    
})(window, undefined, jQuery);