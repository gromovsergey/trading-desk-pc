import { Component }   from '@angular/core';
import { Title }       from '@angular/platform-browser';

import { pageSuffix }  from '../common/common.const';

@Component({
    template: ``
})

export class PageComponent {

    private pageTitleSuffix: string = pageSuffix;
    private pageTitle: string = '';
    private pageTitleService: Title;

    public constructor(){}

    set title(title: string){
        this.pageTitle = title;
        if (this.pageTitleService === undefined) {
            this.pageTitleService = new Title(title);
        }
        this.pageTitleService.setTitle(title && title.length ? `${title} - ${this.pageTitleSuffix}` : this.pageTitleSuffix);
    }

    get title(): string {
        return this.pageTitle;
    }
}
