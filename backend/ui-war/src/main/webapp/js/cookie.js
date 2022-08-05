(function(w){
    w.Cookie = {
        'cookies':  (function(){
            if (document.cookie === "") return {};
            
            var cookies = {},
                pairs   = document.cookie.split("; ");

            for (var i = 0; i < pairs.length; i++){
                var cookie      = pairs[i],
                    delimiter   = cookie.indexOf("=");
                cookies[cookie.substring(0, delimiter)] = decodeURIComponent(cookie.substring(delimiter + 1));
            }

            return cookies;
        })(),
        'read':     function(name){
            return w.Cookie.cookies[name];
        },
        'remove':   function(name){
            w.Cookie.create(name, "", -1);
        },
        'create':   function(name, value, expiredays){
            document.cookie = name + "=" + encodeURIComponent(value) + "; expires=" + w.Cookie._date(expiredays).toGMTString() + "; path=/;";
        },
        '_date':    function(daysDelta) {
            var date    = new Date();
            date.setDate(date.getDate() + daysDelta);
            return date;
        }
    };
})(window);