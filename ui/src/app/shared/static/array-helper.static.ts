export class ArrayHelperStatic {
  static sortByKey(key: string, a: any, b: any): number {
    if (a[key] === b[key]) {
      return 0;
    }
    return (a[key] > b[key]) ? 1 : -1;
  }
}
