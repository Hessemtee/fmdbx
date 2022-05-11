import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAbonne } from '../abonne.model';
import { AbonneService } from '../service/abonne.service';
import { AbonneDeleteDialogComponent } from '../delete/abonne-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-abonne',
  templateUrl: './abonne.component.html',
})
export class AbonneComponent implements OnInit {
  abonnes?: IAbonne[];
  isLoading = false;

  constructor(protected abonneService: AbonneService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.abonneService.query().subscribe({
      next: (res: HttpResponse<IAbonne[]>) => {
        this.isLoading = false;
        this.abonnes = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IAbonne): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(abonne: IAbonne): void {
    const modalRef = this.modalService.open(AbonneDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.abonne = abonne;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
