import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IClub } from '../club.model';
import { ClubService } from '../service/club.service';

@Component({
  templateUrl: './club-delete-dialog.component.html',
})
export class ClubDeleteDialogComponent {
  club?: IClub;

  constructor(protected clubService: ClubService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.clubService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
