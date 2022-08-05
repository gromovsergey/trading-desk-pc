/* English/UK initialisation for the jQuery UI date picker plugin. */
/* Written by Stuart. */
jQuery(function($){
    $.datepicker.regional['ro'] = {
        closeText: 'Incheiat',
        prevText: 'Anterior',
        nextText: 'Urmator',
        currentText: 'Astazi',
        monthNames: ['Ianuarie', 'Februarie', 'Martie', 'Aprilie', 'Mai', 'Iunie',
            'Julie', 'August', 'Septembrie', 'Octombrie', 'Noiembrie', 'Decembrie'],
        monthNamesShort: ['Ian', 'Febr', 'Mart', 'Apr', 'Mai', 'Iun',
            'Iul' ,'Aug', 'Sept', 'Oct', 'Nov', 'Dec'],
        dayNames: ['Duminica','Luni', 'Marti', 'Miercuri', 'Joi', 'Vineri', 'Sambata'],
        dayNamesShort: ['Dum', 'Luni', 'Mar', 'Mier', 'Joi', 'Vin', 'Samb'],
        dayNamesMin: ['Du', 'L', 'M', 'Mi', 'J', 'Vi', 'S'],
        weekHeader: 'Sapt',
        dateFormat: 'dd.mm.yyyy',
        firstDay: 0,
        isRTL: false,
        showMonthAfterYear: false,
        yearSuffix: ''};
    $.datepicker.setDefaults($.datepicker.regional['ro']);
});
