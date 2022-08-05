import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnInit,
  ViewChild,
  ElementRef,
  ChangeDetectorRef,
  OnDestroy
} from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import {AdvertiserService} from "../../../advertiser/services/advertiser.service";

@Component({
  selector: 'ui-option-transfer',
  templateUrl: 'option-transfer.component.html',
  styleUrls: ['./option-transfer.component.scss']
})
export class OptionTransferComponent implements OnInit, OnDestroy {
  @Input()
  set options(options: IdName[]) {
    if (options && options.length && this.firstLoad) {
      this.firstLoad = false;
      this.cashAllSites = options;
    }
  }

  get options(): IdName[] {
    return this.allSites;
  }

  @Input() customSort;
  @Input() selected: IdName[];
  @Input() availableMsg = 'messages.optionTransfer.available';
  @Input() selectedMsg = 'messages.optionTransfer.selected';
  @Input() selectAllMsg = 'messages.optionTransfer.selectAll';
  @Input() deselectAllMsg = 'messages.optionTransfer.deselectAll';
  @Output() transferChange = new EventEmitter();

  @ViewChild('searchInput', { static: true }) imgSearchInput: ElementRef;
  public key = 'id';
  private searchSubject: Subject<any> = new Subject();
  public allSites: IdName[] = [];
  public selectedSites: IdName[] = [];
  private firstLoad: boolean = true;
  private cashAllSites: IdName[] = [];

  constructor(private advertiserService: AdvertiserService, private changeDetectorRef: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.searchSubject.pipe(debounceTime(700)).subscribe((value) => {
      if (value.length > 2) {
        this.allSites = this.options.filter(item => item.name.toLowerCase().indexOf(value.toLowerCase()) >= 0);
      } else {
        this.allSites = this.cashAllSites.filter( el => !this.selectedSites.map((sg) => sg.id).includes(el.id));
      }
    });
    this.selected?.length ? this.selectedSites.push(...this.selected) : null;

    this.allSites = this.cashAllSites.filter( el => !this.selectedSites.map((sg) => sg.id).includes(el.id));
    this.sortUp(this.allSites);
  }

  selectItem(item: IdName): void {
    this.selectedSites.unshift(item);
    this.allSites = this.allSites.filter( el => !this.selectedSites.map((sg) => sg.id).includes(el.id));

    this.transferChange.emit(this.selectedSites);
  }

  deselectItem(item: IdName): void {
    this.selectedSites = this.selectedSites.filter(site => site.id !== item.id);
    this.allSites.unshift(item);

    this.transferChange.emit(this.selectedSites);
  }

  public selectAll(): void {
    this.allSites = [];
    this.selectedSites = this.cashAllSites;
    this.transferChange.emit(this.selectedSites);
  }

  public deselectAll(): void {
    this.allSites = this.cashAllSites;
    this.selectedSites = [];
    this.transferChange.emit(this.selectedSites);
  }

  public sortUp(listForSort: IdName[], mouseEvent?: MouseEvent): void {
    listForSort.sort( (a, b) => {
      if (a.name > b.name) {
        return 1;
      }
      if (a.name < b.name) {
        return -1;
      }
      return 0;
    });
  }

  public sortDown(listForSort: IdName[]): void {
    listForSort.sort( (a, b) => {
      if (a.name > b.name) {
        return -1;
      }
      if (a.name < b.name) {
        return 1;
      }
      return 0;
    });
  }

  public search(): void {
    this.changeDetectorRef.detectChanges();
    this.searchSubject.next(this.imgSearchInput.nativeElement.value);
  }

  ngOnDestroy(): void {}
}
