import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IMessage, Message } from '../message.model';
import { MessageService } from '../service/message.service';
import { IConversation } from 'app/entities/conversation/conversation.model';
import { ConversationService } from 'app/entities/conversation/service/conversation.service';
import { IAbonne } from 'app/entities/abonne/abonne.model';
import { AbonneService } from 'app/entities/abonne/service/abonne.service';

@Component({
  selector: 'jhi-message-update',
  templateUrl: './message-update.component.html',
})
export class MessageUpdateComponent implements OnInit {
  isSaving = false;

  conversationsSharedCollection: IConversation[] = [];
  abonnesSharedCollection: IAbonne[] = [];

  editForm = this.fb.group({
    id: [],
    contenu: [],
    date: [],
    conversation: [null, Validators.required],
    redacteur: [null, Validators.required],
  });

  constructor(
    protected messageService: MessageService,
    protected conversationService: ConversationService,
    protected abonneService: AbonneService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ message }) => {
      this.updateForm(message);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const message = this.createFromForm();
    if (message.id !== undefined) {
      this.subscribeToSaveResponse(this.messageService.update(message));
    } else {
      this.subscribeToSaveResponse(this.messageService.create(message));
    }
  }

  trackConversationById(_index: number, item: IConversation): number {
    return item.id!;
  }

  trackAbonneById(_index: number, item: IAbonne): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMessage>>): void {
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

  protected updateForm(message: IMessage): void {
    this.editForm.patchValue({
      id: message.id,
      contenu: message.contenu,
      date: message.date,
      conversation: message.conversation,
      redacteur: message.redacteur,
    });

    this.conversationsSharedCollection = this.conversationService.addConversationToCollectionIfMissing(
      this.conversationsSharedCollection,
      message.conversation
    );
    this.abonnesSharedCollection = this.abonneService.addAbonneToCollectionIfMissing(this.abonnesSharedCollection, message.redacteur);
  }

  protected loadRelationshipsOptions(): void {
    this.conversationService
      .query()
      .pipe(map((res: HttpResponse<IConversation[]>) => res.body ?? []))
      .pipe(
        map((conversations: IConversation[]) =>
          this.conversationService.addConversationToCollectionIfMissing(conversations, this.editForm.get('conversation')!.value)
        )
      )
      .subscribe((conversations: IConversation[]) => (this.conversationsSharedCollection = conversations));

    this.abonneService
      .query()
      .pipe(map((res: HttpResponse<IAbonne[]>) => res.body ?? []))
      .pipe(map((abonnes: IAbonne[]) => this.abonneService.addAbonneToCollectionIfMissing(abonnes, this.editForm.get('redacteur')!.value)))
      .subscribe((abonnes: IAbonne[]) => (this.abonnesSharedCollection = abonnes));
  }

  protected createFromForm(): IMessage {
    return {
      ...new Message(),
      id: this.editForm.get(['id'])!.value,
      contenu: this.editForm.get(['contenu'])!.value,
      date: this.editForm.get(['date'])!.value,
      conversation: this.editForm.get(['conversation'])!.value,
      redacteur: this.editForm.get(['redacteur'])!.value,
    };
  }
}
