import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPays } from '../pays.model';
import { PaysService } from '../service/pays.service';
import { PaysDeleteDialogComponent } from '../delete/pays-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-pays',
  templateUrl: './pays.component.html',
})
export class PaysComponent implements OnInit {
  pays?: IPays[];
  isLoading = false;

  constructor(protected paysService: PaysService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.paysService.query().subscribe({
      next: (res: HttpResponse<IPays[]>) => {
        this.isLoading = false;
        this.pays = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IPays): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(pays: IPays): void {
    const modalRef = this.modalService.open(PaysDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.pays = pays;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
