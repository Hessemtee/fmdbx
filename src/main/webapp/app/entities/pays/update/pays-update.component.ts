import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPays, Pays } from '../pays.model';
import { PaysService } from '../service/pays.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IJoueur } from 'app/entities/joueur/joueur.model';
import { JoueurService } from 'app/entities/joueur/service/joueur.service';

@Component({
  selector: 'jhi-pays-update',
  templateUrl: './pays-update.component.html',
})
export class PaysUpdateComponent implements OnInit {
  isSaving = false;

  joueursSharedCollection: IJoueur[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [],
    drapeau: [],
    drapeauContentType: [],
    confederation: [],
    indiceConf: [],
    rankingFifa: [],
    anneesAvantNationalite: [],
    importanceEnJeu: [],
    joueurs: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected paysService: PaysService,
    protected joueurService: JoueurService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pays }) => {
      this.updateForm(pays);

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
    const pays = this.createFromForm();
    if (pays.id !== undefined) {
      this.subscribeToSaveResponse(this.paysService.update(pays));
    } else {
      this.subscribeToSaveResponse(this.paysService.create(pays));
    }
  }

  trackJoueurById(_index: number, item: IJoueur): number {
    return item.id!;
  }

  getSelectedJoueur(option: IJoueur, selectedVals?: IJoueur[]): IJoueur {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPays>>): void {
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

  protected updateForm(pays: IPays): void {
    this.editForm.patchValue({
      id: pays.id,
      nom: pays.nom,
      drapeau: pays.drapeau,
      drapeauContentType: pays.drapeauContentType,
      confederation: pays.confederation,
      indiceConf: pays.indiceConf,
      rankingFifa: pays.rankingFifa,
      anneesAvantNationalite: pays.anneesAvantNationalite,
      importanceEnJeu: pays.importanceEnJeu,
      joueurs: pays.joueurs,
    });

    this.joueursSharedCollection = this.joueurService.addJoueurToCollectionIfMissing(this.joueursSharedCollection, ...(pays.joueurs ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.joueurService
      .query()
      .pipe(map((res: HttpResponse<IJoueur[]>) => res.body ?? []))
      .pipe(
        map((joueurs: IJoueur[]) =>
          this.joueurService.addJoueurToCollectionIfMissing(joueurs, ...(this.editForm.get('joueurs')!.value ?? []))
        )
      )
      .subscribe((joueurs: IJoueur[]) => (this.joueursSharedCollection = joueurs));
  }

  protected createFromForm(): IPays {
    return {
      ...new Pays(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      drapeauContentType: this.editForm.get(['drapeauContentType'])!.value,
      drapeau: this.editForm.get(['drapeau'])!.value,
      confederation: this.editForm.get(['confederation'])!.value,
      indiceConf: this.editForm.get(['indiceConf'])!.value,
      rankingFifa: this.editForm.get(['rankingFifa'])!.value,
      anneesAvantNationalite: this.editForm.get(['anneesAvantNationalite'])!.value,
      importanceEnJeu: this.editForm.get(['importanceEnJeu'])!.value,
      joueurs: this.editForm.get(['joueurs'])!.value,
    };
  }
}
