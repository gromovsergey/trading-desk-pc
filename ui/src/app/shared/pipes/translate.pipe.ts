import {Pipe, PipeTransform} from '@angular/core';
import {L10nStatic} from '../static/l10n.static';

@Pipe({
  name: 'translate'
})
export class TranslatePipe implements PipeTransform {

  transform(value: string, ...args: string[]): string {
    return L10nStatic.translate(value, args && args[0] ? args[0] : null);
  }
}
