(function(w, u){
    if (w.UI === u){
        w.UI = {};
    }
    
    w.UI.Text   = {
        'insertAtCaret':    function(obj, text){
            if (document.selection){
                obj.focus();
                var orig    = obj.value.replace(/\r\n/g, "\n"),
                    range   = document.selection.createRange();
                
                if (range.parentElement() != obj) return false;
                
                range.text = text;
                
                var tmp, actual = tmp = obj.value.replace(/\r\n/g, "\n");
                
                for (var diff = 0; diff < orig.length; diff++) {
                    if (orig.charAt(diff) != actual.charAt(diff)) break;
                }
                
                for (var index = 0, start = 0; tmp.indexOf(text) >= 0 && (tmp = tmp.replace(text, "")) && index <= diff; index = start + text.length) {
                    start = actual.indexOf(text, index);
                }
            } else if (obj.selectionStart) {
                var start   = obj.selectionStart,
                    end     = obj.selectionEnd;
                
                obj.value   = obj.value.substr(0, start) + text + obj.value.substr(end, obj.value.length);
            }
            
            if (start != null) {
                w.UI.Text.setCaretTo(obj, start + text.length);
            } else {
                obj.value += text;
            }
        },
        'setCaretTo':   function(obj, pos){
            if (obj.createTextRange) {
                var range = obj.createTextRange();
                range.move('character', pos);
                range.select();
            } else if (obj.setSelectionRange) {
                obj.focus();
                obj.setSelectionRange(pos, pos);
            }
        },
        'setCaretToEnd':    function(obj){
            w.UI.Text.setCaretTo(obj, obj.value.length);
        },
        'escapeHTML': function(htmlString){
            return htmlString.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        }
    };
    
})(window, undefined);