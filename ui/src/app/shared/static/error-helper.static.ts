import {HttpErrorResponse} from '@angular/common/http';
import {ErrorStateMatcher} from '@angular/material/core';

export class ErrorHelperStatic {
  static matchErrors(err: HttpErrorResponse): { [name: string]: string[] } {
    let matched;

    if (err.status === 412) {
      matched = err.error || null;
    }

    return matched;
  }

  static getErrorMatcher(hasError: any): ErrorStateMatcher {
    return {
      isErrorState(): boolean {
        return !!hasError;
      }
    };
  }
}
