import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IConversation, Conversation } from '../conversation.model';
import { ConversationService } from '../service/conversation.service';
import { IAbonne } from 'app/entities/abonne/abonne.model';
import { AbonneService } from 'app/entities/abonne/service/abonne.service';

@Component({
  selector: 'jhi-conversation-update',
  templateUrl: './conversation-update.component.html',
})
export class ConversationUpdateComponent implements OnInit {
  isSaving = false;

  abonnesSharedCollection: IAbonne[] = [];

  editForm = this.fb.group({
    id: [],
    objet: [],
    emetteur: [],
    recepteur: [],
  });

  constructor(
    protected conversationService: ConversationService,
    protected abonneService: AbonneService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ conversation }) => {
      this.updateForm(conversation);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const conversation = this.createFromForm();
    if (conversation.id !== undefined) {
      this.subscribeToSaveResponse(this.conversationService.update(conversation));
    } else {
      this.subscribeToSaveResponse(this.conversationService.create(conversation));
    }
  }

  trackAbonneById(_index: number, item: IAbonne): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConversation>>): void {
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

  protected updateForm(conversation: IConversation): void {
    this.editForm.patchValue({
      id: conversation.id,
      objet: conversation.objet,
      emetteur: conversation.emetteur,
      recepteur: conversation.recepteur,
    });

    this.abonnesSharedCollection = this.abonneService.addAbonneToCollectionIfMissing(
      this.abonnesSharedCollection,
      conversation.emetteur,
      conversation.recepteur
    );
  }

  protected loadRelationshipsOptions(): void {
    this.abonneService
      .query()
      .pipe(map((res: HttpResponse<IAbonne[]>) => res.body ?? []))
      .pipe(
        map((abonnes: IAbonne[]) =>
          this.abonneService.addAbonneToCollectionIfMissing(
            abonnes,
            this.editForm.get('emetteur')!.value,
            this.editForm.get('recepteur')!.value
          )
        )
      )
      .subscribe((abonnes: IAbonne[]) => (this.abonnesSharedCollection = abonnes));
  }

  protected createFromForm(): IConversation {
    return {
      ...new Conversation(),
      id: this.editForm.get(['id'])!.value,
      objet: this.editForm.get(['objet'])!.value,
      emetteur: this.editForm.get(['emetteur'])!.value,
      recepteur: this.editForm.get(['recepteur'])!.value,
    };
  }
}
