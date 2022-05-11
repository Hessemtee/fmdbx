import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IMessage } from '../message.model';
import { MessageService } from '../service/message.service';
import { MessageDeleteDialogComponent } from '../delete/message-delete-dialog.component';

@Component({
  selector: 'jhi-message',
  templateUrl: './message.component.html',
})
export class MessageComponent implements OnInit {
  messages?: IMessage[];
  isLoading = false;

  constructor(protected messageService: MessageService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.messageService.query().subscribe({
      next: (res: HttpResponse<IMessage[]>) => {
        this.isLoading = false;
        this.messages = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IMessage): number {
    return item.id!;
  }

  delete(message: IMessage): void {
    const modalRef = this.modalService.open(MessageDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.message = message;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
