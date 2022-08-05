jQuery(function($){
	$.datepicker.regional['tr'] = {
		closeText: 'Bitti',
		prevText: 'Önceki',
		nextText: 'Sonraki',
		currentText: 'Bugün',
		monthNames: ['Ocak', 'Şubat', 'Mart', 'Nisan', 'Mayıs', 'Haziran',
            'Temmuz', 'Ağustos','Eylül', 'Ekim', 'Kasım', 'Aralık'],
		monthNamesShort: ['Ock', 'Şbt', 'Mrt', 'Nsn', 'May', 'Haz',
            'Tem', 'Ağu', 'Eyl', 'Ekm', 'Ksm', 'Arl'],
		dayNames: ['Pazar', 'Pazartesi', 'Salı', 'Çarşamba', 'Perşembe', 'Cuma', 'Cumartesi'],
		dayNamesShort: ['Pzr', 'Pzt', 'Sal', 'Çrş', 'Prş', 'Cu', 'Cts'],
		dayNamesMin: ['Pz','Pzt','Sa','Ça','Pe','Cu','Cts'],
		weekHeader: 'Hft',
		dateFormat: 'dd.mm.yyyy',
		firstDay: 0,
		isRTL: false,
		showMonthAfterYear: false,
		yearSuffix: ''};
	$.datepicker.setDefaults($.datepicker.regional['tr']);
});