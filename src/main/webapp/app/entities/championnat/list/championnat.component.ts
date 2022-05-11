import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IChampionnat } from '../championnat.model';
import { ChampionnatService } from '../service/championnat.service';
import { ChampionnatDeleteDialogComponent } from '../delete/championnat-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-championnat',
  templateUrl: './championnat.component.html',
})
export class ChampionnatComponent implements OnInit {
  championnats?: IChampionnat[];
  isLoading = false;

  constructor(protected championnatService: ChampionnatService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.championnatService.query().subscribe({
      next: (res: HttpResponse<IChampionnat[]>) => {
        this.isLoading = false;
        this.championnats = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IChampionnat): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(championnat: IChampionnat): void {
    const modalRef = this.modalService.open(ChampionnatDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.championnat = championnat;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
