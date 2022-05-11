import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IClub, Club } from '../club.model';
import { ClubService } from '../service/club.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IChampionnat } from 'app/entities/championnat/championnat.model';
import { ChampionnatService } from 'app/entities/championnat/service/championnat.service';
import { IJeu } from 'app/entities/jeu/jeu.model';
import { JeuService } from 'app/entities/jeu/service/jeu.service';
import { IAbonne } from 'app/entities/abonne/abonne.model';
import { AbonneService } from 'app/entities/abonne/service/abonne.service';

@Component({
  selector: 'jhi-club-update',
  templateUrl: './club-update.component.html',
})
export class ClubUpdateComponent implements OnInit {
  isSaving = false;

  championnatsSharedCollection: IChampionnat[] = [];
  jeusSharedCollection: IJeu[] = [];
  abonnesSharedCollection: IAbonne[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [null, [Validators.required]],
    logo: [],
    logoContentType: [],
    ville: [],
    balance: [],
    masseSalariale: [],
    budgetSalaires: [],
    budgetTransferts: [],
    infrastructuresEntrainement: [],
    infrastructuresJeunes: [],
    recrutementJeunes: [],
    nomStade: [],
    capaciteStade: [],
    previsionMedia: [],
    indiceContinental: [],
    competitionContinentale: [],
    championnat: [null, Validators.required],
    jeuxes: [],
    bookmarks: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected clubService: ClubService,
    protected championnatService: ChampionnatService,
    protected jeuService: JeuService,
    protected abonneService: AbonneService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ club }) => {
      this.updateForm(club);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('fmdbxApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const club = this.createFromForm();
    if (club.id !== undefined) {
      this.subscribeToSaveResponse(this.clubService.update(club));
    } else {
      this.subscribeToSaveResponse(this.clubService.create(club));
    }
  }

  trackChampionnatById(_index: number, item: IChampionnat): number {
    return item.id!;
  }

  trackJeuById(_index: number, item: IJeu): number {
    return item.id!;
  }

  trackAbonneById(_index: number, item: IAbonne): number {
    return item.id!;
  }

  getSelectedJeu(option: IJeu, selectedVals?: IJeu[]): IJeu {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClub>>): void {
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

  protected updateForm(club: IClub): void {
    this.editForm.patchValue({
      id: club.id,
      nom: club.nom,
      logo: club.logo,
      logoContentType: club.logoContentType,
      ville: club.ville,
      balance: club.balance,
      masseSalariale: club.masseSalariale,
      budgetSalaires: club.budgetSalaires,
      budgetTransferts: club.budgetTransferts,
      infrastructuresEntrainement: club.infrastructuresEntrainement,
      infrastructuresJeunes: club.infrastructuresJeunes,
      recrutementJeunes: club.recrutementJeunes,
      nomStade: club.nomStade,
      capaciteStade: club.capaciteStade,
      previsionMedia: club.previsionMedia,
      indiceContinental: club.indiceContinental,
      competitionContinentale: club.competitionContinentale,
      championnat: club.championnat,
      jeuxes: club.jeuxes,
      bookmarks: club.bookmarks,
    });

    this.championnatsSharedCollection = this.championnatService.addChampionnatToCollectionIfMissing(
      this.championnatsSharedCollection,
      club.championnat
    );
    this.jeusSharedCollection = this.jeuService.addJeuToCollectionIfMissing(this.jeusSharedCollection, ...(club.jeuxes ?? []));
    this.abonnesSharedCollection = this.abonneService.addAbonneToCollectionIfMissing(
      this.abonnesSharedCollection,
      ...(club.bookmarks ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.championnatService
      .query()
      .pipe(map((res: HttpResponse<IChampionnat[]>) => res.body ?? []))
      .pipe(
        map((championnats: IChampionnat[]) =>
          this.championnatService.addChampionnatToCollectionIfMissing(championnats, this.editForm.get('championnat')!.value)
        )
      )
      .subscribe((championnats: IChampionnat[]) => (this.championnatsSharedCollection = championnats));

    this.jeuService
      .query()
      .pipe(map((res: HttpResponse<IJeu[]>) => res.body ?? []))
      .pipe(map((jeus: IJeu[]) => this.jeuService.addJeuToCollectionIfMissing(jeus, ...(this.editForm.get('jeuxes')!.value ?? []))))
      .subscribe((jeus: IJeu[]) => (this.jeusSharedCollection = jeus));

    this.abonneService
      .query()
      .pipe(map((res: HttpResponse<IAbonne[]>) => res.body ?? []))
      .pipe(
        map((abonnes: IAbonne[]) =>
          this.abonneService.addAbonneToCollectionIfMissing(abonnes, ...(this.editForm.get('bookmarks')!.value ?? []))
        )
      )
      .subscribe((abonnes: IAbonne[]) => (this.abonnesSharedCollection = abonnes));
  }

  protected createFromForm(): IClub {
    return {
      ...new Club(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      logoContentType: this.editForm.get(['logoContentType'])!.value,
      logo: this.editForm.get(['logo'])!.value,
      ville: this.editForm.get(['ville'])!.value,
      balance: this.editForm.get(['balance'])!.value,
      masseSalariale: this.editForm.get(['masseSalariale'])!.value,
      budgetSalaires: this.editForm.get(['budgetSalaires'])!.value,
      budgetTransferts: this.editForm.get(['budgetTransferts'])!.value,
      infrastructuresEntrainement: this.editForm.get(['infrastructuresEntrainement'])!.value,
      infrastructuresJeunes: this.editForm.get(['infrastructuresJeunes'])!.value,
      recrutementJeunes: this.editForm.get(['recrutementJeunes'])!.value,
      nomStade: this.editForm.get(['nomStade'])!.value,
      capaciteStade: this.editForm.get(['capaciteStade'])!.value,
      previsionMedia: this.editForm.get(['previsionMedia'])!.value,
      indiceContinental: this.editForm.get(['indiceContinental'])!.value,
      competitionContinentale: this.editForm.get(['competitionContinentale'])!.value,
      championnat: this.editForm.get(['championnat'])!.value,
      jeuxes: this.editForm.get(['jeuxes'])!.value,
      bookmarks: this.editForm.get(['bookmarks'])!.value,
    };
  }
}
