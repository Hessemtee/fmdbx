import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IConversation } from '../conversation.model';
import { ConversationService } from '../service/conversation.service';
import { ConversationDeleteDialogComponent } from '../delete/conversation-delete-dialog.component';

@Component({
  selector: 'jhi-conversation',
  templateUrl: './conversation.component.html',
})
export class ConversationComponent implements OnInit {
  conversations?: IConversation[];
  isLoading = false;

  constructor(protected conversationService: ConversationService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.conversationService.query().subscribe({
      next: (res: HttpResponse<IConversation[]>) => {
        this.isLoading = false;
        this.conversations = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IConversation): number {
    return item.id!;
  }

  delete(conversation: IConversation): void {
    const modalRef = this.modalService.open(ConversationDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.conversation = conversation;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
