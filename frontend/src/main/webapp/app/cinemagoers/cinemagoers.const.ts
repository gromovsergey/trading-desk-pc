import { jQuery as $ } from '../common/common.const';
import { getDates } from '../common/utilities';
import { GOOGLE_COLOR, APPLE_COLOR } from '../common/jqplot-utilities';

export const CINEMAGOERS_CATEGORIES = [
  {id: 'demographic', name: 'Демографический портрет', description: 'Возрастные категории и родственные связи', charts: ['social_age', 'social_relation'], class: 'fa-user'},
  {id: 'occupation', name: 'Занятость', description: 'Занятость и использование метро', charts: ['occupation', 'metro_activity'], class: 'fa-users'},
  {id: 'income', name: 'Доход и банки', description: 'Уровни дохода и банки', charts: ['income_level', 'income_level_time', 'banks'], class: 'fa-usd'},
  {id: 'behavior', name: 'Поведение', description: 'Поведение и оффлайн покупки', charts: ['behavior', 'offline_purchase'], class: 'fa-shopping-cart'},
  {id: 'mobile', name: 'Мобильный трафик', description: '', charts: ['mobile'], class: 'fa-mobile'},
  {id: 'geo', name: 'География', description: 'Города - милионники', charts: ['geo'], class: 'fa-map-marker'},
  {id: 'all', name: 'ВСЕ', description: '', charts: ['social_age', 'social_relation', 'occupation', 'metro_activity', 'income_level', 'income_level_time', 'banks', 'behavior', 'offline_purchase', 'mobile', 'geo'], class: 'fa-bars'}
];

export const CINEMAGOERS_DATA = {
  'social_age': [[74, 107, 55, 30, 15, 10, 5], [73, 120, 60, 40, 17, 12, 5]],
  'social_relation': [[35, 155, 40, 119, 50]],
  'occupation': [[70, 120, 52, 32, 6]],
  'metro_activity': [[35, 36, 41, 50, 60, 72, 92, 107]],
  'income_level': [[8, 69, 370, 688]],
  'income_level_time': [[10, 9, 7, 7, 8, 8], [80, 78, 70, 60, 65, 69], [400, 410, 390, 380, 360, 370], [590, 650, 680, 720, 710, 688]],
  'banks': [[80, 94, 130, 124, 52, 101, 20, 74, 148, 41, 93, 71, 75, 135, 86, 105, 112, 72, 142, 88, 120, 84, 95, 79, 56, 180, 211]],
  'behavior': [[94, 139, 145, 76, 79, 100, 115, 105, 92, 102, 114, 96, 111, 177, 186, 303, 74]],
  'offline_purchase': [[340, 279, 175, 165, 146, 234, 250, 157, 164, 153, 144, 193, 150, 144, 230, 144, 140]],
  'mobile': [[37, 106]],
  'geo': [['Челябинск', 44],
          ['Екатеринбург', 90],
          ['Казань', 74],
          ['Красноярск', 23],
          ['Москва', 66],
          ['Нижний Новгород', 37],
          ['Новосибирск', 21],
          ['Омск', 16],
          ['Пермь',74],
          ['Ростов на Дону', 27],
          ['Самара', 27],
          ['Санкт-Петербург', 32],
          ['Уфа', 28],
          ['Волгоград', 21],
          ['Воронеж', 39],
          ['Вологодская область, Череповец', 0]]
};

export const CINEMAGOERS_CHART_CONFIG = {
  'social_age': {
    template: 'BAR_VERTICAL',
    options: {
      title: 'Индекс по возрастным группам',
      series: [
        { label: 'Мужчины' },
        { label: 'Женщины' }
      ],
      axes: {
        xaxis: {
          ticks: ['0-17', '18-24', '25-34', '35-44', '45-54', '55-63', '64+']
        }
      },
      legend: { show: true }
    }
  },

  'social_relation': {
    template: 'BAR_VERTICAL',
    options: {
      title: 'Индекс по родственным связям',
      axes: {
        xaxis: {
          ticks: ['Одинокий', 'Внук', 'Дед', 'Сын', 'Мать']
        }
      }
    }
  },

  'occupation': {
    template: 'BAR_VERTICAL',
    comments: 'Студенты имеют самый высокий индекс. '.repeat(10),
    options: {
      title: 'Индекс по роду занятий',
      axes: {
        xaxis: {
          ticks: ['Школьники Школьники Школьники Школьники Школьники', 'Студенты', 'Рабочие', 'Домохозяйки', 'Пенсионеры']
        }
      }
    }
  },

  'metro_activity': {
    template: 'BAR_VERTICAL',
    options: {
      title: 'Индекс по посещаемости метро в месяц',
      axes: {
        xaxis: {
          ticks: ['0', '2-3', '4-7', '8-15', '16-31', '32-63', '64-127', '128-255']
        }
      }
    }
  },

  'income_level': {
    template: 'BAR_VERTICAL',
    options: {
      title: 'Индекс по доходам',
      axes: {
        xaxis: {
          ticks: ['Низкий', 'Ниже среднего', 'Выше среднего', 'Высокий'],
          tickOptions: {
            angle: -30
          }
        }
      }
    }
  },

  'income_level_time': {
    template: 'LINE_TIME',
    options: {
      title: 'Индекс по доходам: динамика',
      legend: {
        labels: ['Низкий', 'Ниже среднего', 'Выше среднего', 'Высокий_'.repeat(10)]
      },
      axes: {
        xaxis: {
          ticks: getDates(new Date(), 6)
        }
      },
      /*seriesColors: util.getColors('lightness', 5, {hue: 200, alpha: 50})*/
    }
  },

  'banks': {
    template: 'BAR_HORIZONTAL',
    options: {
      title: 'Индекс по банкам',
      seriesDefaults: {
        rendererOptions: {
          barMargin: 2
        }
      },
      axes: {
        yaxis: {
          ticks: ['Сбербанк', 'Тинькофф', 'ВТБ 24', 'Альфа Банк', 'ОТП Банк', 'Банк Москвы',
            'Россельхозбанк', 'Хоум Кредит Банк', 'Райффайзен Банк', 'Совкомбанк', 'Бинбанк',
            'Восточный Экспресс Банк', 'Банк Ренессанс Кредит', 'Московский Кредитный Банк',
            'Банк Открытие', 'Промсвязьбанк', 'Сетелем Банк', 'АТБ Банк', 'Юникредит Банк',
            'Банк Русский Стандарт', 'Кредит Европа Банк', 'Уралсиб', 'Росбанк', 'МТС Банк',
            'Ак Барс', 'Ситибанк', 'Газпромбанк']
        }
      }
    }
  },

  'behavior': {
    template: 'BAR_HORIZONTAL',
    options: {
      title: 'Индекс по интересам в сети',
      axes: {
        yaxis: {
          ticks: ['Карьера', 'Образование', 'Красота', 'Дети', 'Недвижимость', 'Дом и дача',
            'Медицина', 'Мода', 'Автомобили', 'Электронная коммерция', 'Спорт', 'Стиль жизни',
            'Обслуживание', 'Финансы', 'Путешествия', 'Развлечения', 'Электроника']
        }
      }
    }
  },

  'offline_purchase': {
    template: 'BAR_HORIZONTAL',
    options: {
      title: 'Индекс по оффлайн покупкам',
      axes: {
        yaxis: {
          ticks: ['Общественный транспорт и такси', 'Развлечения', 'Продажа алкоголя',
            'Спортивные товары', 'Дом и дача', 'Красота', 'B2B', 'Телеком', 'Супермаркет',
            'Автосервис', 'ATM', 'Одежда', 'Водители', 'Фармацефтика', 'Кафе и рестораны',
            'Медицина', 'Продуктовый магазин']
        }
      }
    }
  },

  'mobile': {
    template: 'DONUT',
    options: {
      title: 'Индекс по мобильной платформе',
      legend: {
        labels: ['Google Android', 'Apple iOS']
      },
      seriesColors: [GOOGLE_COLOR, APPLE_COLOR]
    }
  },

  'geo': {
    template: 'GOOGLE_GEOCHART',
    comments: 'Екатеринбург - самый активный. '.repeat(10),
    options: {
      _head: ['Город', 'Индекс'],
      _selected: CINEMAGOERS_DATA.geo
        .sort((a: any[], b: any[]) => b[1] - a[1])
        .map((v) => v[0])
        .slice(0, 5),
      region: 'RU',
      resolution: 'provinces',
      markerOpacity: 0.8,
      colorAxis: {colors: ['yellow', 'orange']},
      defaultColor: 'white',
      sizeAxis: {
        minSize: 5,
        maxSize: 20
      },
      legend: {textStyle: {color: '#666', fontSize: 16}}
    }
  }
};
