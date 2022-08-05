export const dateFormat:string = 'YYYY-MM-DD HH:mm';
export const dateFormatShort:string = 'YYYY-MM-DD';
export const dateFormatParse:string = 'YYYY-MM-DD HH:mm';

export const moment     = self['moment'];
export const jQuery     = self['jQuery'];
export const pageSuffix = 'target rtb';
export const MAX_UPLOAD_FILE_SIZE = 10 * 1024 * 1024;
export const guid   = () => {
        let s4 = () => {
            return Math.floor((1 + Math.random()) * 0x10000)
                .toString(16)
                .substring(1);
        };
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
    };

export const appConstants = {
    country: 'RU',
    language: 'ru',
};

export const chartColors    = [
    '#fbce5d',
    '#000000',
    '#0400ff',
    '#ff0000',
    '#009900',
    '#4b0082',
    '#ff8c00',
    '#8ac419',
    '#8a9acb',
    '#43c5d7',
    '#94643c',
    '#e020ff',
];

export const chartMetrics = [
    'IMPS',
    'CLICKS',
    'CTR'
];

export const expandedChartMetrics = chartMetrics.concat([
    'POST_IMP_CONV',
    'POST_CLICK_CONV'
]);

export const chartObjectsFlight = [
    'FLIGHT',
    'FLIGHT_CHANNEL',
    'FLIGHT_SITE',
    'FLIGHT_DEVICE',
    'FLIGHT_GEO'
];

export const chartObjectsLineItem = [
    'LINE_ITEM',
    'LINE_ITEM_CHANNEL',
    'LINE_ITEM_SITE',
    'LINE_ITEM_DEVICE',
    'LINE_ITEM_GEO'
];

export const chartTypes = [
    'DAILY',
    'RUNNING_TOTAL',
    'TOTAL'
];

export const chartPeriods = [
    'ALL',
    'TW',
    'TM',
    'LW',
    'LM'
];

export const genderOptions = [
    {value: null, name: 'Any'},
    {value: 'MALE', name: 'Male'},
    {value: 'FEMALE', name: 'Female'}
];

export const displayMainSizes = [
    {value: 'DISPLAY_MAIN_728_90', name: '728x90'},
    {value: 'DISPLAY_MAIN_240_400', name: '240x400'},
    {value: 'DISPLAY_MAIN_300_250', name: '300x250'},
    {value: 'DISPLAY_MAIN_160_600', name: '160x600'},
    {value: 'DISPLAY_MAIN_300_600', name: '300x600'},
    {value: 'DISPLAY_MAIN_336_280', name: '336x280'},
    {value: 'DISPLAY_MAIN_1000_120', name: '1000x120'}
];

export const displayAdditionalSizes = [
    {value: 'DISPLAY_ADDITIONAL_468_60', name: '468x60'},
    {value: 'DISPLAY_ADDITIONAL_320_50', name: '320x50'},
    {value: 'DISPLAY_ADDITIONAL_250_250', name: '250x250'},
    {value: 'DISPLAY_ADDITIONAL_120_600', name: '120x600'},
    {value: 'DISPLAY_ADDITIONAL_320_100', name: '320x100'},
    {value: 'DISPLAY_ADDITIONAL_300_300', name: '300x300'},
    {value: 'DISPLAY_ADDITIONAL_200_200', name: '200x200'}
];

export const dtoSizes = [
    {value: 'DTO', name: 'DTO'}
];

export const videoSizes = [
    {value: 'VIDEO_PRE_ROLL', name: 'pre-roll'},
    {value: 'VIDEO_MID_ROLL', name: 'mid-roll'},
    {value: 'VIDEO_IN_CONTENT_ROLLS', name: 'in content-rolls'}
];

export const inBannerVideoMainSizes = [
    {value: 'IN_BANNER_VIDEO_MAIN_728_90', name: '728x90'},
    {value: 'IN_BANNER_VIDEO_MAIN_240_400', name: '240x400'},
    {value: 'IN_BANNER_VIDEO_MAIN_300_250', name: '300x250'},
    {value: 'IN_BANNER_VIDEO_MAIN_160_600', name: '160x600'},
    {value: 'IN_BANNER_VIDEO_MAIN_300_600', name: '300x600'},
    {value: 'IN_BANNER_VIDEO_MAIN_336_280', name: '336x280'},
    {value: 'IN_BANNER_VIDEO_MAIN_1000_120', name: '1000x120'}
];

export const inBannerVideoAdditionalSizes = [
    {value: 'IN_BANNER_VIDEO_ADDITIONAL_468_60', name: '468x60'},
    {value: 'IN_BANNER_VIDEO_ADDITIONAL_320_50', name: '320x50'},
    {value: 'IN_BANNER_VIDEO_ADDITIONAL_250_250', name: '250x250'},
    {value: 'IN_BANNER_VIDEO_ADDITIONAL_120_600', name: '120x600'},
    {value: 'IN_BANNER_VIDEO_ADDITIONAL_320_100', name: '320x100'},
    {value: 'IN_BANNER_VIDEO_ADDITIONAL_300_300', name: '300x300'},
    {value: 'IN_BANNER_VIDEO_ADDITIONAL_200_200', name: '200x200'}
];

export const audienceIncomeLevel = [
    {value: 'LOW', name: 'Low'},
    {value: 'MIDDLE', name: 'Middle'},
    {value: 'HIGH', name: 'High'}
];

export const audienceGeoMskRegion = [
    {value: 'MOSCOW', name: 'Moscow'},
    {value: 'MOSCOW_REGION', name: 'Moscow Region'}
];

export const audienceGeoSptRegion = [
    {value: 'SAINT_PETERSBURG', name: 'Saint Petersburg'},
    {value: 'SAINT_PETERSBURG_REGION', name: 'Saint Petersburg Region'}
];

export const audienceGeoFDs = [
    {value: 'CENTRAL_FD', name: 'Central Federal District'},
    {value: 'NORTHWESTERN_FD', name: 'Northwestern Federal District'},
    {value: 'NORTH_CAUCASIAN_FD', name: 'North Caucasian Federal District'},
    {value: 'VOLGA_FD', name: 'Volga Federal District'},
    {value: 'URAL_FD', name: 'Ural Federal District'},
    {value: 'SIBERIAN_FD', name: 'Siberian Federal District'},
    {value: 'FAR_EASTERN_FD', name: 'Far Eastern Federal District'}
];

export const audienceGeoMegacities = [
    {value: 'NOVOSIBIRSK', name: 'Novosibirsk'},
    {value: 'YEKATERINBURG', name: 'Yekaterinburg'},
    {value: 'NIZHNY_NOVGOROD', name: 'Nizhny Novgorod'},
    {value: 'KAZAN', name: 'Kazan'},
    {value: 'CHELYABINSK', name: 'Chelyabinsk'},
    {value: 'OMSK', name: 'Omsk'},
    {value: 'SAMARA', name: 'Samara'},
    {value: 'ROSTOV_ON_DON', name: 'Rostov-on-Don'},
    {value: 'UFA', name: 'Ufa'},
    {value: 'KRASNOYARSK', name: 'Krasnoyarsk'},
    {value: 'PERM', name: 'Perm'},
    {value: 'VORONEZH', name: 'Voronezh'},
    {value: 'VOLGOGRAD', name: 'Volgograd'}
];

export const kpiTypes = [
    {value: 'AUDIENCE_METRICS', name: 'Audience metrics'},
    {value: 'BRAND_AWARENESS', name: 'Brand Awareness'},
    {value: 'CONVERSIONS', name: 'Conversions / Calls / Filled Form'},
];

export const kpiConversions = [
    {value: null, name: 'Please select...'},
    {value: 'PURCHASE', name: 'Purchase'},
    {value: 'FILLED_FORM', name: 'Filled form'},
    {value: 'REGISTRATION', name: 'Registration'},
    {value: 'CALL', name: 'Call'},
    {value: 'SUBSCRIBE', name: 'Subscribe'},
    {value: 'VISIT_KEY_PAGE', name: 'Visit key page'},
    {value: 'OTHER', name: 'Other'}
];

export const frequencyOfReporting = [
    {value: null, name: 'Please select...'},
    {value: 'DAILY', name: 'Daily'},
    {value: 'WEEKLY_MON', name: 'Weekly on Monday'},
    {value: 'WEEKLY_TUE', name: 'Weekly on Tuesday'},
    {value: 'WEEKLY_WED', name: 'Weekly on Wednesday'},
    {value: 'WEEKLY_THU', name: 'Weekly on Thursday'},
    {value: 'WEEKLY_FRI', name: 'Weekly on Friday'}
];

export const reportingMetrics = [
    {value: 'ADVERTISER', name: 'Advertiser'},
    {value: 'FLIGHT', name: 'Filght'},
    {value: 'IMPS', name: 'Imps'},
    {value: 'CLICKS', name: 'Clicks'},
    {value: 'CTR', name: 'CTR'},
    {value: 'COST', name: 'Cost'},
    {value: 'ECPM', name: 'eCPM'},
    {value: 'TOTAL_UNIQUE_USERS', name: 'Total unique users'},
    {value: 'DAILY_UNIQUE_USERS', name: 'Daily unique users'}
];
