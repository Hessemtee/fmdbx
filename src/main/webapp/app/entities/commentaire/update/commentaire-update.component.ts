import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICommentaire, Commentaire } from '../commentaire.model';
import { CommentaireService } from '../service/commentaire.service';
import { IJoueur } from 'app/entities/joueur/joueur.model';
import { JoueurService } from 'app/entities/joueur/service/joueur.service';
import { IClub } from 'app/entities/club/club.model';
import { ClubService } from 'app/entities/club/service/club.service';
import { IAbonne } from 'app/entities/abonne/abonne.model';
import { AbonneService } from 'app/entities/abonne/service/abonne.service';

@Component({
  selector: 'jhi-commentaire-update',
  templateUrl: './commentaire-update.component.html',
})
export class CommentaireUpdateComponent implements OnInit {
  isSaving = false;

  joueursSharedCollection: IJoueur[] = [];
  clubsSharedCollection: IClub[] = [];
  abonnesSharedCollection: IAbonne[] = [];

  editForm = this.fb.group({
    id: [],
    contenu: [],
    visible: [],
    joueurConcerne: [],
    clubConcerne: [],
    abonne: [null, Validators.required],
  });

  constructor(
    protected commentaireService: CommentaireService,
    protected joueurService: JoueurService,
    protected clubService: ClubService,
    protected abonneService: AbonneService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ commentaire }) => {
      this.updateForm(commentaire);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const commentaire = this.createFromForm();
    if (commentaire.id !== undefined) {
      this.subscribeToSaveResponse(this.commentaireService.update(commentaire));
    } else {
      this.subscribeToSaveResponse(this.commentaireService.create(commentaire));
    }
  }

  trackJoueurById(_index: number, item: IJoueur): number {
    return item.id!;
  }

  trackClubById(_index: number, item: IClub): number {
    return item.id!;
  }

  trackAbonneById(_index: number, item: IAbonne): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICommentaire>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(commentaire: ICommentaire): void {
    this.editForm.patchValue({
      id: commentaire.id,
      contenu: commentaire.contenu,
      visible: commentaire.visible,
      joueurConcerne: commentaire.joueurConcerne,
      clubConcerne: commentaire.clubConcerne,
      abonne: commentaire.abonne,
    });

    this.joueursSharedCollection = this.joueurService.addJoueurToCollectionIfMissing(
      this.joueursSharedCollection,
      commentaire.joueurConcerne
    );
    this.clubsSharedCollection = this.clubService.addClubToCollectionIfMissing(this.clubsSharedCollection, commentaire.clubConcerne);
    this.abonnesSharedCollection = this.abonneService.addAbonneToCollectionIfMissing(this.abonnesSharedCollection, commentaire.abonne);
  }

  protected loadRelationshipsOptions(): void {
    this.joueurService
      .query()
      .pipe(map((res: HttpResponse<IJoueur[]>) => res.body ?? []))
      .pipe(
        map((joueurs: IJoueur[]) => this.joueurService.addJoueurToCollectionIfMissing(joueurs, this.editForm.get('joueurConcerne')!.value))
      )
      .subscribe((joueurs: IJoueur[]) => (this.joueursSharedCollection = joueurs));

    this.clubService
      .query()
      .pipe(map((res: HttpResponse<IClub[]>) => res.body ?? []))
      .pipe(map((clubs: IClub[]) => this.clubService.addClubToCollectionIfMissing(clubs, this.editForm.get('clubConcerne')!.value)))
      .subscribe((clubs: IClub[]) => (this.clubsSharedCollection = clubs));

    this.abonneService
      .query()
      .pipe(map((res: HttpResponse<IAbonne[]>) => res.body ?? []))
      .pipe(map((abonnes: IAbonne[]) => this.abonneService.addAbonneToCollectionIfMissing(abonnes, this.editForm.get('abonne')!.value)))
      .subscribe((abonnes: IAbonne[]) => (this.abonnesSharedCollection = abonnes));
  }

  protected createFromForm(): ICommentaire {
    return {
      ...new Commentaire(),
      id: this.editForm.get(['id'])!.value,
      contenu: this.editForm.get(['contenu'])!.value,
      visible: this.editForm.get(['visible'])!.value,
      joueurConcerne: this.editForm.get(['joueurConcerne'])!.value,
      clubConcerne: this.editForm.get(['clubConcerne'])!.value,
      abonne: this.editForm.get(['abonne'])!.value,
    };
  }
}
