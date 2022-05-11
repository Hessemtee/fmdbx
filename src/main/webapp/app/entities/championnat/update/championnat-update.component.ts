import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IChampionnat, Championnat } from '../championnat.model';
import { ChampionnatService } from '../service/championnat.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IPays } from 'app/entities/pays/pays.model';
import { PaysService } from 'app/entities/pays/service/pays.service';

@Component({
  selector: 'jhi-championnat-update',
  templateUrl: './championnat-update.component.html',
})
export class ChampionnatUpdateComponent implements OnInit {
  isSaving = false;

  paysSharedCollection: IPays[] = [];

  editForm = this.fb.group({
    id: [],
    nom: [],
    nombreDEquipes: [],
    logo: [],
    logoContentType: [],
    niveau: [],
    reputation: [],
    pays: [null, Validators.required],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected championnatService: ChampionnatService,
    protected paysService: PaysService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ championnat }) => {
      this.updateForm(championnat);

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
    const championnat = this.createFromForm();
    if (championnat.id !== undefined) {
      this.subscribeToSaveResponse(this.championnatService.update(championnat));
    } else {
      this.subscribeToSaveResponse(this.championnatService.create(championnat));
    }
  }

  trackPaysById(_index: number, item: IPays): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IChampionnat>>): void {
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

  protected updateForm(championnat: IChampionnat): void {
    this.editForm.patchValue({
      id: championnat.id,
      nom: championnat.nom,
      nombreDEquipes: championnat.nombreDEquipes,
      logo: championnat.logo,
      logoContentType: championnat.logoContentType,
      niveau: championnat.niveau,
      reputation: championnat.reputation,
      pays: championnat.pays,
    });

    this.paysSharedCollection = this.paysService.addPaysToCollectionIfMissing(this.paysSharedCollection, championnat.pays);
  }

  protected loadRelationshipsOptions(): void {
    this.paysService
      .query()
      .pipe(map((res: HttpResponse<IPays[]>) => res.body ?? []))
      .pipe(map((pays: IPays[]) => this.paysService.addPaysToCollectionIfMissing(pays, this.editForm.get('pays')!.value)))
      .subscribe((pays: IPays[]) => (this.paysSharedCollection = pays));
  }

  protected createFromForm(): IChampionnat {
    return {
      ...new Championnat(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
      nombreDEquipes: this.editForm.get(['nombreDEquipes'])!.value,
      logoContentType: this.editForm.get(['logoContentType'])!.value,
      logo: this.editForm.get(['logo'])!.value,
      niveau: this.editForm.get(['niveau'])!.value,
      reputation: this.editForm.get(['reputation'])!.value,
      pays: this.editForm.get(['pays'])!.value,
    };
  }
}
