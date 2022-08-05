import { Component, Input, Output, EventEmitter, OnInit} from '@angular/core';

@Component({
    selector: 'ui-option-transfer',
    templateUrl: 'option_transfer.html'
})

export class OptionTransferComponent implements OnInit {
    @Input() options: Array<any>    = [];
    @Input() selected: Array<any>   = [];
    @Input() customSort;
    @Input() availableMsg: string = 'Available';
    @Input() selectedMsg: string = 'Selected';
    @Input() selectAllMsg: string = 'select all';
    @Input() deselectAllMsg: string = 'deselect all';
    @Output() onChange = new EventEmitter();


    ngOnInit(){
        if (this.selected.length && this.options.length){
            this.options = this.options.filter(v => {
                return !this.selected.find(f => { return f.id === v.id});
            });
        }
    }


    private selectItem(e: any, item: any){
        e.preventDefault();
        e.stopPropagation();

        this.options    = this.options.filter(v => {
            return v !== item;
        });

        this.selected.push(item);

        this.selected.sort(this.customSort);
        this.onChange.emit(this.selected);
    }

    private deselectItem(e: any, item: any){
        e.preventDefault();
        e.stopPropagation();

        this.selected    = this.selected.filter(v => {
            return v !== item;
        });
        this.onChange.emit(this.selected);

        this.options.push(item);
        this.options.sort(this.customSort);
    }

    public selectAll(e: any){
        e.preventDefault();
        e.stopPropagation();

        if (this.options && this.options.length){
            Array.prototype.push.call(this.selected, ...this.options);
            this.selected.sort(this.customSort);
            this.onChange.emit(this.selected);

            this.options    = [];
        }
    }

    public deselectAll(e: any){
        e.preventDefault();
        e.stopPropagation();

        if (this.selected && this.selected.length){
            Array.prototype.push.call(this.options, ...this.selected);
            this.options.sort(this.customSort);

            this.selected    = [];
            this.onChange.emit(this.selected);
        }
    }
}
