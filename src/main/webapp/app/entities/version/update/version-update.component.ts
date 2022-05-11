import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IVersion, Version } from '../version.model';
import { VersionService } from '../service/version.service';
import { IJeu } from 'app/entities/jeu/jeu.model';
import { JeuService } from 'app/entities/jeu/service/jeu.service';

@Component({
  selector: 'jhi-version-update',
  templateUrl: './version-update.component.html',
})
export class VersionUpdateComponent implements OnInit {
  isSaving = false;

  jeusSharedCollection: IJeu[] = [];

  editForm = this.fb.group({
    id: [],
    version: [null, [Validators.required]],
    jeu: [],
  });

  constructor(
    protected versionService: VersionService,
    protected jeuService: JeuService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ version }) => {
      this.updateForm(version);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const version = this.createFromForm();
    if (version.id !== undefined) {
      this.subscribeToSaveResponse(this.versionService.update(version));
    } else {
      this.subscribeToSaveResponse(this.versionService.create(version));
    }
  }

  trackJeuById(_index: number, item: IJeu): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVersion>>): void {
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

  protected updateForm(version: IVersion): void {
    this.editForm.patchValue({
      id: version.id,
      version: version.version,
      jeu: version.jeu,
    });

    this.jeusSharedCollection = this.jeuService.addJeuToCollectionIfMissing(this.jeusSharedCollection, version.jeu);
  }

  protected loadRelationshipsOptions(): void {
    this.jeuService
      .query()
      .pipe(map((res: HttpResponse<IJeu[]>) => res.body ?? []))
      .pipe(map((jeus: IJeu[]) => this.jeuService.addJeuToCollectionIfMissing(jeus, this.editForm.get('jeu')!.value)))
      .subscribe((jeus: IJeu[]) => (this.jeusSharedCollection = jeus));
  }

  protected createFromForm(): IVersion {
    return {
      ...new Version(),
      id: this.editForm.get(['id'])!.value,
      version: this.editForm.get(['version'])!.value,
      jeu: this.editForm.get(['jeu'])!.value,
    };
  }
}
