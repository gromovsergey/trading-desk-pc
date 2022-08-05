(function(w, u, $){

    var iKeyp       = 0,
    iDelay          = 300,
    perfomSearch    = function(sVal){
        new w.UI.AjaxLoader().switchOff();
        $('#quick_search_preloader').show();
        if ($('#quick_search_data').hasClass('scrollable')){
            $('#quick_search_data').removeClass('scrollable').css('height','auto');
        }
        $.post('/json/quickSearch.action',{'name':sVal}, function(data) {
            $('#quick_search_preloader').hide();
            $('#quick_search_data').html('');

            if (data && data.length) {
                $.each(data, function(i, group) {
                    var jqGroupDD   = $('<dd />').attr({'class':    'b-qsearchresults__group'}).text(group.name),
                        jqGroupDL   = $('<dl />').attr({'class':    'b-qsearchresults__list'});

                    $.each(group.items, function (i, item) {
                        $('<a />').attr({
                            'href':     item.link,
                            'class':    'b-qsearchresults__item'+(item.color ? ' '+item.color : '')
                        }).html(item.name).appendTo(jqGroupDL);
                    });
                    $('#quick_search_data').append(jqGroupDD,jqGroupDL);
                });

                if ($('#quick_search_data').height() > ($(document.body).height() - 50)) {
                    $('#quick_search_data').addClass('scrollable').height($(document.body).height() - 50);
                }
                $('#quick_search_data').on('mouseover', 'a', function () {
                    $('#quick_search_data a').removeClass('on');
                    $(this).addClass('on');
                }).show();
            } else {
                $('#quick_search_data').hide();
            }
        }, 'json');
        new w.UI.AjaxLoader().switchOn();
    };
    $(document).on('keydown', function(e){

        var isQSDataShown   = $('#quick_search_data').is(':visible');
        if (isQSDataShown && (e.keyCode == 40 || e.keyCode == 38)) {
            e.stopPropagation();
            e.preventDefault();
        }

        if (isQSDataShown && e.keyCode == 27) {
            $('#quick_search_data').hide();
            $('#quick_search_data a').removeClass('on');
        }

        switch ( $(e.target).attr('id') ) {
            case 'quick_search':
                if (e.keyCode == 40 && isQSDataShown) {
                    $('#quick_search_data a').removeClass('on');
                    $('#quick_search_data a').eq(0).addClass('on');
                    $('#quick_search').blur();
                } else {
                    iKeyp++;
                    setTimeout(function(x){
                        return function(){
                            if (x < iKeyp) {
                                return false;
                            } else {
                                var sVal    = $(e.target).val();
                                if (sVal.length > 1) {
                                    perfomSearch(sVal);
                                }
                            }
                        }
                    }(iKeyp), iDelay);
                }
                break;
            default:
                if (isQSDataShown) {
                    var jqCurrent   = $('#quick_search_data a.on'),
                        iLength     = $('#quick_search_data a').length,
                        iPos        = $('#quick_search_data a').index(jqCurrent);

                    if (iLength) {
                        switch (e.keyCode) {
                            case 27: // esc
                                $('#quick_search_data').hide();
                                $('#quick_search_data a').removeClass('on');
                                break;
                            case 13: // enter
                                if (jqCurrent) {
                                    location.href   = jqCurrent.attr('href');
                                }
                                break;
                            case 38: // up arr
                                $('#quick_search_data a').removeClass('on');
                                if (iPos) {
                                    var jqHighlitedItem = $('#quick_search_data a').eq(--iPos).addClass('on'),
                                    iOffset             = 36; // magic number! sic...
                                    if ($('#quick_search_data').hasClass('scrollable') && (jqHighlitedItem.offset().top < iOffset)) {
                                        $('#quick_search_data').scrollTop( $('#quick_search_data').scrollTop() + jqHighlitedItem.offset().top - iOffset);
                                    }
                                } else {
                                    $('#quick_search').focus();
                                }
                                break;
                            case 40: // down arr
                                if (++iPos < iLength) {
                                    $('#quick_search_data a').removeClass('on');
                                    var jqHighlitedItem = $('#quick_search_data a').eq(iPos).addClass('on');
                                    if ($('#quick_search_data').hasClass('scrollable') && ($('#quick_search_data').height() < jqHighlitedItem.offset().top)) {
                                        $('#quick_search_data').scrollTop( $('#quick_search_data').scrollTop() + (jqHighlitedItem.offset().top - $('#quick_search_data').height()));
                                    }
                                }
                                break;
                        }
                    }
                }
        }
    });
    
    $(function(){
        $(document.body).on('click', function(e){
            $('#quick_search_data').hide();
            $('#quick_search_data a').removeClass('on');
        });
        
        $('#quick_search').on('paste', function(e){
            setTimeout(function(){
                if ($('#quick_search').val().length > 2 && iKeyp == 0) {
                    perfomSearch($('#quick_search').val());
                }
            }, iDelay);
        });
    });
})(window, undefined, jQuery);