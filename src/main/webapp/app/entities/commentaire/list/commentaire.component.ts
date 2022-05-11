import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICommentaire } from '../commentaire.model';
import { CommentaireService } from '../service/commentaire.service';
import { CommentaireDeleteDialogComponent } from '../delete/commentaire-delete-dialog.component';

@Component({
  selector: 'jhi-commentaire',
  templateUrl: './commentaire.component.html',
})
export class CommentaireComponent implements OnInit {
  commentaires?: ICommentaire[];
  isLoading = false;

  constructor(protected commentaireService: CommentaireService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.commentaireService.query().subscribe({
      next: (res: HttpResponse<ICommentaire[]>) => {
        this.isLoading = false;
        this.commentaires = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ICommentaire): number {
    return item.id!;
  }

  delete(commentaire: ICommentaire): void {
    const modalRef = this.modalService.open(CommentaireDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.commentaire = commentaire;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
