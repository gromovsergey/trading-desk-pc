import { jQuery as $ } from './common.const';

export function splitArray(array: Array<any>, count: number): Array<[any]> {
  return array
    .reduce((accum, elem, idx, arr) => accum.push(arr.slice(idx*count, idx*count+count)) && accum, [])
    .filter(elem => elem.length);
}

export function onWindowEvent(name: string, handler: Function, delay = 50): void {
  try {
    let timeout;
    $(window).on(name, (event) => {
      if (timeout===undefined) {
        timeout = setTimeout(() => {
          handler(event);
          clearTimeout(timeout);
          timeout = undefined;
        }, delay);
      }
    });
  } catch (e) {}
}

export function offWindowEvent(name: string): void {
  $(window).off(name);
}

export function traverse(object, propStr): any {
  try {
    return propStr.split('.').reduce((o, v) => o[+v || v], object);
  } catch (e) {
    return;
  }
}

export function getDates(endDate: any, count: number): string[] {
  endDate = endDate instanceof Date ? endDate : new Date(endDate);
  let dates = [];
  for (let i=0; i<count; i++) {
    let month = endDate.getMonth();
    let cycleCnt = Math.floor((12 + i - month)/12);
    dates.unshift(new Date(endDate.getFullYear() - cycleCnt, 12*cycleCnt + month - i, endDate.getDate()).toLocaleDateString());
  }
  return dates;
}

function getColorsArr(param: string, count: number) {
  // hsla(0-360, 0-100%, 0-100%, 0-1)
  return new Array(count).fill(Math.floor(({
    hue: 360,
    saturation: 100,
    lightness: 100,
    alpha: 100
  })[param]/count));
}

function getColorValueFunc(param, values) {
  return (pos, val, idx) => {
    let curParam = ['hue', 'saturation', 'lightness', 'alpha'][pos];
    return curParam==param ?
      (idx+1)*val :
      values[curParam]===undefined ? [0, 100, 50, 100][pos] : values[curParam];
  }
}

export function getColors(param: string, count: number, values: any): string[] {
  let getVal = getColorValueFunc(param, values);
  let colors = getColorsArr(param, count);
  colors.forEach((val, idx, arr) => {
    arr[idx] = `hsla(${getVal(0, val, idx)}, ${getVal(1, val, idx)}%, ${getVal(2, val, idx)}%, ${getVal(3, val, idx)/100})`;
  });
  return colors;
}

export function mergeObjects(...objects): any {
  return $.extend(true, {}, ...objects);
}
