(function( $ ) {

    var _storage            = {},
    _settings               = {},
    _hashIframe             = {},
    _ie7OrLower             = $.browser.msie && parseFloat($.browser.version) <= 7,
    _getHash            = function(){
        return window.location.href.split('#')[1] || '';
    },
    _getHashData        = function(){
        var hash    = _getHash(),
        search      = 0,
        page        = 1;
        if (hash.lastIndexOf('s') >= 0) {
            page    = (hash.split('s')[0]|0 > 1) ? hash.split('s')[0]|0 : 1;
            search  = (hash.split('s')[1]|0 > 0) ? hash.split('s')[1]|0 : 0;
        } else {
            page    = (hash|0 > 1) ? hash|0 : 1;
        }
        return {"search":search, "page":page};
    },
    _setHash                = function(page,session) {
        var sHash               = ((page|0 > 1) ? page : 1)+'s'+((session|0 > 1) ? session : 1);
        if (_ie7OrLower) {
            _ie7FrameSet(sHash);
        } else {
            window.location.hash    = sHash;
        }
        
    },
    _ie7FrameSet         = function(sHash) {
        if (_hashIframe.length === undefined)   _hashIframe = document.getElementById('historyFrame');
        var doc = _hashIframe.contentWindow.document;
        doc.open("javascript:'<html></html>'");
        doc.write("<html><head><scri" + "pt type=\"text/javascript\">parent.location.hash = '" + sHash + "';</scri" + "pt></head><body></body></html>");
        doc.close();
    },
    _lastHash           = _getHash(),
    _historyMax         = 200,
    _hashChangeFlag     = false,
    _autoSubmitFlag     = false,
    _submitFlag         = false;

    var methods = {
        init : function( options ) {

            _settings = $.extend( {
                action: '',
                autoSubmit: false,
                dontRestore: false,
                message: 'Loading...',
                onBeforeRestore: function(data, callback) {callback();},
                onBeforeSubmit: function(callback) {callback();},
                onLoad: function() {},
                result: {},
                sendChangeEvent: false
            }, options),
            _storage            = window.sessionStorage;

            return this.each(function(){

                var $this       = $(this),
                data            = $this.data('pagingAssist'),
                _search         = function(){
                    var hashData    = _getHashData(),
                    sessionLast     = _storage.getItem('paLastSession')|0 || 0,
                    fLoad   = function(data){
                        var postData    = (data !== undefined && data.length) ? data : $this.formKeeper('serialize');
                        postData.push({"name":"page","value":hashData.page});

                        _settings.result.html('<h3 class="level1">' + _settings.message + '</h3>');
                        _settings.result.load(
                            _settings.action || $this.attr('action'),
                            postData, 
                            function(responseText, textStatus, XMLHttpRequest){
                                if(XMLHttpRequest.status == 401){
                                    window.location.href = window.location.pathname + window.location.search;
                                } else {
                                    _settings.onLoad();
                                    _settings.result.find('.pagingButton').each(function(){
                                        $(this).attr({'href':'#'+$(this).data('page')+'s'+(_storage.getItem('paLastSession') || 1)});
                                    });
                                    _submitFlag = false;
                                }
                            }
                        );
                    };
                    
                    if (_hashChangeFlag || (hashData.search > 0)) { 

                        if (hashData.search == 0) {
                            if (_settings.autoSubmit || _getHash() == 'auto') {
                                hashData.search = 1;
                            } else {
                                return;
                            }
                        }

                        _storage.setItem('paLastSession', hashData.search);
                        
                        if (!_submitFlag && !_settings.dontRestore) {
                            var data    = $this.formKeeper({
                                "key":"paSessionData"+hashData.search, 
                                "sendChangeEvent": _settings.sendChangeEvent
                            }).formKeeper("restore").formKeeper("getStored");
                            _settings.onBeforeRestore(data, fLoad);
                            return;
                        }
                    }
                    fLoad();
                },
                _newSearch          = function(){
                    var sessionCurrent  = _storage.getItem('paSession')|0;
                    sessionCurrent++;
                    $this.formKeeper({key:'paSessionData'+sessionCurrent}).formKeeper('keep');
                    _storage.setItem('paSession', sessionCurrent);
                    _storage.setItem('paLastSession', sessionCurrent);

                    if (_submitFlag) {
                        _setHash(1, sessionCurrent);
                    }
                    _search();

                    if (sessionCurrent > _historyMax) { // session clean
                        _storage.removeItem('paSessionData'+(sessionCurrent - _historyMax));
                    }
                };
                
                if (!_settings.result) {
                    _settings.result = $('<div class="pa_result" />');
                    $this.after(_settings.result);
                }

                if ( !data ) { // init here

                    $(document).off('submit.preventDoubleSubmit');
                
                    $this
                        .on('submit.pagingAssist', function(e){
                            e.preventDefault();
                            _submitFlag = true;
                            _settings.onBeforeSubmit(_newSearch);
                        });

                    if(_ie7OrLower){ // hashchange event callback
                        setInterval(function(){
                            var currentHash = _getHash();
                            if (currentHash != _lastHash) {
                                if (_submitFlag || _autoSubmitFlag) return;
                                _hashChangeFlag = true;
                                _settings.onBeforeSubmit(_storage.getItem('paSession')?_search:_newSearch);
                                _lastHash   = currentHash;
                                _ie7FrameSet(currentHash);
                            }
                        }, 200);
                    } else {
                        $(window).on('hashchange.pagingAssist', function() {
                            if (_submitFlag || _autoSubmitFlag) return;
                            _hashChangeFlag = true;
                            _settings.onBeforeSubmit(_storage.getItem('paSession')?_search:_newSearch);
                        });
                    }

                    _settings.result.find('.pagingButton').each(function(){
                        $(this).attr({'href':'#'+$(this).data('page')+'s'+(_storage.getItem('paLastSession') || 1)});
                    });

                    // auto submit
                    var initHashData    = _getHashData(),
                    initSession         = _storage.getItem('paSession')|0;
                    if (_settings.autoSubmit || _getHash() == 'auto') {
                        _autoSubmitFlag = true;
                        setTimeout(function(){
                            _hashChangeFlag = false;
                            _settings.onBeforeSubmit(_newSearch);
                        },100);
                        _autoSubmitFlag = false;
                    } else if ((initSession > 0) && (initHashData.search > 0)) {
                        _hashChangeFlag = true;
                        _autoSubmitFlag = true;
                        _settings.onBeforeSubmit(_search);
                        _autoSubmitFlag = false;
                    }

                    $this.data('pagingAssist', true);
                }
            });
        },
        options : function(data){
            _settings = $.extend(_settings, data);
        }
    };

    $.fn.pagingAssist = function( method ) {
        if ( methods[method] ) {
            return methods[method].apply( this, Array.prototype.slice.call( arguments, 1 ));
        } else if ( typeof method === 'object' || ! method ) {
            return methods.init.apply( this, arguments ); // default init method
        } else {
            $.error( 'Method ' +  method + ' does not exist on pagingAssist' );
        }    
    };

})( jQuery );