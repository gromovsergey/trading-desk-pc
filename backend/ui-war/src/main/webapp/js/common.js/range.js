(function(w, u, $){

    if (w.UI === u){
        w.UI = {};
    }
    
    w.UI.Daterange  = {
        '_setDate': function(name, date){
            $('[name=' + name.replace(/([^\\])\./g, '$1\\.') + ']').val($.datepicker.formatDate($.environment('datepickerDateFormat'), date));        
        },
        '_replaceAmpms':    function(time, ampms){
            if (time.indexOf(ampms.am) > -1) {
                return time.replace(ampms.am, "am");
            }
            if (time.indexOf(ampms.pm) > -1) {
                return time.replace(ampms.pm, "pm");
            }
            return time;
        },
        'checkTime':    function(timeId, timePattern, ampms, errorMsg){
            var date1 = $.trim($('#' + timeId).val());

            if (timePattern.indexOf("a") > -1) {
                date1 = w.UI.Daterange._replaceAmpms(date1, ampms);
            }
            if ($.jDate.isDate(date1, timePattern) == false) {
                alert(errorMsg);
                return false;
            }
            return true;
        },
        '_toDataRangeOptions':  function(val){
            var toId = function(prefix, suffix){
                return (prefix + suffix).replace('.', '_');
            };

            if (typeof(val) == 'string' || val == null) {
                // use default ids
                val = {
                    'fastChangeId':     toId('fastChangeId', val),
                    'fromDateId':       toId('fromDate', val),
                    'toDateId':         toId('toDate', val),
                    'timeZoneShiftId':  toId('timeZoneShift', val),
                    'fromDateName':     toId('fromDate', val),
                    'toDateName':       toId('toDate', val)
                }
            } else if (typeof(val) == 'object') {
                if (!val.fastChangeId) {
                    val.fastChangeId = 'fastChangeId';
                }
                if (!val.timeZoneShiftId) {
                    val.timeZoneShiftId = 'timeZoneShift';
                }
                if (!val.fromDateId) {
                    val.fromDateId = val.fromDateName.replace('\.', '_');
                }
                if (!val.toDateId) {
                    val.toDateId = val.toDateName.replace('\.', '_');
                }
            }
            return val;
        },
        'setTimeZoneShiftByTZName': function(code, settings, timeZoneName, callback){
            settings    = w.UI.Daterange._toDataRangeOptions(settings);
            w.UI.Data.get('accountTimeZone', {timeZoneName: timeZoneName}, function(data){
                $('#' + settings.timeZoneShiftId).val( $('timeZoneShift', data).text()|0 );
                w.UI.Daterange.setDateRange(code, settings, callback);
            }, null, {waitHolder: null});
        },
        'setTimeZoneShift': function(code, settings, accountId, callback){
            settings = w.UI.Daterange._toDataRangeOptions(settings);
            if (accountId > 0) {
                w.UI.Data.get('accountTimeZone', {'accountId': accountId}, function(data){
                    var timeZoneShift = +$('timeZoneShift', data).text();
                    $('#' + settings.timeZoneShiftId).val(timeZoneShift);
                    w.UI.Daterange.setDateRange(code, settings, callback);
                }, null, {waitHolder: null});
            } else {
                w.UI.Daterange.setDateRange(code, settings, callback);
            }
        },
        'setDateRange': function(code, settings, callback){
            var msec, 
                year, 
                endDaysOffset, 
                startDaysOffset,
                startDate   = new Date();

            msec = startDate.getTime();
            msec += startDate.getTimezoneOffset() * 60 * 1000;
            var timeZoneShift = settings ? $('#' + settings.timeZoneShiftId).val() : null;
            if (timeZoneShift) {
                msec += timeZoneShift * 24 * 3600 * 1000;
            }
            startDate.setTime(msec);

            var endDate     = new Date(startDate);

            var startOfWeek = 1;
            var dayOfWeek   = endDate.getDay();
            
            switch (code) {
                case 'QTD':
                    startDate.setMonth( (startDate.getMonth()/3|0)*3 );
                    startDate.setDate(1);
                    endDate     = new Date(endDate.getFullYear(), startDate.getMonth()+3, 0);
                break;
                case 'YTD':
                    startDate   = new Date(endDate.getFullYear(), 0, 1);
                    endDate     = new Date(endDate.getFullYear(), 12, 0);
                break;
                case 'LW':
                    endDaysOffset   = dayOfWeek - startOfWeek + 1;
                    if (endDaysOffset == 0) endDaysOffset += 7;
                    startDaysOffset = endDaysOffset + 6;
                    msec            = startDate.getTime();
                    if (startDaysOffset != 0) startDate.setTime(msec - (startDaysOffset*24*60*60*1000));
                    msec = endDate.getTime();
                    if (endDaysOffset != 0) endDate.setTime(msec - (endDaysOffset*24*60*60*1000));
                break;
                case 'NW':
                    endDaysOffset   = dayOfWeek - startOfWeek + 1;
                    if (endDaysOffset < 0) endDaysOffset += 7;
                    startDaysOffset = endDaysOffset + 6;
                    startDaysOffset -= 14;
                    endDaysOffset   -= 14;
                    msec            = startDate.getTime();
                    if (startDaysOffset != 0) startDate.setTime(msec - (startDaysOffset*24*3600*1000));
                    msec = endDate.getTime();
                    if (endDaysOffset != 0) endDate.setTime(msec - (endDaysOffset*24*3600*1000));
                break;
                case 'LM':
                    year    = endDate.getFullYear();
                    if (startDate.getMonth() == 0) {
                        year--;
                        startDate = new Date(year, 11, 1);
                        endDate   = new Date(year, 11, 31);
                    } else {
                        startDate = new Date(endDate.getFullYear(), endDate.getMonth()-1, 1);
                        // We have to finesse endDate to accommodate a potential leap year (29 days in Feb)
                        endDate = new Date(startDate.getFullYear(), endDate.getMonth(), 1);
                        while (endDate.getMonth() != startDate.getMonth()) {
                            // Reduce date by an hour until the month matches to get the last day of the start month
                            msec = endDate.getTime();
                            endDate.setTime(msec - (3600*1000));
                        }
                    }
                break;
                case 'LQ':
                    year = endDate.getFullYear();
                    if (startDate.getMonth() <= 2) {
                        // if Q1 then show Q4 of last year
                        year--;
                        startDate = new Date(year, 9, 1);
                        endDate   = new Date(year, 11, 31);
                    } else if (startDate.getMonth() <= 5) {
                        // if Q2 then show Q1
                        startDate = new Date(endDate.getFullYear(), 0, 1);
                        endDate   = new Date(startDate.getFullYear(), 2, 31);
                    } else if (startDate.getMonth() <= 8) {
                        // if Q3 then show Q2
                        startDate = new Date(endDate.getFullYear(), 3, 1);
                        endDate   = new Date(startDate.getFullYear(), 5, 30);
                    } else if (startDate.getMonth() <= 11) {
                        // if Q4 then show Q3
                        startDate = new Date(endDate.getFullYear(), 6, 1);
                        endDate   = new Date(startDate.getFullYear(), 8, 30);
                    }
                break;
                case 'LY':
                    year        = endDate.getFullYear();
                    year--;
                    startDate   = new Date(year, 0, 1);
                    endDate     = new Date(year, 11, 31);
                break;
                case 'ALL':
                    startDate   = new Date(90, 0, 1);
                break;
                case 'MTD':
                    var tempDate    = startDate;
                    startDate       = new Date(tempDate.getFullYear(), tempDate.getMonth(), 1);
                    endDate         = new Date(endDate.getFullYear(), endDate.getMonth()+1, 0);
                break;
                case 'WTD':
                    startDaysOffset = dayOfWeek - startOfWeek;
                    if (startDaysOffset < 0) startDaysOffset += 7;
                    msec = startDate.getTime();
                    if (startDaysOffset != 0) startDate.setTime(msec - (startDaysOffset*24*3600*1000));
                break;
                case 'Y':
                    msec    = startDate.getTime() - (24*3600*1000);
                    startDate.setTime(msec);
                    endDate.setTime(msec);
                break;
            }

            if (settings && code != 'R'){
                w.UI.Daterange._setDate(settings.fromDateName, startDate);
                w.UI.Daterange._setDate(settings.toDateName, endDate);
            }

            eval(callback);
        },
        'setInitialDateRange': function(code, settings){
            settings = w.UI.Daterange._toDataRangeOptions(settings);
            w.UI.Daterange.setDateRange(code, settings);
            $('#'+ (settings && settings.fastChangeId)).val(code);
        },
        'setTimeRange': function(settings, from, to){
            if (settings.fromTimeId != '') $('#' + settings.fromTimeId).val(from);
            if (settings.toTimeId != '') $('#' + settings.toTimeId).val(to);
        },
        'checkDateRange': function(fromDateId, toDateId, errorMsg){
            if($.jDate.compareDates($('#' + fromDateId).val(), $.environment('dateFormat'), $('#' + toDateId).val(), $.environment('dateFormat')) == 1) {
                alert(errorMsg);
                return false;
            }
            return true;
        },
        'checkDateTimeRange': function(fromDateId, toDateId, fromTimeId, toTimeId, timePattern, errorMsg){
            var date1 = $('#' + fromDateId).val() + ' ' + $.trim($('#' + fromTimeId).val());
            var date2 = $('#' + toDateId).val() + ' ' + $.trim($('#' + toTimeId).val()); 
            if ($.jDate.compareDates(date1, $.environment('dateFormat') + ' ' + timePattern, date2, $.environment('dateFormat') + ' ' + timePattern) == 1) {
                alert(errorMsg);
                return false;
            }
            return true;
        }
    };

})(window, undefined, jQuery);