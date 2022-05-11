import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IJeu, Jeu } from '../jeu.model';
import { JeuService } from '../service/jeu.service';

@Component({
  selector: 'jhi-jeu-update',
  templateUrl: './jeu-update.component.html',
})
export class JeuUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nom: [null, [Validators.required]],
  });

  constructor(protected jeuService: JeuService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ jeu }) => {
      this.updateForm(jeu);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const jeu = this.createFromForm();
    if (jeu.id !== undefined) {
      this.subscribeToSaveResponse(this.jeuService.update(jeu));
    } else {
      this.subscribeToSaveResponse(this.jeuService.create(jeu));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJeu>>): void {
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

  protected updateForm(jeu: IJeu): void {
    this.editForm.patchValue({
      id: jeu.id,
      nom: jeu.nom,
    });
  }

  protected createFromForm(): IJeu {
    return {
      ...new Jeu(),
      id: this.editForm.get(['id'])!.value,
      nom: this.editForm.get(['nom'])!.value,
    };
  }
}
