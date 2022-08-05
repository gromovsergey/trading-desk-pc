(function(w, u, $){
    if (w.UI === u){
        w.UI = {};
    }
    
    if (w.UI.Util === u){
        w.UI.Util = {};
    }
    
    w.UI.Util.ERROR_PAGE_URL        = '/errorHandler.action?errorCode=500';
    w.UI.Util.ERROR_PAGE_CODE_URL   = '/errorHandler.action?errorCode=';
    
    w.UI.Util.redirectToErrorPage   = function(code){
        w.location  = code ? (w.UI.Util.ERROR_PAGE_CODE_URL + code) : w.UI.Util.ERROR_PAGE_URL;
    };
    
    w.UI.Util.confirmCopy   = function(obj){
        if (!$(obj).data("is_pressed") && confirm( $.localize("confirmCopy") )) {
            new w.UI.AjaxLoader().show();
            $(obj).data("is_pressed", true);
            return true;
        }
        return false;
    };
    
    w.UI.Util.Pair = {
        'fetchId': function(pair){
            if (!pair) return null;
            var found = pair.match(/(.*?)_.*/);
            return found && (found[1] || null);
        },
        'createPairById':   function(id){
            return id + '_none';
        }
    };
    
    w.UI.Util.openLink = function(href, target){
        if (target !== u && target === '_blank') {
            w.open(href, 'utilWnd_'+((Math.random()*10000)|0));
        } else {
            w.location.href = href;    
        }
    };
    
    w.UI.Util.setIntervalUntil = function(callback, interval){
        if (!callback()) {
            return;
        }
        var cbCaller = function(){
            if (callback()) {
                setTimeout(cbCaller, interval);
            }
        };
        setTimeout(cbCaller, interval);
    };

    w.UI.Util.escapeRegexp    = function(sInput){
        return (typeof sInput === 'string') ? sInput.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|"']/g, '\\$&') : '';
    };
    
    w.UI.Util.assignSubmitForFields = function(container){
        var container = container || document;
        if ($.browser.mozilla){
            $('form:has(:input[type=text]), form:has(:password)', container).not(':has(:submit)').each(function(){
                $(this).append('<input type="submit" style="display:none;" />');
            });
        } else if (!$.browser.safari) {
            $(container).on('keypress', ':input[type=text], :password', function(e){
                if (e.which == 13) {
                    $(this.form).submit();
                }
            })
        }
    };
    
    w.UI.Util.enableButtons = function(jqCurrForm){
        if ( jqCurrForm.data('disabledButts') === undefined ) return;
        $.each(jqCurrForm.data('disabledButts'), function(i, n) {
            $(n).prop({disabled : false});
        })
        jqCurrForm.removeData('disabledButts');
    };
    
    w.UI.Util.disableButtons = function(jqCurrForm){
        jqCurrForm.data('disabledButts', $.makeArray( 
            $(':submit, :button', jqCurrForm).not(':disabled').each(function(){
                $(this).prop('disabled', true);
            })
        ));
    };
    
    w.UI.Util.preventSafariDblClk   = function(){ // OUI-18324
        if (!$.browser.safari) return false;
    
        $(document).on('mousedown', 'select:not([multiple])', function(e){
            var newTime = new Date().getTime();
            if ($(this).data('clickTime')){
                var deltaTime = newTime - ($(this).data('clickTime')|0);
                if (deltaTime < 300){
                    e.preventDefault();
                }
            }
            $(this).data('clickTime', newTime);
        });
    };
    
    w.UI.Util.redrawSelect   = function(jSelect, copyEvents){ // Safari has a bug with re-populating <select> elements
        var jNewSelect = jSelect;
        if ($.browser.safari){
            jNewSelect = jSelect.clone(copyEvents);
            jSelect.after(jNewSelect).remove();
            w.UI.SelTiper.bindTipToSelect(jNewSelect);
        }
        return jNewSelect;
    };
    
    w.UI.Util.cleanSelect   = function(jSelect, copyEvents){ // Safari has a bug with re-populating <select> elements
        var jEmptySelect = jSelect.empty();
        return w.UI.Util.redrawSelect(jEmptySelect, copyEvents);
    };
    
    w.UI.Util.togglePreview = function(id, postfix){
        var jqBox   = $('#preview_box'+(postfix?postfix:'') + '_' + id),
        jqTr        = $('#preview_tr_' + id + (postfix?'_'+postfix:''));
        
        if (jqTr.is(':visible')) {
            jqTr.hide().find('div.loaded').removeClass('loaded').addClass('unloaded').html('');
        } else {
            jqTr.show();
            $.ajax({
                type: 'POST',
                url: '/xml/previewInfo.action',
                dataType: 'xml',
                data: {"creativeId":id},
                success : function(data){
                    var jqData  = $(data),
                    iWidth      = jqData.find('width').text()|0,
                    iHeight     = jqData.find('height').text()|0,
                    sPath       = jqData.find('path').text();
                    var errors = [];
                    jqData.find("error").each(function () {
                        errors.push($(this).text());
                    });
                    if (errors.length > 0) {
                        $.each(errors, function(index, value) {
                            jqBox.append('<p><span class="errors">'+value+'</span></p>');
                        });
                    } else if (sPath) {
                        var jqFrame = $('<iframe />').attr({"src":sPath,"frameborder":"0","style":jqBox.data("style")});
                        if (iWidth) jqFrame.attr({"width":iWidth});
                        if (iHeight) jqFrame.attr({"height":iHeight});
                        jqFrame.appendTo(jqBox);
                    } else {
                        jqBox.html('<p><span class="infos">'+jqBox.data("info")+'</span></p>');
                    }
                    jqBox.removeClass('unloaded').addClass('loaded');
                },
                error: function(){
                    jqBox.removeClass('unloaded').addClass('loaded').html('<p>'+jqBox.data("info")+'</p>');
                }
            });
        }
    };
    
    w.UI.Util.Table = {
        '_cnt':     0,
        'addRow':   function(id, keepDisabled){ 
            var table       = document.getElementById(id),
                tbody       = table.tBodies[0],
                rowTempl    = tbody.rows[0],
                jNewRow     = $(rowTempl).clone(true).show(),
                newRow      = jNewRow[0];
            
            tbody.appendChild(newRow);
            jNewRow.removeClass('hide');
            
            if (w.UI.Util.Table._cnt == 0) {
                w.UI.Util.Table._cnt    = w.UI.Util.Table.getCnt(table, rowTempl);
            } else {
                w.UI.Util.Table._cnt++;
            }
            
            $('input, select, div, span, td, table', newRow).add(newRow).each(function(){
                var name    = $(this).attr('name'),
                    id      = $(this).attr('id'),
                    value   = $(this).attr('value'),
                    re      = new RegExp('\\?');
                
                re.test(name) && $(this).attr({name : name.replace(re, w.UI.Util.Table._cnt)});
                re.test(id) && $(this).attr({id : id.replace(re, w.UI.Util.Table._cnt)});
                re.test(value) && $(this).attr({value : value.replace(re, w.UI.Util.Table._cnt)});
                if (keepDisabled === undefined || !keepDisabled) {
                    $(this).prop({'disabled': false});
                }
            });
            $(rowTempl).attr('_cnt', +w.UI.Util.Table._cnt + 1);
            
            return jNewRow;
        },
        'delRow':   function(row){
            if (w.UI.Util.Table._cnt == 0) {
                w.UI.Util.Table._cnt    = $(row).siblings('tr').length;
            }
            $(row).remove();
            w.UI.Util.Table._cnt--;
        },
        'getCnt':   function(table, rowTempl){
            return ($(rowTempl).attr('_cnt') || $(table).children('tbody:eq(0)').children('tr').length);
        },
        'initCnt':  function(id){
            var table       = document.getElementById(id),
                jRowTempl   = $(table.tBodies[0].rows[0]),
                cnt         = $(table).children('tbody:eq(0)').children('tr').length;
            jRowTempl.attr('_cnt', cnt+1);
        },
        'deleteRowByButtonInIt':    function(btn, table){
            var rowToDelete = $(btn).parents('tr').filter(function(){
                return ($(this).parent().parent()[0] == table);
            })[0];
            w.UI.Util.Table.delRow(rowToDelete);
        }
    }

})(window, undefined, jQuery);


// jQuery plugin - button with menu
(function($){

    $.fn.menubutton = function(options){
    
        var menuLast,
            settings = $.extend({
                'beforeclick':  function(){}
            }, options);
        
        return this.each(function(){
            $(this).css({
                'text-decoration':'none'
            })
            .append('&nbsp;&#9660;')
            .click(function() {
                if (menuLast) {
                    menuLast.hide();
                }
                
                if (settings.beforeclick.call(this) === false) {
                    return false;
                }
                
                var menu = menuLast = $(this).next().show().position({
                    my: "left top",
                    at: "left bottom",
                    of: this
                });
                
                $(document).one("click", function() {
                    menu.hide();
                });
                return false;
            })
            .next()
            .hide()
            .menu()
            .css({
                position:'absolute',
                'z-index':1000
            });
        });
    };
    
    $.fn.ajaxTableSorter = function(options) {
        options = options || {};
        var table = $(this);
        table.addClass('tablesorter');

        table.find("th").each(function(i) {
            var columnOpts = options.sortList[i];
            if (!columnOpts) {
                return;
            }
            var header = $(this);
            header.wrapInner('<div class="icon" />');
            header.addClass('header');

            var sortOrder = options.sortOrder(columnOpts);
            if (sortOrder == 'ASC') {
                header.addClass('headerSortDown');
            } else if (sortOrder == 'DESC') {
                header.addClass('headerSortUp');
            }

            header.click(function() {
                var newOrder;
                if (sortOrder == 'ASC') {
                    newOrder = 'DESC';
                } else if (sortOrder == 'DESC') {
                    newOrder = 'ASC';
                } else {
                    newOrder = columnOpts.defaultOrder;
                }

                options.sortCallback(columnOpts.sortKey, newOrder);
            });
        });
        return this;
    };
    
    $.fn.extend({
        textChanged: function(fn) {
            var bFlag   = false;
            this.data('previous', this.val()).on('input.textChanged drop.textChanged paste.textChanged', function(e){
                var $this   = $(this),
                    self    = this;
                if (!bFlag && $this.val() !== '') {
                    bFlag   = true;
                    setTimeout(function(){
                        if ($this.val() !== $this.data('previous')) {
                            fn.apply(self, [e]);
                        }
                        $this.data('previous', $this.val());
                        bFlag   = false;
                    }, 1000);
                }
            });
        }
    });
})(jQuery);