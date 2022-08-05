
const locales = {
  'ru-RU': require('../locales/ru.json'),
  'en-US': require('../locales/en.json')
};
const localeName = process.env._LANG_ || 'ru-RU';
const activeLocale = locales[localeName];
const prefix = '_L10N_';
const re = new RegExp(`${prefix}\\(([^\\)]+)\\)`);
const substitute = (content, context) => {
  var value,
  key = RegExp.$1;
  if (activeLocale[key]===undefined) {
    value = key;
    context.emitError(`"${key}" could not be localized`);
  } else {
    value = activeLocale[key];
  }
  return content.replace(re, value);
};

console.log(`${__filename}: using locale "${localeName}"`);

module.exports = function (content) {
  while (re.test(content)) {
    content = substitute(content, this);
  }
  return content;
};
