import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IJoueur, Joueur } from '../joueur.model';
import { JoueurService } from '../service/joueur.service';
import { IClub } from 'app/entities/club/club.model';
import { ClubService } from 'app/entities/club/service/club.service';
import { IAbonne } from 'app/entities/abonne/abonne.model';
import { AbonneService } from 'app/entities/abonne/service/abonne.service';

@Component({
  selector: 'jhi-joueur-update',
  templateUrl: './joueur-update.component.html',
})
export class JoueurUpdateComponent implements OnInit {
  isSaving = false;

  clubsSharedCollection: IClub[] = [];
  abonnesSharedCollection: IAbonne[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [],
    prenom: [],
    photo: [],
    position: [],
    dateNaissance: [],
    nombreSelections: [],
    nombreButsInternationaux: [],
    valeur: [],
    salaire: [],
    coutEstime: [],
    club: [],
    favorises: [],
  });

  constructor(
    protected joueurService: JoueurService,
    protected clubService: ClubService,
    protected abonneService: AbonneService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ joueur }) => {
      this.updateForm(joueur);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const joueur = this.createFromForm();
    if (joueur.id !== undefined) {
      this.subscribeToSaveResponse(this.joueurService.update(joueur));
    } else {
      this.subscribeToSaveResponse(this.joueurService.create(joueur));
    }
  }

  trackClubById(_index: number, item: IClub): number {
    return item.id!;
  }

  trackAbonneById(_index: number, item: IAbonne): number {
    return item.id!;
  }

  getSelectedAbonne(option: IAbonne, selectedVals?: IAbonne[]): IAbonne {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJoueur>>): void {
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

  protected updateForm(joueur: IJoueur): void {
    this.editForm.patchValue({
      id: joueur.id,
      nom: joueur.nom,
      prenom: joueur.prenom,
      photo: joueur.photo,
      position: joueur.position,
      dateNaissance: joueur.dateNaissance,
      nombreSelections: joueur.nombreSelections,
      nombreButsInternationaux: joueur.nombreButsInternationaux,
      valeur: joueur.valeur,
      salaire: joueur.salaire,
      coutEstime: joueur.coutEstime,
      club: joueur.club,
      favorises: joueur.favorises,
    });

    this.clubsSharedCollection = this.clubService.addClubToCollectionIfMissing(this.clubsSharedCollection, joueur.club);
    this.abonnesSharedCollection = this.abonneService.addAbonneToCollectionIfMissing(
      this.abonnesSharedCollection,
      ...(joueur.favorises ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.clubService
      .query()
      .pipe(map((res: HttpResponse<IClub[]>) => res.body ?? []))
      .pipe(map((clubs: IClub[]) => this.clubService.addClubToCollectionIfMissing(clubs, this.editForm.get('club')!.value)))
      .subscribe((clubs: IClub[]) => (this.clubsSharedCollection = clubs));

    this.abonneService
      .query()
      .pipe(map((res: HttpResponse<IAbonne[]>) => res.body ?? []))
      .pipe(
        map((abonnes: IAbonne[]) =>
          this.abonneService.addAbonneToCollectionIfMissing(abonnes, ...(this.editForm.get('favorises')!.value ?? []))
        )
      )
      .subscribe((abonnes: IAbonne[]) => (this.abonnesSharedCollection = abonnes));
  }

  protected createFromForm(): IJoueur {
    return {
      ...new Joueur(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      prenom: this.editForm.get(['prenom'])!.value,
      photo: this.editForm.get(['photo'])!.value,
      position: this.editForm.get(['position'])!.value,
      dateNaissance: this.editForm.get(['dateNaissance'])!.value,
      nombreSelections: this.editForm.get(['nombreSelections'])!.value,
      nombreButsInternationaux: this.editForm.get(['nombreButsInternationaux'])!.value,
      valeur: this.editForm.get(['valeur'])!.value,
      salaire: this.editForm.get(['salaire'])!.value,
      coutEstime: this.editForm.get(['coutEstime'])!.value,
      club: this.editForm.get(['club'])!.value,
      favorises: this.editForm.get(['favorises'])!.value,
    };
  }
}
