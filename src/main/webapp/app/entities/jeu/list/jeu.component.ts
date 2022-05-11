import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IJeu } from '../jeu.model';
import { JeuService } from '../service/jeu.service';
import { JeuDeleteDialogComponent } from '../delete/jeu-delete-dialog.component';

@Component({
  selector: 'jhi-jeu',
  templateUrl: './jeu.component.html',
})
export class JeuComponent implements OnInit {
  jeus?: IJeu[];
  isLoading = false;

  constructor(protected jeuService: JeuService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.jeuService.query().subscribe({
      next: (res: HttpResponse<IJeu[]>) => {
        this.isLoading = false;
        this.jeus = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IJeu): number {
    return item.id!;
  }

  delete(jeu: IJeu): void {
    const modalRef = this.modalService.open(JeuDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.jeu = jeu;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
